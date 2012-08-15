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

import static org.junit.Assert.assertEquals;
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
    parameters.add( new Object[] { "com/jshint/jshint-r06.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r07.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r08.js" } );
    parameters.add( new Object[] { "com/jshint/jshint-r09.js" } );
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
  public void check_noProblemsIfValid() {
    jsHint.check( "var a = 23;", handler );

    assertTrue( problems.isEmpty() );
  }

  @Test
  public void check_problemLineIs_1_Relative() {
    jsHint.check( "#", handler );

    assertEquals( 1, problems.get( 0 ).getLine() );
  }

  @Test
  public void check_problemCharacterIs_0_Relative() {
    jsHint.check( "#", handler );

    assertEquals( 0, problems.get( 0 ).getCharacter() );
  }

  @Test
  public void check_problemMessageIsNotEmpty() {
    jsHint.check( "#", handler );

    assertTrue( problems.get( 0 ).getMessage().length() > 0 );
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
