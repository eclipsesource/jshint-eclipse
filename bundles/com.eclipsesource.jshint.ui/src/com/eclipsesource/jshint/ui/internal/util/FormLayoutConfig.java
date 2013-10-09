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

import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Layout;


public class FormLayoutConfig {

  private final FormLayout layout;

  FormLayoutConfig() {
    this.layout = new FormLayout();
  }

  Layout getLayout() {
    return layout;
  }

  public FormLayoutConfig margin( int allEdges ) {
    margin( allEdges, allEdges, allEdges, allEdges );
    return this;
  }

  public FormLayoutConfig margin( int leftAndRight, int topAndBottom ) {
    margin( topAndBottom, leftAndRight, topAndBottom, leftAndRight );
    return this;
  }

  public FormLayoutConfig margin( int top, int right, int bottom, int left ) {
    layout.marginTop = top;
    layout.marginRight = right;
    layout.marginBottom = bottom;
    layout.marginLeft = left;
    return this;
  }

  public FormLayoutConfig spacing( int spacing ) {
    layout.spacing = spacing;
    return this;
  }

}
