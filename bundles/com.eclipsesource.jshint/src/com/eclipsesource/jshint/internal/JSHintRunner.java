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
package com.eclipsesource.jshint.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.jshint.JSHint;
import com.eclipsesource.jshint.Problem;
import com.eclipsesource.jshint.ProblemHandler;
import com.eclipsesource.json.JsonObject;


public class JSHintRunner {

  private static final String PARAM_CHARSET = "--charset";
  private static final String PARAM_CUSTOM_JSHINT = "--custom";
  private List<File> files;
  private Charset charset;
  private File library;
  private JSHint jshint;

  public void run( String... args ) {
    try {
      readArgs( args );
      ensureCharset();
      ensureInputFiles();
      loadJSHint();
      configureJSHint();
      processFiles();
    } catch( Exception e ) {
      System.out.println( e.getMessage() );
      System.out.println();
      System.out.println( "Usage: JSHint [ <options> ] <input-file> [ <input-file> ... ]" );
      System.out.println( "Options: --custom <custom-jshint-file>" );
      System.out.println( "         --charset <charset>" );
    }
  }

  private void readArgs( String[] args ) {
    files = new ArrayList<File>();
    String lastArg = null;
    for( String arg : args ) {
      if( PARAM_CHARSET.equals( lastArg ) ) {
        setCharset( arg );
      } else if( PARAM_CUSTOM_JSHINT.equals( lastArg ) ) {
        setLibrary( arg );
      } else if( PARAM_CHARSET.equals( arg ) || PARAM_CUSTOM_JSHINT.equals( arg ) ) {
        // continue
      } else {
        File file = new File( arg );
        checkFile( file );
        files.add( file );
      }
      lastArg = arg;
    }
  }

  private void checkFile( File file ) throws IllegalArgumentException {
    if( !file.isFile() ) {
      throw new IllegalArgumentException( "No such file: " + file.getAbsolutePath() );
    }
    if( !file.canRead() ) {
      throw new IllegalArgumentException( "Cannot read file: " + file.getAbsolutePath() );
    }
  }

  private void ensureCharset() {
    if( charset == null ) {
      setCharset( "UTF-8" );
    }
  }

  private void setCharset( String name ) {
    try {
      charset = Charset.forName( name );
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Unknown or unsupported charset: " + name );
    }
  }

  private void setLibrary( String name ) {
    library = new File( name );
  }

  private void ensureInputFiles() {
    if( files.isEmpty() ) {
      throw new IllegalArgumentException( "No input files" );
    }
  }

  private void loadJSHint() {
    jshint = new JSHint();
    try {
      if( library != null ) {
        FileInputStream inputStream = new FileInputStream( library );
        try {
          jshint.load( inputStream );
        } finally {
          inputStream.close();
        }
      } else {
        jshint.load();
      }
    } catch( Exception exception ) {
      String message = "Failed to load JSHint library: " + exception.getMessage();
      throw new IllegalArgumentException( message );
    }
  }

  private void processFiles() throws IOException {
    for( File file : files ) {
      String code = readFileContents( file );
      ProblemHandler handler = new SysoutProblemHandler( file.getAbsolutePath() );
      jshint.check( code, handler );
    }
  }

  private void configureJSHint() {
    JsonObject configuration = new JsonObject();
    configuration.add( "undef", true );
    jshint.configure( configuration );
  }

  private String readFileContents( File file ) throws FileNotFoundException, IOException {
    FileInputStream inputStream = new FileInputStream( file );
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    try {
      StringBuilder builder = new StringBuilder();
      String line = reader.readLine();
      while( line != null ) {
        builder.append( line );
        builder.append( '\n' );
        line = reader.readLine();
      }
      return builder.toString();
    } finally {
      reader.close();
    }
  }

  private static final class SysoutProblemHandler implements ProblemHandler {

    private final String fileName;

    public SysoutProblemHandler( String fileName ) {
      this.fileName = fileName;
    }

    public void handleProblem( Problem problem ) {
      int line = problem.getLine();
      String message = problem.getMessage();
      System.out.println( "Problem in file " + fileName + " at line " + line + ": " + message );
    }

  }

}
