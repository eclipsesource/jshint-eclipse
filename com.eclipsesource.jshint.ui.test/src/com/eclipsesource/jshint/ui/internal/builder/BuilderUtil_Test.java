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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BuilderUtil_Test {

  private static final String BUILDER_ID = "com.eclipsesource.jshint.ui.builder";
  private IProject project;
  private IFile file;

  @Before
  public void setUp() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    project = workspace.getRoot().getProject( "test.project" );
    project.create( null );
    project.open( null );
    file = project.getFile( "/test.js" );
    file.create( new ByteArrayInputStream( "test".getBytes() ), true, null );
  }

  @After
  public void tearDown() throws CoreException {
    if( project.exists() ) {
      project.delete( true, null );
    }
  }

  @Test
  public void addBuilder() throws CoreException, IOException {
    boolean result = BuilderUtil.addBuilderToProject( project, BUILDER_ID );

    assertTrue( result );
    IFile metadata = project.getFile( ".project" );
    assertTrue( readContent( metadata ).contains( BUILDER_ID ) );
  }

  @Test
  public void addBuilderTwice() throws CoreException {
    BuilderUtil.addBuilderToProject( project, BUILDER_ID );

    boolean result = BuilderUtil.addBuilderToProject( project, BUILDER_ID );

    assertFalse( result );
  }

  @Test
  public void removeBuilder() throws CoreException, IOException {
    BuilderUtil.addBuilderToProject( project, BUILDER_ID );

    boolean result = BuilderUtil.removeBuilderFromProject( project, BUILDER_ID );

    assertTrue( result );
    IFile metadata = project.getFile( ".project" );
    assertFalse( readContent( metadata ).contains( BUILDER_ID ) );
  }

  @Test
  public void removeNonExistingBuilder() throws CoreException {
    boolean result = BuilderUtil.removeBuilderFromProject( project, BUILDER_ID );

    assertFalse( result );
  }

  private static String readContent( IFile resource ) throws CoreException, IOException {
    String string = null;
    InputStream inputStream = resource.getContents();
    if( inputStream != null ) {
      try {
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
        StringBuffer buffer = new StringBuffer();
        String line = reader.readLine();
        while( line != null ) {
          buffer.append( line );
          line = reader.readLine();
        }
        string = buffer.toString();
      } finally {
        inputStream.close();
      }
    }
    return string;
  }

}
