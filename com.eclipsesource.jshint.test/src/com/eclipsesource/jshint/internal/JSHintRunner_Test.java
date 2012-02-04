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
package com.eclipsesource.jshint.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JSHintRunner_Test {

  private static final String SYSOUT_ENCODING = "UTF-8";
  private PrintStream bufferedSysout;
  private ByteArrayOutputStream sysout;

  @Before
  public void setUp() throws UnsupportedEncodingException {
    bufferedSysout = System.out;
    sysout = new ByteArrayOutputStream();
    System.setOut( new PrintStream( sysout, true, SYSOUT_ENCODING ) );
  }

  @After
  public void tearDown() {
    System.setOut( bufferedSysout );
  }

  @Test
  public void emptyArgs() throws Exception {
    JSHintRunner runner = new JSHintRunner();

    runner.run();

    assertTrue( getSysout().startsWith( "No input files\n" ) );
  }

  @Test
  public void onlyOptionArgs() throws Exception {
    JSHintRunner runner = new JSHintRunner();

    runner.run( "--charset" );

    assertTrue( getSysout().startsWith( "No input files\n" ) );
  }

  @Test
  public void emptyFile() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "", "UTF-8" );

    runner.run( file.getAbsolutePath() );

    assertEquals( "", getSysout() );
  }

  @Test
  public void validFile() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "var a = 23;", "UTF-8" );

    runner.run( file.getAbsolutePath() );

    assertEquals( "", getSysout() );
  }

  @Test
  public void invalidFile() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "var a == 23;", "UTF-8" );

    runner.run( file.getAbsolutePath() );

    String fileName = file.getAbsolutePath();
    assertTrue( getSysout().contains( "Problem in file " + fileName + " at line 1: " ) );
    assertTrue( getSysout().contains( "\nProblem in file" ) );
  }

  @Test
  public void charsetDefaultsToUtf8() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "var föhn = 23;", "UTF-8" );

    runner.run( file.getAbsolutePath() );

    assertTrue( getSysout().contains( "ö" ) );
  }

  @Test
  public void customCharset() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "var föhn = 23;", "ISO-8859-1" );

    runner.run( "--charset", "ISO-8859-1", file.getAbsolutePath() );

    assertTrue( getSysout().contains( "ö" ) );
  }

  @Test
  public void illegalCharset() throws Exception {
    JSHintRunner runner = new JSHintRunner();
    File file = createTmpFile( "var föhn = 23;", "ISO-8859-1" );

    runner.run( "--charset", "HMPF!", file.getAbsolutePath() );

    assertTrue( getSysout().startsWith( "Unknown or unsupported charset: HMPF!" ) );
  }

  private String getSysout() {
    try {
      return sysout.toString( SYSOUT_ENCODING );
    } catch( UnsupportedEncodingException exception ) {
      throw new RuntimeException( exception );
    }
  }

  private File createTmpFile( String content, String charset ) throws IOException {
    File file = File.createTempFile( "jshint-test", ".tmp" );
    FileOutputStream outputStream = new FileOutputStream( file );
    if( outputStream != null ) {
      try {
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( outputStream, charset ) );
        try {
          writer.write( content );
        } finally {
          writer.close();
        }
      } finally {
        outputStream.close();
      }
    }
    return file;
  }

}
