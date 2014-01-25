/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.json.JsonObject;

import static com.eclipsesource.jshint.ui.internal.builder.CommentsFilter.filterComments;
import static com.eclipsesource.jshint.ui.internal.util.IOUtil.readFileUtf8;


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

  private JsonObject getProjectConfig( OptionsPreferences projectPrefs ) {
    try {
      String json = getProjectConfigJson( projectPrefs );
      return JsonObject.readFrom( filterComments( json ) );
    } catch( Exception exception ) {
      String message = "Failed to read jshint configuration for project " + project.getName();
      Activator.logError( message, exception );
      return new JsonObject();
    }
  }

  private String getProjectConfigJson( OptionsPreferences projectPrefs ) throws CoreException,
      IOException
  {
    IFile configFile = getProjectConfigFile();
    if( !configFile.exists() ) {
      // compatibility
      return projectPrefs.getConfig();
    }
    return readFileUtf8( configFile );
  }

  private IFile getProjectConfigFile() {
    return project.getFile( ".jshintrc" );
  }

  private static JsonObject getWorkspaceConfig() {
    try {
      String json = getWorkspaceConfigJson();
      return JsonObject.readFrom( filterComments( json ) );
    } catch( Exception exception ) {
      String message = "Failed to read jshint configuration from workspace preferences";
      Activator.logError( message, exception );
      return new JsonObject();
    }
  }

  private static String getWorkspaceConfigJson() {
    Preferences workspaceNode = PreferencesFactory.getWorkspacePreferences();
    return new OptionsPreferences( workspaceNode ).getConfig();
  }

}
