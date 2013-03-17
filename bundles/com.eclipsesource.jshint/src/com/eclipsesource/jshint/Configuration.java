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
package com.eclipsesource.jshint;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * The configuration for JSHint.
 *
 * @see http://www.jshint.com/options/
 */
public class Configuration {

  private final JsonObject options;
  private final JsonObject predefs;

  public Configuration() {
    options = new JsonObject();
    predefs = new JsonObject();
  }

  /**
   * Adds an option. If the option is already defined, it is overridden.
   *
   * @param name
   *          the name of the option
   * @param value
   *          the value for this option
   * @return the configuration itself to allow chaining
   * @see http://www.jshint.com/docs/
   */
  public Configuration addOption( String name, boolean value ) {
    options.remove( name ).add( name, value );
    return this;
  }

  /**
   * Adds an option. If the option is already defined, it is overridden.
   *
   * @param name
   *          the name of the option
   * @param value
   *          the value for this option
   * @return the configuration itself to allow chaining
   * @see http://www.jshint.com/docs/
   */
  public Configuration addOption( String name, long value ) {
    options.remove( name ).add( name, value );
    return this;
  }

  /**
   * Adds an option. If the option is already defined, it is overridden.
   *
   * @param name
   *          the name of the option
   * @param value
   *          the value for this option
   * @return the configuration itself to allow chaining
   * @see http://www.jshint.com/docs/
   */
  public Configuration addOption( String name, JsonValue value ) {
    options.remove( name ).add( name, value );
    return this;
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
   * @return the configuration to allow chaining
   */
  public Configuration addPredefined( String identifier, boolean overwrite ) {
    predefs.remove( identifier ).add( identifier, overwrite );
    return this;
  }

  public String toJson() {
    if( !predefs.isEmpty() ) {
      options.remove( "predef" ).add( "predef", predefs );
    }
    return options.toString();
  }

  Object getOption( String option ) {
    return options.get( option );
  }

}
