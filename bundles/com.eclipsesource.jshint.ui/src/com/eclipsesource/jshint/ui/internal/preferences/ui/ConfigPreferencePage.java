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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.CommentsFilter;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;


public class ConfigPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private StyledText configText;

  public ConfigPreferencePage() {
    setDescription( "Global JSHint configuration" );
  }

  public void init( IWorkbench workbench ) {
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return null;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    configText.setText( "" ); // TODO set sensible default config
  }

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
  protected Control createContents( Composite parent ) {
    Composite composite = LayoutUtil.createDefaultComposite( parent, 2 );
    createConfigText( composite );
    loadPreferences();
    return composite;
  }

  private void createConfigText( Composite composite ) {
    configText = new StyledText( composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
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

  private void loadPreferences() {
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    configText.setText( optionsPreferences.getConfig() );
  }

  private boolean storePreferences() throws CoreException {
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    optionsPreferences.setConfig( configText.getText() );
    boolean changed = optionsPreferences.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
  }

  private static void savePreferences() throws CoreException {
    Preferences node = getPreferences();
    try {
      node.flush();
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  private static void triggerRebuild() throws CoreException {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for( IProject project : projects ) {
      if( project.isAccessible() ) {
        BuilderUtil.triggerClean( project, JSHintBuilder.ID );
      }
    }
  }

  private static Preferences getPreferences() {
    return PreferencesFactory.getWorkspacePreferences();
  }

}
