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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


public class IOUtil {

  public static final String UTF_8 = "UTF-8";

  public static String readFileUtf8( IFile file ) throws CoreException, IOException {
    if( file.isAccessible() ) {
      InputStream inputStream = file.getContents( true );
      try {
        return readStringUtf8( inputStream );
      } finally {
        inputStream.close();
      }
    }
    return null;
  }

  public static void writeFileUtf8( IFile file, String content ) throws CoreException {
    ByteArrayInputStream inputStream = createInputStreamUtf8( content );
    if( file.isAccessible() ) {
      file.setContents( inputStream, true, true, null );
    } else {
      file.create( inputStream, true, null );
      file.setCharset( UTF_8, null );
    }
  }

  public static String readFromFileUtf8( String fileName ) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new BufferedInputStream( new FileInputStream( fileName ) );
      return readStringUtf8( inputStream );
    } finally {
      if( inputStream != null ) {
        inputStream.close();
      }
    }
  }

  public static void writeToFileUtf8( String fileName, String content ) throws IOException {
    OutputStream outputStream = null;
    try {
      outputStream = new BufferedOutputStream( new FileOutputStream( fileName ) );
      outputStream.write( content.getBytes( UTF_8 ) );
    } finally {
      if( outputStream != null ) {
        outputStream.close();
      }
    }
  }

  public static String readStringUtf8( InputStream inputStream ) throws IOException {
    BufferedReader reader = createReaderUtf8( inputStream );
    char[] buffer = new char[ 1024 ];
    StringBuilder builder = new StringBuilder();
    try {
      int read = reader.read( buffer );
      while( read != -1 ) {
        builder.append( buffer, 0, read );
        read = reader.read( buffer );
      }
    } finally {
      reader.close();
    }
    return builder.toString();
  }

  public static BufferedReader createReaderUtf8( InputStream inputStream ) {
    try {
      return new BufferedReader( new InputStreamReader( inputStream, UTF_8 ) );
    } catch( UnsupportedEncodingException exception ) {
      throw new RuntimeException( exception );
    }
  }

  public static ByteArrayInputStream createInputStreamUtf8( String string ) {
    try {
      return new ByteArrayInputStream( string.getBytes( UTF_8 ) );
    } catch( UnsupportedEncodingException exception ) {
      throw new RuntimeException( exception );
    }
  }

}
