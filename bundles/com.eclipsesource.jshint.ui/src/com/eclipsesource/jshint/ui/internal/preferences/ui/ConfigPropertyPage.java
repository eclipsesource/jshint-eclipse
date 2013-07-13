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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.CommentsFilter;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;

import static com.eclipsesource.jshint.ui.internal.builder.IOUtil.readFileUtf8;
import static com.eclipsesource.jshint.ui.internal.builder.IOUtil.writeFileUtf8;
import static com.eclipsesource.jshint.ui.internal.preferences.ui.LayoutUtil.createMainComposite;


public class ConfigPropertyPage extends AbstractPropertyPage {

  private Button projectSpecificCheckbox;
  private StyledText configText;
  private String origConfig;

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
    createProjectSpecificSection( composite );
    createConfigText( composite );
    loadPreferences();
    return composite;
  }

  private void createProjectSpecificSection( Composite parent ) {
    projectSpecificCheckbox = new Button( parent, SWT.CHECK );
    projectSpecificCheckbox.setText( "Enable project specific configuration" );
    projectSpecificCheckbox.setLayoutData( new GridData( SWT.BEGINNING, SWT.BEGINNING, false, false ) );
    projectSpecificCheckbox.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        validate();
      }
    } );
    Label label = new Label( parent, SWT.WRAP );
    label.setText( "The project specific configuration will be read from a file named .jshintrc"
                   + " in the project root."
                   + " For the syntax of this file, see http://www.jshint.com/docs/" );
    GridData labelData = new GridData( SWT.FILL, SWT.TOP, true, false );
    labelData.widthHint = 400;
    label.setLayoutData( labelData );
  }

  private void createConfigText( Composite parent ) {
    configText = new StyledText( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    configText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
    layoutData.widthHint = 400;
    layoutData.heightHint = 400;
    configText.setLayoutData( layoutData );
    configText.addModifyListener( new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        validateConfigText();
      }
    } );
  }

  private void validateConfigText() {
    String config = new CommentsFilter( configText.getText() ).toString();
    try {
      JsonObject.readFrom( config );
      setErrorMessage( null );
    } catch( ParseException exception ) {
      int line = exception.getLine();
      int column = exception.getColumn();
      setErrorMessage( "Syntax error in configuration " + line + ":" + column );
    }
  }

  private void validate() {
    boolean projectSpecific = projectSpecificCheckbox.getSelection();
    if( projectSpecific ) {
      validateConfigText();
    } else {
      setErrorMessage( null );
    }
  }

  private void loadPreferences() {
    Preferences node = getPreferences();
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    projectSpecificCheckbox.setSelection( optionsPreferences.getProjectSpecific() );
    IFile configFile = getConfigFile();
    if( configFile.isAccessible() ) {
      setErrorMessage( "Config file does not exist in project" );
    }
    origConfig = readConfigFile( configFile );
    if( origConfig != null ) {
      configText.setText( origConfig );
    }
  }

  private boolean storePreferences() throws CoreException {
    Preferences node = getPreferences();
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    optionsPreferences.setProjectSpecific( projectSpecificCheckbox.getSelection() );
    boolean prefsChanged = optionsPreferences.hasChanged();
    if( prefsChanged ) {
      savePreferences();
    }
    String newConfig = configText.getText();
    boolean configChanged = !newConfig.equals( origConfig );
    if( configChanged ) {
      writeFileUtf8( getConfigFile(), newConfig );
    }
    return prefsChanged || configChanged;
  }

  private String readConfigFile( IFile configFile ) {
    try {
      return readFileUtf8( configFile );
    } catch( Exception exception ) {
      setErrorMessage( "Could not read config file" );
      setValid( false );
      return null;
    }
  }

  private IFile getConfigFile() {
    return getResource().getProject().getFile( ".jshintrc" );
  }

  private void triggerRebuild() throws CoreException {
    IProject project = getResource().getProject();
    BuilderUtil.triggerClean( project, JSHintBuilder.ID );
  }

}
