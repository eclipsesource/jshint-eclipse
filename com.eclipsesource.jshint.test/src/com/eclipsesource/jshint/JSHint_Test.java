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

import com.eclipsesource.jshint.test.Problem;


public class JSHint_Test {

  private static final String CODE_WITH_EQNULL = "var f = x == null ? null : x + 1;";
  private static final String CODE_WITH_GLOBAL_ORG = "org = {};";

  private static final String WARN_EQNULL = "Use '===' to compare with 'null'";
  private List<Problem> log;
  private TestHandler handler;
  private JSHint jsHint;

  @Before
  public void setUp() throws IOException {
    log = new ArrayList<Problem>();
    handler = new TestHandler();
    jsHint = new JSHint();
    jsHint.init();
  }

  @Test
  public void checkEmpty() throws Exception {
    jsHint.check( "", handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkOk() throws Exception {
    jsHint.check( "var foo = 23;", handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkErrors() throws Exception {
    jsHint.check( "cheese!", handler );

    assertFalse( log.isEmpty() );
  }

  @Test
  public void checkUndefWithoutConfig() throws Exception {
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithEmptyConfig() throws Exception {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithConfig() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.get( 0 ).message.contains( "'org' is not defined" ) );
  }

  @Test
  public void checkUndefWithConfigAndGlobal() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", true );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithConfigAndReadonlyGlobal() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", false );
    jsHint.configure( configuration );

    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.get( 0 ).message.contains( "Read only" ) );
  }

  @Test
  public void checkEqNullWithoutConfig() throws Exception {
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.get( 0 ).message.contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithEmptyConfig() throws Exception {
    jsHint.configure( new Configuration() );

    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.get( 0 ).message.contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithConfig() throws Exception {
    Configuration configuration = new Configuration();
    configuration.addOption( "eqnull", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void testPosition() throws Exception {
    jsHint.configure( new Configuration() );
    jsHint.check( "var a = x == null ? null : 1;", handler );

    assertEquals( "1.11", log.get( 0 ).getPosition() );
  }

  @Test
  public void testPositionWithLeadingSpace() throws Exception {
    jsHint.configure( new Configuration() );
    jsHint.check( " var a = x == null ? null : 1;", handler );

    assertEquals( "1.12", log.get( 0 ).getPosition() );
  }

  private class TestHandler implements ProblemHandler {

    public void handleProblem( int line, int character, String message ) {
      log.add( new Problem( line, character, message ) );
    }
  }

}
