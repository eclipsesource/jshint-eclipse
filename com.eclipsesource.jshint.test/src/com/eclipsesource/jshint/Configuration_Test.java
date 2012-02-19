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
package com.eclipsesource.jshint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class Configuration_Test {

  private Configuration configuration;

  @Before
  public void setUp() {
    configuration = new Configuration();
  }

  @Test
  public void emptyAfterCreation() {
    assertEquals( "{\"indent\": 1}", configuration.getOptionsString() );
  }

  @Test
  public void addOneOption() {
    configuration.addOption( "foo", true );

    assertEquals( "{\"indent\": 1, \"foo\": true}", configuration.getOptionsString() );
  }

  @Test
  public void addSeparateOptions() {
    configuration.addOption( "foo", true );
    configuration.addOption( "bar", false );

    assertEquals( "{\"indent\": 1, \"foo\": true, \"bar\": false}",
                  configuration.getOptionsString() );
  }

  @Test
  public void addSameOptionTwice() {
    configuration.addOption( "foo", true );
    configuration.addOption( "foo", false );

    assertEquals( "{\"indent\": 1, \"foo\": false}", configuration.getOptionsString() );
  }

  @Test
  public void addSamePredefTwice() {
    configuration.addPredefined( "foo", true );
    configuration.addPredefined( "foo", false );

    assertEquals( "{\"predef\": {\"foo\": false}, \"indent\": 1}", configuration.getOptionsString() );
  }

  @Test
  public void addOnePredef() {
    configuration.addPredefined( "foo", true );

    assertEquals( "{\"predef\": {\"foo\": true}, \"indent\": 1}", configuration.getOptionsString() );
  }

  @Test
  public void addSeparatePredefs() {
    configuration.addPredefined( "foo", true );
    configuration.addPredefined( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true, \"bar\": false}, \"indent\": 1}",
                  configuration.getOptionsString() );
  }

  @Test
  public void addPredefAndOption() {
    configuration.addPredefined( "foo", true );
    configuration.addOption( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true}, \"indent\": 1, \"bar\": false}",
                  configuration.getOptionsString() );
  }

}
