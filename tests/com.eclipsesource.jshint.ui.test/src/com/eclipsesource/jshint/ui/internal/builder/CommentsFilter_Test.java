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
package com.eclipsesource.jshint.ui.internal.builder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CommentsFilter_Test {

  @Test
  public void filter_emptyImput() {
    assertEquals( "",
          filter( "" ) );
  }

  @Test
  public void filter_lineComment_atStart() {
    assertEquals( "     ",
          filter( "//foo" ) );
  }

  @Test
  public void filter_lineComment_embedded() {
    assertEquals( "one\ntwo     \nthreee\n",
          filter( "one\ntwo//foo\nthreee\n" ) );
  }

  @Test
  public void filter_blockComment_inSingleLine() {
    assertEquals( "one\ntwo       bar\nthreee\n",
          filter( "one\ntwo/*foo*/bar\nthreee\n" ) );
  }

  @Test
  public void filter_blockComment_overMultipleLines() {
    assertEquals( "one\ntwo     \n     threee\n",
          filter( "one\ntwo/*foo\nbar*/threee\n" ) );
  }

  private static String filter( String input ) {
    return new CommentsFilter( input ).toString();
  }

}
