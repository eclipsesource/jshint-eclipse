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

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;


public class OptionParserUtil {

  private OptionParserUtil() {
    // prevent instantiation
  }

  public static JsonObject createConfiguration( String options, String globals ) {
    JsonObject configuration = new JsonObject();
    for( Entry entry : parseOptionString( options ) ) {
      configuration.add( entry.name, entry.value );
    }
    JsonObject globalsObject = new JsonObject();
    for( Entry entry : parseOptionString( globals ) ) {
      globalsObject.add( entry.name, entry.value == JsonValue.TRUE );
    }
    if( !globalsObject.isEmpty() ) {
      configuration.add( "globals", globalsObject );
    }
    return configuration;
  }

  static List<Entry> parseOptionString( String input ) {
    List<Entry> result = new ArrayList<Entry>();
    String[] elements = input.split( "," );
    for( String element : elements ) {
      element = parseOptionElement( result, element.trim() );
    }
    return result;
  }

  private static String parseOptionElement( List<Entry> result, String element ) {
    if( element.length() > 0 ) {
      String[] parts = element.split( ":", 2 );
      String key = parts[ 0 ].trim();
      if( key.length() > 0 ) {
        if( parts.length != 2 ) {
          // TODO handle error
        } else {
          try {
            JsonValue value = JsonValue.readFrom( parts[ 1 ].trim() );
            result.add( new Entry( key, value ) );
          } catch( ParseException exception ) {
            // TODO handle error
          }
        }
      }
    }
    return element;
  }

  static class Entry {
    public final String name;
    public final JsonValue value;
    public Entry( String name, JsonValue value ) {
      this.name = name;
      this.value = value;
    }
  }

}
