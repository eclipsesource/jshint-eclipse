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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


/**
 * Wrapper class for the text, used to read the content of a text file and to track line offsets.
 */
public class Text {

  private String code;
  private int lineCount = 1;
  private int[] lineOffsets = new int[ 200 ];

  public Text( String text ) {
    if( text == null ) {
      throw new NullPointerException( "text is null" );
    }
    StringReader reader = new StringReader( text );
    try {
      read( reader );
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    } finally {
      reader.close();
    }
  }

  public Text( Reader reader ) throws IOException {
    if( reader == null ) {
      throw new NullPointerException( "reader is null" );
    }
    read( reader );
  }

  public String getContent() {
    return code;
  }

  public int getLineCount() {
    return lineCount;
  }

  /**
   * Returns the offset of the given line's first character.
   *
   * @param line
   *          zero-relative line index
   * @return the line offset
   */
  public int getLineOffset( int line ) {
    if( line >= lineCount ) {
      throw new IndexOutOfBoundsException( "line does not exist: " + line );
    }
    return lineOffsets[ line ];
  }

  /**
   * Returns the length of the given line, including linebreak characters.
   *
   * @param line
   *          zero-relative line index
   * @return the line length in characters
   */
  public int getLineLength( int line ) {
    if( line >= lineCount ) {
      throw new IndexOutOfBoundsException( "line does not exist" );
    }
    int nextOffset = line + 1 == lineCount ? code.length() : lineOffsets[ line + 1 ];
    return nextOffset - lineOffsets[ line ];
  }

  private void read( Reader reader ) throws IOException {
    StringBuilder builder = new StringBuilder();
    char[] cbuf = new char[ 8096 ];
    int read = reader.read( cbuf );
    while( read != -1 ) {
      for( int i = 0; i < read; i++ ) {
        if( cbuf[i] == '\n' ) {
          if( lineCount >= lineOffsets.length ) {
            growLineOffsets();
          }
          lineOffsets[ lineCount++ ] = builder.length() + i + 1;
        }
      }
      builder.append( cbuf, 0, read );
      read = reader.read( cbuf );
    }
    code = builder.toString();
  }

  private void growLineOffsets() {
    int[] newLineOffsets = new int[ lineOffsets.length * 2 ];
    System.arraycopy( lineOffsets, 0, newLineOffsets, 0, lineOffsets.length );
    lineOffsets = newLineOffsets;
  }

}
