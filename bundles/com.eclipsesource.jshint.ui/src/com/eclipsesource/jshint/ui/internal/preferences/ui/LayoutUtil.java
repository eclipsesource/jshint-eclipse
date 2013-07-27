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
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class LayoutUtil {

  public static Composite createMainComposite( Composite parent ) {
    return createMainComposite( parent, 1 );
  }

  public static Composite createMainComposite( Composite parent, int columns ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( createGridLayoutWithoutMargins( columns, false ) );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    return composite;
  }

  public static GridLayout createGridLayoutWithoutMargins( int numColumns,
                                                           boolean makeColumnsEqualWidth )
  {
    GridLayout layout = new GridLayout( numColumns, makeColumnsEqualWidth );
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    return layout;
  }

  public static GridData createGridDataFillWithMinSize( int minWidth, int minHeight ) {
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
    layoutData.widthHint = minWidth;
    layoutData.heightHint = minHeight;
    return layoutData;
  }

  public static GridData createGridDataHFillWithMinWidth( int minWidth ) {
    GridData layoutData = new GridData( SWT.FILL, SWT.BEGINNING, true, false );
    layoutData.widthHint = minWidth;
    return layoutData;
  }

}
