package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


public class FilePropertyPage extends AbstractPropertyPage {

  private Button excludeCheckbox;

  @Override
  public boolean performOk() {
    try {
      IResource resource = getResource();
      boolean excluded = excludeCheckbox.getSelection();
      StatusHelper.setFileExcluded( resource, excluded );
      resource.touch( null );
    } catch( CoreException e ) {
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    if( isValid() ) {
      excludeCheckbox.setSelection( false );
    }
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = createMainComposite( parent );
    try {
      State state = readState();
      addEnablementSection( composite, state );
    } catch( CoreException exception ) {
      addErrorSection( composite );
      hideButtons();
    }
    return composite;
  }

  private void addEnablementSection( Composite parent, State state ) {
    Composite composite = createDefaultComposite( parent );
    Label label = new Label( composite, SWT.NONE );
    label.setLayoutData( createSpanGridData() );
    label.setText( "The project has JSHint " + ( state.projectEnabled ? "enabled" : "disabled" ) );
    excludeCheckbox = new Button( composite, SWT.CHECK );
    excludeCheckbox.setText( "Exclude this file from jshint checks" );
    excludeCheckbox.setSelection( state.fileExcluded );
    excludeCheckbox.setEnabled( state.projectEnabled );
  }

  private void addErrorSection( Composite parent ) {
    Composite composite = createDefaultComposite( parent );
    Label iconLabel = new Label( composite, SWT.NONE );
    iconLabel.setImage( parent.getDisplay().getSystemImage( SWT.ICON_ERROR ) );
    Label messageLabel = new Label( composite, SWT.NONE );
    messageLabel.setText( "Failed to read properties" );
  }

  private State readState() throws CoreException {
    boolean projectEnabled = getProjectEnabled();
    boolean fileExcluded = getFileExcluded();
    return new State( projectEnabled, fileExcluded );
  }

  private boolean getFileExcluded() throws CoreException {
    IResource resource = getResource();
    return StatusHelper.getFileExcluded( resource );
  }

  static class State {
    public final boolean projectEnabled;
    public final boolean fileExcluded;

    public State( boolean projectEnabled, boolean fileExcluded ) {
      this.projectEnabled = projectEnabled;
      this.fileExcluded = fileExcluded;
    }
  }

}
