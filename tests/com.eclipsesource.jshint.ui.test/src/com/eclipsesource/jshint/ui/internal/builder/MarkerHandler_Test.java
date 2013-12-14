/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.Problem;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.preferences.JSHintPreferences;

import static org.mockito.Mockito.*;


public class MarkerHandler_Test {

  private MarkerAdapter adapter;

  @Before
  public void setUp() {
    adapter = mock( MarkerAdapter.class );
  }

  @After
  public void tearDown() throws CoreException {
    JSHintPreferences prefs = new JSHintPreferences();
    prefs.resetToDefaults();
    prefs.save();
  }

  @Test
  public void handleProblem_createsWarning() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockWarning( 1, 2, "foo" ) );

    verify( adapter ).createWarning( 1, 2, 2, "foo" );
  }

  @Test
  public void handleProblem_createsWarningForError() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockError( 1, 2, "foo" ) );

    verify( adapter ).createWarning( 1, 2, 2, "foo" );
  }

  @Test
  public void handleProblem_createsError_ifEnabled() throws CoreException {
    JSHintPreferences prefs = new JSHintPreferences();
    prefs.setEnableErrorMarkers( true );
    prefs.save();
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockError( 1, 2, "foo" ) );

    verify( adapter ).createError( 1, 2, 2, "foo" );
  }

  @Test
  public void handleProblem_createsProblemAtDocumentWhenLineIsZero() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockWarning( 0, 1, "test" ) );

    verify( adapter ).createWarning( -1, -1, -1, "test" );
  }

  @Test
  public void handleProblem_createsProblemAtDocumentWhenLineIsNegative() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockWarning( -1, 1, "test" ) );

    verify( adapter ).createWarning( -1, -1, -1, "test" );
  }

  @Test
  public void handleProblem_createsProblemAtDocumentWhenLineExceedsDocument() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockWarning( 2, 1, "test" ) );

    verify( adapter ).createWarning( -1, -1, -1, "test" );
  }

  @Test
  public void handleProblem_createsProblemAtLineWhenCharacterIsNegative() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "test" ) );

    handler.handleProblem( mockWarning( 1, -1, "test" ) );

    verify( adapter ).createWarning( 1, -1, -1, "test" );
  }

  @Test
  public void handleProblem_createsProblemAtLineWhenCharacterExceedsLine() throws CoreException {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "line1\nline2\n" ) );

    handler.handleProblem( mockWarning( 1, 6, "test" ) );

    verify( adapter ).createWarning( 1, -1, -1, "test" );
  }

  @Test
  public void handleProblem_createsProblemAtLineWhenCharacterExceedsDocument() throws CoreException
  {
    MarkerHandler handler = new MarkerHandler( adapter, new Text( "line1\nline2\n" ) );

    handler.handleProblem( mockWarning( 1, 13, "test" ) );

    verify( adapter ).createWarning( 1, -1, -1, "test" );
  }

  private Problem mockError( int line, int character, String message ) {
    Problem problem = mockWarning( line, character, message );
    when( Boolean.valueOf( problem.isError() ) ).thenReturn( Boolean.TRUE );
    return problem;
  }

  private Problem mockWarning( int line, int character, String message ) {
    Problem problem = mock( Problem.class );
    when( Integer.valueOf( problem.getLine() ) ).thenReturn( Integer.valueOf( line ) );
    when( Integer.valueOf( problem.getCharacter() ) ).thenReturn( Integer.valueOf( character ) );
    when( problem.getMessage() ).thenReturn( message );
    return problem;
  }

}
