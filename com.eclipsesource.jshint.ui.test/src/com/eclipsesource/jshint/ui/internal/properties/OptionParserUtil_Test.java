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
package com.eclipsesource.jshint.ui.internal.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.properties.OptionParserUtil.Entry;


public class OptionParserUtil_Test {

  @Test( expected = NullPointerException.class )
  public void parseNull() throws Exception {
    OptionParserUtil.parseOptionString( null );
  }

  @Test
  public void parseEmpty() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "" );

    assertTrue( result.isEmpty() );
  }

  @Test
  public void parseSingle() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "foo: true" );

    assertEquals( 1, result.size() );
    assertEquals( "foo", result.get( 0 ).name );
    assertEquals( true, result.get( 0 ).value );
  }

  @Test
  public void parseTwoOptions() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "foo: true, bar: false" );

    assertEquals( 2, result.size() );
    assertEquals( "foo", result.get( 0 ).name );
    assertEquals( true, result.get( 0 ).value );
    assertEquals( "bar", result.get( 1 ).name );
    assertEquals( false, result.get( 1 ).value );
  }

  @Test
  public void parseAdditionalWhitespace() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "\tfoo : true  ,\n\t bar  : false  " );

    assertEquals( 2, result.size() );
    assertEquals( "foo", result.get( 0 ).name );
    assertEquals( true, result.get( 0 ).value );
    assertEquals( "bar", result.get( 1 ).name );
    assertEquals( false, result.get( 1 ).value );
  }

  // TODO check for illegal names
  @Test
  public void parseWhitespaceInName() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "foo bar: true" );

    assertEquals( 1, result.size() );
    assertEquals( "foo bar", result.get( 0 ).name );
    assertEquals( true, result.get( 0 ).value );
  }

  @Test
  public void parseNonBooleanValue() throws Exception {
    List<Entry> result = OptionParserUtil.parseOptionString( "foo: bar" );

    assertEquals( 1, result.size() );
    assertEquals( "foo", result.get( 0 ).name );
    assertEquals( false, result.get( 0 ).value );
  }
}
