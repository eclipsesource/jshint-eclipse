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
package com.eclipsesource.jshint.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;



public class OldProjectPreferences {

  private static final String OLD_PLUGIN_ID = "com.eclipsesource.jshint";
  private static final String KEY_ENABLED = "enabled";
  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String KEY_EXCLUDED = "excluded";
  private static final String DEF_ENABLED = "false";
  private static final String DEF_GLOBALS = "";
  private static final String DEF_OPTIONS = "";
  private static final String DEF_EXCLUDED = "";

  private final IEclipsePreferences node;

  public OldProjectPreferences( IProject project ) {
    node = new ProjectScope( project ).getNode( OLD_PLUGIN_ID );
  }

  public boolean exists() throws BackingStoreException {
    return node.keys().length != 0;
  }

  public void delete() throws BackingStoreException {
    for( String key : node.keys() ) {
      node.remove( key );
    }
    node.flush();
  }

  public String getEnabled() {
    return node.get( KEY_ENABLED, DEF_ENABLED );
  }

  public String getGlobals() {
    return node.get( KEY_GLOBALS, DEF_GLOBALS );
  }

  public String getOptions() {
    return node.get( KEY_OPTIONS, DEF_OPTIONS );
  }

  public List<String> getExcluded() {
    String value = node.get( KEY_EXCLUDED, DEF_EXCLUDED );
    return decodePath( value );
  }

  private static ArrayList<String> decodePath( String value ) {
    ArrayList<String> list = new ArrayList<String>();
    for( String path : value.split( ":" ) ) {
      list.add( path );
    }
    return list;
  }

}
