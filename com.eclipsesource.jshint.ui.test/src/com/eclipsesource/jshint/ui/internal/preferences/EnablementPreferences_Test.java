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

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class EnablementPreferences_Test {

  private Preferences node;
  private EnablementPreferences prefs;

  @Before
  public void setUp() {
    node = new PreferencesMock( "test" );
    prefs = new EnablementPreferences( node );
  }

  @Test
  public void defaults() {
    assertFalse( prefs.getEnabled() );
    assertTrue( prefs.getExcluded().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled() {
    prefs.setEnabled( true );

    assertTrue( prefs.getEnabled() );
    assertTrue( prefs.hasChanged() );
    assertTrue( new EnablementPreferences( node ).getEnabled() );
  }

  @Test
  public void setEnabled_unchanged() {
    prefs.setEnabled( false );

    assertFalse( prefs.getEnabled() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled_reset() throws BackingStoreException {
    prefs.setEnabled( true );
    prefs.clearChanged();

    prefs.setEnabled( false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void setExcluded() {
    prefs.setExcluded( "/test", true );

    assertTrue( prefs.getExcluded( "/test" ) );
    assertTrue( prefs.hasChanged() );
    assertTrue( new EnablementPreferences( node ).getExcluded( "/test" ) );
  }

  @Test
  public void setExcluded_emptyList() {
    prefs.setExcluded( "/foo", true );
    prefs.clearChanged();

    prefs.setExcluded( Collections.<String>emptyList() );

    assertTrue( prefs.getExcluded().isEmpty() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setExcluded_multiplePaths() {
    prefs.setExcluded( "/foo", true );
    prefs.setExcluded( "/bar", true );

    assertTrue( prefs.getExcluded( "/foo" ) );
    assertTrue( prefs.getExcluded( "/bar" ) );
    assertFalse( prefs.getExcluded( "/baz" ) );
  }

  @Test
  public void setExcluded_unchanged() {
    prefs.setExcluded( "/test", false );

    assertFalse( prefs.getExcluded( "/test" ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcluded_reset() throws BackingStoreException {
    prefs.setExcluded( "/test", true );
    prefs.clearChanged();

    prefs.setExcluded( "/test", false );

    assertTrue( prefs.hasChanged() );
    assertEquals( 0, node.keys().length );
  }

  @Test
  public void getExcluded_emptyPathIsAlwaysFalse() {
    prefs.setExcluded( "", true );

    boolean excluded = prefs.getExcluded( "" );

    assertFalse( excluded );
  }

  @Test
  public void getExcluded_emptyPathIsAlwaysFalse1() {
    prefs.setExcluded( "", true );

    List<String> excluded = prefs.getExcluded();

    assertTrue( excluded.isEmpty() );
  }

  @Test
  public void getExcluded_emptyPathIsAlwaysFalse2() {
    prefs.setExcluded( "", true );
    prefs.setExcluded( "/foo", true );

    List<String> excluded = prefs.getExcluded();

    assertEquals( 1, excluded.size() );
  }

  @Test
  public void clearChanged() {
    prefs.setExcluded( "", true );

    prefs.clearChanged();

    assertFalse( prefs.hasChanged() );
  }

}
