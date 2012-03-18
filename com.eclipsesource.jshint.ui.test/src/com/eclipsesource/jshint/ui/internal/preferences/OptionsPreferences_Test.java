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

    assertFalse( prefs.getProjectSpecific() );
    assertEquals( "", prefs.getGlobals() );
    assertEquals( "", prefs.getOptions() );
  }

  @Test
  public void setProjectSpecificOptions() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setProjectSpecific( true );

    assertTrue( prefs.getProjectSpecific() );
    assertTrue( prefs.hasChanged() );
    assertTrue( new OptionsPreferences( node ).getProjectSpecific() );
  }

  @Test
  public void setProjectSpecificOptions_unchanged() {
    OptionsPreferences prefs = new OptionsPreferences( node );

    prefs.setProjectSpecific( false );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setProjectSpecificOptions_reset() throws BackingStoreException {
    OptionsPreferences prefs = new OptionsPreferences( node );
    prefs.setProjectSpecific( true );
    prefs.clearChanged();

    prefs.setProjectSpecific( false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
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
  public void setGlobals_reset() throws BackingStoreException {
    OptionsPreferences prefs = new OptionsPreferences( node );
    prefs.setGlobals( "foo" );
    prefs.clearChanged();

    prefs.setGlobals( "" );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
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
  public void setOptions_reset() throws BackingStoreException {
    OptionsPreferences prefs = new OptionsPreferences( node );
    prefs.setOptions( "foo" );
    prefs.clearChanged();

    prefs.setOptions( "" );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void clearChanges() {
    OptionsPreferences prefs = new OptionsPreferences( node );
    prefs.setOptions( "foo" );

    prefs.clearChanged();

    assertFalse( prefs.hasChanged() );
  }

}
