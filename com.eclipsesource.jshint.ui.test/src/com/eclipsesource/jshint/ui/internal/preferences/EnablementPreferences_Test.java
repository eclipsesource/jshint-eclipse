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


public class EnablementPreferences_Test {

  private Preferences node;

  @Before
  public void setUp() {
    node = new PreferencesMock( "test" );
  }

  @Test
  public void defaults() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    assertFalse( prefs.getEnabled() );
    assertFalse( prefs.getExcluded( "/test" ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    prefs.setEnabled( true );

    assertTrue( prefs.getEnabled() );
    assertTrue( prefs.hasChanged() );
    assertTrue( new EnablementPreferences( node ).getEnabled() );
  }

  @Test
  public void setEnabled_unchanged() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    prefs.setEnabled( false );

    assertFalse( prefs.getEnabled() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled_reset() throws BackingStoreException {
    EnablementPreferences prefs = new EnablementPreferences( node );
    prefs.setEnabled( true );
    prefs.clearChanged();

    prefs.setEnabled( false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void setExcluded() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    prefs.setExcluded( "/test", true );

    assertTrue( prefs.getExcluded( "/test" ) );
    assertTrue( prefs.hasChanged() );
    assertTrue( new EnablementPreferences( node ).getExcluded( "/test" ) );
  }

  @Test
  public void setExcluded_multiplePaths() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    prefs.setExcluded( "/foo", true );
    prefs.setExcluded( "/bar", true );

    assertTrue( prefs.getExcluded( "/foo" ) );
    assertTrue( prefs.getExcluded( "/bar" ) );
    assertFalse( prefs.getExcluded( "/baz" ) );
  }

  @Test
  public void setExcluded_unchanged() {
    EnablementPreferences prefs = new EnablementPreferences( node );

    prefs.setExcluded( "/test", false );

    assertFalse( prefs.getExcluded( "/test" ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcluded_reset() throws BackingStoreException {
    EnablementPreferences prefs = new EnablementPreferences( node );
    prefs.setExcluded( "/test", true );
    prefs.clearChanged();

    prefs.setExcluded( "/test", false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void getExcluded_emptyPathIsAlwaysFalse() {
    EnablementPreferences prefs = new EnablementPreferences( node );
    prefs.setExcluded( "", true );

    boolean excluded = prefs.getExcluded( "" );

    assertFalse( excluded );
  }

  @Test
  public void clearChanged() {
    EnablementPreferences prefs = new EnablementPreferences( node );
    prefs.setExcluded( "", true );

    prefs.clearChanged();

    assertFalse( prefs.hasChanged() );
  }

}
