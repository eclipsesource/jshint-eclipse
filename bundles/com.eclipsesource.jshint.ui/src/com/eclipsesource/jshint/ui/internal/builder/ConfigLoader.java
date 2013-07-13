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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.json.JsonObject;


public class ConfigLoader {

  private final IProject project;

  public ConfigLoader( IProject project ) {
    this.project = project;
  }

  public JsonObject getConfiguration() {
    Preferences projectNode = PreferencesFactory.getProjectPreferences( project );
    OptionsPreferences projectPreferences = new OptionsPreferences( projectNode );
    if( projectPreferences.getProjectSpecific() ) {
      return getProjectConfig( projectPreferences );
    }
    return getWorkspaceConfig();
  }

  private static JsonObject getWorkspaceConfig() {
    Preferences workspaceNode = PreferencesFactory.getWorkspacePreferences();
    return JsonObject.readFrom( new OptionsPreferences( workspaceNode ).getConfig() );
  }

  private JsonObject getProjectConfig( OptionsPreferences projectPrefs ) {
    IFile configFile = getConfigFile();
    // compatibility
    if( !configFile.exists() ) {
      return JsonObject.readFrom( projectPrefs.getConfig() );
    }
    return readConfig( configFile );
  }

  private IFile getConfigFile() {
    return project.getFile( ".jshintrc" );
  }

  private JsonObject readConfig( IFile file ) {
    try {
      String contents = IOUtil.readFileUtf8( file );
      String filtered = new CommentsFilter( contents ).toString();
      return JsonObject.readFrom( filtered );
    } catch( Exception exception ) {
      String message = "Failed to read jshint configuration for project " + project.getName();
      Activator.logError( message, exception );
    }
    return new JsonObject();
  }

}
