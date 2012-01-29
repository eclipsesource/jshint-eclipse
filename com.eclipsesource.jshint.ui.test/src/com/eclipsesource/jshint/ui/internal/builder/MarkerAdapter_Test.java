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
package com.eclipsesource.jshint.ui.internal.builder;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.builder.MarkerAdapter;


public class MarkerAdapter_Test {

  private static final String PROBLEM_MARKER = "com.eclipsesource.jshint.ui.problemmarker";
  private static final String PROBLEM_MARKER_OLD = "com.eclipsesource.jshint.problemmarker";
  private static final String TEST_PROJECT = "jshint.ui.test.project";
  private IProject project;
  private IFile file;

  @Before
  public void setUp() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    project = workspace.getRoot().getProject( TEST_PROJECT );
    project.create( null );
    project.open( null );
    file = project.getFile( "/test.js" );
    file.create( new ByteArrayInputStream( "test".getBytes() ), true, null );
  }

  @After
  public void teardown() throws CoreException {
    if( project.exists() ) {
      project.delete( true, null );
    }
  }

  @Test
  public void markersAreDeleted() throws CoreException {
    file.createMarker( PROBLEM_MARKER );
    file.createMarker( PROBLEM_MARKER );

    new MarkerAdapter( project ).removeMarkers();

    IMarker[] markers = project.findMarkers( PROBLEM_MARKER_OLD, true, IResource.DEPTH_INFINITE );
    assertEquals( 0, markers.length );
  }

  @Test
  public void oldMarkersAreDeleted() throws CoreException {
    file.createMarker( PROBLEM_MARKER_OLD );
    file.createMarker( PROBLEM_MARKER_OLD );

    new MarkerAdapter( project ).removeMarkers();

    IMarker[] markers = project.findMarkers( PROBLEM_MARKER, true, IResource.DEPTH_INFINITE );
    assertEquals( 0, markers.length );
  }

}
