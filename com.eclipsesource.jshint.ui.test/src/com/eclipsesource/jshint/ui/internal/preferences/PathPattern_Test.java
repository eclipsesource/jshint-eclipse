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

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;


public class PathPattern_Test {

  @Test
  public void emptyPattern_matchesAllFiles() {
    PathPattern pattern = PathPattern.create( "" );

    assertTrue( pattern.matchesFile( "" ) );
    assertTrue( pattern.matchesFile( "test" ) );
    assertTrue( pattern.matchesFile( "test.txt" ) );
  }

  @Test
  public void emptyPattern_matchesOnlyRoot() {
    PathPattern pattern = PathPattern.create( "" );

    assertTrue( pattern.matchesFolder() );

    assertFalse( pattern.matchesFolder( "foo" ) );
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
  public void fileOnlyPattern_matchesOnlyRoot() {
    PathPattern pattern = PathPattern.create( "*.js" );

    assertTrue( pattern.matchesFolder() );

    assertFalse( pattern.matchesFolder( "foo" ) );
  }

  @Test
  public void filePatternAtRootPath_matchesOnlyRoot() {
    PathPattern pattern = PathPattern.create( "/*.js" );

    assertTrue( pattern.matchesFolder() );

    assertFalse( pattern.matchesFolder( "foo" ) );
  }

  @Test
  public void relativePathOnlyPattern_matchesAllFiles() {
    PathPattern pattern = PathPattern.create( "foo/" );

    assertTrue( pattern.matchesFile( "" ) );
    assertTrue( pattern.matchesFile( "test" ) );
    assertTrue( pattern.matchesFile( "test.txt" ) );
  }

  @Test
  public void relativePathOnlyPattern_matchesExactPath() {
    PathPattern pattern = PathPattern.create( "foo/" );

    assertTrue( pattern.matchesFolder( "foo" ) );

    assertFalse( pattern.matchesFolder() );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
    assertFalse( pattern.matchesFolder( "zoo", "foo" ) );
  }

  @Test
  public void absolutePathOnlyPattern_matchesExactPath() {
    PathPattern pattern = PathPattern.create( "/foo/" );

    assertTrue( pattern.matchesFolder( "foo" ) );

    assertFalse( pattern.matchesFolder() );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "bar", "foo" ) );
    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
  }

  @Test
  public void nestedPathOnlyPattern_matchesExactPath() {
    PathPattern pattern = PathPattern.create( "foo/bar/" );

    assertTrue( pattern.matchesFolder( "foo", "bar" ) );

    assertFalse( pattern.matchesFolder( "foo" ) );
    assertFalse( pattern.matchesFolder( "bar" ) );
    assertFalse( pattern.matchesFolder( "zoo", "foo", "bar" ) );
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
    assertFalse( pattern.matchesFolder( "foo", "x", "bar" ) );
  }

  @Test
  public void wildcardSegment_matchesExactlyOnePathSegment() {
    PathPattern pattern = PathPattern.create( "/foo/*/bar/" );

    assertTrue( pattern.matchesFolder( "foo", "zoo", "bar" ) );

    assertFalse( pattern.matchesFolder( "foo", "bar" ) );
    assertFalse( pattern.matchesFolder( "foo", "zoo", "moo", "bar" ) );
  }

  @Test
  public void tooManySuccessiveSlashes() {
    assertCreateFailsWithTooManySuccessiveSlashes( "///foo" );
    assertCreateFailsWithTooManySuccessiveSlashes( "foo///" );
    assertCreateFailsWithTooManySuccessiveSlashes( "foo///bar" );
  }

  private static void assertCreateFailsWithTooManySuccessiveSlashes( String expression ) {
    try {
      PathPattern.create( expression );
      fail( "Expected IllegalArgumentsException for expression " + expression );
    } catch( IllegalArgumentException exception ) {
      String expected = "Too many successive slashes in expression";
      assertEquals( expected, exception.getMessage() );
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

  @Test
  public void matchesAllFiles() {
    assertTrue( PathPattern.create( "" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "/" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "//" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "src/" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "src//" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "*" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "/*" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "//*" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "src/*" ).matchesAllFiles() );
    assertTrue( PathPattern.create( "src//*" ).matchesAllFiles() );

    assertFalse( PathPattern.create( "*.*" ).matchesAllFiles() );
    assertFalse( PathPattern.create( "*.js" ).matchesAllFiles() );
    assertFalse( PathPattern.create( "src/*.js" ).matchesAllFiles() );
  }

  @Test
  public void matchesAllFolders() {
    assertTrue( PathPattern.create( "//" ).matchesAllFolders() );
    assertTrue( PathPattern.create( "//*.js" ).matchesAllFolders() );

    assertFalse( PathPattern.create( "" ).matchesAllFolders() );
    assertFalse( PathPattern.create( "/" ).matchesAllFolders() );
    assertFalse( PathPattern.create( "src//" ).matchesAllFolders() );
    assertFalse( PathPattern.create( "src//*.js" ).matchesAllFolders() );
    assertFalse( PathPattern.create( "//src//" ).matchesAllFolders() );
    assertFalse( PathPattern.create( "src//js/" ).matchesAllFolders() );
  }

  @Test
  public void getFilePattern() {
    assertEquals( "*", PathPattern.create( "" ).getFilePattern() );
    assertEquals( "*", PathPattern.create( "/" ).getFilePattern() );
    assertEquals( "*", PathPattern.create( "//" ).getFilePattern() );
    assertEquals( "*", PathPattern.create( "src/" ).getFilePattern() );
    assertEquals( "*", PathPattern.create( "src/*" ).getFilePattern() );
    assertEquals( "*.js", PathPattern.create( "src/*.js" ).getFilePattern() );
    assertEquals( "foo", PathPattern.create( "//foo" ).getFilePattern() );
  }

  @Test
  public void getPathPattern() {
    assertEquals( "", PathPattern.create( "" ).getPathPattern() );
    assertEquals( "", PathPattern.create( "file" ).getPathPattern() );
    assertEquals( "/", PathPattern.create( "/" ).getPathPattern() );
    assertEquals( "/", PathPattern.create( "/file" ).getPathPattern() );
    assertEquals( "//", PathPattern.create( "//" ).getPathPattern() );
    assertEquals( "//", PathPattern.create( "//file" ).getPathPattern() );
    assertEquals( "src/", PathPattern.create( "src/file" ).getPathPattern() );
    assertEquals( "src/", PathPattern.create( "src/*" ).getPathPattern() );
    assertEquals( "src//", PathPattern.create( "src//file" ).getPathPattern() );
    assertEquals( "src/js//", PathPattern.create( "src/js//file" ).getPathPattern() );
    assertEquals( "//src//js/", PathPattern.create( "//src//js/file" ).getPathPattern() );
  }

}
