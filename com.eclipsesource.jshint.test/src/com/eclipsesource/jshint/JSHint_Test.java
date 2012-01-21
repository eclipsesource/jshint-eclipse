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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class JSHint_Test {

  private static final String CODE_WITH_EQNULL = "var f = x == null ? null : x + 1;";
  private static final String CODE_WITH_GLOBAL_ORG = "org = {};";

  private static final String WARN_EQNULL = "Use '===' to compare with 'null'";
  private List<String> log;
  private TestHandler handler;
  private JSHint jsHint;

  @Before
  public void setUp() {
    log = new ArrayList<String>();
    handler = new TestHandler();
    jsHint = new JSHint();
  }

  @Test
  public void checkEmpty() throws Exception {
    jsHint.init();
    jsHint.check( "", handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkOk() throws Exception {
    jsHint.init();
    jsHint.check( "var foo = 23;", handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkErrors() throws Exception {
    jsHint.init();
    jsHint.check( "cheese!", handler );

    assertFalse( log.isEmpty() );
  }

  @Test
  public void checkUndefWithoutConfig() throws Exception {
    jsHint.init();
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithEmptyConfig() throws Exception {
    jsHint.init();
    jsHint.configure( new Configuration() );
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithConfig() throws Exception {
    jsHint.init();
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.get( 0 ).contains( "'org' is not defined" ) );
  }

  @Test
  public void checkUndefWithConfigAndGlobal() throws Exception {
    jsHint.init();
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.isEmpty() );
  }

  @Test
  public void checkUndefWithConfigAndReadonlyGlobal() throws Exception {
    jsHint.init();
    Configuration configuration = new Configuration();
    configuration.addOption( "undef", true );
    configuration.addGlobal( "org", false );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_GLOBAL_ORG, handler );

    assertTrue( log.get( 0 ).contains( "Read only" ) );
  }

  @Test
  public void checkEqNullWithoutConfig() throws Exception {
    jsHint.init();
    jsHint.configure( new Configuration() );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.get( 0 ).contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithEmptyConfig() throws Exception {
    jsHint.init();
    jsHint.configure( new Configuration() );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.get( 0 ).contains( WARN_EQNULL ) );
  }

  @Test
  public void checkEqNullWithConfig() throws Exception {
    jsHint.init();
    Configuration configuration = new Configuration();
    configuration.addOption( "eqnull", true );
    jsHint.configure( configuration );
    jsHint.check( CODE_WITH_EQNULL, handler );

    assertTrue( log.isEmpty() );
  }

  public class TestHandler implements ErrorHandler {

    public void handleError( int line, int character, String message ) {
      log.add( line + "." + character + ": " + message );
    }
  }
}
