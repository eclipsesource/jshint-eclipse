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

import com.eclipsesource.json.JsonObject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;


public class JSHint_Test {

  private List<Problem> problems;
  private List<Task> tasks;
  private TestHandler handler;
  private JSHint jsHint;

  @Before
  public void setUp() throws IOException {
    problems = new ArrayList<Problem>();
    handler = new TestHandler( problems, tasks );
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
    jsHint.configure( null, null );
  }

  @Test
  public void configureBeforeLoad() throws Exception {
    JsonObject configuration = new JsonObject().add( "undef", true );

    JSHint jsHint = new JSHint();
    jsHint.configure( configuration, "" );
    jsHint.load();
    jsHint.check( "x = 23;", handler, handler );

    assertEquals( "'x' is not defined", problems.get( 0 ).getMessage() );
  }

  @Test
    public void loadBeforeConfigure() throws Exception {
    JsonObject configuration = new JsonObject().add( "undef", true );

    JSHint jsHint = new JSHint();
    jsHint.load();
    jsHint.configure( configuration, "" );
    jsHint.check( "x = 23;", handler, handler );

    assertEquals( "'x' is not defined", problems.get( 0 ).getMessage() );
  }

  @Test( expected = IllegalStateException.class )
  public void checkWithoutLoad() {
    JSHint jsHint = new JSHint();
    jsHint.check( "code", handler, handler );
  }

  @Test( expected = NullPointerException.class )
  public void checkWithNullCode() {
    jsHint.check( (String)null, handler, handler );
  }

  @Test( expected = NullPointerException.class )
  public void checkWithNullText() {
    jsHint.check( (Text)null, handler, handler );
  }

  @Test
  public void checkWithNullHandler() {
    assertTrue( jsHint.check( "var a = 23;", null, null ) );
    assertFalse( jsHint.check( "HMPF!", null, null ) );
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
      assertEquals( "Global JSHINT or JSLINT function missing in input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithWrongJsFile() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "var a = 23;".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Global JSHINT or JSLINT function missing in input", exception.getMessage() );
    }
  }

  @Test
  public void loadCustomWithFakeJsHintFile() throws Exception {
    JSHint jsHint = new JSHint();
    try {
      jsHint.load( new ByteArrayInputStream( "JSHINT = {};".getBytes() ) );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertEquals( "Global JSHINT or JSLINT is not a function", exception.getMessage() );
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

    jsHint.check( "cheese! :D", handler, handler );

    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkWithEmptyCode() {
    boolean result = jsHint.check( "", handler, handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithOnlyWhitespace() {
    boolean result = jsHint.check( " ", handler, handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithValidCode() {
    boolean result = jsHint.check( "var foo = 23;", handler, handler );

    assertTrue( result );
    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkWithFaultyCode() {
    boolean result = jsHint.check( "cheese!", handler, handler );

    assertFalse( result );
    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkWithJavaScriptException() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.load( new ByteArrayInputStream( "JSHINT = function() { throw 'ERROR'; };".getBytes() ) );

    try {
      jsHint.check( "var a = 1;", handler, handler );
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
      jsHint.check( "var a = 1;", handler, handler );
      fail();
    } catch( RuntimeException exception ) {

      String expected = "JavaScript exception caused by JSHint: ReferenceError";
      assertThat( exception.getMessage(), startsWith( expected ) );
      assertSame( EcmaError.class, exception.getCause().getClass() );
    }
  }

  @Test
  public void noErrorsWithoutConfig() {
    // undefined variable is only reported with 'undef' in config
    jsHint.check( "var f = function () { v = {}; };", handler, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void noErrorsWithEmptyConfig() {
    // undefined variable is only reported with 'undef' in config
    jsHint.configure( new JsonObject(), null );

    jsHint.check( "var f = function () { v = {}; };", handler, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void errorWithUndefInConfig() {
    jsHint.configure( new JsonObject().add( "undef", true ), null );

    jsHint.check( "var f = function () { v = {}; };", handler, handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( "'v' is not defined" ) );
  }

  @Test
  public void errorAfterTabHasCorrectPosition() {
    jsHint.configure( new JsonObject().add( "undef", true ), null );

    jsHint.check( "var x = 1,\t# y = 2;", handler, handler );

    assertEquals( 11, problems.get( 0 ).getStartCharacter() );
  }

  @Test
  public void errorAtEndDoesNotThrowException() {
    jsHint.configure( new JsonObject().add( "undef", true ), null );

    // Must not throw SIOOBE
    // See https://github.com/eclipsesource/jshint-eclipse/issues/34
    jsHint.check( "var x = 1;\t#", handler, handler );
  }

  @Test
  public void checkSameInputTwice() {
    jsHint.configure( new JsonObject().add( "undef", true ), null );
    LoggingHandler handler1 = new LoggingHandler();
    LoggingHandler handler2 = new LoggingHandler();

    jsHint.check( "var x = 1;\t#", handler1, handler );
    jsHint.check( "var x = 1;\t#", handler2, handler );

    assertTrue( handler1.toString().length() > 0 );
    assertEquals( handler1.toString(), handler2.toString() );
  }

  @Test
  public void checkMultipleFiles() {
    // see https://github.com/jshint/jshint/issues/931
    jsHint.configure( new JsonObject().add( "undef", true ), null );

    jsHint.check( "var x = 1;\t#", handler, handler );
    jsHint.check( "var x = 1;\t#", handler, handler );
    jsHint.check( "var x = 1;\t#", handler, handler );
    jsHint.check( "var x = 1;\t#", handler, handler );
    jsHint.check( "var x = 1;\t#", handler, handler );
  }

  private static class LoggingHandler implements ProblemHandler {

    StringBuilder log = new StringBuilder();

    public void handleProblem( Problem problem ) {
      log.append( problem.getLine() );
      log.append( ':' );
      log.append( problem.getStartCharacter() );
      log.append( ':' );
      log.append( problem.getMessage() );
      log.append( '\n' );
    }

    @Override
    public String toString() {
      return log.toString();
    }

  }

  private static class TestHandler implements ProblemHandler, TaskHandler {

    private final List<Problem> problems;
    private final List<Task> tasks;

    public TestHandler( List<Problem> problems, List<Task> tasks ) {
      this.problems = problems;
      this.tasks = tasks;
    }

    public void handleProblem( Problem problem ) {
      problems.add( problem );
    }

    public void handleTask( Task task ) {
    	tasks.add( task );
    }

  }

}
