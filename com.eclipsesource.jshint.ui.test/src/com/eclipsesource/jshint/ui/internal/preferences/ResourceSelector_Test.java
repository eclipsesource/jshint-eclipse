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

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.test.TestUtil;

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
  public void includeProject_falseByDefault() {
    assertFalse( selector.includeProject() );
  }

  @Test
  public void includeProject_trueWithIncludePaths() {
    preferences.setIncluded( "foo", true );

    assertTrue( selector.includeProject() );
  }

}
