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


/**
 * A pattern to match folders and files. The following pattern constructs are supported:
 * <dl>
 * <dt><code>/</code></dt>
 * <dd>A slash separates path segments. A single leading slash stands for an absolute path. A
 * trailing slash denotes a folder.</dd>
 * <dt><code>//</code></dt>
 * <dd>A double slash matches zero or more path segments. Can also be used at the beginning and at
 * the end of a pattern.</dd>
 * <dt><code>*</code></dt>
 * <dd>An asterisk stands for zero or more characters in a path segment.</dd>
 * <dt><code>?</code></dt>
 * <dd>A question tag matches a single character in a path segment.</dd>
 * </dl>
 * <p>
 * In the current implementation, relative and absolute path patterns are handled alike. Both match
 * a path from the beginning. For example, both <code>/foo/</code> and <code>foo/</code> match only
 * <code>/foo/</code>, but not <code>/lib/foo/</code> or <code>/src/org/example/foo/</code>. Future
 * implementations may take a context directory into account when matching relative path patterns.
 * </p>
 * <p>
 * If a patterns ends with a slash, the last segment of the pattern matches a folder, otherwise it
 * matches a file. If a pattern matches a folder, it will also match all files in it. Hence, the
 * patterns <code>foo/</code> and <code>foo/*</code> are equivalent.
 * </p>
 */
public class PathPattern {

  private final boolean isAbsolute;
  private final PathSegmentPattern[] segmentPatterns;

  private PathPattern( String expression ) {
    checkExpression( expression );
    isAbsolute = expression.startsWith( "/" ) && !expression.startsWith( "//" );
    segmentPatterns = extractSegments( expression );
  }

  /**
   * Creates a pattern from the given expression.
   */
  public static PathPattern create( String expression ) {
    return new PathPattern( expression );
  }

  /**
   * Attempts to match the given file name against this pattern.
   *
   * @param fileName
   *          the name of the file to match, not including the path
   * @return <code>true</code> if the pattern matches the given file name
   */
  public boolean matchesFile( String fileName ) {
    return getFileSegmentPattern().matches( fileName );
  }

  /**
   * Attempts to match a given folder path against this pattern.
   *
   * @param pathSegments
   *          the names of the path segments that constitute the path, not including a file name
   * @return <code>true</code> if the pattern matches the given path
   */
  public boolean matchesFolder( String ... pathSegments ) {
    if( segmentPatterns.length == 1 ) {
      return pathSegments.length == 0;
    }
    return match( 0, 0, pathSegments );
  }

  /**
   * Checks whether the file part of this pattern matches all files in a folder, independent from
   * the path part. For example, this method will return <code>true</code> for <code>src/*</code>,
   * but <code>false</code> for <code>src/*.js</code>.
   *
   * @return <code>true</code> if and only if this pattern matches all files in a folder
   */
  public boolean matchesAllFiles() {
    return getFileSegmentPattern() == PathSegmentPattern.ALL;
  }

  /**
   * Checks whether the path part of this pattern matches all folders. The result does not depend on
   * the file part of the pattern. For example, this method will return <code>true</code> for
   * <code>//*</code> and <code>//*.txt</code>, but <code>false</code> for <code>src/*</code>.
   *
   * @return <code>true</code> if and only if this pattern matches files in all folders
   */
  public boolean matchesAllFolders() {
    return segmentPatterns.length == 2 && segmentPatterns[ 0 ] == PathSegmentPattern.ANY_NUMBER;
  }

  /**
   * Returns the file part of this pattern.
   *
   * @return the file part of this pattern, never <code>null</code>
   */
  public String getFilePattern() {
    return getFileSegmentPattern().toString();
  }

  /**
   * Returns the path part of this pattern.
   *
   * @return the path part of this pattern, never <code>null</code>
   */
  public String getPathPattern() {
    StringBuilder builder = new StringBuilder();
    if( isAbsolute ) {
      builder.append( '/' );
    }
    for( int i = 0; i < segmentPatterns.length - 1; i++ ) {
      PathSegmentPattern pattern = segmentPatterns[ i ];
      if( pattern != PathSegmentPattern.ANY_NUMBER ) {
        builder.append( pattern.toString() );
      } else if( i == 0 ) {
        builder.append( '/' );
      }
      builder.append( '/' );
    }
    return builder.toString();
  }

  private PathSegmentPattern getFileSegmentPattern() {
    return segmentPatterns[ segmentPatterns.length - 1 ];
  }

  private boolean match( int patternPos, int inputPos, String[] segments ) {
    if( patternPos == segmentPatterns.length - 1 ) {
      // pattern is eaten up ( -1 because we ignore the file segment )
      return inputPos == segments.length;
    }
    if( inputPos == segments.length ) {
      // input is eaten up
      if( segmentPatterns[ patternPos ] == PathSegmentPattern.ANY_NUMBER ) {
        return match( patternPos + 1, inputPos, segments );
      }
      return false;
    }
    if( segmentPatterns[ patternPos ] == PathSegmentPattern.ANY_NUMBER ) {
      int nextInputPos = inputPos;
      while( nextInputPos <= segments.length ) {
        if( match( patternPos + 1, nextInputPos, segments ) ) {
          return true;
        }
        nextInputPos++;
      }
      return false;
    } else if( segmentPatterns[ patternPos ].matches( segments[ inputPos ] ) ) {
      return match( patternPos + 1, inputPos + 1, segments );
    }
    return false;
  }

  private static void checkExpression( String expression ) {
    if( expression.contains( "///" ) ) {
      throw new IllegalArgumentException( "Too many successive slashes in expression" );
    }
  }

  private static PathSegmentPattern[] extractSegments( String expression ) {
    String preparedExpression = prepareExpression( expression );
    return splitIntoSegmentPatterns( preparedExpression );
  }

  private static String prepareExpression( String expression ) {
    String result = expression;
    if( expression.startsWith( "/" ) ) {
      result = expression.substring( 1 );
    }
    return result;
  }

  static PathSegmentPattern[] splitIntoSegmentPatterns( String expression ) {
    PathSegmentPattern[] patterns = new PathSegmentPattern[ 8 ];
    int count = 0;
    int begin = 0;
    int next = expression.indexOf( '/', begin );
    while( next != -1 ) {
      if( count == patterns.length - 1 ) {
        patterns = createCopy( patterns, count * 2 );
      }
      patterns[ count++ ] = createPathSegmentPattern( expression, begin, next );
      begin = next + 1;
      next = expression.indexOf( '/', begin );
    }
    patterns[ count++ ] = createFileSegmentPattern( expression, begin, expression.length() );
    return createCopy( patterns, count );
  }

  private static PathSegmentPattern createPathSegmentPattern( String expression, int begin, int next )
  {
    if( begin == next ) {
      return PathSegmentPattern.ANY_NUMBER;
    }
    return PathSegmentPattern.create( expression.substring( begin, next ) );
  }

  private static PathSegmentPattern createFileSegmentPattern( String expression, int begin, int next )
  {
    if( begin == next ) {
      return PathSegmentPattern.ALL;
    }
    return PathSegmentPattern.create( expression.substring( begin, next ) );
  }

  private static PathSegmentPattern[] createCopy( PathSegmentPattern[] original, int newLength ) {
    PathSegmentPattern[] copy = new PathSegmentPattern[ newLength ];
    System.arraycopy( original, 0, copy, 0, Math.min( original.length, newLength ) );
    return copy;
  }

}
