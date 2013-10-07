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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.eclipsesource.jshint.ui.test.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class MarkerAdapter_Test {

  private static final String TYPE_PROBLEM = "com.eclipsesource.jshint.ui.problemmarker";
  private static final String TYPE_PROBLEM_OLD = "com.eclipsesource.jshint.problemmarker";
  private static final String TEST_PROJECT = "jshint.ui.test.project";
  private IProject project;
  private IFile file;

  @Before
  public void setUp() {
    project = createProject( TEST_PROJECT );
    file = createFile( project, "/test.js", "test" );
  }

  @After
  public void tearDown() {
    deleteProject( project );
  }

  @Test
  public void createWarning() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 0, 0, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( 1, markers.length );
    assertEquals( file, markers[ 0 ].getResource() );
    assertEquals( TYPE_PROBLEM, markers[ 0 ].getType() );
    assertEquals( IMarker.SEVERITY_WARNING, markers[ 0 ].getAttribute( IMarker.SEVERITY, -1 ) );
  }

  @Test
  public void createError() throws CoreException {
    new MarkerAdapter( file ).createError( 1, 0, 0, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( 1, markers.length );
    assertEquals( file, markers[ 0 ].getResource() );
    assertEquals( TYPE_PROBLEM, markers[ 0 ].getType() );
    assertEquals( IMarker.SEVERITY_ERROR, markers[ 0 ].getAttribute( IMarker.SEVERITY, -1 ) );
  }

  @Test
  public void createMarker_withValidLine() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 0, 0, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( Integer.valueOf( 1 ), markers[ 0 ].getAttribute( IMarker.LINE_NUMBER ) );
  }

  @Test
  public void createMarker_withInvalidLine() throws CoreException {
    new MarkerAdapter( file ).createWarning( 0, 0, 0, "test" );

    IMarker[] markers = findMarkers( file );
    assertNull( markers[ 0 ].getAttribute( IMarker.LINE_NUMBER ) );
  }

  @Test
  public void createMarker_withValidRange() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 3, 5, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( Integer.valueOf( 3 ), markers[ 0 ].getAttribute( IMarker.CHAR_START ) );
    assertEquals( Integer.valueOf( 5 ), markers[ 0 ].getAttribute( IMarker.CHAR_END ) );
  }

  @Test
  public void createMarker_withNegativeStart() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, -1, 5, "test" );

    IMarker[] markers = findMarkers( file );
    assertNull( markers[ 0 ].getAttribute( IMarker.CHAR_START ) );
    assertNull( markers[ 0 ].getAttribute( IMarker.CHAR_END ) );
  }

  @Test
  public void createMarker_withEndLowerThanStart() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 3, 2, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( Integer.valueOf( 3 ), markers[ 0 ].getAttribute( IMarker.CHAR_START ) );
    assertEquals( Integer.valueOf( 3 ), markers[ 0 ].getAttribute( IMarker.CHAR_END ) );
  }

  @Test
  public void createMarker_withMessage() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 0, 0, "test" );

    IMarker[] markers = findMarkers( file );
    assertEquals( "test", markers[ 0 ].getAttribute( IMarker.MESSAGE ) );
  }

  @Test( expected=NullPointerException.class )
  public void createMarker_withNullMessage() throws CoreException {
    new MarkerAdapter( file ).createWarning( 1, 3, 5, null );
  }

  @Test
  public void removeMarkers_deletesMarkers() throws CoreException {
    file.createMarker( TYPE_PROBLEM );
    file.createMarker( TYPE_PROBLEM );

    new MarkerAdapter( project ).removeMarkers();

    IMarker[] markers = findMarkers( project );
    assertEquals( 0, markers.length );
  }

  @Test
  public void removeMarkers_deletesOldMarkers() throws CoreException {
    file.createMarker( TYPE_PROBLEM_OLD );
    file.createMarker( TYPE_PROBLEM_OLD );

    new MarkerAdapter( project ).removeMarkers();

    IMarker[] markers = project.findMarkers( TYPE_PROBLEM_OLD, true, IResource.DEPTH_INFINITE );
    assertEquals( 0, markers.length );
  }

  private static IMarker[] findMarkers( IResource resource ) throws CoreException {
    return resource.findMarkers( TYPE_PROBLEM, true, IResource.DEPTH_INFINITE );
  }

}
