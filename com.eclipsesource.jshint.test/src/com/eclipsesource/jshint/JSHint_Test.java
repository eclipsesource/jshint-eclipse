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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;



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
    jsHint.init();
  }

  @Test
  public void checkEmpty() throws Exception {
    jsHint.check( "", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkOk() throws Exception {
    jsHint.check( "var foo = 23;", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkErrors() throws Exception {
    jsHint.check( "cheese!", handler );

    assertFalse( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithoutConfig() throws Exception {
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithEmptyConfig() throws Exception {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithConfig() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.get( 0 ).getMessage().contains( "'org' is not defined" ) );
  }

  @Test
  public void checkUndefWithConfigAndGlobal() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void checkUndefWithConfigAndReadonlyGlobal() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", false );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( problems.get( 0 ).getMessage().contains( "Read only" ) );
  }

  @Test
  public void checkEqNullWithoutConfig() throws Exception {
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( problems.get( 0 ).getMessage().contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithEmptyConfig() throws Exception {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( problems.get( 0 ).getMessage().contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithConfig() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "eqnull", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void testPosition() throws Exception {
    jsHint.configure( new Configuration() );
    jsHint.check( "var a = x == null ? null : 1;", handler );

    assertEquals( "1.11", getPosition( problems.get( 0 ) ) );
  }

  @Test
  public void testPositionWithLeadingSpace() throws Exception {
    jsHint.configure( new Configuration() );
    jsHint.check( " var a = x == null ? null : 1;", handler );

    assertEquals( "1.12", getPosition( problems.get( 0 ) ) );
  }

  @Test
  public void testPositionWithLeadingTab() throws Exception {
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
