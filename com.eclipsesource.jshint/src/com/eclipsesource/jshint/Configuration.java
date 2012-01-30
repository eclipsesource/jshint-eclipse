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
package com.eclipsesource.jshint;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * See http://www.jshint.com/options/
 */
public class Configuration {

  private final Map<String,Object> options;
  private final Map<String, Object> globals;

  public Configuration() {
    globals = new LinkedHashMap<String, Object>();
    options = new LinkedHashMap<String, Object>();
    options.put( "indent", Integer.valueOf( 1 ) );
  }

  public void addOption( String option, boolean value ) {
    if( options.containsKey( option ) ) {
      throw new IllegalArgumentException( "Duplicate option: " + option );
    }
    options.put( option, Boolean.valueOf( value ) );
  }

  public void addGlobal( String identifier, boolean overwrite ) {
    if( globals.containsKey( identifier ) ) {
      throw new IllegalArgumentException( "Duplicate global identifier: " + identifier );
    }
    globals.put( identifier, Boolean.valueOf( overwrite ) );
  }

  public String getOptionsString() {
    StringBuilder builder = new StringBuilder();
    builder.append( "{" );
    if( !globals.isEmpty() ) {
      builder.append( "\"predef\": {" );
      addMap( builder, globals );
      builder.append( "}" );
      if( !options.isEmpty() ) {
        builder.append( ", " );
      }
    }
    addMap( builder, options );
    builder.append( "}" );
    return builder.toString();
  }

  private void addMap( StringBuilder builder, Map<String, Object> map ) {
    boolean first = true;
    for( String key : map.keySet() ) {
      if( !first ) {
        builder.append( ", " );
      }
      builder.append( '"' );
      builder.append( key );
      builder.append( "\": " );
      builder.append( map.get( key ) );
      first = false;
    }
  }

}
