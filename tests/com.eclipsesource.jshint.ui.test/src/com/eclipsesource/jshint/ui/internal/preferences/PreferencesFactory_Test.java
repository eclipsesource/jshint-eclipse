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

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import static com.eclipsesource.jshint.ui.test.TestUtil.createProject;
import static com.eclipsesource.jshint.ui.test.TestUtil.deleteProject;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;


public class PreferencesFactory_Test {
  private IProject project;

  @Before
  public void setUp() {
    project = createProject( "test" );
  }

  @After
  public void tearDown() {
    deleteProject( project );
  }

  @Test
  public void getProjectPreferences() {
    Preferences preferences = PreferencesFactory.getProjectPreferences( project );

    assertNotNull( preferences );
    assertSame( preferences, PreferencesFactory.getProjectPreferences( project ) );
  }

  @Test
  public void getWorkspacePreferences() {
    Preferences preferences = PreferencesFactory.getWorkspacePreferences();

    assertNotNull( preferences );
    assertSame( preferences, PreferencesFactory.getWorkspacePreferences() );
  }

}
