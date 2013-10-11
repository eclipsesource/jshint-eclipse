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
package com.eclipsesource.jshint.ui.internal.util;

import org.junit.Test;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import static org.junit.Assert.*;


public class JsonUtil_Test {

  @Test
  public void jsonEquals() {
    assertFalse( JsonUtil.jsonEquals( null, null ) );
    assertFalse( JsonUtil.jsonEquals( null, "{}" ) );
    assertFalse( JsonUtil.jsonEquals( "{}", null ) );
    assertFalse( JsonUtil.jsonEquals( "{}", "" ) );
    assertFalse( JsonUtil.jsonEquals( "", "" ) );
    assertFalse( JsonUtil.jsonEquals( "{\"a\":true}", "{\"a\":false}" ) );
    assertTrue( JsonUtil.jsonEquals( "{}", "{}" ) );
    assertTrue( JsonUtil.jsonEquals( "{\"a\":true}", "{\"a\":true}" ) );
  }

  @Test
  public void prettyPrint_emptyObject() {
    assertEquals( "{\n  \n}", JsonUtil.prettyPrint( new JsonObject() ) );
  }

  @Test
  public void prettyPrint_withNestedArray() {
    JsonObject object = new JsonObject()
      .add( "foo", true )
      .add( "bar", new JsonArray().add( 23 ).add( 42 ) );

    assertEquals( "{\n  \"foo\": true,\n  \"bar\": [23, 42]\n}", JsonUtil.prettyPrint( object ) );
  }

}
