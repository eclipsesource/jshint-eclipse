/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;

import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.jshint.ui.test.TestUtil;
import com.eclipsesource.json.JsonObject;

import static com.eclipsesource.jshint.ui.test.TestUtil.createProject;
import static com.eclipsesource.jshint.ui.test.TestUtil.deleteProject;
import static org.junit.Assert.*;


public class ConfigLoader_Test {

  private IProject project;
  private OptionsPreferences workspacePrefs;
  private OptionsPreferences projectPrefs;

  @Before
  public void setUp() {
    project = createProject( "test" );
    workspacePrefs = new OptionsPreferences( PreferencesFactory.getWorkspacePreferences() );
    projectPrefs = new OptionsPreferences( PreferencesFactory.getProjectPreferences( project ) );
  }

  @After
  public void tearDown() throws BackingStoreException {
    deleteProject( project );
    PreferencesFactory.getWorkspacePreferences().clear();
  }

  @Test
  public void usesWorkspaceOptionsByDefault() {
    workspacePrefs.getNode().put( "options", "a: 1, b: 1" );
    createProjectConfig( new JsonObject().add( "b", 2 ).add( "c", 2 ) );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( 1, configuration.get( "a" ).asInt() );
    assertEquals( 1, configuration.get( "b" ).asInt() );
    assertNull( configuration.get( "c" ) );
  }

  @Test
  public void ignoresWorkspaceOptionsIfProjectSpecific() {
    workspacePrefs.getNode().put( "options", "a: 1, b: 1" );
    createProjectConfig( new JsonObject().add( "b", 2 ).add( "c", 2 ) );
    projectPrefs.setProjectSpecific( true );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertNull( configuration.get( "a" ) );
    assertEquals( 2, configuration.get( "b" ).asInt() );
    assertEquals( 2, configuration.get( "c" ).asInt() );
  }

  @Test
  public void usesWorkspaceConfigIfNotProjectSpecific() {
    workspacePrefs.setConfig( "{\"a\": 1, \"b\": 1}" );
    createProjectConfig( new JsonObject().add( "b", 2 ).add( "c", 2 ) );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( 1, configuration.get( "a" ).asInt() );
    assertEquals( 1, configuration.get( "b" ).asInt() );
    assertNull( configuration.get( "c" ) );
  }

  @Test
  public void fallsBackToOldProjectProperties_ifConfigFileMissing() {
    projectPrefs.setProjectSpecific( true );
    projectPrefs.getNode().put( "options", "a: 1" );
    projectPrefs.getNode().put( "globals", "foo: true" );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( 1, configuration.get( "a" ).asInt() );
    assertTrue( configuration.get( "globals" ).asObject().get( "foo" ).asBoolean() );
  }

  @Test
  public void ignoresOldProjectProperties_ifConfigFilePresent() {
    projectPrefs.setProjectSpecific( true );
    projectPrefs.getNode().put( "options", "a: 1, b: 1" );
    createProjectConfig( new JsonObject().add( "b", 2 ).add( "c", 2 ) );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertNull( configuration.get( "a" ) );
    assertEquals( 2, configuration.get( "b" ).asInt() );
    assertEquals( 2, configuration.get( "c" ).asInt() );
  }

  @Test
  public void emptyConfigForProjectsWithoutConfigFileAndProperties() {
    projectPrefs.setProjectSpecific( true );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( new JsonObject(), configuration );
  }

  @Test
  public void filtersCommentsFromProjectConfig() {
    projectPrefs.setConfig( "{\n// \"a\": 1,\n\"b\": 2 /*, \"c\": 3*/}" );
    projectPrefs.setProjectSpecific( true );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( new JsonObject().add( "b", 2 ), configuration );
    assertEquals( Arrays.asList( "b" ), configuration.names() );
  }

  @Test
  public void filtersCommentsFromWorkspaceConfig() {
    workspacePrefs.setConfig( "{\n// \"a\": 1,\n\"b\": 2 /*, \"c\": 3*/}" );

    JsonObject configuration = new ConfigLoader( project ).getConfiguration();

    assertEquals( new JsonObject().add( "b", 2 ), configuration );
    assertEquals( Arrays.asList( "b" ), configuration.names() );
  }

  private void createProjectConfig( JsonObject projectConfig ) {
    TestUtil.createFile( project, ".jshintrc", projectConfig.toString() );
  }

}
