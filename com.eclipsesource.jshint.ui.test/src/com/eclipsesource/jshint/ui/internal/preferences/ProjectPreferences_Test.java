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
package com.eclipsesource.jshint.ui.internal.preferences;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.test.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProjectPreferences_Test {
  private IProject project;
  private IFile file;
  private Preferences node;

  @Before
  public void setUp() {
    project = TestUtil.createProject( "test" );
    file = TestUtil.createFile( project, "/test.js", "test content" );
    node = PreferencesFactory.getProjectPreferences( project );
  }

  @After
  public void tearDown() {
    TestUtil.deleteProject( project );
  }

  @Test
  public void defaultPrefsForEmptyProject() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    assertFalse( prefs.getIncluded( EnablementPreferences.getResourcePath( project.getFile( "js/test.js" ) ) ) );
  }

  @Test
  public void enablementPrefsFromExampleSettingsFile() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );

    EnablementPreferences prefs = new EnablementPreferences( node );

    assertTrue( prefs.getIncluded( EnablementPreferences.getResourcePath( project.getFile( "js/test.js" ) ) ) );
    assertFalse( prefs.getIncluded( EnablementPreferences.getResourcePath( project.getFile( "js/foo.js" ) ) ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void optionPrefsFromExampleSettingsFile() throws Exception {
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );

    OptionsPreferences optionPrefs = new OptionsPreferences( node );

    assertEquals( "org: true, com: false", optionPrefs.getGlobals() );
    assertEquals( "bitwise: true, curly: true, eqnull: true", optionPrefs.getOptions() );
    assertFalse( optionPrefs.hasChanged() );
  }

  @Test
  public void resourcePaths() {
    assertEquals( "", EnablementPreferences.getResourcePath( project ) );
    assertEquals( "test.js", EnablementPreferences.getResourcePath( file ) );
  }

}
