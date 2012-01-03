package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ralfstx.eclipse.jshint.builder.BuilderAdapter;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private Button enablementCheckbox;
  protected boolean dirty;

  @Override
  public boolean performOk() {
    try {
      IResource resource = getResource();
      IProject project = resource.getProject();
      boolean enabled = enablementCheckbox.getSelection();
      StatusHelper.setProjectEnabled( resource, enabled );
      if( dirty ) {
        if( enabled ) {
          new BuilderAdapter( project ).enableJSHint();
        } else {
          new BuilderAdapter( project ).disableJSHint();
        }
      }
      dirty = false;
    } catch( CoreException e ) {
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    enablementCheckbox.setSelection( false );
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = createMainComposite( parent );
    try {
      boolean enabled = getProjectEnabled();
      addEnablementSection( composite, enabled );
    } catch( CoreException e ) {
      addErrorSection( composite );
      hideButtons();
    }
    return composite;
  }

  private void addEnablementSection( Composite parent, boolean enabled ) {
    Composite composite = createDefaultComposite( parent );
    enablementCheckbox = new Button( composite, SWT.CHECK );
    enablementCheckbox.setText( "Enable jshint for this project" );
    enablementCheckbox.setSelection( enabled );
    enablementCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        dirty = true;
      }
    } );
  }

  private void addErrorSection( Composite parent ) {
    Composite composite = createDefaultComposite( parent );
    Label iconLabel = new Label( composite, SWT.NONE );
    iconLabel.setImage( parent.getDisplay().getSystemImage( SWT.ICON_ERROR ) );
    Label messageLabel = new Label( composite, SWT.NONE );
    messageLabel.setText( "Failed to read properties" );
  }

}
