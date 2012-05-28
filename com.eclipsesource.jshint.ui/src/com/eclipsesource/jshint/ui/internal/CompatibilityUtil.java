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
package com.eclipsesource.jshint.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;


class CompatibilityUtil {

  static final String KEY_PLUGIN_VERSION = "plugin.version";
  private static final String OLD_PLUGIN_ID = "com.eclipsesource.jshint";

  private CompatibilityUtil() {
  }

  static void run() throws CoreException {
    Preferences preferences = PreferencesFactory.getWorkspacePreferences();
    String lastVersion = preferences.get( KEY_PLUGIN_VERSION, "" );
    String currentVersion = getPluginVersion();
    if( !currentVersion.equals( lastVersion ) ) {
      firstRun( lastVersion, currentVersion );
      setVersion( preferences, currentVersion );
    }
  }

  static String getPluginVersion() {
    Version version = Activator.getDefault().getBundle().getVersion();
    return version.toString();
  }

  private static void firstRun( String lastVersion, String currentVersion ) throws CoreException {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for( IProject project : projects ) {
      if( project.isAccessible() ) {
        updateObsoleteBuilder( project );
        updateObsoleteProperties( project );
      }
    }
  }

  private static void updateObsoleteBuilder( IProject project ) throws CoreException {
    if( BuilderUtil.removeBuilderFromProject( project, JSHintBuilder.ID_OLD ) ) {
      BuilderUtil.addBuilderToProject( project, JSHintBuilder.ID );
    }
  }

  private static void updateObsoleteProperties( IProject project ) throws CoreException {
    ProjectScope projectScope = new ProjectScope( project );
    Preferences oldNode = projectScope.getNode( OLD_PLUGIN_ID );
    try {
      String[] keys = oldNode.keys();
      if( keys.length > 0 ) {
        Preferences newNode = projectScope.getNode( Activator.PLUGIN_ID );
        for( String key : keys ) {
          newNode.put( key, oldNode.get( key, "" ) );
          oldNode.remove( key );
        }
        oldNode.flush();
        newNode.flush();
      }
    } catch( BackingStoreException exception ) {
      String message = "Failed to copy old properties";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  private static void setVersion( Preferences preferences, String version ) throws CoreException {
    preferences.put( KEY_PLUGIN_VERSION, version );
    flushPreferences( preferences );
  }

  private static void flushPreferences( Preferences preferences ) throws CoreException {
    try {
      preferences.flush();
    } catch( BackingStoreException exception ) {
      String message = "Failed to write to preferences: " + preferences.absolutePath();
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

}
