/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PathEncoder_Test {

  @Test
  public void encodeEmptyList() {
    assertEquals( "", PathEncoder.encodePaths( createList() ) );
  }

  @Test
  public void encodeEmptyString() {
    assertEquals( "", PathEncoder.encodePaths( createList( "" ) ) );
  }

  @Test
  public void encodeSingleString() {
    assertEquals( "foo", PathEncoder.encodePaths( createList( "foo" ) ) );
  }

  @Test
  public void encodeMixedStrings() {
    assertEquals( "foo:bar", PathEncoder.encodePaths( createList( "foo", "", "bar" ) ) );
  }

  @Test
  public void decodeEmptyList() {
    assertListEquals( createList(), PathEncoder.decodePaths( "" ) );
  }

  @Test
  public void decodeSimpleString() {
    assertListEquals( createList( "foo" ), PathEncoder.decodePaths( "foo" ) );
  }

  @Test
  public void decodeMultipleStrings() {
    assertListEquals( createList( "foo", "bar" ), PathEncoder.decodePaths( "foo:bar" ) );
  }

  private static List<String> createList( String ... strings ) {
    ArrayList<String> result = new ArrayList<String>();
    for( String string : strings ) {
      result.add( string );
    }
    return result;
  }

  private static void assertListEquals( List<String> list1, List<String> list2 ) {
    assertEquals( "lists size differs", list1.size(), list2.size() );
    for( int i = 0; i < list1.size(); i++ ) {
      assertEquals( list1.get( i ), list2.get( i ) );
    }
  }

}
