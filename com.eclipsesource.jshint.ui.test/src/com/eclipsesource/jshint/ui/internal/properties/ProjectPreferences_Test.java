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
package com.eclipsesource.jshint.ui.internal.properties;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.test.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProjectPreferences_Test {
  private IProject project;
  private IFile file;

  @Before
  public void setUp() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    project = workspace.getRoot().getProject( "test" );
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
  public void defaultPrefsForEmptyProject() {
    ProjectPreferences prefs = new ProjectPreferences( project );

    assertFalse( prefs.getEnabled() );
    assertFalse( prefs.getExcluded( project.getFile( "js/test.js" ) ) );
    assertEquals( "", prefs.getGlobals() );
    assertEquals( "", prefs.getOptions() );
  }

  @Test
  public void prefsFromExampleSettingsFile() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );

    ProjectPreferences prefs = new ProjectPreferences( project );

    assertTrue( prefs.getEnabled() );
    assertTrue( prefs.getExcluded( project.getFile( "js/test.js" ) ) );
    assertFalse( prefs.getExcluded( project.getFile( "js/foo.js" ) ) );
    assertEquals( "org: true, com: false", prefs.getGlobals() );
    assertEquals( "bitwise: true, curly: true, eqnull: true", prefs.getOptions() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcluded() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setExcluded( file, true );

    assertTrue( prefs.getExcluded( file ) );
    assertTrue( prefs.hasChanged() );
    assertTrue( new ProjectPreferences( project ).getExcluded( file ) );
  }

  @Test
  public void setExcluded_unchanged() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setExcluded( file, false );

    assertFalse( prefs.getExcluded( file ) );
    assertFalse( prefs.hasChanged() );
    assertFalse( new ProjectPreferences( project ).getExcluded( file ) );
  }

  @Test
  public void setEnabled() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setEnabled( false );

    assertFalse( prefs.getEnabled() );
    assertTrue( prefs.hasChanged() );
    assertFalse( new ProjectPreferences( project ).getEnabled() );
  }

  @Test
  public void setEnabled_unchanged() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setEnabled( prefs.getEnabled() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setGlobals() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setGlobals( "foo" );

    assertEquals( "foo", prefs.getGlobals() );
    assertTrue( prefs.hasChanged() );
    assertEquals( prefs.getGlobals(), new ProjectPreferences( project ).getGlobals() );
  }

  @Test
  public void setGlobals_unchanged() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setGlobals( prefs.getGlobals() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setOptions() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setOptions( "foo" );

    assertEquals( "foo", prefs.getOptions() );
    assertTrue( prefs.hasChanged() );
    assertEquals( prefs.getOptions(), new ProjectPreferences( project ).getOptions() );
  }

  @Test
  public void setOptions_unchanged() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );
    ProjectPreferences prefs = new ProjectPreferences( project );

    prefs.setOptions( prefs.getOptions() );

    assertFalse( prefs.hasChanged() );
  }

}
