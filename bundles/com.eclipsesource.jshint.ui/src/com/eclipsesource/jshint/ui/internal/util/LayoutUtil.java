/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class LayoutUtil {

  public static FillLayoutConfig fillLayout( Composite composite ) {
    FillLayoutConfig config = new FillLayoutConfig();
    composite.setLayout( config.getLayout() );
    return config;
  }

  public static FormLayoutConfig formLayout( Composite composite ) {
    FormLayoutConfig config = new FormLayoutConfig();
    composite.setLayout( config.getLayout() );
    return config;
  }

  public static RowLayoutConfig rowLayout( Composite composite ) {
    RowLayoutConfig config = new RowLayoutConfig();
    composite.setLayout( config.getLayout() );
    return config;
  }

  public static GridLayoutConfig gridLayout( Composite composite ) {
    GridLayoutConfig config = new GridLayoutConfig();
    composite.setLayout( config.getLayout() );
    return config;
  }

  public static FormDataConfig formData( Control control ) {
    FormDataConfig config = new FormDataConfig();
    control.setLayoutData( config.getLayoutData() );
    return config;
  }

  public static GridDataConfig gridData( Control control ) {
    GridDataConfig config = new GridDataConfig();
    control.setLayoutData( config.getLayoutData() );
    return config;
  }

  public static RowDataConfig rowData( Control control ) {
    RowDataConfig config = new RowDataConfig();
    control.setLayoutData( config.getLayoutData() );
    return config;
  }

}
