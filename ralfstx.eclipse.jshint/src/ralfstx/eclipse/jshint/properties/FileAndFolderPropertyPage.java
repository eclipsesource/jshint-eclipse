/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


public class FileAndFolderPropertyPage extends AbstractPropertyPage {

  private Button excludeCheckbox;

  @Override
  public boolean performOk() {
    try {
      IResource resource = getResource();
      ProjectPreferences preferences = getProjectPreferences();
      preferences.setExcluded( resource, excludeCheckbox.getSelection() );
      preferences.save();
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
    State state = readState();
    addEnablementSection( composite, state );
    return composite;
  }

  private void addEnablementSection( Composite parent, State state ) {
    Composite composite = createDefaultComposite( parent );
    Label label = new Label( composite, SWT.NONE );
    label.setLayoutData( createSpanGridData() );
    label.setText( "The project has JSHint " + ( state.projectEnabled ? "enabled" : "disabled" ) );
    excludeCheckbox = new Button( composite, SWT.CHECK );
    String fileOrFolder = getResource().getType() == IResource.FILE ? "file" : "folder";
    excludeCheckbox.setText( "Exclude this " + fileOrFolder + " from jshint validation" );
    excludeCheckbox.setSelection( state.fileExcluded );
    excludeCheckbox.setEnabled( state.projectEnabled );
  }

  private State readState() {
    ProjectPreferences preferences = getProjectPreferences();
    boolean projectEnabled = preferences.getEnabled();
    boolean fileExcluded = preferences.getExcluded( getResource() );
    return new State( projectEnabled, fileExcluded );
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
