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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;


public class PreferencesFactory_Test {
  private IProject project;

  @Before
  public void setUp() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    project = workspace.getRoot().getProject( "test" );
    if( project.exists() ) {
      project.delete( true, null );
    }
    project.create( null );
    project.open( null );
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
