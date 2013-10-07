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
package com.eclipsesource.jshint;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class Text_Test {

  @Test( expected = NullPointerException.class )
  public void creation_failsWithNullString() {
    new Text( (String)null );
  }

  @Test( expected = NullPointerException.class )
  public void creaion_failsWithNullReader() throws IOException {
    new Text( (Reader)null );
  }

  @Test
  public void creation_withString() {
    Text text = new Text( "foo\nbar" );

    assertEquals( "foo\nbar", text.getContent() );
    assertEquals( 2, text.getLineCount() );
    assertEquals( "0, 4", getAllLineOffsets( text ) );
    assertEquals( "4, 3", getAllLineLengths( text ) );
  }

  @Test
  public void creation_withReader() throws Exception {
    Text text = new Text( new StringReader( "foo\nbar" ) );

    assertEquals( "foo\nbar", text.getContent() );
    assertEquals( 2, text.getLineCount() );
    assertEquals( "0, 4", getAllLineOffsets( text ) );
    assertEquals( "4, 3", getAllLineLengths( text ) );
  }

  @Test
  public void emptyString() {
    Text text = new Text( "" );

    assertEquals( "", text.getContent() );
    assertEquals( 1, text.getLineCount() );
    assertEquals( "0", getAllLineOffsets( text ) );
    assertEquals( "0", getAllLineLengths( text ) );
  }

  @Test
  public void oneLine() {
    Text text = new Text( "foo" );

    assertEquals( "foo", text.getContent() );
    assertEquals( 1, text.getLineCount() );
    assertEquals( "0", getAllLineOffsets( text ) );
    assertEquals( "3", getAllLineLengths( text ) );
  }

  @Test
  public void oneLineTerminated() {
    Text text = new Text( "foo\n" );

    assertEquals( "foo\n", text.getContent() );
    assertEquals( 2, text.getLineCount() );
    assertEquals( "0, 4", getAllLineOffsets( text ) );
    assertEquals( "4, 0", getAllLineLengths( text ) );
  }

  @Test
  public void twoLines() {
    Text text = new Text( "line1\nline2" );

    assertEquals( "line1\nline2", text.getContent() );
    assertEquals( 2, text.getLineCount() );
    assertEquals( "0, 6", getAllLineOffsets( text ) );
    assertEquals( "6, 5", getAllLineLengths( text ) );
  }

  @Test
  public void twoLinesTerminated() {
    Text text = new Text( "line1\nline2\n" );

    assertEquals( "line1\nline2\n", text.getContent() );
    assertEquals( 3, text.getLineCount() );
    assertEquals( "0, 6, 12", getAllLineOffsets( text ) );
    assertEquals( "6, 6, 0", getAllLineLengths( text ) );
  }

  @Test
  public void twoSubsequentNewlines() {
    Text text = new Text( "line1\n\nline3" );

    assertEquals( "line1\n\nline3", text.getContent() );
    assertEquals( 3, text.getLineCount() );
    assertEquals( "0, 6, 7", getAllLineOffsets( text ) );
    assertEquals( "6, 1, 5", getAllLineLengths( text ) );
  }

  @Test
  public void windowsNewlines() {
    Text text = new Text( "line1\r\nline2\r\n" );

    assertEquals( "line1\r\nline2\r\n", text.getContent() );
    assertEquals( 3, text.getLineCount() );
    assertEquals( "0, 7, 14", getAllLineOffsets( text ) );
    assertEquals( "7, 7, 0", getAllLineLengths( text ) );
  }

  @Test
  public void readLongFile() {
    StringBuilder builder = new StringBuilder();
    for( int i = 1; i <= 5000; i++ ) {
      builder.append( "line " + i + "\n" );
    }
    Text text = new Text( builder.toString() );

    assertThat( text.getContent(), startsWith( "line 1\n" ) );
    assertThat( text.getContent(), endsWith( "line 5000\n" ) );
    assertEquals( 5001, text.getLineCount() );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void getLineOffset_failsWithNegativeLine() {
    Text text = new Text( "line1\nline2\n" );

    text.getLineOffset( -1 );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void getLineOffset_failsWithExceedingLine() {
    Text text = new Text( "line1\nline2\n" );

    text.getLineOffset( 3 );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void getLineLength_failsWithNegativeLine() {
    Text text = new Text( "line1\nline2\n" );

    text.getLineLength( -1 );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void getLineLength_failsWithExceedingLine() {
    Text text = new Text( "line1\nline2\n" );

    text.getLineLength( 3 );
  }

  private static String getAllLineOffsets( Text text ) {
    StringBuilder result = new StringBuilder();
    int line = 0;
    while( true ) {
      try {
        int offset = text.getLineOffset( line++ );
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

  private static String getAllLineLengths( Text text ) {
    StringBuilder result = new StringBuilder();
    int line = 0;
    while( true ) {
      try {
        int length = text.getLineLength( line++ );
        if( result.length() > 0 ) {
          result.append( ", " );
        }
        result.append( length );
      } catch( IndexOutOfBoundsException e ) {
        break;
      }
    }
    return result.toString();
  }

}
