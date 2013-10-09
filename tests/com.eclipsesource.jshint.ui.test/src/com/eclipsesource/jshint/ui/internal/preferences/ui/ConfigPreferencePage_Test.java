/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesMock;

import static com.eclipsesource.jshint.ui.test.TestUtil.deleteProject;
import static org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class ConfigPreferencePage_Test {

  private Preferences preferences;
  private Shell shell;
  private ConfigPreferencePage page;
  private IProject project;

  @Before
  public void setUp() {
    preferences = spy( new PreferencesMock( "test" ) );
    project = mock( IProject.class );
    when( Boolean.valueOf( project.isAccessible() ) ).thenReturn( Boolean.TRUE );
    shell = new Shell();
    shell.open();
    page = new ConfigPreferencePage() {
      @Override
      Preferences getPreferences() {
        return preferences;
      }
      @Override
      IProject[] getProjects() {
        return new IProject[] { project };
      }
    };
  }

  @After
  public void tearDown() {
    shell.close();
    deleteProject( project );
  }

  @Test
  public void getDescription() {
    assertEquals( "Global JSHint configuration", page.getDescription() );
  }

  @Test
  public void createContents_createsConfigText() {
    page.createContents( shell );

    assertNotNull( getConfigText() );
  }

  @Test
  public void configText_initilizedFromConfig() {
    preferences.put( "options", "foo:true" );
    new OptionsPreferences( preferences ).setConfig( "{ \"bar\": true }" );

    page.createContents( shell );

    assertEquals( "{ \"bar\": true }", getConfigText().getText() );
  }

  @Test
  public void configText_initializedFromOldConfigIfConfigMissing() {
    preferences.put( "options", "foo:true" );

    page.createContents( shell );

    assertEquals( "{\n  \"foo\": true\n}", getConfigText().getText() );
  }

  @Test
  public void configText_isEditable() {
    new OptionsPreferences( preferences ).setConfig( "foo" );

    page.createContents( shell );

    assertTrue( getConfigText().widget.getEditable() );
  }

  @Test
  public void performDefaults_resetsConfigText() {
    page.createContents( shell );
    getConfigText().setText( "foo bar" );

    page.performDefaults();

    assertEquals( OptionsPreferences.DEFAULT_CONFIG, getConfigText().getText() );
  }

  /////////

  @Test
  public void performOk_savesPreferences() throws BackingStoreException {
    page.createContents( shell );
    getConfigText().setText( "{ \"changed\": true }" );

    page.performOk();

    verify( preferences ).put( "config", "{ \"changed\": true }" );
    verify( preferences ).flush();
  }

  @Test
  public void performOk_doesNotSavePreferencesIfUnchanged() throws BackingStoreException {
    new OptionsPreferences( preferences ).setConfig( "{ \"foo\": true }" );
    page.createContents( shell );

    page.performOk();

    verify( preferences, never() ).flush();
  }

  @Test
  public void performOk_triggersRebuildIfPrefsChanged() throws CoreException {
    page.createContents( shell );
    getConfigText().setText( "{ \"changed\": true }" );

    page.performOk();

    verify( project ).build( CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  @Test
  public void performOk_doesNotTriggerRebuildIfPrefsUnchanged() throws CoreException {
    page.createContents( shell );

    page.performOk();

    verify( project, never() ).build( CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  @Test
  public void performOk_doesNotTriggerRebuildIfOnlyWhitespaceAdded() throws CoreException {
    new OptionsPreferences( preferences ).setConfig( "{\"foo\":true}" );
    page.createContents( shell );
    getConfigText().setText( "{ \"foo\" : true }" );

    page.performOk();

    verify( project, never() ).build( CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  @Test
  public void performOk_doesNotFailWhenInitialConfigWasInvalid() {
    new OptionsPreferences( preferences ).setConfig( "-invalid-" );
    page.createContents( shell );
    getConfigText().setText( "{ \"changed\" : true }" );

    boolean result = page.performOk();

    assertTrue( result );
  }

  @Test
  public void validationError_onStartup() {
    new OptionsPreferences( preferences ).setConfig( "-invalid-" );

    page.createContents( shell );

    assertTrue( page.getErrorMessage().startsWith( "Syntax error" ) );
    assertFalse( page.isValid() );
  }

  @Test
  public void validationError_onConfigTextChange() {
    page.createContents( shell );

    getConfigText().setText( "-invalid-" );

    assertTrue( page.getErrorMessage().startsWith( "Syntax error" ) );
    assertFalse( page.isValid() );
  }

  @Test
  public void validationError_clearedOnConfigTextChange() {
    page.createContents( shell );
    getConfigText().setText( "-invalid-" );

    getConfigText().setText( "{}" );

    assertNull( page.getErrorMessage() );
    assertTrue( page.isValid() );
  }

  private SWTBotStyledText getConfigText() {
    return new SWTBot( shell ).styledText();
  }

}
