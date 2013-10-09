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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Layout;


public class RowLayoutConfig {

  private final RowLayout layout;

  RowLayoutConfig() {
    layout = new RowLayout();
    layout.marginTop = 0;
    layout.marginRight = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 0;
    layout.spacing = 0;
    layout.wrap = false;
    layout.pack = false;
  }

  Layout getLayout() {
    return layout;
  }

  public RowLayoutConfig type( int type ) {
    layout.type = type;
    return this;
  }

  public RowLayoutConfig vertical() {
    layout.type = SWT.VERTICAL;
    return this;
  }

  public RowLayoutConfig horizontal() {
    layout.type = SWT.HORIZONTAL;
    return this;
  }

  public RowLayoutConfig center( boolean center ) {
    layout.center = center;
    return this;
  }

  public RowLayoutConfig fill( boolean fill ) {
    layout.fill = fill;
    return this;
  }

  public RowLayoutConfig justify( boolean justify ) {
    layout.justify = justify;
    return this;
  }

  public RowLayoutConfig pack( boolean pack ) {
    layout.pack = pack;
    return this;
  }

  public RowLayoutConfig wrap( boolean wrap ) {
    layout.wrap = wrap;
    return this;
  }

  public RowLayoutConfig margin( int allEdges ) {
    layout.marginTop = allEdges;
    layout.marginRight = allEdges;
    layout.marginBottom = allEdges;
    layout.marginLeft = allEdges;
    return this;
  }

  public RowLayoutConfig margin( int leftAndRight, int topAndBottom ) {
    layout.marginTop = topAndBottom;
    layout.marginRight = leftAndRight;
    layout.marginBottom = topAndBottom;
    layout.marginLeft = leftAndRight;
    return this;
  }

  public RowLayoutConfig margin( int top, int right, int bottom, int left ) {
    layout.marginTop = top;
    layout.marginRight = right;
    layout.marginBottom = bottom;
    layout.marginLeft = left;
    return this;
  }

  public RowLayoutConfig marginTop( int margin ) {
    layout.marginTop = margin;
    return this;
  }

  public RowLayoutConfig marginBottom( int margin ) {
    layout.marginBottom = margin;
    return this;
  }

  public RowLayoutConfig marginLeft( int margin ) {
    layout.marginLeft = margin;
    return this;
  }

  public RowLayoutConfig marginRight( int margin ) {
    layout.marginRight = margin;
    return this;
  }

  public RowLayoutConfig spacing( int spacing ) {
    layout.spacing = spacing;
    return this;
  }

}
