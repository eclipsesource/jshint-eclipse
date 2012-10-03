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


public class PathSegmentPattern {

  public static final PathSegmentPattern ALL = new PathSegmentPattern( "*" );
  public static final PathSegmentPattern NONE = new PathSegmentPattern( "" );
  public static final PathSegmentPattern ANY_NUMBER = new PathSegmentPattern( "" );

  private final String expression;

  private final Matcher matcher;

  private PathSegmentPattern( String expression ) {
    this.expression = checkExpression( expression );
    matcher = new Matcher( expression );
  }

  public static PathSegmentPattern create( String expression ) {
    if( expression == null ) {
      throw new NullPointerException( "parameter is null" );
    }
    if( "".equals( expression ) ) {
      return NONE;
    }
    if( "*".equals( expression ) ) {
      return ALL;
    }
    return new PathSegmentPattern( expression );
  }

  public boolean matches( String string ) {
    if( this == ALL || this == ANY_NUMBER ) {
      return true;
    }
    if( this == NONE ) {
      return false;
    }
    return matcher.match( string );
  }

  @Override
  public String toString() {
    if( this == ANY_NUMBER ) {
      return "//";
    }
    if( this == NONE ) {
      return "NONE";
    }
    return expression;
  }

  private static String checkExpression( String expression ) {
    int length = expression.length();
    for( int i = 0; i < length; i++ ) {
      char ch = expression.charAt( i );
      if( isIllegalCharacter( ch ) ) {
        throw new IllegalArgumentException( "Illegal character in expression: '" + ch + "'" );
      }
    }
    return expression;
  }

  private static boolean isIllegalCharacter( char ch ) {
    return ch == '!'
           || ch == '+'
           || ch == ':'
           || ch == '|'
           || ch == '('
           || ch == ')'
           || ch == '['
           || ch == ']'
           || ch == '}'
           || ch == '{'
           || ch == '/'
           || ch == '\\';
  }

  private static class Matcher {

    private final char[] pattern;

    public Matcher( String pattern ) {
      this.pattern = pattern.toCharArray();
    }

    public boolean match( String input ) {
      return match( 0, 0, input.toCharArray() );
    }

    private boolean match( int patternPos, int inputPos, char[] input ) {
      if( patternPos == pattern.length ) {
        return inputPos == input.length;
      }
      if( inputPos == input.length ) {
        if( pattern[ patternPos ] == '*' ) {
          return match( patternPos + 1, inputPos, input );
        }
        return false;
      }
      if( pattern[ patternPos ] == '*' ) {
        int nextInputPos = inputPos;
        while( nextInputPos <= input.length ) {
          if( match( patternPos + 1, nextInputPos, input ) ) {
            return true;
          }
          nextInputPos++;
        }
        return false;
      } else if( pattern[ patternPos ] == '?' ) {
        return match( patternPos + 1, inputPos + 1, input );
      } else if( pattern[ patternPos ] == input[ inputPos ] ) {
        return match( patternPos + 1, inputPos + 1, input );
      }
      return false;
    }

  }

}
