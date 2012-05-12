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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

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
    assertTrue( prefs.getIncludedPaths().isEmpty() );
    assertTrue( prefs.getExcludedPaths().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled() {
    prefs.setEnabled( true );

    assertTrue( prefs.getEnabled() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setEnabled_unchanged() {
    prefs.setEnabled( false );

    assertFalse( prefs.getEnabled() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setEnabled_writeThrough() {
    prefs.setEnabled( true );

    assertTrue( new EnablementPreferences( node ).getEnabled() );
  }

  @Test
  public void setEnabled_reset() {
    prefs.setEnabled( true );
    prefs.clearChanged();

    prefs.setEnabled( false );

    assertTrue( prefs.hasChanged() );
    assertTrue( isEmpty( node ) );
  }

  @Test
  public void getExcluded_allPathsFalseByDefault() {
    assertFalse( prefs.getExcluded( "" ) );
    assertFalse( prefs.getExcluded( "/foo" ) );
  }

  @Test
  public void setExcluded() {
    prefs.setExcluded( "/foo", true );

    assertTrue( prefs.getExcluded( "/foo" ) );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setExcluded_writeThrough() {
    prefs.setExcluded( "/foo", true );

    assertTrue( new EnablementPreferences( node ).getExcluded( "/foo" ) );
  }

  @Test
  public void setExcluded_emptyPathIgnored() {
    prefs.setExcluded( "", true );

    assertFalse( prefs.hasChanged() );
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
    prefs.setExcluded( "/foo", false );

    assertFalse( prefs.getExcluded( "/foo" ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcluded_reset() {
    prefs.setExcluded( "/foo", true );
    prefs.clearChanged();

    prefs.setExcluded( "/foo", false );

    assertTrue( prefs.hasChanged() );
    assertTrue( isEmpty( node ) );
  }

  @Test
  public void getExcludedPaths_default() {
    assertTrue( prefs.getExcludedPaths().isEmpty() );
  }

  @Test
  public void setExcludedPaths_unchanged() {
    prefs.setExcludedPaths( Collections.<String>emptyList() );

    assertTrue( prefs.getExcludedPaths().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcludedPaths_emptyList() {
    prefs.setExcluded( "/foo", true );
    prefs.clearChanged();

    prefs.setExcludedPaths( Collections.<String>emptyList() );

    assertTrue( prefs.getExcludedPaths().isEmpty() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void getIncluded_allPathsFalseByDefault() {
    assertFalse( prefs.getIncluded( "" ) );
    assertFalse( prefs.getIncluded( "/foo" ) );
  }

  @Test
  public void setIncluded() {
    prefs.setIncluded( "/foo", true );

    assertTrue( prefs.getIncluded( "/foo" ) );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setIncluded_writeThrough() {
    prefs.setIncluded( "/foo", true );

    assertTrue( new EnablementPreferences( node ).getIncluded( "/foo" ) );
  }

  @Test
  public void setIncluded_emptyPathIgnored() {
    prefs.setIncluded( "", true );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncluded_multiplePaths() {
    prefs.setIncluded( "/foo", true );
    prefs.setIncluded( "/bar", true );

    assertTrue( prefs.getIncluded( "/foo" ) );
    assertTrue( prefs.getIncluded( "/bar" ) );
    assertFalse( prefs.getIncluded( "/baz" ) );
  }

  @Test
  public void setIncluded_unchanged() {
    prefs.setIncluded( "/foo", false );

    assertFalse( prefs.getIncluded( "/foo" ) );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncluded_reset() {
    prefs.setIncluded( "/foo", true );
    prefs.clearChanged();

    prefs.setIncluded( "/foo", false );

    assertTrue( prefs.hasChanged() );
    assertTrue( isEmpty( node ) );
  }

  @Test
  public void getIncludedPaths_default() {
    assertTrue( prefs.getIncludedPaths().isEmpty() );
  }

  @Test
  public void setIncludedPaths_unchanged() {
    prefs.setIncludedPaths( Collections.<String>emptyList() );

    assertTrue( prefs.getIncludedPaths().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncludedPaths_emptyList() {
    prefs.setIncluded( "/foo", true );
    prefs.clearChanged();

    prefs.setIncludedPaths( Collections.<String>emptyList() );

    assertTrue( prefs.getIncludedPaths().isEmpty() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setIncludedPaths_emptyPathIgnored() {
    prefs.setIncludedPaths( createList( "" ) );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncludedPaths_multiplePaths() {
    prefs.setIncludedPaths( createList( "/foo", "/bar", "" ) );

    assertTrue( prefs.getIncludedPaths().contains( "/foo" ) );
    assertTrue( prefs.getIncludedPaths().contains( "/bar" ) );
    assertFalse( prefs.getIncludedPaths().contains( "" ) );
  }

  @Test
  public void getIncluded_derivedFromIncludedPaths() {
    prefs.setIncludedPaths( createList( "/foo", "/bar" ) );

    assertTrue( prefs.getIncluded( "/foo" ) );
    assertTrue( prefs.getIncluded( "/bar" ) );
    assertFalse( prefs.getIncluded( "/baz" ) );
  }

  @Test
  public void setIncluded_setsIncludedPaths() {
    prefs.setIncluded( "/foo", true );
    prefs.setIncluded( "/bar", true );

    assertTrue( prefs.getIncludedPaths().contains( "/foo" ) );
    assertTrue( prefs.getIncludedPaths().contains( "/bar" ) );
  }

  @Test
  public void clearChanged() {
    prefs.setExcluded( "/foo", true );

    prefs.clearChanged();

    assertFalse( prefs.hasChanged() );
  }

  private static boolean isEmpty( Preferences node ) {
    try {
      return node.keys().length == 0;
    } catch( BackingStoreException exception ) {
      throw new RuntimeException( exception );
    }
  }

  private List<String> createList( String ... strings ) {
    ArrayList<String> list = new ArrayList<String>();
    for( String string : strings ) {
      list.add( string );
    }
    return list;
  }
}
