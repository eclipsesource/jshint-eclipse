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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;

import static com.eclipsesource.jshint.ui.test.TestUtil.*;
import static org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


public class ConfigPropertyPage_Test {

  private ConfigPropertyPage page;
  private Shell shell;
  private IProject project;

  @Before
  public void setUp() {
    project = spy( createProject( "test" ) );
    page = new ConfigPropertyPage() {
      @Override
      protected IResource getResource() {
        return project;
      }
    };
    shell = new Shell();
    shell.open();
  }

  @After
  public void tearDown() throws Exception {
    shell.close();
    getPreferences().clear();
    deleteProject( project );
  }

  @Test
  public void createContents_createsControls() {
    page.createContents( shell );

    assertNotNull( getProjectSpecificCheckbox() );
    assertNotNull( getConfigText() );
  }

  @Test
  public void createContents_doesNotCreateConfigFile() {
    page.createContents( shell );

    assertFalse( getConfigFile().exists() );
  }

  @Test
  public void projectSpecificCheckbox_checkedIfTrue() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );

    page.createContents( shell );

    assertTrue( getProjectSpecificCheckbox().isChecked() );
  }

  @Test
  public void projectSpecificCheckbox_uncheckedIfFalse() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( false );

    page.createContents( shell );

    assertFalse( getProjectSpecificCheckbox().isChecked() );
  }

  @Test
  public void configText_disabledIfNotProjectSpecific() {
    page.createContents( shell );

    assertFalse( getConfigText().widget.getEditable() );
  }

  @Test
  public void configText_enabledIfProjectSpecific() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );

    page.createContents( shell );

    assertTrue( getConfigText().widget.getEditable() );
  }

  @Test
  public void configText_enabledAfterCheckingProjectSpecific() {
    page.createContents( shell );

    getProjectSpecificCheckbox().click();

    assertTrue( getConfigText().widget.getEditable() );
  }

  @Test
  public void configText_disabledAfterUncheckingProjectSpecific() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );

    getProjectSpecificCheckbox().click();

    assertFalse( getConfigText().widget.getEditable() );
  }

  @Test
  public void configText_initializedFromConfigFile() {
    getPreferences().put( "options", "foo:true" );
    createConfigFile( "{ bar: false }" );

    page.createContents( shell );

    assertEquals( "{ bar: false }", getConfigText().getText() );
  }

  @Test
  public void configText_initializedFromOldConfigIfConfigFileMissing() {
    getPreferences().put( "options", "foo:true" );

    page.createContents( shell );

    assertEquals( "{\n  \"foo\": true\n}", getConfigText().getText() );
  }

  @Test
  public void performDefaults_resetsProjectSpecificCheckbox() {
    page.createContents( shell );
    getProjectSpecificCheckbox().select();

    page.performDefaults();

    assertFalse( getProjectSpecificCheckbox().isChecked() );
  }

  @Test
  public void performDefaults_resetsConfigText() {
    page.createContents( shell );
    getConfigText().setText( "foo bar" );

    page.performDefaults();

    assertEquals( OptionsPreferences.DEFAULT_CONFIG, getConfigText().getText() );
  }

  @Test
  public void performOk_savesPreferences() {
    page.createContents( shell );
    getProjectSpecificCheckbox().select();

    page.performOk();

    assertTrue( new OptionsPreferences( getPreferences() ).getProjectSpecific() );
    assertTrue( getSettingsFile().exists() );
  }

  @Test
  public void performOk_doesNotSavePreferencesIfUnchanged() {
    page.createContents( shell );

    page.performOk();

    assertFalse( getSettingsFile().exists() );
  }

  @Test
  public void performOk_savesConfigFile() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );

    page.performOk();

    assertTrue( getConfigFile().exists() );
  }

  @Test
  public void performOk_doesNotSaveConfigFileIfNotProjectSpecific() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );

    page.performOk();

    assertTrue( getConfigFile().exists() );
  }

  @Test
  public void performOk_triggersRebuildIfPrefsChanged() throws CoreException {
    page.createContents( shell );
    getProjectSpecificCheckbox().select();

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
  public void performOk_triggersRebuildIfConfigChanged() throws CoreException {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );
    getConfigText().setText( "{ \"changed\": true }" );

    page.performOk();

    verify( project ).build( CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  @Test
  public void performOk_doesNotTriggerRebuildIfOnlyWhitespaceAdded() throws CoreException {
    page.createContents( shell );
    StyledText configText = getConfigText().widget;
    configText.setText( configText.getText() + " " );

    page.performOk();

    verify( project, never() ).build( CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  @Ignore( "does not work on Eclipse 3.6" )
  @Test
  public void performOk_failsIfWritingFails() throws CoreException {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );
    createConfigFile( "" ).setResourceAttributes( readOnly() );

    boolean result = page.performOk();

    assertFalse( result );
  }

  @Test
  public void performOk_doesNotFailWhenInitialConfigWasInvalid() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    new OptionsPreferences( getPreferences() ).setConfig( "-invalid-" );
    page.createContents( shell );
    getConfigText().setText( "{ \"changed\" : true }" );

    boolean result = page.performOk();

    assertTrue( result );
  }

  @Test
  public void validationError_onStartup() {
    createConfigFile( "-invalid-" );
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );

    page.createContents( shell );

    assertTrue( page.getErrorMessage().startsWith( "Syntax error" ) );
    assertFalse( page.isValid() );
  }

  @Test
  public void validationError_onConfigTextChange() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );

    getConfigText().setText( "-invalid-" );

    assertTrue( page.getErrorMessage().startsWith( "Syntax error" ) );
    assertFalse( page.isValid() );
  }

  @Test
  public void validationError_clearedOnConfigTextChange() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );
    getConfigText().setText( "-invalid-" );

    getConfigText().setText( "{}" );

    assertNull( page.getErrorMessage() );
    assertTrue( page.isValid() );
  }

  @Test
  public void validationError_clearedOnUnsetProjectSpecific() {
    new OptionsPreferences( getPreferences() ).setProjectSpecific( true );
    page.createContents( shell );
    getConfigText().setText( "-invalid-" );

    getProjectSpecificCheckbox().deselect();

    assertNull( page.getErrorMessage() );
    assertTrue( page.isValid() );
  }

  private SWTBotCheckBox getProjectSpecificCheckbox() {
    return new SWTBot( shell ).checkBox();
  }

  private SWTBotStyledText getConfigText() {
    return new SWTBot( shell ).styledText();
  }

  private IFile getSettingsFile() {
    return project.getFile( "/.settings/" + Activator.PLUGIN_ID + ".prefs" );
  }

  private IFile createConfigFile( String content ) {
    return createFile( project, "/.jshintrc", content );
  }

  private IFile getConfigFile() {
    return project.getFile( "/.jshintrc" );
  }

  private Preferences getPreferences() {
    return PreferencesFactory.getProjectPreferences( project );
  }

  private static ResourceAttributes readOnly() {
    ResourceAttributes resourceAttributes = new ResourceAttributes();
    resourceAttributes.setReadOnly( true );
    return resourceAttributes;
  }

}
