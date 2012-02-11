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

import static org.hamcrest.Matchers.greaterThan;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith( value = Parameterized.class )
public class JSHint_Compatibility_Test {

  private ArrayList<Problem> problems;
  private TestHandler handler;
  private final String jsHintResource;
  private JSHint jsHint;

  @Parameters
  public static Collection<Object[]> getParameters() {
    ArrayList<Object[]> parameters = new ArrayList<Object[]>();
    parameters.add( new Object[] { "com/jshint/jshint-r03.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r04.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r05.js" } );
    parameters.add( new Object[] { "com/jslint/jslint-2012-02-03.js" } );
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
  public void checkValid_noProblems() throws Exception {
    jsHint.check( "var a = 23;", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  @SuppressWarnings( "boxing" )
  public void checkInvalid_problemFieldsSet() throws Exception {
    jsHint.check( "hmpf!", handler );

    assertFalse( problems.isEmpty() );
    assertThat( problems.get( 0 ).getLine(), greaterThan( 0 ) );
    assertThat( problems.get( 0 ).getCharacter(), greaterThan( 0 ) );
    assertThat( problems.get( 0 ).getMessage().length(), greaterThan( 0 ) );
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

  private class TestHandler implements ProblemHandler {

    public void handleProblem( Problem problem ) {
      problems.add( problem );
    }
  }

}
