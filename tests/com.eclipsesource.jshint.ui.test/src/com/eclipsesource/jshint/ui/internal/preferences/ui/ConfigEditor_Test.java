/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class ConfigEditor_Test {

  private Shell shell;

  @Before
  public void setUp() {
    shell = new Shell();
    shell.open();
  }

  @After
  public void tearDown() {
    shell.close();
  }

  @Test
  public void createsStyledText() {
    ConfigEditor editor = new ConfigEditor( shell );

    assertSame( shell, editor.getControl().getParent() );
  }

  @Test
  public void styledText_isEditable() {
    ConfigEditor editor = new ConfigEditor( shell );

    assertTrue( editor.getControl().getEditable() );
  }

  @Test
  public void styledText_hasMonospaceFont() {
    ConfigEditor editor = new ConfigEditor( shell );

    GC gc = new GC( editor.getControl() );
    assertEquals( gc.stringExtent( "i" ), gc.stringExtent( "m" ) );
  }

  @Test
  public void setEnabled_setsEditableAndForeground() {
    ConfigEditor editor = new ConfigEditor( shell );
    Color gray = shell.getDisplay().getSystemColor( SWT.COLOR_GRAY );

    editor.setEnabled( false );

    assertFalse( editor.getControl().getEditable() );
    assertEquals( gray, editor.getControl().getForeground() );
  }

  @Test
  public void setEnabled_restoresEditableAndForeground() {
    ConfigEditor editor = new ConfigEditor( shell );
    Color defaultForeground = editor.getControl().getForeground();
    editor.setEnabled( false );

    editor.setEnabled( true );

    assertTrue( editor.getControl().getEditable() );
    assertEquals( defaultForeground, editor.getControl().getForeground() );
  }

  @Test
  public void getText_returnsTrimmedTextWithTrailingLinebreak() {
    ConfigEditor editor = new ConfigEditor( shell );
    editor.setText( "  foo bar  " );

    String text = editor.getText();

    assertEquals( "foo bar", text );
  }

  @Test
  public void validatesOnType() {
    final AtomicReference<String> errorMessage = new AtomicReference<String>();
    ConfigEditor editor = new ConfigEditor( shell ) {
      @Override
      protected void handleError( String string ) {
        errorMessage.set( string );
      }
    };

    editor.getControl().setText( "-invalid-" );

    assertTrue( errorMessage.get().startsWith( "Syntax error" ) );
  }

}
