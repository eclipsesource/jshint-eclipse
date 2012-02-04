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
 * The configuration for JSHint.
 *
 * @see http://www.jshint.com/options/
 */
public class Configuration {

  private final Map<String,Object> options;
  private final Map<String, Object> predefs;

  public Configuration() {
    predefs = new LinkedHashMap<String, Object>();
    options = new LinkedHashMap<String, Object>();
    options.put( "indent", Integer.valueOf( 1 ) );
  }

  /**
   * Adds an option. If the option is already defined, it is overridden.
   *
   * @param option
   *          the name of the option
   * @param value
   *          the value for this option, <code>true</code> to enable the option, <code>false</code>
   *          to disable it
   * @see http://www.jshint.com/options/
   */
  public void addOption( String option, boolean value ) {
    options.put( option, Boolean.valueOf( value ) );
  }

  /**
   * Adds an identifier that is to be considered predefined. If an identifier of this name is
   * already registered, it is overridden.
   *
   * @param identifier
   *          the name of the identifier
   * @param overwrite
   *          <code>true</code> if this identifier can be assigned, <code>false</code> if it is
   *          read-only
   */
  public void addPredefined( String identifier, boolean overwrite ) {
    predefs.put( identifier, Boolean.valueOf( overwrite ) );
  }

  String getOptionsString() {
    StringBuilder builder = new StringBuilder();
    builder.append( "{" );
    if( !predefs.isEmpty() ) {
      builder.append( "\"predef\": {" );
      addMap( builder, predefs );
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
