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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Layout;


public class FillLayoutConfig {

  private final FillLayout layout;

  FillLayoutConfig() {
    layout = new FillLayout();
  }

  Layout getLayout() {
    return layout;
  }

  public FillLayoutConfig type( int type ) {
    layout.type = type;
    return this;
  }

  public FillLayoutConfig vertical() {
    layout.type = SWT.VERTICAL;
    return this;
  }

  public FillLayoutConfig horizontal() {
    layout.type = SWT.HORIZONTAL;
    return this;
  }

  public FillLayoutConfig margin( int allEdges ) {
    layout.marginWidth = allEdges;
    layout.marginHeight = allEdges;
    return this;
  }

  public FillLayoutConfig margin( int leftAndRight, int topAndBottom ) {
    layout.marginWidth = leftAndRight;
    layout.marginHeight = topAndBottom;
    return this;
  }

  public FillLayoutConfig spacing( int spacing ) {
    layout.spacing = spacing;
    return this;
  }

}
