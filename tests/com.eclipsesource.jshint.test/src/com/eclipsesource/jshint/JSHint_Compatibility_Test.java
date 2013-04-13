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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.eclipsesource.json.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;


@RunWith( value = Parameterized.class )
public class JSHint_Compatibility_Test {

  private ArrayList<Problem> problems;
  private TestHandler handler;
  private final String jsHintResource;
  private JSHint jsHint;

  @Parameters
  public static Collection<Object[]> getParameters() {
    ArrayList<Object[]> parameters = new ArrayList<Object[]>();
    parameters.add( new Object[] { "com/jslint/jslint-2012-02-03.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r03.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r04.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r05.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r06.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r07.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r08.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r09.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r10.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r11.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r12.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-1.1.0.js" } );
    return parameters;
  }

  public JSHint_Compatibility_Test( String jsHintResource ) {
    this.jsHintResource = jsHintResource;
  }

  @Before
  public void setUp() throws IOException {
    problems = new ArrayList<Problem>();
    handler = new TestHandler();
    jsHint = new JSHint();
    loadJsHint();
  }

  @Test
  public void noProblemsForValidCode() {
    jsHint.check( "var a = 23;", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void problemLineIs_1_Relative() {
    jsHint.check( "#", handler );

    assertEquals( 1, problems.get( 0 ).getLine() );
  }

  @Test
  public void problemCharacterIs_0_Relative() {
    jsHint.check( "#", handler );

    assertEquals( 0, problems.get( 0 ).getCharacter() );
  }

  @Test
  public void cproblemMessageIsNotEmpty() {
    jsHint.check( "#", handler );

    assertTrue( problems.get( 0 ).getMessage().length() > 0 );
  }

  @Test
  public void undefinedVariable_withoutConfig_succeeds() {
    jsHint.check( "foo = {};", handler );

    // seems that the undef option is inverted in jslint
    String expected = isJsLint() ? "1.0:'foo' was used before it was defined" : "";
    assertEquals( expected, getAllProblems() );
  }

  @Test
  public void undefinedVariable_withoutPredefInConfig_fails() {
    jsHint.configure( new JsonObject().add( "undef", true ) );

    jsHint.check( "foo = {};", handler );

    // seems that the undef option is inverted in jslint
    String expected = isJsLint() ? "" : "1.0:'foo' is not defined";
    assertEquals( expected, getAllProblems() );
  }

  @Test
  public void undefinedVariable_withPredefInConfig_succeeds() {
    JsonObject predefined = new JsonObject().add( "foo", true );
    jsHint.configure( new JsonObject().add( "undef", true ).add( "predef", predefined ) );

    jsHint.check( "foo = {};", handler );

    assertEquals( "", getAllProblems() );
  }

  @Test
  public void undefinedVariable_withReadOnlyPredefInConfig_fails() {
    // FIXME [rst] See https://github.com/jshint/jshint/issues/665
    assumeTrue( !isVersion( "r10" ) && !isVersion( "r11" ) && !isVersion( "r12" ) );
    JsonObject predefined = new JsonObject().add( "foo", false );
    jsHint.configure( new JsonObject().add( "undef", true ).add( "predef", predefined ) );

    jsHint.check( "foo = {};", handler );

    assertEquals( "1.0:Read only", getAllProblems() );
  }

  @Test
  public void eqnull_withoutConfig() {
    jsHint.check( "var x = 23 == null;", handler );

    String expected = isJsLint() ? "Expected '===' and instead saw '=='"
                                 : "Use '===' to compare with 'null'";
    assertEquals( "1.11:" + expected, getAllProblems() );
  }

  @Test
  public void eqnull_withEmptyConfig() {
    jsHint.configure( new JsonObject() );

    jsHint.check( "var x = 23 == null;", handler );

    String expected = isJsLint() ? "Expected '===' and instead saw '=='"
                                   : "Use '===' to compare with 'null'";
    assertEquals( "1.11:" + expected, getAllProblems() );
  }

  @Test
  public void eqnull_withEqnullInConfig() {
    // JSLint doesn't get this right
    assumeTrue( !isJsLint() );
    jsHint.configure( new JsonObject().add( "eqnull", true ) );

    jsHint.check( "var f = x == null ? null : x + 1;", handler );

    assertEquals( "", getAllProblems() );
  }

  @Test
  public void positionIsCorrect() {
    jsHint.check( "var x = 23 == null;", handler );

    assertEquals( "1.11", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithLeadingSpace() {
    assumeTrue( !isJsLint() );
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( " var x = 23 == null;", handler );

    assertEquals( "1.12", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithLeadingTab() {
    assumeTrue( !isJsLint() );
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( "\tvar x = 23 == null;", handler );

    assertEquals( "1.12", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithMultipleTabs() {
    assumeTrue( !isJsLint() );
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( "\tvar x\t= 23 == null;", handler );

    assertEquals( "1.12", getPositionFromProblem( 0 ) );
  }

  @Test
  public void toleratesWindowsLineBreaks() {
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( "var x = 1;\r\nvar y = 2;\r\nvar z = 23 == null;", handler );

    assertEquals( "3.11", getPositionFromProblem( 0 ) );
  }

  private void loadJsHint() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream stream = classLoader.getResourceAsStream( jsHintResource );
    try {
      jsHint.load( stream );
    } finally {
      stream.close();
    }
  }

  private boolean isVersion( String version ) {
    return jsHintResource.contains( version );
  }

  private boolean isJsLint() {
    return jsHintResource.contains( "jslint" );
  }

  private String getPositionFromProblem( int n ) {
    Problem problem = problems.get( n );
    return problem.getLine() + "." + problem.getCharacter();
  }

  private String getAllProblems() {
    StringBuilder builder = new StringBuilder();
    for( Problem problem : problems ) {
      if( builder.length() > 0 ) {
        builder.append( ", " );
      }
      builder.append( problem.getLine() );
      builder.append( '.' );
      builder.append( problem.getCharacter() );
      builder.append( ':' );
      builder.append( problem.getMessage() );
    }
    return builder.toString();
  }

  private class TestHandler implements ProblemHandler {

    public void handleProblem( Problem problem ) {
      problems.add( problem );
    }
  }

}
