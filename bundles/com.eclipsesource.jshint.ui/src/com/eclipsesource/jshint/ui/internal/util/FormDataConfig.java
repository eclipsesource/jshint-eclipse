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

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;


public class FormDataConfig {

  private final FormData data;

  FormDataConfig() {
    data = new FormData();
  }

  Object getLayoutData() {
    return data;
  }

  public FormDataConfig top( int percentage ) {
    data.top = new FormAttachment( percentage );
    return this;
  }

  public FormDataConfig top( int percentage, int offset ) {
    data.top = new FormAttachment( percentage, offset );
    return this;
  }

  public FormDataConfig top( Control control ) {
    data.top = new FormAttachment( control );
    return this;
  }

  public FormDataConfig top( Control control, int offset ) {
    data.top = new FormAttachment( control, offset );
    return this;
  }

  public FormDataConfig top( Control control, int offset, int alignment ) {
    data.top = new FormAttachment( control, offset, alignment );
    return this;
  }

  public FormDataConfig right( int percentage ) {
    data.right = new FormAttachment( percentage );
    return this;
  }

  public FormDataConfig right( int percentage, int offset ) {
    data.right = new FormAttachment( percentage, offset );
    return this;
  }

  public FormDataConfig right( Control control ) {
    data.right = new FormAttachment( control );
    return this;
  }

  public FormDataConfig right( Control control, int offset ) {
    data.right = new FormAttachment( control, offset );
    return this;
  }

  public FormDataConfig right( Control control, int offset, int alignment ) {
    data.right = new FormAttachment( control, offset, alignment );
    return this;
  }

  public FormDataConfig bottom( int percentage ) {
    data.bottom = new FormAttachment( percentage );
    return this;
  }

  public FormDataConfig bottom( int percentage, int offset ) {
    data.bottom = new FormAttachment( percentage, offset );
    return this;
  }

  public FormDataConfig bottom( Control control ) {
    data.bottom = new FormAttachment( control );
    return this;
  }

  public FormDataConfig bottom( Control control, int offset ) {
    data.bottom = new FormAttachment( control, offset );
    return this;
  }

  public FormDataConfig bottom( Control control, int offset, int alignment ) {
    data.bottom = new FormAttachment( control, offset, alignment );
    return this;
  }

  public FormDataConfig left( int percentage ) {
    data.left = new FormAttachment( percentage );
    return this;
  }

  public FormDataConfig left( int percentage, int offset ) {
    data.left = new FormAttachment( percentage, offset );
    return this;
  }

  public FormDataConfig left( Control control ) {
    data.left = new FormAttachment( control );
    return this;
  }

  public FormDataConfig left( Control control, int offset ) {
    data.left = new FormAttachment( control, offset );
    return this;
  }

  public FormDataConfig left( Control control, int offset, int alignment ) {
    data.left = new FormAttachment( control, offset, alignment );
    return this;
  }

  public FormDataConfig width( int width ) {
    data.width = width;
    return this;
  }

  public FormDataConfig height( int height ) {
    data.height = height;
    return this;
  }

}
