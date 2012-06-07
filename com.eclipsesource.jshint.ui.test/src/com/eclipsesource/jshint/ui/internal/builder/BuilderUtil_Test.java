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

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.test.TestUtil;

import static com.eclipsesource.jshint.ui.test.TestUtil.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BuilderUtil_Test {

  private IProject project;

  @Before
  public void setUp() {
    project = createProject( "test" );
    createFile( project, "/test.js", "test" );
  }

  @After
  public void tearDown() {
    deleteProject( project );
  }

  @Test
  public void addBuilder() throws CoreException, IOException {
    boolean result = BuilderUtil.addBuilderToProject( project, TestUtil.BUILDER_ID );

    assertTrue( result );
    IFile metadata = project.getFile( ".project" );
    assertTrue( readContent( metadata ).contains( TestUtil.BUILDER_ID ) );
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
    assertFalse( readContent( metadata ).contains( TestUtil.BUILDER_ID ) );
  }

  @Test
  public void removeNonExistingBuilder() throws CoreException {
    boolean result = BuilderUtil.removeBuilderFromProject( project, TestUtil.BUILDER_ID );

    assertFalse( result );
  }

}
