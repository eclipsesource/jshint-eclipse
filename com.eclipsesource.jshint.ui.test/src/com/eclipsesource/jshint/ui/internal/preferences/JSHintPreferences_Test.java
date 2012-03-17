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

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class JSHintPreferences_Test {

  @Before
  public void setUp() throws BackingStoreException {
    Preferences node = PreferencesFactory.getWorkspacePreferences();
    node.clear();
  }

  @Test
  public void defaultPrefsForEmptyProject() {
    JSHintPreferences prefs = new JSHintPreferences();

    assertFalse( prefs.getUseCustomLib() );
    assertEquals( "", prefs.getCustomLibPath() );
  }

  @Test
  public void resetToDefaults() throws Exception {
    JSHintPreferences prefs = new JSHintPreferences();
    prefs.setUseCustomLib( true );
    prefs.setCustomLibPath( "foo" );
    prefs.save();

    prefs.resetToDefaults();

    assertTrue( prefs.hasChanged() );
    assertFalse( prefs.getUseCustomLib() );
    assertEquals( "", prefs.getCustomLibPath() );
  }

  @Test
  public void setUseCustomLib() {
    JSHintPreferences prefs = new JSHintPreferences();

    prefs.setUseCustomLib( true );

    assertTrue( prefs.hasChanged() );
    assertTrue( prefs.getUseCustomLib() );
    assertFalse( new JSHintPreferences().getUseCustomLib() );
  }

  @Test
  public void setUseCustomLib_unchanged() {
    JSHintPreferences prefs = new JSHintPreferences();

    prefs.setUseCustomLib( prefs.getUseCustomLib() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setCustomLibPath() {
    JSHintPreferences prefs = new JSHintPreferences();

    prefs.setCustomLibPath( "foo" );

    assertTrue( prefs.hasChanged() );
    assertEquals( "foo", prefs.getCustomLibPath() );
    assertEquals( "", new JSHintPreferences().getCustomLibPath() );
  }

  @Test
  public void setCustomLibPath_unchanged() {
    JSHintPreferences prefs = new JSHintPreferences();

    prefs.setCustomLibPath( prefs.getCustomLibPath() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void save() throws Exception {
    JSHintPreferences prefs = new JSHintPreferences();
    prefs.setCustomLibPath( "foo" );

    prefs.save();

    assertFalse( prefs.hasChanged() );
    assertEquals( "foo", new JSHintPreferences().getCustomLibPath() );
  }

}
