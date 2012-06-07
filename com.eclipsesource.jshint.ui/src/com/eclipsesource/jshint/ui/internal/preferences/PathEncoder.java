/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;


public class PathEncoder {

  public static String encodePaths( List<String> paths ) {
    StringBuilder builder = new StringBuilder();
    for( String path : paths ) {
      if( path.length() > 0 ) {
        if( builder.length() > 0 ) {
          builder.append( ':' );
        }
        builder.append( path );
      }
    }
    return builder.toString();
  }

  public static ArrayList<String> decodePaths( String encodedPaths ) {
    ArrayList<String> list = new ArrayList<String>();
    for( String path : encodedPaths.split( ":" ) ) {
      if( path.length() > 0 ) {
        list.add( path );
      }
    }
    return list;
  }

}
