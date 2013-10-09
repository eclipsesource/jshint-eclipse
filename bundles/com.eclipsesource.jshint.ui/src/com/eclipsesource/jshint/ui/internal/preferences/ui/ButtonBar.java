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
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.rowLayout;


public class ButtonBar extends Composite {

  public ButtonBar( Composite parent, int style ) {
    super( parent, style );
    rowLayout( this ).vertical().fill( true ).spacing( 3 );
  }

  public Button addButton( String text, Listener listener ) {
    Button button = new Button( this, SWT.PUSH );
    button.setText( text );
    button.addListener( SWT.Selection, listener );
    return button;
  }

}
