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

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import static com.eclipsesource.jshint.ui.test.TestUtil.list;
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
    assertTrue( prefs.getIncludePatterns().isEmpty() );
    assertTrue( prefs.getExcludePatterns().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void getExcludePatterns_default() {
    assertTrue( prefs.getExcludePatterns().isEmpty() );
  }

  @Test
  public void setExcludePatterns_unchanged() {
    prefs.setExcludePatterns( Collections.<String>emptyList() );

    assertTrue( prefs.getExcludePatterns().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcludePatterns_emptyList() {
    prefs.setExcludePatterns( list( "/foo" ) );
    prefs.clearChanged();

    prefs.setExcludePatterns( Collections.<String>emptyList() );

    assertTrue( prefs.getExcludePatterns().isEmpty() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setExcludePatterns_reset() {
    prefs.setExcludePatterns( list( "/foo" ) );
    prefs.clearChanged();

    prefs.setExcludePatterns( Collections.<String>emptyList() );

    assertTrue( prefs.hasChanged() );
    assertTrue( isEmpty( node ) );
  }

  @Test
  public void setExcludePatterns_emptyPatternIgnored() {
    prefs.setExcludePatterns( list( "" ) );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setExcludePatterns_multiplePaths() {
    prefs.setExcludePatterns( list( "/foo", "/bar", "" ) );

    assertTrue( prefs.getExcludePatterns().contains( "/foo" ) );
    assertTrue( prefs.getExcludePatterns().contains( "/bar" ) );
    assertFalse( prefs.getExcludePatterns().contains( "" ) );
  }

  @Test
  public void getIncludePatterns_default() {
    assertTrue( prefs.getIncludePatterns().isEmpty() );
  }

  @Test
  public void setIncludePatterns_unchanged() {
    prefs.setIncludePatterns( Collections.<String>emptyList() );

    assertTrue( prefs.getIncludePatterns().isEmpty() );
    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncludePatterns_writeThrough() {
    prefs.setIncludePatterns( list( "/foo" ) );

    assertTrue( new EnablementPreferences( node ).getIncludePatterns().contains( "/foo" ) );
  }

  @Test
  public void setIncludePatterns_emptyList() {
    prefs.setIncludePatterns( list( "/foo" ) );
    prefs.clearChanged();

    prefs.setIncludePatterns( Collections.<String>emptyList() );

    assertTrue( prefs.getIncludePatterns().isEmpty() );
    assertTrue( prefs.hasChanged() );
  }

  @Test
  public void setIncludePatterns_emptyPatternIgnored() {
    prefs.setIncludePatterns( list( "" ) );

    assertFalse( prefs.hasChanged() );
  }

  @Test
  public void setIncludePatterns_multiplePaths() {
    prefs.setIncludePatterns( list( "/foo", "/bar", "" ) );

    assertTrue( prefs.getIncludePatterns().contains( "/foo" ) );
    assertTrue( prefs.getIncludePatterns().contains( "/bar" ) );
    assertFalse( prefs.getIncludePatterns().contains( "" ) );
  }

  @Test
  public void clearChanged() {
    prefs.setExcludePatterns( list( "/foo" ) );

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
}
