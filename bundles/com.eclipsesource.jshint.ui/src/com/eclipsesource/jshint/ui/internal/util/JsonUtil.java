/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.util;

import java.io.IOException;
import java.io.StringWriter;

import com.eclipsesource.jshint.ui.internal.builder.CommentsFilter;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.PrettyPrintJsonWriter;


public class JsonUtil {

  public static boolean jsonEquals( String string1, String string2 ) {
    if( string1 != null && string2 != null ) {
      try {
        JsonObject json1 = JsonObject.readFrom( new CommentsFilter( string1 ).toString() );
        JsonObject json2 = JsonObject.readFrom( new CommentsFilter( string2 ).toString() );
        return json1.equals( json2 );
      } catch( Exception exception ) {
        // ignore exceptions and return false
      }
    }
    return false;
  }

  public static String prettyPrint( JsonObject oldConfig ) throws RuntimeException {
    StringWriter writer = new StringWriter();
    try {
      oldConfig.writeTo( new PrettyPrintJsonWriter( writer ) );
    } catch( IOException exception ) {
      // StringWriter does not throw IOExceptions
      throw new RuntimeException( exception );
    }
    return writer.toString();
  }

}
