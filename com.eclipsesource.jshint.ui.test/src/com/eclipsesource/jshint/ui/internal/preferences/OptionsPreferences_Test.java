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

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.test.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OptionsPreferences_Test {

  private PreferencesMock node;

  @Before
  public void setUp() {
    node = new PreferencesMock( "test" );
  }

  @Test
  public void defaultPrefsForEmptyProject() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    assertEquals( "", prefs.getGlobals() );
    assertEquals( "", prefs.getOptions() );
  }

  @Test
  public void setGlobals() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setGlobals( "foo" );

    assertEquals( "foo", prefs.getGlobals() );
    assertTrue( prefs.hasChanged() );
    assertEquals( prefs.getGlobals(), new OptionsPreferences( node ).getGlobals() );
  }

  @Test
  public void setGlobals_unchanged() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setGlobals( prefs.getGlobals() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setOptions() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setOptions( "foo" );

    assertEquals( "foo", prefs.getOptions() );
    assertTrue( prefs.hasChanged() );
    assertEquals( prefs.getOptions(), new OptionsPreferences( node ).getOptions() );
  }

  @Test
  public void setOptions_unchanged() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setOptions( prefs.getOptions() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void prefsFromExampleSettingsFile() throws Exception {
    IProject project = createTestProject();
    TestUtil.createExampleSettingsFile( project, TestUtil.NEW_SETTINGS_FILE );

    Preferences node = PreferencesFactory.getProjectPreferences( project );
    OptionsPreferences prefs = new OptionsPreferences( node );

    assertEquals( "org: true, com: false", prefs.getGlobals() );
    assertEquals( "bitwise: true, curly: true, eqnull: true", prefs.getOptions() );
    assertFalse( prefs.hasChanged() );
  }

  private static IProject createTestProject() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProject project = workspace.getRoot().getProject( "test" );
    if( project.exists() ) {
      project.delete( true, null );
    }
    project.create( null );
    project.open( null );
    IFile file = project.getFile( "/test.js" );
    file.create( new ByteArrayInputStream( "test".getBytes() ), true, null );
    return project;
  }

}
