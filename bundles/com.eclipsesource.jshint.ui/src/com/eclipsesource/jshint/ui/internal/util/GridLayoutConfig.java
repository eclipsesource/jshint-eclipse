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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;


public class GridLayoutConfig {

  private final GridLayout layout;

  GridLayoutConfig() {
    layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;
    layout.horizontalSpacing = 0;
  }

  Layout getLayout() {
    return layout;
  }

  public GridLayoutConfig columns( int columns ) {
    layout.numColumns = columns;
    return this;
  }

  public GridLayoutConfig columns( int columns, boolean equalWidth ) {
    layout.numColumns = columns;
    layout.makeColumnsEqualWidth = equalWidth;
    return this;
  }

  public GridLayoutConfig margin( int allEdges ) {
    margin( allEdges, allEdges, allEdges, allEdges );
    return this;
  }

  public GridLayoutConfig margin( int leftAndRight, int topAndBottom ) {
    margin( topAndBottom, leftAndRight, topAndBottom, leftAndRight );
    return this;
  }

  public GridLayoutConfig margin( int top, int right, int bottom, int left ) {
    layout.marginTop = top;
    layout.marginRight = right;
    layout.marginBottom = bottom;
    layout.marginLeft = left;
    return this;
  }

  public GridLayoutConfig marginTop( int margin ) {
    layout.marginTop = margin;
    return this;
  }

  public GridLayoutConfig marginBottom( int margin ) {
    layout.marginBottom = margin;
    return this;
  }

  public GridLayoutConfig marginLeft( int margin ) {
    layout.marginLeft = margin;
    return this;
  }

  public GridLayoutConfig marginRight( int margin ) {
    layout.marginRight = margin;
    return this;
  }

  public GridLayoutConfig spacing( int spacing ) {
    layout.verticalSpacing = spacing;
    layout.horizontalSpacing = spacing;
    return this;
  }

  public GridLayoutConfig spacing( int horizontal, int vertical ) {
    layout.verticalSpacing = vertical;
    layout.horizontalSpacing = horizontal;
    return this;
  }

}
