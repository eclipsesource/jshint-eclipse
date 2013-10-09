/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.util.JsonUtil;

import static com.eclipsesource.jshint.ui.internal.util.IOUtil.readFileUtf8;
import static com.eclipsesource.jshint.ui.internal.util.IOUtil.writeFileUtf8;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridData;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridLayout;


public class ConfigPropertyPage extends AbstractPropertyPage {

  private Button projectSpecificCheckbox;
  private ConfigEditor configEditor;
  private String origConfig;

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    Control projectSpecificPart = createProjectSpecificPart( composite );
    Control labelPart = createLabelPart( composite );
    Control configTextPart = createConfigTextPart( composite );
    Control buttonsPart = createButtonsPart( composite );
    gridData( composite ).fillBoth();
    gridLayout( composite ).columns( 2 ).spacing( 3 );
    gridData( projectSpecificPart );
    gridData( labelPart ).span( 2, 1 ).fillHorizontal().widthHint( 360 );
    gridData( configTextPart ).fillBoth().sizeHint( 360, 180 );
    gridData( buttonsPart ).align( SWT.BEGINNING, SWT.BEGINNING );
    loadPreferences();
    return composite;
  }

  private Control createProjectSpecificPart( Composite parent ) {
    projectSpecificCheckbox = new Button( parent, SWT.CHECK );
    projectSpecificCheckbox.setText( "Enable project specific configuration" );
    projectSpecificCheckbox.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        prefsChanged();
      }
    });
    return projectSpecificCheckbox;
  }

  private Control createLabelPart( Composite parent ) {
    Link link = new Link( parent, SWT.WRAP );
    link.setText( "The project specific configuration will be read from a file named .jshintrc"
                   + " in the project root."
                   + " For the syntax of this file, see <a>http://www.jshint.com/docs/</a>." );
    BrowserSupport.INSTANCE.enableHyperlinks( link );
    return link;
  }

  private Control createConfigTextPart( Composite parent ) {
    configEditor = new ConfigEditor( parent ) {
      @Override
      public void handleError( String message ) {
        if( projectSpecificCheckbox.getSelection() ) {
          setErrorMessage( message );
          setValid( message == null );
        }
      }
    };
    return configEditor.getControl();
  }

  private Control createButtonsPart( Composite parent ) {
    ButtonBar buttonBar = new ButtonBar( parent, SWT.NONE );
    buttonBar.addButton( "I&mport", new Listener() {
      public void handleEvent( Event event ) {
        configEditor.importConfig();
      }
    });
    buttonBar.addButton( "E&xport", new Listener() {
      public void handleEvent( Event event ) {
        configEditor.exportConfig();
      }
    });
    return buttonBar;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    projectSpecificCheckbox.setSelection( OptionsPreferences.DEFAULT_PROJ_SPECIFIC );
    configEditor.setText( OptionsPreferences.DEFAULT_CONFIG );
  }

  @Override
  public boolean performOk() {
    try {
      boolean prefsChanged = storePrefs();
      boolean configChanged = projectSpecificCheckbox.getSelection() && storeConfig();
      if( prefsChanged || configChanged ) {
        triggerRebuild();
      }
    } catch( CoreException exception ) {
      String message = "Failed to store preferences";
      Activator.logError( message, exception );
      return false;
    }
    return true;
  }

  private void prefsChanged() {
    boolean projectSpecific = projectSpecificCheckbox.getSelection();
    configEditor.setEnabled( projectSpecific );
    if( projectSpecific ) {
      configEditor.validate();
    } else {
      setErrorMessage( null );
      setValid( true );
    }
  }

  private void loadPreferences() {
    OptionsPreferences prefs = new OptionsPreferences( getPreferences() );
    projectSpecificCheckbox.setSelection( prefs.getProjectSpecific() );
    origConfig = readConfigFile();
    configEditor.setText( origConfig != null ? origConfig : getDefaultConfig() );
    configEditor.setEnabled( prefs.getProjectSpecific() );
  }

  private boolean storePrefs() throws CoreException {
    OptionsPreferences prefs = new OptionsPreferences( getPreferences() );
    prefs.setProjectSpecific( projectSpecificCheckbox.getSelection() );
    boolean changed = prefs.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
  }

  private boolean storeConfig() throws CoreException {
    String config = configEditor.getText();
    boolean jsonChanged = !JsonUtil.jsonEquals( config, origConfig );
    writeConfigFile( config );
    origConfig = config;
    return jsonChanged;
  }

  private String readConfigFile() {
    IFile configFile = getConfigFile();
    if( checkExists( configFile ) ) {
      try {
        return readFileUtf8( configFile );
      } catch( Exception exception ) {
        String message = "Failed to read config file";
        setErrorMessage( message );
        Activator.logError( message, exception );
      }
    }
    return null;
  }

  private void writeConfigFile( String content ) throws CoreException {
    try {
      writeFileUtf8( getConfigFile(), content );
    } catch( CoreException exception ) {
      String message = "Could not write to config file";
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message ) );
    }
  }

  private IFile getConfigFile() {
    return getResource().getProject().getFile( ".jshintrc" );
  }

  private String getDefaultConfig() {
    return new OptionsPreferences( getPreferences() ).getConfig();
  }

  private void triggerRebuild() throws CoreException {
    IProject project = getResource().getProject();
    BuilderUtil.triggerClean( project, JSHintBuilder.ID );
  }

  private static boolean checkExists( IFile file ) {
    if( file.exists() ) {
      return true;
    }
    try {
      file.refreshLocal( IResource.DEPTH_ZERO, null );
    } catch( CoreException exception ) {
      Activator.logError( exception.getLocalizedMessage(), exception );
    }
    return file.exists();
  }

}
