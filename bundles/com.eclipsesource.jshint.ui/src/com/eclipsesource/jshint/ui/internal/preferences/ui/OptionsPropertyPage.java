/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;

import static com.eclipsesource.jshint.ui.internal.preferences.ui.LayoutUtil.*;


public class OptionsPropertyPage extends AbstractPropertyPage {

  private Button projectSpecificCheckbox;

  @Override
  public boolean performOk() {
    try {
      boolean preferencesChanged = storePreferences();
      if( preferencesChanged ) {
        triggerRebuild();
      }
    } catch( CoreException exception ) {
      String message = "Failed to store settings";
      Activator.logError( message, exception );
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    projectSpecificCheckbox.setSelection( OptionsPreferences.DEFAULT_PROJ_SPECIFIC_OPTIONS );
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = createMainComposite( parent );
    addProjectSpecificSection( composite );
    loadPreferences();
    return composite;
  }

  private void addProjectSpecificSection( Composite parent ) {
    Composite composite = createDefaultComposite( parent, 1 );
    projectSpecificCheckbox = new Button( composite, SWT.CHECK );
    projectSpecificCheckbox.setText( "Enable project specific configuration" );
    projectSpecificCheckbox.setLayoutData( createHorizontalFillGridData() );
    projectSpecificCheckbox.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        validate();
      }
    } );
    Label label = new Label( composite, SWT.WRAP );
    label.setText( "The project specific configuration will be read from a file named .jshintrc"
                   + " in the project root."
                   + " For the syntax of this file, see http://www.jshint.com/docs/" );
    label.setLayoutData( createHorizontalFillGridDataWithWidth( 400 ) );
  }

  private IFile getConfigFile() {
    return getResource().getProject().getFile( ".jshintrc" );
  }

  private void validate() {
    boolean projectSpecific = projectSpecificCheckbox.getSelection();
    if( projectSpecific && !getConfigFile().exists() ) {
      setErrorMessage( "Config file does not exist in project" );
    } else {
      setErrorMessage( null );
    }
  }

  private void loadPreferences() {
    Preferences node = getPreferences();
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    projectSpecificCheckbox.setSelection( optionsPreferences.getProjectSpecific() );
  }

  private boolean storePreferences() throws CoreException {
    Preferences node = getPreferences();
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    optionsPreferences.setProjectSpecific( projectSpecificCheckbox.getSelection() );
    boolean changed = optionsPreferences.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
  }

  private void triggerRebuild() throws CoreException {
    IProject project = getResource().getProject();
    BuilderUtil.triggerClean( project, JSHintBuilder.ID );
  }

}
