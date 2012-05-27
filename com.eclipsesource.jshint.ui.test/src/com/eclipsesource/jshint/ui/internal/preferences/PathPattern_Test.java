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

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class PathPattern_Test {

  @Test
  public void emptyPattern_matchesAllFiles() {
    PathPattern pattern = PathPattern.create( "" );

    assertTrue( pattern.matchesFile( "" ) );
    assertTrue( pattern.matchesFile( "test" ) );
    assertTrue( pattern.matchesFile( "test.txt" ) );
  }

  @Test
  public void emptyPattern_matchesAllPaths() {
    PathPattern pattern = PathPattern.create( "" );

    assertTrue( pattern.matchesFolder() );
    assertTrue( pattern.matchesFolder( "foo" ) );
    assertTrue( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void rootPattern_matchesAllFiles() {
    PathPattern pattern = PathPattern.create( "/" );

    assertTrue( pattern.matchesFile( "" ) );
    assertTrue( pattern.matchesFile( "test" ) );
    assertTrue( pattern.matchesFile( "test.txt" ) );
  }

  @Test
  public void rootPattern_matchesOnlyRoot() {
    PathPattern pattern = PathPattern.create( "/" );

    assertTrue( pattern.matchesFolder() );

    assertFalse( pattern.matchesFolder( "foo" ) );
  }

  @Test
  public void fileOnlyPattern_matchesFile() {
    PathPattern pattern = PathPattern.create( "*.js" );

    assertTrue( pattern.matchesFile( "test.js" ) );
    assertTrue( pattern.matchesFile( ".js" ) );

    assertFalse( pattern.matchesFile( "test" ) );
    assertFalse( pattern.matchesFile( "js" ) );
  }

  @Test
  public void fileOnlyPattern_matchesAllPaths() {
    PathPattern pattern = PathPattern.create( "*.js" );

    assertTrue( pattern.matchesFolder( "" ) );
    assertTrue( pattern.matchesFolder( "foo" ) );
    assertTrue( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void relativePathOnlyPattern_matchesAllFiles() {
    PathPattern pattern = PathPattern.create( "foo/" );

    assertTrue( pattern.matchesFile( "" ) );
    assertTrue( pattern.matchesFile( "test" ) );
    assertTrue( pattern.matchesFile( "test.txt" ) );
  }

  @Test
  public void relativePathOnlyPattern_matchesAnyPrefixPath() {
    PathPattern pattern = PathPattern.create( "foo/" );

    assertTrue( pattern.matchesFolder( "foo" ) );
    assertTrue( pattern.matchesFolder( "doo", "foo" ) );
    assertTrue( pattern.matchesFolder( "woo", "doo", "foo" ) );

    assertFalse( pattern.matchesFolder( "" ) );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void absolutePathOnlyPattern_matchesAnyPrefixPath() {
    PathPattern pattern = PathPattern.create( "/foo/" );

    assertTrue( pattern.matchesFolder( "foo" ) );

    assertFalse( pattern.matchesFolder() );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "bar", "foo" ) );
    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void nestedPathOnlyPattern_matchesPath() {
    PathPattern pattern = PathPattern.create( "foo/bar/" );

    assertTrue( pattern.matchesFolder( "foo", "bar" ) );
    assertTrue( pattern.matchesFolder( "zoo", "foo", "bar" ) );

    assertFalse( pattern.matchesFolder( "foo" ) );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "foo", "bar", "baz" ) );
  }

  @Test
  public void wildcardPath_atStart() {
    PathPattern pattern = PathPattern.create( "//foo/" );

    assertTrue( pattern.matchesFolder( "foo" ) );
    assertTrue( pattern.matchesFolder( "xoo", "foo" ) );
    assertTrue( pattern.matchesFolder( "xoo", "yoo", "zoo", "foo" ) );

    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
    assertFalse( pattern.matchesFolder( "x", "foo", "bar" ) );
  }

  @Test
  public void wildcardPath_inBetween() {
    PathPattern pattern = PathPattern.create( "foo//bar/" );

    assertTrue( pattern.matchesFolder( "foo", "bar" ) );
    assertTrue( pattern.matchesFolder( "foo", "zoo", "bar" ) );
    assertTrue( pattern.matchesFolder( "foo", "moo", "zoo", "loo", "bar" ) );
  }

  @Test
  public void wildcardPath_atEnd() {
    PathPattern pattern = PathPattern.create( "foo//" );

    assertTrue( pattern.matchesFolder( "foo" ) );
    assertTrue( pattern.matchesFolder( "foo", "bar" ) );
    assertTrue( pattern.matchesFolder( "foo", "zoo", "bar" ) );
  }

  @Test
  public void wildcardSegment_isNotWildcardPath() {
    PathPattern pattern = PathPattern.create( "/foo*bar/" );

    assertTrue( pattern.matchesFolder( "foo-bar" ) );

    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void wildcardSegment_matchesExactlyOnePathSegment() {
    PathPattern pattern = PathPattern.create( "/foo/*/bar/" );

    assertTrue( pattern.matchesFolder( "foo", "zoo", "bar" ) );

    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
    assertFalse( pattern.matchesFolder( "foo", "zoo", "moo", "bar" ) );
  }

  @Test
  public void filePatternAtRootPath() {
    PathPattern pattern = PathPattern.create( "/*.foo" );

    assertTrue( pattern.matchesFolder() );

    assertFalse( pattern.matchesFolder( "x" ) );
    assertFalse( pattern.matchesFolder( "foo" ) );
  }

  @Test
  public void tooManySlashesInARow() {
    assertCreateFailsWithIAE( "///foo" );
    assertCreateFailsWithIAE( "foo///" );
    assertCreateFailsWithIAE( "foo///bar" );
  }

  private static void assertCreateFailsWithIAE( String expression ) {
    try {
      PathPattern.create( expression );
      fail( "Expected IllegalArgumentsException for expression " + expression );
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "Too many slashes in a row" ) );
    }
  }

  @Test
  public void splitIntoSegments() {
    assertEquals( "[*]", split( "" ) );
    assertEquals( "[//, *]", split( "/" ) );
    assertEquals( "[//, foo]", split( "/foo" ) );
    assertEquals( "[foo, *]", split( "foo/" ) );
    assertEquals( "[//, foo, *]", split( "/foo/" ) );
    assertEquals( "[foo, bar]", split( "foo/bar" ) );
    assertEquals( "[foo, bar, *]", split( "foo/bar/" ) );
    assertEquals( "[foo, //, bar]", split( "foo//bar" ) );
    // test array increment, current initial size is 8
    assertEquals( "[1, 2, 3, 4, 5, 6, 7]", split( "1/2/3/4/5/6/7" ) );
    assertEquals( "[1, 2, 3, 4, 5, 6, 7, 8]", split( "1/2/3/4/5/6/7/8" ) );
    assertEquals( "[1, 2, 3, 4, 5, 6, 7, 8, 9]", split( "1/2/3/4/5/6/7/8/9" ) );
  }

  private static String split( String expression ) {
    return Arrays.toString( PathPattern.splitIntoSegmentPatterns( expression ) );
  }

}
