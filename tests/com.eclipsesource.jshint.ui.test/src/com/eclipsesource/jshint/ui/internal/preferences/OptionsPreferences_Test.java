/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
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

import static org.junit.Assert.*;


public class OptionsPreferences_Test {

  private Preferences node;
  private OptionsPreferences prefs;

  @Before
  public void setUp() {
    node = new PreferencesMock( "test" );
    prefs = new OptionsPreferences( node );
  }

  @Test
  public void getNode() {
    assertSame( node, prefs.getNode() );
  }

  @Test
  public void defaultPrefsForEmptyProject() {
    assertFalse( prefs.getProjectSpecific() );
    assertEquals( "{\n  \n}", prefs.getConfig() );
  }

  @Test
  public void setProjectSpecific() {
    prefs.setProjectSpecific( true );

    assertTrue( prefs.getProjectSpecific() );
    assertTrue( prefs.hasChanged() );
    assertTrue( new OptionsPreferences( node ).getProjectSpecific() );
  }

  @Test
  public void setProjectSpecific_unchanged() {
    prefs.setProjectSpecific( false );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setProjectSpecific_reset() throws BackingStoreException {
    prefs.setProjectSpecific( true );
    prefs.clearChanged();

    prefs.setProjectSpecific( false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void setConfig() {
    prefs.setConfig( "foo" );

    assertEquals( "foo", prefs.getConfig() );
    assertTrue( prefs.hasChanged() );
    assertEquals( prefs.getConfig(), new OptionsPreferences( node ).getConfig() );
  }

  @Test
  public void setConfig_unchanged() {
    prefs.setConfig( "{ \"foo\": true }" );
    prefs.clearChanged();

    prefs.setConfig( prefs.getConfig() );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void getConfig_fallsBackToOldPreferences() {
    prefs.getNode().put( "options", "foo: true, bar: false" );
    prefs.getNode().put( "globals", "org: true, com: false" );

    String expected = "{\n"
        + "  \"foo\": true,\n"
        + "  \"bar\": false,\n"
        + "  \"globals\": {\n"
        + "    \"org\": true,\n"
        + "    \"com\": false\n"
        + "  }\n"
        + "}";
    assertEquals( expected, prefs.getConfig() );
  }

  @Test
  public void clearChanges() {
    prefs.setConfig( "foo" );

    prefs.clearChanged();

    assertFalse( prefs.hasChanged() );
  }

}
