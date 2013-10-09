/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class LayoutUtil_Test {

  @Test
  public void fillLayout() {
    Composite composite = mock( Composite.class );

    LayoutUtil.fillLayout( composite );

    FillLayout layout = (FillLayout)getLayout( composite );
    assertEquals( SWT.HORIZONTAL, layout.type );
    assertEquals( 0, layout.marginWidth );
    assertEquals( 0, layout.marginHeight );
    assertEquals( 0, layout.spacing );
  }

  @Test
  public void formLayout() {
    Composite composite = mock( Composite.class );

    LayoutUtil.formLayout( composite );

    FormLayout layout = (FormLayout)getLayout( composite );
    assertEquals( 0, layout.marginLeft );
    assertEquals( 0, layout.marginRight );
    assertEquals( 0, layout.marginTop );
    assertEquals( 0, layout.marginBottom );
    assertEquals( 0, layout.marginWidth );
    assertEquals( 0, layout.marginHeight );
    assertEquals( 0, layout.spacing );
  }

  @Test
  public void gridLayout() {
    Composite composite = mock( Composite.class );

    LayoutUtil.gridLayout( composite );

    GridLayout layout = (GridLayout)getLayout( composite );
    assertEquals( 1, layout.numColumns );
    assertFalse( layout.makeColumnsEqualWidth );
    assertEquals( 0, layout.marginLeft );
    assertEquals( 0, layout.marginRight );
    assertEquals( 0, layout.marginTop );
    assertEquals( 0, layout.marginBottom );
    assertEquals( 0, layout.marginWidth );
    assertEquals( 0, layout.marginHeight );
    assertEquals( 0, layout.horizontalSpacing );
    assertEquals( 0, layout.verticalSpacing );
  }

  @Test
  public void rowLayout() {
    Composite composite = mock( Composite.class );

    LayoutUtil.rowLayout( composite );

    RowLayout layout = (RowLayout)getLayout( composite );
    assertEquals( SWT.HORIZONTAL, layout.type );
    assertFalse( layout.center );
    assertFalse( layout.fill );
    assertFalse( layout.justify );
    assertFalse( layout.pack );
    assertFalse( layout.wrap );
    assertEquals( 0, layout.marginLeft );
    assertEquals( 0, layout.marginRight );
    assertEquals( 0, layout.marginTop );
    assertEquals( 0, layout.marginBottom );
    assertEquals( 0, layout.marginWidth );
    assertEquals( 0, layout.marginHeight );
    assertEquals( 0, layout.spacing );
  }

  @Test
  public void formData() {
    Control control = mock( Control.class );

    LayoutUtil.formData( control );

    FormData data = (FormData)getLayoutData( control );
    assertNull( data.left );
    assertNull( data.right );
    assertNull( data.top );
    assertNull( data.bottom );
    assertEquals( SWT.DEFAULT, data.width );
    assertEquals( SWT.DEFAULT, data.height );
  }

  @Test
  public void gridData() {
    Control control = mock( Control.class );

    LayoutUtil.gridData( control );

    GridData data = (GridData)getLayoutData( control );
    assertEquals( SWT.BEGINNING, data.horizontalAlignment );
    assertEquals( SWT.CENTER, data.verticalAlignment );
    assertFalse( data.grabExcessHorizontalSpace );
    assertFalse( data.grabExcessVerticalSpace );
    assertEquals( 0, data.horizontalIndent );
    assertEquals( 0, data.verticalIndent );
    assertEquals( 1, data.horizontalSpan );
    assertEquals( 1, data.verticalSpan );
    assertEquals( SWT.DEFAULT, data.heightHint );
    assertEquals( SWT.DEFAULT, data.widthHint );
    assertEquals( 0, data.minimumHeight );
    assertEquals( 0, data.minimumWidth );
    assertFalse( data.exclude );
  }

  @Test
  public void rowData() {
    Control control = mock( Control.class );

    LayoutUtil.rowData( control );

    RowData data = (RowData)getLayoutData( control );
    assertEquals( SWT.DEFAULT, data.height );
    assertEquals( SWT.DEFAULT, data.width );
    assertFalse( data.exclude );
  }

  private static Layout getLayout( Composite composite ) {
    ArgumentCaptor<Layout> captor = ArgumentCaptor.forClass( Layout.class );
    verify( composite ).setLayout( captor.capture() );
    return captor.getValue();
  }

  private Object getLayoutData( Control control ) {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass( Object.class );
    verify( control ).setLayoutData( captor.capture() );
    return captor.getValue();
  }

}
