/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.eclipsesource.jshint.ui.test.TestUtil.*;
import static org.junit.Assert.*;


public class ResourceSelector_Test {

  private IProject project;
  private EnablementPreferences preferences;
  private IFolder src;
  private IFile test_js;
  private IFile test_txt;
  private IFile src_test_js;
  private IFile bin_test_js;

  @Before
  public void setUp() {
    project = createProject( "test" );
    src = createFolder( project, "src" );
    createFolder( project, "bin" );
    test_js = createFile( project, "test.js", "content" );
    test_txt = createFile( project, "test.txt", "content" );
    src_test_js = createFile( project, "src/test.js", "content" );
    bin_test_js = createFile( project, "bin/test.js", "content" );
    preferences = new EnablementPreferences( PreferencesFactory.getProjectPreferences( project ) );
  }

  @After
  public void tearDown() {
    deleteProject( project );
  }

  @Test
  public void allowVisitProject_falseByDefault() {
    ResourceSelector selector = new ResourceSelector( project );

    assertFalse( selector.allowVisitProject() );
  }

  @Test
  public void allowVisitProject_trueWithIncludePaths() {
    preferences.setIncludePatterns( list( "foo" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitProject() );
  }

  @Test
  public void allowVisitFolder_falseByDefault() {
    IFolder folder = createFolder( project, "foo" );
    ResourceSelector selector = new ResourceSelector( project );

    assertFalse( selector.allowVisitFolder( folder ) );
  }

  @Test
  public void allowVisitFolder_trueWhenDirectlyIncluded() {
    preferences.setIncludePatterns( list( "/src/" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFolder( src ) );
  }

  @Test
  public void allowVisitFolder_trueWhenChildIncluded() {
    preferences.setIncludePatterns( list( "/src/foo/" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFolder( src ) );
  }

  @Test
  public void allowVisitFolder_trueWhenExcluded() {
    // true because sub-folders are not excluded
    preferences.setIncludePatterns( list( "*" ) );
    preferences.setExcludePatterns( list( "/src/" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFolder( src ) );
  }

  @Test
  public void allowVisitFile_falseByDefault() {
    ResourceSelector selector = new ResourceSelector( project );

    assertFalse( selector.allowVisitFile( test_js ) );
    assertFalse( selector.allowVisitFile( test_txt ) );
    assertFalse( selector.allowVisitFile( src_test_js ) );
  }

  @Test
  public void allowVisitFile_parentIncluded() {
    preferences.setIncludePatterns( list( "/src/" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFile( src_test_js ) );
    assertFalse( selector.allowVisitFile( test_js ) );
  }

  @Test
  public void allowVisitFile_fileTypeIncluded() {
    preferences.setIncludePatterns( list( "//*.js" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFile( test_js ) );
    assertTrue( selector.allowVisitFile( src_test_js ) );
    assertFalse( selector.allowVisitFile( test_txt ) );
  }

  @Test
  public void allowVisitFile_directoryAndFileType() {
    preferences.setIncludePatterns( list( "/src/*.js" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFile( src_test_js ) );
    assertFalse( selector.allowVisitFile( bin_test_js ) );
    assertFalse( selector.allowVisitFile( test_js ) );
  }

  @Test
  public void allowVisitFile_parentExcluded() {
    preferences.setIncludePatterns( list( "//*.js" ) );
    preferences.setExcludePatterns( list( "/bin/" ) );
    ResourceSelector selector = new ResourceSelector( project );

    assertTrue( selector.allowVisitFile( src_test_js ) );
    assertFalse( selector.allowVisitFile( bin_test_js ) );
  }

  @Test
  public void understandPathSegments() {
    createFolder( project, "foo" );
    IFolder folder = createFolder( project, "foo/bar" );
    IFile file = createFile( project, "foo/bar/test.txt", "content" );

    assertArrayEquals( array( "foo", "bar" ), folder.getProjectRelativePath().segments() );
    assertArrayEquals( array( "foo", "bar", "test.txt" ), file.getProjectRelativePath().segments() );
  }

}
