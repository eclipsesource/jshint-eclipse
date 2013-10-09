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
import org.eclipse.swt.layout.GridData;


public class GridDataConfig {

  private final GridData data;

  GridDataConfig() {
    data = new GridData();
    data.verticalAlignment = SWT.CENTER;
  }

  Object getLayoutData() {
    return data;
  }

  public GridDataConfig align( int horizontal, int vertical ) {
    data.horizontalAlignment = horizontal;
    data.verticalAlignment = vertical;
    return this;
  }

  public GridDataConfig align( int horizontalAlignment,
                               int verticalAlignment,
                               boolean grabExcessHorizontal,
                               boolean grabExcessVertical )
  {
    data.horizontalAlignment = horizontalAlignment;
    data.verticalAlignment = verticalAlignment;
    data.grabExcessHorizontalSpace = grabExcessHorizontal;
    data.grabExcessVerticalSpace = grabExcessVertical;
    return this;
  }

  public GridDataConfig fillBoth() {
    data.horizontalAlignment = SWT.FILL;
    data.verticalAlignment = SWT.FILL;
    data.grabExcessHorizontalSpace = true;
    data.grabExcessVerticalSpace = true;
    return this;
  }

  public GridDataConfig fillHorizontal() {
    data.horizontalAlignment = SWT.FILL;
    data.grabExcessHorizontalSpace = true;
    return this;
  }

  public GridDataConfig fillVertical() {
    data.verticalAlignment = SWT.FILL;
    data.grabExcessVerticalSpace = true;
    return this;
  }

  public GridDataConfig span( int horizontal, int vertical ) {
    data.horizontalSpan = horizontal;
    data.verticalSpan = vertical;
    return this;
  }

  public GridDataConfig indent( int horizontal, int vertical ) {
    data.horizontalIndent = horizontal;
    data.verticalIndent = vertical;
    return this;
  }

  public GridDataConfig sizeHint( int width, int height ) {
    data.widthHint = width;
    data.heightHint = height;
    return this;
  }

  public GridDataConfig widthHint( int width ) {
    data.widthHint = width;
    return this;
  }

  public GridDataConfig heightHint( int height ) {
    data.widthHint = height;
    return this;
  }

  public GridDataConfig minimalSize( int width, int height ) {
    data.minimumWidth = width;
    data.minimumHeight = height;
    return this;
  }

  public GridDataConfig minimalWidth( int width ) {
    data.minimumWidth = width;
    return this;
  }

  public GridDataConfig minimalHeight( int height ) {
    data.minimumHeight = height;
    return this;
  }

  public GridDataConfig exclude( boolean exclude ) {
    data.exclude = exclude;
    return this;
  }

}
