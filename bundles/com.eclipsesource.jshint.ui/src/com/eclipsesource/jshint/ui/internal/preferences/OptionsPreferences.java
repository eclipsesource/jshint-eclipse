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
package com.eclipsesource.jshint.ui.internal.preferences;

import org.osgi.service.prefs.Preferences;

import static com.eclipsesource.jshint.ui.internal.util.JsonUtil.prettyPrint;


public class OptionsPreferences {

  private static final String KEY_PROJ_SPECIFIC = "projectSpecificOptions";
  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String KEY_CONFIG = "config";

  public static final boolean DEFAULT_PROJ_SPECIFIC = false;
  // TODO set sensible default config
  public static final String DEFAULT_CONFIG = "{\n  \n}";

  private final Preferences node;
  private boolean changed;

  public OptionsPreferences( Preferences node ) {
    this.node = node;
  }

  public Preferences getNode() {
    return node;
  }

  public boolean getProjectSpecific() {
    return node.getBoolean( KEY_PROJ_SPECIFIC, DEFAULT_PROJ_SPECIFIC );
  }

  public void setProjectSpecific( boolean value ) {
    if( value != node.getBoolean( KEY_PROJ_SPECIFIC, DEFAULT_PROJ_SPECIFIC ) ) {
      if( value == DEFAULT_PROJ_SPECIFIC ) {
        node.remove( KEY_PROJ_SPECIFIC );
      } else {
        node.putBoolean( KEY_PROJ_SPECIFIC, value );
      }
      changed = true;
    }
  }

  public String getConfig() {
    String config = node.get( KEY_CONFIG, null );
    return config != null ? config : getOldConfig();
  }

  private String getOldConfig() {
    String options = node.get( KEY_OPTIONS, "" );
    String globals = node.get( KEY_GLOBALS, "" );
    return prettyPrint( OptionParserUtil.createConfiguration( options, globals ) );
  }

  public void setConfig( String value ) {
    if( !value.equals( node.get( KEY_CONFIG, null ) ) ) {
      node.put( KEY_CONFIG, value );
      changed = true;
    }
  }

  public boolean hasChanged() {
    return changed;
  }

  public void clearChanged() {
    changed = false;
  }

}
