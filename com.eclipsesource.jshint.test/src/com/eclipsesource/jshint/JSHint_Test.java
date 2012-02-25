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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class JSHint_Test {

  private static final String CODE_WITH_EQNULL = "var f = x == null ? null : x + 1;";
  private static final String CODE_WITH_GLOBAL_ORG = "org = {};";

  private static final String WARN_EQNULL = "Use '===' to compare with 'null'";
  private List<Problem> problems;
  private TestHandler handler;
  private JSHint jsHint;

  @Before
  public void setUp() throws IOException {
    problems = new ArrayList<Problem>();
    handler = new TestHandler();
    jsHint = new JSHint();
    jsHint.load();
  }

  @Test
  public void getDefaultVersion() {
    String version = JSHint.getDefaultLibraryVersion();

    assertTrue( version.startsWith( "r" ) );
  }

  @Test( expected = IllegalStateException.class )
  public void checkWithoutLoad() {
    JSHint jsHint = new JSHint();
    jsHint.check( "hmpf!", handler );
  }

  @Test
  public void configureBeforeLoad() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.configure( new Configuration() );
    jsHint.load();
    jsHint.check( "hmpf!", handler );

    assertFalse( problems.isEmpty() );
  }

  @Test( expected = NullPointerException.class )
  public void checkWithNullCode() {
    jsHint.check( null, handler );
  }

  @Test
  public void checkWithNullHandler() {
    assertTrue( jsHint.check( "var a = 23;", null ) );
    assertFalse( jsHint.check( "HMPF!", null ) );
  }

  @Test( expected = NullPointerException.class )
  public void configWithNull() {
    jsHint.configure( null );
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

    jsHint.check( "cheese! :D", handler );

    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkEmpty() {
    jsHint.check( "", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkOk() {
    jsHint.check( "var foo = 23;", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkErrors() {
    jsHint.check( "cheese!", handler );

    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkWithJavaScriptException() throws Exception {
    JSHint jsHint = new JSHint();
    jsHint.load( new ByteArrayInputStream( "JSHINT = function() { throw 'ERROR'; };".getBytes() ) );

    try {
      jsHint.check( "var a = 1;", handler );
    } catch( RuntimeException exception ) {

      String expected = "JavaScript exception occured in JSHint check: ERROR";
      assertThat( exception.getMessage(), startsWith( expected ) );
    }
  }

  @Test
  public void checkUndefWithoutConfig() {
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithEmptyConfig() {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithConfig() {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( "'org' is not defined" ) );
  }

  @Test
  public void checkUndefWithConfigAndGlobal() {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addPredefined( "org", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithConfigAndReadonlyGlobal() {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addPredefined( "org", false );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( "Read only" ) );
  }

  @Test
  public void checkEqNullWithoutConfig() {
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithEmptyConfig() {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_EQNULL, handler );

    assertThat( problems.get( 0 ).getMessage(), containsString( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithConfig() {
    Configuration configuration = new Configuration();
    configuration.addOption( "eqnull", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void testPosition() {
    jsHint.configure( new Configuration() );
    jsHint.check( "var a = x == null ? null : 1;", handler );

    assertEquals( "1.11", getPosition( problems.get( 0 ) ) );
  }

  @Test
  public void testPositionWithLeadingSpace() {
    jsHint.configure( new Configuration() );
    jsHint.check( " var a = x == null ? null : 1;", handler );

    assertEquals( "1.12", getPosition( problems.get( 0 ) ) );
  }

  @Test
  public void testPositionWithLeadingTab() {
    jsHint.configure( new Configuration() );
    jsHint.check( "\tvar a = x == null ? null : 1;", handler );

    assertEquals( "1.12", getPosition( problems.get( 0 ) ) );
  }

  private static String getPosition( Problem problem ) {
    return problem.getLine() + "." + problem.getCharacter();
  }

  private class TestHandler implements ProblemHandler {

    public void handleProblem( Problem problem ) {
      problems.add( problem );
    }
  }

}
