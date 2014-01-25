/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;


public class CommentsFilter {

  private final char[] chars;
  private char lastCh;
  private boolean inLineComment;
  private boolean inBlockComment;

  public CommentsFilter( String input ) {
    this.chars = input.toCharArray();
    process();
  }

  private void process() {
    for( int i = 0; i < chars.length; i++ ) {
      char ch = chars[i];
      if( inLineComment ) {
        if( ch == '\n' || ch == '\r' ) {
          inLineComment = false;
        } else {
          chars[i] = ' ';
        }
      } else if( inBlockComment ) {
        if( lastCh == '*' && ch == '/' ) {
          inBlockComment = false;
        }
        chars[i] = chars[i] == '\n' ? '\n' : ' ';
      } else if( lastCh == '/' && ch == '/' ) {
        inLineComment = true;
        chars[i-1] = ' ';
        chars[i] = ' ';
      } else if( lastCh == '/' && ch == '*' ) {
        inBlockComment = true;
        chars[i-1] = ' ';
        chars[i] = ' ';
      }
      lastCh = ch;
    }
  }

  @Override
  public String toString() {
    return new String( chars );
  }

  public static String filterComments( String input ) {
    return new CommentsFilter( input ).toString();
  }

}
