/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.eclipsesource.json.JsonObject;


@RunWith( value = Parameterized.class )
public class JSHint_Compatibility_Test {

  private final String version;
  private ArrayList<Problem> problems;
  private TestHandler handler;
  private JSHint jsHint;

  @Parameters( name = "{0}" )
  public static Collection<Object[]> getParameters() {
    return Arrays.asList( new Object[][] {
      { "r03" },
      { "r04" },
      { "r05" },
      { "r06" },
      { "r07" },
      { "r08" },
      { "r09" },
      { "r10" },
      { "r11" },
      { "r12" },
      { "1.1.0" },
      { "2.1.2" },
      { "2.1.10" },
      { "2.4.3" },
      { "2.5.6" } } );
  }

  public JSHint_Compatibility_Test( String version ) {
    this.version = version;
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

    assertEquals( "", getAllProblems() );
  }

  @Test
  public void undefinedVariable_withoutPredefInConfig_fails() {
    jsHint.configure( new JsonObject().add( "undef", true ) );

    jsHint.check( "foo = {};", handler );

    assertEquals( "1.0:'foo' is not defined", getAllProblems() );
  }

  @Test
  public void undefinedVariable_withPredefInConfig_succeeds() {
    JsonObject globals = new JsonObject().add( "foo", true );
    jsHint.configure( new JsonObject().add( "undef", true ).add( "globals", globals ) );

    jsHint.check( "foo = {};", handler );

    assertEquals( "", getAllProblems() );
  }

  @Test
  public void undefinedVariable_withReadOnlyPredefInConfig_fails() {
    JsonObject globals = new JsonObject().add( "foo", false );
    jsHint.configure( new JsonObject().add( "undef", true ).add( "globals", globals ) );

    jsHint.check( "foo = {};", handler );

    assertEquals( "1.0:Read only", getAllProblems() );
  }

  @Test
  public void eqnull_withoutConfig() {
    jsHint.check( "var x = 23 == null;", handler );

    assertEquals( "1.11:Use '===' to compare with 'null'", getAllProblems() );
  }

  @Test
  public void eqnull_withEmptyConfig() {
    jsHint.configure( new JsonObject() );

    jsHint.check( "var x = 23 == null;", handler );

    assertEquals( "1.11:Use '===' to compare with 'null'", getAllProblems() );
  }

  @Test
  public void eqnull_withEqnullInConfig() {
    jsHint.configure( new JsonObject().add( "eqnull", true ) );

    jsHint.check( "var f = x == null ? null : x + 1;", handler );

    assertEquals( "", getAllProblems() );
  }

  @Test
  public void extraComma() {
    JsonObject config = new JsonObject();
    if( versionGreaterThan( "2.0.0" ) ) {
      config.add( "es3", true );
    }
    jsHint.configure( config );

    jsHint.check( "var o = { x: 1, y: 2, z: 3, };", handler );

    assertThat( getAllProblems(), startsWith( "1.26:Extra comma" ) );
  }

  @Test
  public void positionIsCorrect() {
    jsHint.check( "var x = 23 == null;", handler );

    assertEquals( "1.11", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithLeadingSpace() {
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( " var x = 23 == null;", handler );

    assertEquals( "1.12", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithLeadingTab() {
    jsHint.configure( new JsonObject().add( "white", false ) );
    jsHint.check( "\tvar x = 23 == null;", handler );

    assertEquals( "1.12", getPositionFromProblem( 0 ) );
  }

  @Test
  public void positionIsCorrectWithMultipleTabs() {
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
    String resource = "com/jshint/jshint-" + version + ".js";
    InputStream stream = classLoader.getResourceAsStream( resource );
    if( stream == null ) {
      throw new IllegalArgumentException( "JSHint resource not found: " + resource );
    }
    try {
      jsHint.load( stream );
    } finally {
      stream.close();
    }
  }

  private String getPositionFromProblem( int n ) {
    Problem problem = problems.get( n );
    return problem.getLine() + "." + problem.getCharacter();
  }

  @Test
  public void testVersions() {
    assertEquals( 0, compareVersion( "1.2.3", "1.2.3" ) );
    assertEquals( 0, compareVersion( "r10", "r10" ) );
    assertEquals( -1, compareVersion( "r9", "r10" ) );
    assertEquals( -1, compareVersion( "r10", "1.0.0" ) );
    assertEquals( -1, compareVersion( "2.0.0", "3.0.0" ) );
    assertEquals( -1, compareVersion( "2.0.0", "2.1.0" ) );
    assertEquals( -1, compareVersion( "2.0.0", "2.0.1" ) );
    assertEquals( 1, compareVersion( "r10", "r9" ) );
    assertEquals( 1, compareVersion( "1.0.0", "r10" ) );
    assertEquals( 1, compareVersion( "2.0.0", "1.0.0" ) );
    assertEquals( 1, compareVersion( "2.1.0", "2.0.0" ) );
    assertEquals( 1, compareVersion( "2.0.1", "2.0.0" ) );
  }

  private boolean versionGreaterThan( String version ) {
    return compareVersion( this.version, version ) > 0;
  }

  private static int compareVersion( String version1, String version2 ) {
    int[] parts1 = transformVersion( version1 );
    int[] parts2 = transformVersion( version2 );
    for( int i = 0; i < 3; i++ ) {
      if( parts1[i] < parts2[i] ) {
        return -1;
      }
      if( parts1[i] > parts2[i] ) {
        return 1;
      }
    }
    return 0;
  }

  private static int[] transformVersion( String version ) {
    String[] parts = version.replaceAll( "r", "0.0." ).split( "\\." );
    int[] result = new int[3];
    for( int i = 0; i < 3; i++ ) {
      result[i] = Integer.parseInt( parts[i] );
    }
    return result;
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
