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
package com.eclipsesource.jshint.ui.internal.preferences;

import org.osgi.service.prefs.Preferences;

import com.eclipsesource.json.JsonObject;


public class OptionsPreferences {

  private static final String KEY_SPECIFIC_OPTIONS = "projectSpecificOptions";
  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String KEY_ANNOTATION = "annotation";
  private static final boolean DEF_SPECIFIC_OPTIONS = false;
  private static final String DEF_GLOBALS = "";
  private static final String DEF_OPTIONS = "";
  private static final String DEF_ANNOTATION = "";

  private final Preferences node;
  private boolean changed;

  public OptionsPreferences( Preferences node ) {
    this.node = node;
  }

  public boolean getProjectSpecific() {
    return node.getBoolean( KEY_SPECIFIC_OPTIONS, DEF_SPECIFIC_OPTIONS );
  }

  public void setProjectSpecific( boolean value ) {
    if( value != node.getBoolean( KEY_SPECIFIC_OPTIONS, DEF_SPECIFIC_OPTIONS ) ) {
      if( value == DEF_SPECIFIC_OPTIONS ) {
        node.remove( KEY_SPECIFIC_OPTIONS );
      } else {
        node.putBoolean( KEY_SPECIFIC_OPTIONS, value );
      }
      changed = true;
    }
  }

  public String getGlobals() {
    return node.get( KEY_GLOBALS, DEF_GLOBALS );
  }

  public void setGlobals( String value ) {
    if( !value.equals( node.get( KEY_GLOBALS, DEF_GLOBALS ) ) ) {
      if( DEF_GLOBALS.equals( value ) ) {
        node.remove( KEY_GLOBALS );
      } else {
        node.put( KEY_GLOBALS, value );
      }
      changed = true;
    }
  }

  public String getOptions() {
    return node.get( KEY_OPTIONS, DEF_OPTIONS );
  }

  public void setOptions( String value ) {
    if( !value.equals( node.get( KEY_OPTIONS, DEF_OPTIONS ) ) ) {
      if( value.equals( DEF_OPTIONS ) ) {
        node.remove( KEY_OPTIONS );
      } else {
        node.put( KEY_OPTIONS, value );
      }
      changed = true;
    }
  }

  public String getAnnotation() {
    return node.get( KEY_ANNOTATION, DEF_ANNOTATION );
  }

  public void setAnnotation( String value ) {
    if( !value.equals( node.get( KEY_ANNOTATION, DEF_ANNOTATION ) ) ) {
      if( value.equals( DEF_ANNOTATION ) ) {
        node.remove( KEY_ANNOTATION );
      } else {
        node.put( KEY_ANNOTATION, value );
      }
      changed = true;
    }
  }

  public JsonObject getConfiguration() {
    String options = getOptions();
    String globals = getGlobals();
    return OptionParserUtil.createConfiguration( options, globals );
  }

  public boolean hasChanged() {
    return changed;
  }

  public void clearChanged() {
    changed = false;
  }

}
