package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ralfstx.eclipse.jshint.Activator;
import ralfstx.eclipse.jshint.builder.BuilderAdapter;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private Button enablementCheckbox;
  private Text predefinedText;
  private Text optionsText;
  private Composite configSection;
  private ProjectPreferences prefs;

  @Override
  public boolean performOk() {
    try {
      prefs.setGlobals( predefinedText.getText() );
      prefs.setOptions( optionsText.getText() );
      prefs.save();
      IProject project = getResource().getProject();
      boolean enabled = enablementCheckbox.getSelection();
      StatusHelper.setProjectEnabled( project, enabled );
      if( enabled ) {
        new BuilderAdapter( project ).enableJSHint();
      } else {
        new BuilderAdapter( project ).disableJSHint();
      }
    } catch( CoreException exception ) {
      String message = "Failed to store settings";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      Platform.getLog( Activator.getDefault().getBundle() ).log( status );
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    enablementCheckbox.setSelection( false );
    predefinedText.setText( "" );
    optionsText.setText( "" );
  }

  @Override
  protected Control createContents( Composite parent ) {
    prefs = new ProjectPreferences( (IProject)getResource() );
    Composite composite = createMainComposite( parent );
    try {
      boolean enabled = getProjectEnabled();
      addEnablementSection( composite, enabled );
      addConfigSection( composite, enabled );
    } catch( CoreException e ) {
      addErrorSection( composite );
      hideButtons();
    }
    return composite;
  }

  private void addEnablementSection( Composite parent, boolean enabled ) {
    Composite composite = createDefaultComposite( parent );
    enablementCheckbox = new Button( composite, SWT.CHECK );
    enablementCheckbox.setText( "Enable JSHint for this project" );
    enablementCheckbox.setSelection( enabled );
    enablementCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        configSection.setEnabled( enablementCheckbox.getSelection() );
      }
    } );
  }

  private void addConfigSection( Composite parent, boolean enabled ) {
    configSection = new Composite( parent, SWT.NONE );
    configSection.setLayout( new GridLayout() );
    configSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    configSection.setEnabled( enabled );

    Label predefinedLabel = new Label( configSection, SWT.NONE );
    predefinedLabel.setText( "Predefined globals:" );

    predefinedText = new Text( configSection, SWT.BORDER );
    predefinedText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    predefinedText.setText( prefs.getGlobals() );

    Text predefinedSubLabel = new Text( configSection, SWT.READ_ONLY | SWT.WRAP );
    predefinedSubLabel.setText( "Example: \"org, com, ...\"" );
    predefinedSubLabel.setLayoutData( createGridDataWithIndent( 20 ) );
    predefinedSubLabel.setBackground( configSection.getBackground() );

    Label optionsLabel = new Label( configSection, SWT.NONE );
    optionsLabel.setText( "JSHint Options:" );

    optionsText = new Text( configSection, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    optionsText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    optionsText.setText( prefs.getOptions() );

    Text optionsSubLabel = new Text( configSection, SWT.READ_ONLY | SWT.WRAP );
    optionsSubLabel.setText( "Example: \"strict: false, sub: true, ...\"\n"
                             + "see http://www.jshint.com/options/" );
    optionsSubLabel.setLayoutData( createGridDataWithIndent( 20 ) );
    optionsSubLabel.setBackground( configSection.getBackground() );
  }

  private GridData createGridDataWithIndent( int indent ) {
    GridData predefinedSubData = new GridData();
    predefinedSubData.horizontalIndent = indent;
    return predefinedSubData;
  }

  private void addErrorSection( Composite parent ) {
    Composite composite = createDefaultComposite( parent );
    Label iconLabel = new Label( composite, SWT.NONE );
    iconLabel.setImage( parent.getDisplay().getSystemImage( SWT.ICON_ERROR ) );
    Label messageLabel = new Label( composite, SWT.NONE );
    messageLabel.setText( "Failed to read properties" );
  }

}
