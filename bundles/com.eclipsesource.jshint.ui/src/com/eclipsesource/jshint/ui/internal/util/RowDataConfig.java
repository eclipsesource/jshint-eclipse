/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.util;

import org.eclipse.swt.layout.RowData;


public class RowDataConfig {

  private final RowData data;

  RowDataConfig() {
    data = new RowData();
  }

  RowData getLayoutData() {
    return data;
  }

  public RowDataConfig size( int width, int height ) {
    data.width = width;
    data.height = height;
    return this;
  }

  public RowDataConfig width( int width ) {
    data.width = width;
    return this;
  }

  public RowDataConfig height( int height ) {
    data.height = height;
    return this;
  }

  public RowDataConfig exclude( boolean exclude ) {
    data.exclude = exclude;
    return this;
  }

}
