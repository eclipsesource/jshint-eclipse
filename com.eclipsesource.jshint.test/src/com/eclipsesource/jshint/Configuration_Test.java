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
    assertEquals( "{}", configuration.toJson() );
  }

  @Test
  public void addOneOption() {
    configuration.addOption( "foo", true );

    assertEquals( "{\"foo\": true}", configuration.toJson() );
  }

  @Test
  public void addSeparateOptions() {
    configuration.addOption( "foo", true );
    configuration.addOption( "bar", false );

    assertEquals( "{\"foo\": true, \"bar\": false}", configuration.toJson() );
  }

  @Test
  public void addSeparateOptionsWithChaining() {
    configuration.addOption( "foo", true ).addOption( "bar", false );

    assertEquals( "{\"foo\": true, \"bar\": false}", configuration.toJson() );
  }

  @Test
  public void addSameOptionTwice() {
    configuration.addOption( "foo", true );
    configuration.addOption( "foo", false );

    assertEquals( "{\"foo\": false}", configuration.toJson() );
  }

  @Test
  public void addSamePredefTwice() {
    configuration.addPredefined( "foo", true );
    configuration.addPredefined( "foo", false );

    assertEquals( "{\"predef\": {\"foo\": false}}", configuration.toJson() );
  }

  @Test
  public void addOnePredef() {
    configuration.addPredefined( "foo", true );

    assertEquals( "{\"predef\": {\"foo\": true}}", configuration.toJson() );
  }

  @Test
  public void addSeparatePredefsWithChaining() {
    configuration.addPredefined( "foo", true ).addPredefined( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true, \"bar\": false}}", configuration.toJson() );
  }

  @Test
  public void addSeparatePredefs() {
    configuration.addPredefined( "foo", true );
    configuration.addPredefined( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true, \"bar\": false}}", configuration.toJson() );
  }

  @Test
  public void addPredefAndOption() {
    configuration.addPredefined( "foo", true );
    configuration.addOption( "bar", false );

    assertEquals( "{\"predef\": {\"foo\": true}, \"bar\": false}", configuration.toJson() );
  }

}
