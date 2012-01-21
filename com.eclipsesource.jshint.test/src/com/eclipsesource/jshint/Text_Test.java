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

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;


public class Text_Test {

  @Test
  public void emptyString() throws Exception {
    Reader reader = new StringReader( "" );
    Text textFile = new Text( reader );

    assertEquals( "", textFile.getContent() );
    assertEquals( 1, textFile.getLineCount() );
    assertEquals( "0", getAllLineOffsets( textFile ) );
  }

  @Test
  public void oneLine() throws Exception {
    Reader reader = new StringReader( "foo" );
    Text textFile = new Text( reader );

    assertEquals( "foo", textFile.getContent() );
    assertEquals( 1, textFile.getLineCount() );
    assertEquals( "0", getAllLineOffsets( textFile ) );
  }

  @Test
  public void oneLineTerminated() throws Exception {
    Reader reader = new StringReader( "foo\n" );
    Text textFile = new Text( reader );

    assertEquals( "foo\n", textFile.getContent() );
    assertEquals( 2, textFile.getLineCount() );
    assertEquals( "0, 4", getAllLineOffsets( textFile ) );
  }

  @Test
  public void twoLines() throws Exception {
    Reader reader = new StringReader( "line1\nline2" );
    Text textFile = new Text( reader );

    assertEquals( "line1\nline2", textFile.getContent() );
    assertEquals( 2, textFile.getLineCount() );
    assertEquals( "0, 6", getAllLineOffsets( textFile ) );
  }

  @Test
  public void twoLinesTerminated() throws Exception {
    Reader reader = new StringReader( "line1\nline2\n" );
    Text textFile = new Text( reader );

    assertEquals( "line1\nline2\n", textFile.getContent() );
    assertEquals( 3, textFile.getLineCount() );
    assertEquals( "0, 6, 12", getAllLineOffsets( textFile ) );
  }

  @Test
  public void twoSubsequentNewlines() throws Exception {
    Reader reader = new StringReader( "line1\n\nline3" );
    Text textFile = new Text( reader );

    assertEquals( "line1\n\nline3", textFile.getContent() );
    assertEquals( 3, textFile.getLineCount() );
    assertEquals( "0, 6, 7", getAllLineOffsets( textFile ) );
  }

  @Test
  public void windowsNewlines() throws Exception {
    Reader reader = new StringReader( "line1\r\nline2\r\n" );
    Text textFile = new Text( reader );

    assertEquals( "line1\r\nline2\r\n", textFile.getContent() );
    assertEquals( 3, textFile.getLineCount() );
    assertEquals( "0, 7, 14", getAllLineOffsets( textFile ) );
  }

  private static String getAllLineOffsets( Text textFile ) {
    StringBuilder result = new StringBuilder();
    int line = 0;
    while( true ) {
      try {
        int offset = textFile.getLineOffset( line++ );
        if( result.length() > 0 ) {
          result.append( ", " );
        }
        result.append( offset );
      } catch( IndexOutOfBoundsException e ) {
        break;
      }
    }
    return result.toString();
  }

}
