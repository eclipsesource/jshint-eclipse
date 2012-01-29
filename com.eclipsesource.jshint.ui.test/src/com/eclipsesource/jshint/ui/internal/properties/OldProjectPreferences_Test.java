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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class OldProjectPreferences_Test {
  private static final String SETTINGS_FOLDER_PATH = "/.settings";
  private static final String OLD_SETTINGS_FILE = "com.eclipsesource.jshint.prefs";
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
  public void noPrefsExistForEmptyProject() throws Exception {
    OldProjectPreferences prefs = new OldProjectPreferences( project );

    assertFalse( prefs.exists() );
  }

  @Test
  public void defaultsForEmptyProject() throws Exception {
    OldProjectPreferences prefs = new OldProjectPreferences( project );

    assertEquals( "false", prefs.getEnabled() );
    assertEquals( "[]", prefs.getExcluded().toString() );
    assertEquals( "", prefs.getGlobals() );
    assertEquals( "", prefs.getOptions() );
  }

  @Test
  public void findsExamplePrefs() throws Exception {
    createExampleSettingsFile( OLD_SETTINGS_FILE );

    OldProjectPreferences prefs = new OldProjectPreferences( project );

    assertTrue( prefs.exists() );
    assertEquals( "true", prefs.getEnabled() );
    assertEquals( "[js/test.js, target]", prefs.getExcluded().toString() );
    assertEquals( "org: true, com: false", prefs.getGlobals() );
    assertEquals( "bitwise: true, curly: true, eqnull: true", prefs.getOptions() );
  }

  @Test
  public void deleteExistingPrefs() throws Exception {
    createExampleSettingsFile( OLD_SETTINGS_FILE );

    OldProjectPreferences prefs = new OldProjectPreferences( project );
    prefs.delete();

    assertFalse( new OldProjectPreferences( project ).exists() );
    assertFalse( getSettingsFolder().getFile( OLD_SETTINGS_FILE ).exists() );
  }

  private void createExampleSettingsFile( String settingsFileName ) throws CoreException,
      IOException
  {
    IFolder folder = getSettingsFolder();
    IFile settingsFile = folder.getFile( settingsFileName );
    InputStream inputStream = getClass().getResourceAsStream( settingsFileName );
    if( inputStream != null ) {
      try {
        settingsFile.create( inputStream, true, null );
        assertTrue( settingsFile.exists() );
      } finally {
        inputStream.close();
      }
    }
  }

  private IFolder getSettingsFolder() throws CoreException {
    IFolder folder = project.getFolder( SETTINGS_FOLDER_PATH );
    if( !folder.exists() ) {
      folder.create( true, true, null );
    }
    return folder;
  }
}
