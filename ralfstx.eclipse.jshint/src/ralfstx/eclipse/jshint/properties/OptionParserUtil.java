/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.eclipse.jshint.properties;

import java.util.ArrayList;
import java.util.List;


class OptionParserUtil {

  private OptionParserUtil() {
    // prevent instantiation
  }

  static List<Entry> parseOptionString( String input ) {
    List<Entry> result = new ArrayList<Entry>();
    String[] elements = input.split( "," );
    for( String element : elements ) {
      element = element.trim();
      if( element.length() > 0 ) {
        String[] parts = element.split( ":", 2 );
        String key = parts[ 0 ].trim();
        if( key.length() > 0 ) {
          boolean value = parts.length > 1 ? Boolean.parseBoolean( parts[ 1 ].trim() ) : false;
          result.add( new Entry( key, value ) );
        }
      }
    }
    return result;
  }

  static class Entry {
    public final String name;
    public final boolean value;
    public Entry( String name, boolean value ) {
      this.name = name;
      this.value = value;
    }
  }

}
