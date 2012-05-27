/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ralf - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.test.TestUtil;

import static com.eclipsesource.jshint.ui.test.TestUtil.array;
import static com.eclipsesource.jshint.ui.test.TestUtil.list;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ResourceSelector_Test {

  private IProject project;
  private EnablementPreferences preferences;
  private ResourceSelector selector;

  @Before
  public void setUp() {
    project = TestUtil.createProject( "test" );
    preferences = new EnablementPreferences( PreferencesFactory.getProjectPreferences( project ) );
    selector = new ResourceSelector( project );
  }

  @After
  public void tearDown() {
    TestUtil.deleteProject( project );
  }

  @Test
  public void isProjectIncluded_falseByDefault() {
    assertFalse( selector.isProjectIncluded() );
  }

  @Test
  public void isProjectIncluded_trueWithIncludePaths() {
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isProjectIncluded() );
  }

  @Test
  public void isIncluded_project_falseByDefault() {
    assertFalse( selector.isIncluded( project ) );
  }

  @Test
  public void isIncluded_project_trueWithIncludePaths() {
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isIncluded( project ) );
  }

  @Test
  public void isIncluded_folder_falseByDefault() {
    IFolder folder = TestUtil.createFolder( project, "foo" );

    assertFalse( selector.isIncluded( folder ) );
  }

  @Test
  public void isIncluded_folder_directlyIncluded() {
    IFolder folder = TestUtil.createFolder( project, "foo" );
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isIncluded( folder ) );
  }

  @Test
  public void isIncluded_folder_parentIncluded() {
    TestUtil.createFolder( project, "foo" );
    IFolder folder = TestUtil.createFolder( project, "foo/bar" );
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isIncluded( folder ) );
  }

  @Test
  public void isIncluded_folder_childIncluded() {
    IFolder folder = TestUtil.createFolder( project, "foo" );
    TestUtil.createFolder( project, "foo/bar" );
    preferences.setIncludePatterns( list( "foo/bar" ) );

    assertTrue( selector.isIncluded( folder ) );
  }

  @Test
  public void isIncluded_file_falseByDefault() {
    IFile file = TestUtil.createFile( project, "test.js", "content" );

    assertFalse( selector.isIncluded( file ) );
  }

  @Test
  public void isIncluded_file_parentIncluded() {
    TestUtil.createFolder( project, "foo" );
    IFile file = TestUtil.createFile( project, "foo/test.js", "content" );
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isIncluded( file ) );
  }

  @Test
  public void isIncluded_file_grandParentIncluded() {
    TestUtil.createFolder( project, "foo" );
    TestUtil.createFolder( project, "foo/bar" );
    IFile file = TestUtil.createFile( project, "foo/bar/test.js", "content" );
    preferences.setIncludePatterns( list( "foo" ) );

    assertTrue( selector.isIncluded( file ) );
  }

  @Test
  public void isIncluded_file_wrongFileExtension() {
    TestUtil.createFolder( project, "foo" );
    IFile file = TestUtil.createFile( project, "foo/test.txt", "content" );
    preferences.setIncludePatterns( list( "foo" ) );

    assertFalse( selector.isIncluded( file ) );
  }

  @Test
  public void understandPathSegments() {
    TestUtil.createFolder( project, "foo" );
    IFolder folder = TestUtil.createFolder( project, "foo/bar" );
    IFile file = TestUtil.createFile( project, "foo/bar/test.txt", "content" );

    assertArrayEquals( array( "foo", "bar" ), folder.getProjectRelativePath().segments() );
    assertArrayEquals( array( "foo", "bar", "test.txt" ), file.getProjectRelativePath().segments() );
  }

}
