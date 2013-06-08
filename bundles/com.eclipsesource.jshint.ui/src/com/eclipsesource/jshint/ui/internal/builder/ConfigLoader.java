/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.json.JsonObject;


class ConfigLoader {

  private final IProject project;

  public ConfigLoader( IProject project ) {
    this.project = project;
  }

  public JsonObject getConfiguration() {
    Preferences projectNode = PreferencesFactory.getProjectPreferences( project );
    OptionsPreferences projectPreferences = new OptionsPreferences( projectNode );
    if( projectPreferences.getProjectSpecific() ) {
      return getProjectConfig( project, projectPreferences );
    }
    return getWorkspaceConfig();
  }

  private static JsonObject getWorkspaceConfig() {
    Preferences workspaceNode = PreferencesFactory.getWorkspacePreferences();
    return new OptionsPreferences( workspaceNode ).getConfiguration();
  }

  private static JsonObject getProjectConfig( IProject project, OptionsPreferences projectPrefs ) {
    IFile configFile = project.getFile( ".jshintrc" );
    // compatibility
    if( !configFile.exists() ) {
      return projectPrefs.getConfiguration();
    }
    return readConfig( project, configFile );
  }

  private static JsonObject readConfig( IProject project, IFile file ) {
    try {
      return readFileContents( file );
    } catch( Exception exception ) {
      String message = "Failed to read jshint configuration for project " + project.getName();
      Activator.logError( message, exception );
    }
    return new JsonObject();
  }

  private static JsonObject readFileContents( IFile file ) throws IOException, CoreException {
    InputStream inputStream = file.getContents();
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
      return JsonObject.readFrom( reader );
    } finally {
      inputStream.close();
    }
  }

}
