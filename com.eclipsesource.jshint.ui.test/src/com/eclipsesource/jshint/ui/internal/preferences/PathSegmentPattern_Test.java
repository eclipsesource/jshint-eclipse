/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ralf - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class PathSegmentPattern_Test {

  @Test
  public void asteriskPattern_returnsALL() {
    assertSame( PathSegmentPattern.ALL, PathSegmentPattern.create( "*" ) );
  }

  @Test
  public void emptyPattern_returnsNONE() {
    assertSame( PathSegmentPattern.NONE, PathSegmentPattern.create( "" ) );
  }

  @Test
  public void ALL_matchesEverything() {
    assertTrue( PathSegmentPattern.ALL.matches( "" ) );
    assertTrue( PathSegmentPattern.ALL.matches( "a" ) );
    assertTrue( PathSegmentPattern.ALL.matches( "test.js" ) );
  }

  @Test
  public void ANY_NUMBER_matchesEverything() {
    assertTrue( PathSegmentPattern.ANY_NUMBER.matches( "" ) );
    assertTrue( PathSegmentPattern.ANY_NUMBER.matches( "a" ) );
    assertTrue( PathSegmentPattern.ANY_NUMBER.matches( "test.js" ) );
  }

  @Test
  public void NONE_matchesNothing() {
    assertFalse( PathSegmentPattern.NONE.matches( "" ) );
    assertFalse( PathSegmentPattern.NONE.matches( "a" ) );
    assertFalse( PathSegmentPattern.NONE.matches( "test.js" ) );
  }

  @Test
  public void simplePattern_onlyMatchesExactString() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a" );

    assertTrue( pattern.matches( "a" ) );

    assertFalse( pattern.matches( "" ) );
    assertFalse( pattern.matches( "ab" ) );
    assertFalse( pattern.matches( "ba" ) );
  }

  @Test
  public void asterisk_atStart() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "*a" );

    assertTrue( pattern.matches( "a" ) );
    assertTrue( pattern.matches( "xa" ) );
    assertTrue( pattern.matches( "fooa" ) );

    assertFalse( pattern.matches( "ax" ) );
  }

  @Test
  public void asterisk_atEnd() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a*" );

    assertTrue( pattern.matches( "a" ) );
    assertTrue( pattern.matches( "ab" ) );
    assertTrue( pattern.matches( "afoo" ) );

    assertFalse( pattern.matches( "xa" ) );
  }

  @Test
  public void asterisk_middle() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a*b" );

    assertTrue( pattern.matches( "ab" ) );
    assertTrue( pattern.matches( "axb" ) );
    assertTrue( pattern.matches( "afoob" ) );

    assertFalse( pattern.matches( "xab" ) );
    assertFalse( pattern.matches( "abx" ) );
  }

  @Test
  public void doubleAsterisk_atStart_doesNotHurt() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "**foo" );

    assertTrue( pattern.matches( "foo" ) );
    assertTrue( pattern.matches( "afoo" ) );
    assertTrue( pattern.matches( "superfoo" ) );

    assertFalse( pattern.matches( "foox" ) );
  }

  @Test
  public void doubleAsterisk_atEnd_doesNotHurt() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "foo**" );

    assertTrue( pattern.matches( "foo" ) );
    assertTrue( pattern.matches( "foox" ) );
    assertTrue( pattern.matches( "foobar" ) );

    assertFalse( pattern.matches( "xfoo" ) );
  }

  @Test
  public void questionTag_atStart() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "?a" );

    assertTrue( pattern.matches( "ba" ) );

    assertFalse( pattern.matches( "a" ) );
    assertFalse( pattern.matches( "xba" ) );
  }

  @Test
  public void questionTag_atEnd() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a?" );

    assertTrue( pattern.matches( "ab" ) );

    assertFalse( pattern.matches( "a" ) );
    assertFalse( pattern.matches( "abx" ) );
  }

  @Test
  public void dotInPattern_matchesOnlyDot() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a.b" );

    assertTrue( pattern.matches( "a.b" ) );

    assertFalse( pattern.matches( "axb" ) );
  }

  @Test
  public void forbiddenCharacters() {
    // reserved for advanced patterns
    assertCreateFailsWithIAE( "!" );
    assertCreateFailsWithIAE( "?" );
    assertCreateFailsWithIAE( "+" );
    assertCreateFailsWithIAE( ":" );
    assertCreateFailsWithIAE( "|" );
    assertCreateFailsWithIAE( "(" );
    assertCreateFailsWithIAE( ")" );
    assertCreateFailsWithIAE( "[" );
    assertCreateFailsWithIAE( "]" );
    assertCreateFailsWithIAE( "}" );
    assertCreateFailsWithIAE( "{" );
    // file separator
    assertCreateFailsWithIAE( "/" );
  }

  @Test
  public void matchingIsCaseSensitive() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "a" );

    assertTrue( pattern.matches( "a" ) );

    assertFalse( pattern.matches( "A" ) );
  }

  @Test
  public void typicalFilePatterns_1() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "*.js" );

    assertTrue( pattern.matches( ".js" ) );
    assertTrue( pattern.matches( "test.js" ) );
    assertTrue( pattern.matches( "test.min.js" ) );

    assertFalse( pattern.matches( "test.jsp" ) );
    assertFalse( pattern.matches( "testxjs" ) );
    assertFalse( pattern.matches( "testjs" ) );
  }

  @Test
  public void typicalFilePatterns_2() {
    PathSegmentPattern pattern = PathSegmentPattern.create( "*.*" );

    assertTrue( pattern.matches( "." ) );
    assertTrue( pattern.matches( ".js" ) );
    assertTrue( pattern.matches( "test." ) );
    assertTrue( pattern.matches( "test.js" ) );

    assertFalse( pattern.matches( "test" ) );
  }

  @Test
  public void toString_returnsExpression() {
    assertEquals( "*", PathSegmentPattern.ALL.toString() );
    assertEquals( "*.*", PathSegmentPattern.create( "*.*" ).toString() );
    assertEquals( "test.js", PathSegmentPattern.create( "test.js" ).toString() );
  }

  private static void assertCreateFailsWithIAE( String expression ) {
    try {
      PathSegmentPattern.create( ":" );
      fail( "Expected IllegalArgumentsException for expression " + expression );
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "Illegal character" ) );
    }
  }

}
