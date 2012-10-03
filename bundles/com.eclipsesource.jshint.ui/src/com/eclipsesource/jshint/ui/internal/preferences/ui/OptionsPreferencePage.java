/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;


public class OptionsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private OptionsView optionsView;

  public OptionsPreferencePage() {
    setDescription( "Global JSHint options" );
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
    optionsView.loadDefaults();
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
    Composite composite = LayoutUtil.createMainComposite( parent );
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    optionsView = new OptionsView( composite, SWT.NONE );
    optionsView.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    optionsView.loadPreferences( optionsPreferences );
    return composite;
  }

  private boolean storePreferences() throws CoreException {
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    optionsView.storePreferences( optionsPreferences );
    boolean changed = optionsPreferences.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
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
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for( IProject project : projects ) {
      if( project.isAccessible() ) {
        BuilderUtil.triggerClean( project, JSHintBuilder.ID );
      }
    }
  }

  private Preferences getPreferences() {
    return PreferencesFactory.getWorkspacePreferences();
  }

}
