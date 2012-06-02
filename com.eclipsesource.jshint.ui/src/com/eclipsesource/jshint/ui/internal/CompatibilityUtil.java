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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
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
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;


class CompatibilityUtil {

  static final String KEY_PLUGIN_VERSION = "plugin.version";
  private static final String OLD_PLUGIN_ID = "com.eclipsesource.jshint";

  private CompatibilityUtil() {
  }

  static void run() {
    Preferences preferences = PreferencesFactory.getWorkspacePreferences();
    Version previousVersion = getPreviousVersion( preferences );
    Version currentVersion = getCurrentVersion();
    if( !currentVersion.equals( previousVersion ) ) {
      firstRun( previousVersion, currentVersion );
      setVersion( preferences, currentVersion );
    }
  }

  static Version getPreviousVersion( Preferences preferences ) {
    String versionString = preferences.get( KEY_PLUGIN_VERSION, null );
    return versionString == null ? null : new Version( versionString );
  }

  static Version getCurrentVersion() {
    return Activator.getDefault().getBundle().getVersion();
  }

  private static void firstRun( Version previousVersion, Version currentVersion ) {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for( IProject project : projects ) {
      if( project.isAccessible() ) {
        try {
          updateProject( project, previousVersion, currentVersion );
        } catch( CoreException exception ) {
          Activator.logError( "Failed to update project metadata:" + project.getName(), exception );
        }
      }
    }
  }

  private static void updateProject( IProject project,
                                     Version previousVersion,
                                     Version currentVersion ) throws CoreException
  {
    updateObsoleteBuilderId( project );
    movePropertiesFromObsoleteNode( project );
    turnEnabledToIncludes( project );
    if( !isGreaterOrEqual( previousVersion, 0, 9, 4 ) ) {
      fixPre09FolderExcludePatterns( project );
    }
  }

  private static boolean isGreaterOrEqual( Version version, int major, int minor, int micro ) {
    boolean result = false;
    if( version != null ) {
      result = version.compareTo( new Version( major, minor, micro ) ) >= 0;
    }
    return result;
  }

  private static void updateObsoleteBuilderId( IProject project ) throws CoreException {
    if( BuilderUtil.removeBuilderFromProject( project, JSHintBuilder.ID_OLD ) ) {
      BuilderUtil.addBuilderToProject( project, JSHintBuilder.ID );
    }
  }

  private static void movePropertiesFromObsoleteNode( IProject project ) throws CoreException {
    ProjectScope projectScope = new ProjectScope( project );
    Preferences oldNode = projectScope.getNode( OLD_PLUGIN_ID );
    if( oldNode != null ) {
      String[] keys = readPreferencesKeys( oldNode );
      if( keys.length > 0 ) {
        Preferences newNode = projectScope.getNode( Activator.PLUGIN_ID );
        for( String key : keys ) {
          newNode.put( key, oldNode.get( key, "" ) );
          oldNode.remove( key );
        }
        flushPreferences( oldNode );
        flushPreferences( newNode );
      }
    }
  }

  private static void turnEnabledToIncludes( IProject project ) throws CoreException {
    Preferences projectPrefs = PreferencesFactory.getProjectPreferences( project );
    EnablementPreferences enablementPrefs = new EnablementPreferences( projectPrefs );
    if( "true".equals( projectPrefs.get( "enabled", "" ) ) ) {
      enablementPrefs.setIncludePatterns( createBasicIncludesList() );
      projectPrefs.remove( "enabled" );
      flushPreferences( projectPrefs );
    }
  }

  private static void fixPre09FolderExcludePatterns( IProject project ) throws CoreException {
    Preferences projectPrefs = PreferencesFactory.getProjectPreferences( project );
    EnablementPreferences enablementPrefs = new EnablementPreferences( projectPrefs );
    List<String> oldExcludePatterns = enablementPrefs.getExcludePatterns();
    List<String> newExcludePatterns = new ArrayList<String>();
    for( String pattern : oldExcludePatterns ) {
      if( !pattern.endsWith( "/" ) && hasFolder( project, pattern ) ) {
        newExcludePatterns.add( pattern + "//" );
      } else {
        newExcludePatterns.add( pattern );
      }
    }
    enablementPrefs.setExcludePatterns( newExcludePatterns );
    flushPreferences( projectPrefs );
  }

  private static boolean hasFolder( IProject project, String path ) {
    return project.findMember( path ) instanceof IFolder;
  }

  private static List<String> createBasicIncludesList() {
    List<String> list = new ArrayList<String>();
    list.add( "//*.js" );
    return list;
  }

  private static void setVersion( Preferences preferences, Version version ) {
    preferences.put( KEY_PLUGIN_VERSION, version.toString() );
    try {
      flushPreferences( preferences );
    } catch( CoreException exception ) {
      Activator.logError( "Failed to store new jshint-eclipse version in workspace", exception );
    }
  }

  private static String[] readPreferencesKeys( Preferences preferences ) throws CoreException {
    try {
      return preferences.keys();
    } catch( BackingStoreException exception ) {
      String message = "Failed to read preferences keys: " + preferences.absolutePath();
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
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
