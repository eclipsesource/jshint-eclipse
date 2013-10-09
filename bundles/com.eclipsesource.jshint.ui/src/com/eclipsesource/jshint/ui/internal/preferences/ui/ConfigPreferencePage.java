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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;

import static com.eclipsesource.jshint.ui.internal.util.JsonUtil.jsonEquals;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridData;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridLayout;


public class ConfigPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private ConfigEditor configEditor;
  private String origConfig;

  public ConfigPreferencePage() {
    setDescription( "Global JSHint configuration" );
  }

  public void init( IWorkbench workbench ) {
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    Control labelPart = createLabelPart( composite );
    Control configTextPart = createConfigTextPart( composite );
    Control buttonsPart = createButtonsPart( composite );
    gridData( composite ).fillBoth();
    gridLayout( composite ).columns( 2 ).spacing( 3 );
    gridData( labelPart ).span( 2, 1 ).fillHorizontal().widthHint( 360 );
    gridData( configTextPart ).fillBoth().sizeHint( 360, 180 );
    gridData( buttonsPart ).align( SWT.BEGINNING, SWT.BEGINNING );
    loadPreferences();
    return composite;
  }

  private Control createLabelPart( Composite parent ) {
    Link link = new Link( parent, SWT.WRAP );
    link.setText( "For syntax, see <a>http://www.jshint.com/docs/</a>." );
    BrowserSupport.INSTANCE.enableHyperlinks( link );
    return link;
  }

  private Control createConfigTextPart( Composite parent ) {
    configEditor = new ConfigEditor( parent ) {
      @Override
      public void handleError( String message ) {
        setErrorMessage( message );
        setValid( message == null );
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

  private void loadPreferences() {
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    origConfig = optionsPreferences.getConfig();
    configEditor.setText( origConfig );
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    configEditor.setText( OptionsPreferences.DEFAULT_CONFIG );
  }

  @Override
  public boolean performOk() {
    try {
      storePreferences();
      if( !jsonEquals( configEditor.getText(), origConfig ) ) {
        triggerRebuild();
      }
    } catch( CoreException exception ) {
      // TODO revise error handling
      String message = "Failed to store settings";
      Activator.logError( message, exception );
      return false;
    }
    return true;
  }

  private void storePreferences() throws CoreException {
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    optionsPreferences.setConfig( configEditor.getText() );
    if( optionsPreferences.hasChanged() ) {
      savePreferences();
    }
  }

  private void savePreferences() throws CoreException {
    Preferences node = getPreferences();
    try {
      node.flush();
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  private void triggerRebuild() throws CoreException {
    for( IProject project : getProjects() ) {
      if( project.isAccessible() ) {
        BuilderUtil.triggerClean( project, JSHintBuilder.ID );
      }
    }
  }

  IProject[] getProjects() {
    return ResourcesPlugin.getWorkspace().getRoot().getProjects();
  }

  Preferences getPreferences() {
    return PreferencesFactory.getWorkspacePreferences();
  }

}
