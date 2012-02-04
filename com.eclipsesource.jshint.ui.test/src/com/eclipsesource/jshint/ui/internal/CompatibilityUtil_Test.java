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
package com.eclipsesource.jshint.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.properties.ProjectPreferences;
import com.eclipsesource.jshint.ui.test.TestUtil;


public class CompatibilityUtil_Test {
  private IProject project;

  @Before
  public void setUp() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    project = workspace.getRoot().getProject( "test" );
    project.create( null );
    project.open( null );
  }

  @After
  public void tearDown() throws CoreException {
    if( project.exists() ) {
      project.delete( true, null );
    }
  }

  @Test
  public void updateObsoleteBuilder() throws Exception {
    BuilderUtil.addBuilderToProject( project, TestUtil.OLD_BUILDER_ID );

    CompatibilityUtil.fixObsoleteMetadataInProjects();

    IFile projectFile = project.getFile( "/.project" );
    assertTrue( TestUtil.readContent( projectFile ).contains( TestUtil.BUILDER_ID ) );
    assertFalse( TestUtil.readContent( projectFile ).contains( TestUtil.OLD_BUILDER_ID ) );
  }

  @Test
  public void updateObsoletePrefs() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.OLD_SETTINGS_FILE );

    CompatibilityUtil.fixObsoleteMetadataInProjects();

    IFolder settingsFolder = project.getFolder( TestUtil.SETTINGS_FOLDER_PATH );
    assertFalse( settingsFolder.getFile( TestUtil.OLD_SETTINGS_FILE ).exists() );
    assertTrue( settingsFolder.getFile( TestUtil.NEW_SETTINGS_FILE ).exists() );
    String newContent = TestUtil.readContent( settingsFolder.getFile( TestUtil.NEW_SETTINGS_FILE ) );
    assertTrue( newContent.contains( "enabled=true" ) );
  }


  @Test
  public void fallbackToOldPrefs() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.OLD_SETTINGS_FILE );

    CompatibilityUtil.fixObsoleteMetadataInProjects();

    ProjectPreferences prefs = new ProjectPreferences( project );
    assertEquals( true, prefs.getEnabled() );
    assertEquals( true, prefs.getExcluded( project.getFile( "js/test.js" ) ) );
    assertEquals( false, prefs.getExcluded( project.getFile( "js/foo.js" ) ) );
    assertEquals( "org: true, com: false", prefs.getGlobals() );
    assertEquals( "bitwise: true, curly: true, eqnull: true", prefs.getOptions() );
  }

}
