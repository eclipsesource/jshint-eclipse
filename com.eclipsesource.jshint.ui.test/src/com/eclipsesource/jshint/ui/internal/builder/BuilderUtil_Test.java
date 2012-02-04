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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.test.TestUtil;


public class BuilderUtil_Test {

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
    boolean result = BuilderUtil.addBuilderToProject( project, TestUtil.BUILDER_ID );

    assertTrue( result );
    IFile metadata = project.getFile( ".project" );
    assertTrue( TestUtil.readContent( metadata ).contains( TestUtil.BUILDER_ID ) );
  }

  @Test
  public void addBuilderTwice() throws CoreException {
    BuilderUtil.addBuilderToProject( project, TestUtil.BUILDER_ID );

    boolean result = BuilderUtil.addBuilderToProject( project, TestUtil.BUILDER_ID );

    assertFalse( result );
  }

  @Test
  public void removeBuilder() throws CoreException, IOException {
    BuilderUtil.addBuilderToProject( project, TestUtil.BUILDER_ID );

    boolean result = BuilderUtil.removeBuilderFromProject( project, TestUtil.BUILDER_ID );

    assertTrue( result );
    IFile metadata = project.getFile( ".project" );
    assertFalse( TestUtil.readContent( metadata ).contains( TestUtil.BUILDER_ID ) );
  }

  @Test
  public void removeNonExistingBuilder() throws CoreException {
    boolean result = BuilderUtil.removeBuilderFromProject( project, TestUtil.BUILDER_ID );

    assertFalse( result );
  }

}
