/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.eclipsesource.json.JsonObject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class JSHint_Test {

  private List<Problem> problems;
  private TestHandler handler;
  private JSHint jsHint;

  @Before
  public void setUp() throws IOException {
    problems = new ArrayList<Problem>();
    handler = new TestHandler( problems );
    jsHint = new JSHint();
    jsHint.load();
  }

  @Test
  public void getDefaultVersion() {
    String version = JSHint.getDefaultLibraryVersion();

    assertTrue( version.matches( "\\d+\\.\\d+\\.\\d+" ) );
  }

  @Test( expected = NullPointerException.class )
  public void configureWithNull() {
    jsHint.configure( null );
  }

  @Test
  public void configureBeforeLoad() throws Exception {
    JsonObject configuration = new JsonObject().add( "undef", true );

    JSHint jsHint = new JSHint();
    jsHint.configure( configuration );
    jsHint.load();
    jsHint.check( "x = 23;", handler );

    assertEquals( "'x' is not defined", problems.get( 0 ).getMessage() );
  }

  @Test
    public void loadBeforeConfigure() throws Exception {
    JsonObject configuration = new JsonObject().add( "undef", true );

    JSHint jsHint = new JSHint();
    jsHint.load();
    jsHint.configure( configuration );
    jsHint.check( "x = 23;", handler );

    assertEquals( "'x' is not defined", problems.get( 0 ).getMessage() );
  }

  @Test( expected = IllegalStateException.class )
  public void checkWithoutLoad() {
    JSHint jsHint = new JSHint();
    jsHint.check( "code", handler );
  }

  @Test( expected = NullPointerException.class )
  public void checkWithNullCode() {
    jsHint.check( (String)null, handler );
  }

  @Test( expected = NullPointerException.class )
  public void checkWithNullText() {
    jsHint.check( (Text)null, handler );
  }

  @Test
  public void checkWithNullHandler() {
    assertTrue( jsHint.check( "var a = 23;", null ) );
    assertFalse( jsHint.check( "HMPF!", null ) );
  }

  @Test( expected = NullPointerException.class )
  public void loadCustomWithNullParameter() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.load( null );
  }

  @Test
  public void loadCustomWithEmptyFile() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Global JSHINT function missing in input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithWrongJsFile() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "var a = 23;".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Global JSHINT function missing in input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithFakeJsHintFile() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "JSHINT = {};".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Global JSHINT is not a function", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithEcmaException() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "JSHINT = foo;".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Could not evaluate JavaScript input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithGarbage() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "cheese! :D".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Could not evaluate JavaScript input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustom() throws Exception {
    JSHint jsHint = new JSHint();
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream stream = classLoader.getResourceAsStream( "com/jshint/jshint-r03.js" );
    try {
      jsHint.load( stream );
    } finally {
      stream.close();
    }

    jsHint.check( "cheese! :D", handler );

    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkWithEmptyCode() {
    boolean result = jsHint.check( "", handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithOnlyWhitespace() {
    boolean result = jsHint.check( " ", handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithValidCode() {
    boolean result = jsHint.check( "var foo = 23;", handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithSyntaxError() {
    boolean result = jsHint.check( "cheese!", handler );

    assertFalse( result );
    assertTrue( problems.get( 0 ).isError() );
  }

  @Test
  public void checkWithWarning() {
    boolean result = jsHint.check( "x = 23", handler ); // missing semicolon

    assertFalse( result );
    assertFalse( problems.get( 0 ).isError() );
  }

  @Test
  public void checkWithJavaScriptException() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.load( new ByteArrayInputStream( "JSHINT = function() { throw 'ERROR'; };".getBytes() ) );

    try {
      jsHint.check( "var a = 1;", handler );
      fail();
    } catch( RuntimeException exception ) {

      String expected = "JavaScript exception thrown by JSHint: ERROR";
      assertThat( exception.getMessage(), startsWith( expected ) );
      assertSame( JavaScriptException.class, exception.getCause().getClass() );
    }
  }

  @Test
  public void checkWithRhinoException() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.load( new ByteArrayInputStream( "JSHINT = function() { throw x[ 0 ]; };".getBytes() ) );

    try {
      jsHint.check( "var a = 1;", handler );
      fail();
    } catch( RuntimeException exception ) {

      String expected = "JavaScript exception caused by JSHint: ReferenceError";
      assertThat( exception.getMessage(), startsWith( expected ) );
      assertSame( EcmaError.class, exception.getCause().getClass() );
    }
  }

  public void createdProblemsContainErrorCode() {
    jsHint.check( "x = 1", handler );

    problems.get( 0 ).getCode().matches( "^W\\d+" );
  }

  @Test
  public void noErrorsWithoutConfig() {
    // undefined variable is only reported with 'undef' in config
    jsHint.check( "var f = function () { v = {}; };", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void noErrorsWithEmptyConfig() {
    // undefined variable is only reported with 'undef' in config
    jsHint.configure( new JsonObject() );

    jsHint.check( "var f = function () { v = {}; };", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void errorWithUndefInConfig() {
    jsHint.configure( new JsonObject().add( "undef", true ) );

    jsHint.check( "var f = function () { v = {}; };", handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( "'v' is not defined" ) );
  }

  @Test
  public void errorAfterTabHasSamePositionAsAfterSpace() {
    jsHint.configure( new JsonObject().add( "undef", true ) );
    jsHint.check( "var x = 1, # y = 2;", handler );
    Problem problemWithSpace = problems.get( 0 );
    problems.clear();

    jsHint.check( "var x = 1,\t# y = 2;", handler );

    Problem problemWithTab = problems.get( 0 );
    assertEquals( problemWithSpace.getCharacter(), problemWithTab.getCharacter() );
  }

  @Test
  public void errorAtEndDoesNotThrowException() {
    jsHint.configure( new JsonObject().add( "undef", true ) );

    // Must not throw SIOOBE
    // See https://github.com/eclipsesource/jshint-eclipse/issues/34
    jsHint.check( "var x = 1;\t#", handler );
  }

  @Test
  public void checkSameInputTwice() {
    jsHint.configure( new JsonObject().add( "undef", true ) );
    LoggingHandler handler1 = new LoggingHandler();
    LoggingHandler handler2 = new LoggingHandler();

    jsHint.check( "var x = 1;\t#", handler1 );
    jsHint.check( "var x = 1;\t#", handler2 );

    assertTrue( handler1.toString().length() > 0 );
    assertEquals( handler1.toString(), handler2.toString() );
  }

  @Test
  public void checkMultipleFiles() {
    // see https://github.com/jshint/jshint/issues/931
    jsHint.configure( new JsonObject().add( "undef", true ) );

    jsHint.check( "var x = 1;\t#", handler );
    jsHint.check( "var x = 1;\t#", handler );
    jsHint.check( "var x = 1;\t#", handler );
    jsHint.check( "var x = 1;\t#", handler );
    jsHint.check( "var x = 1;\t#", handler );
  }

  @Test
  public void createProblem() {
    Text text = new Text( "line1\nline2\n" );
    ScriptableObject error = mockError( "foo", 1, 3, "T001" );

    Problem problem = jsHint.createProblem( error, text );

    assertEquals( "foo", problem.getMessage() );
    assertEquals( 1, problem.getLine() );
    assertEquals( 2, problem.getCharacter() );
    assertEquals( "T001", problem.getCode() );
  }

  @Test
  public void createProblem_cutsOffTrailingPeriodFromMessage() {
    Text text = new Text( "line1\nline2\n" );
    ScriptableObject error = mockError( "Foo.", 1, 3, "T001" );

    Problem problem = jsHint.createProblem( error, text );

    assertEquals( "Foo", problem.getMessage() );
  }

  @Test
  public void createProblem_mapsVisualToRealIndex() {
    Text text = new Text( "\tline1\n" );
    ScriptableObject error = mockError( "foo", 1, 6, "T001" );

    Problem problem = jsHint.createProblem( error, text );

    assertEquals( 1, problem.getLine() );
    assertEquals( 2, problem.getCharacter() );
  }

  @Test
  public void createProblem_toleratesIllegalLine() {
    // Protect against the case that jshint reports illegal lines
    // See https://github.com/eclipsesource/jshint-eclipse/issues/27
    //     https://github.com/eclipsesource/jshint-eclipse/issues/60
    Text text = new Text( "line1\nline2\n" );
    ScriptableObject error = mockError( "Foo.", 4, 7, "T001" );

    Problem problem = jsHint.createProblem( error, text );

    assertEquals( -1, problem.getLine() );
    assertEquals( -1, problem.getCharacter() );
  }

  /*
   * index:  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10| 11|
   * char:   | a | » | b | » | c |
   * visual:     | a | »             | b | »             | c |
   */
  @Test
  public void visualToCharIndex() {
    Text text = new Text( "a\tb\tc" );

    assertEquals( 0, jsHint.visualToCharIndex( text, 1, 1 ) );
    assertEquals( 1, jsHint.visualToCharIndex( text, 1, 2 ) );
    assertEquals( 2, jsHint.visualToCharIndex( text, 1, 6 ) );
    assertEquals( 3, jsHint.visualToCharIndex( text, 1, 7 ) );
    assertEquals( 4, jsHint.visualToCharIndex( text, 1, 11 ) );
  }

  public void visualToCharIndex_withoutTabs() {
    Text text = new Text( "a b c" );

    assertEquals( 0, jsHint.visualToCharIndex( text, 1, 1 ) );
    assertEquals( 1, jsHint.visualToCharIndex( text, 1, 2 ) );
    assertEquals( 2, jsHint.visualToCharIndex( text, 1, 3 ) );
    assertEquals( 3, jsHint.visualToCharIndex( text, 1, 4 ) );
    assertEquals( 4, jsHint.visualToCharIndex( text, 1, 5 ) );
  }

  private static ScriptableObject mockError( String message, int line, int character, String code )
  {
    ScriptableObject error = mock( ScriptableObject.class );
    when( error.get( eq( "reason" ), any( Scriptable.class ) ) ).thenReturn( message );
    when( error.get( eq( "line" ), any( Scriptable.class ) ) ).thenReturn( Integer.valueOf( line ) );
    when( error.get( eq( "character" ), any( Scriptable.class ) ) ).thenReturn( Integer.valueOf( character ) );
    when( error.get( eq( "code" ), any( Scriptable.class ) ) ).thenReturn( code );
    return error;
  }

  private static class LoggingHandler implements ProblemHandler {

    StringBuilder log = new StringBuilder();

    public void handleProblem( Problem problem ) {
      log.append( problem.getLine() );
      log.append( ':' );
      log.append( problem.getCharacter() );
      log.append( ':' );
      log.append( problem.getMessage() );
      log.append( '\n' );
    }

    @Override
    public String toString() {
      return log.toString();
    }

  }

  private static class TestHandler implements ProblemHandler {

    private final List<Problem> problems;

    public TestHandler( List<Problem> problems ) {
      this.problems = problems;
    }

    public void handleProblem( Problem problem ) {
      problems.add( problem );
    }

  }

}
