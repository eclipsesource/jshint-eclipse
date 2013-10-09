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

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.CommentsFilter;
import com.eclipsesource.jshint.ui.internal.util.IOUtil;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;


public class ConfigEditor {

  private final StyledText styledText;
  private final Color errorBorderColor;
  private int errorOffset;

  public ConfigEditor( Composite parent ) {
    errorBorderColor = new Color( parent.getDisplay(), 230, 0, 0 );
    styledText = new StyledText( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
    styledText.setFont( JFaceResources.getFont( JFaceResources.TEXT_FONT ) );
    styledText.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        validate();
      }
    } );
    styledText.addListener( SWT.Paint, new Listener() {
      public void handleEvent( Event event ) {
        drawError( event.gc );
      }
    } );
    styledText.addListener( SWT.Dispose, new Listener() {
      public void handleEvent( Event event ) {
        dispose();
      }
    } );
  }

  public StyledText getControl() {
    return styledText;
  }

  public void setText( String text ) {
    styledText.setText( text );
  }

  public String getText() {
    return styledText.getText().trim();
  }

  public void setEnabled( boolean enabled ) {
    styledText.setEditable( enabled );
    styledText.setForeground( enabled ? null : styledText.getDisplay().getSystemColor( SWT.COLOR_GRAY ) );
  }

  public void validate() {
    String config = new CommentsFilter( styledText.getText() ).toString();
    try {
      JsonObject.readFrom( config );
      handleError( null );
      removeErrorMarker();
    } catch( ParseException exception ) {
      int line = exception.getLine();
      int column = exception.getColumn();
      handleError( "Syntax error in config at " + line + ":" + column );
      setErrorMarker( line, column );
    }
  }

  public void importConfig() {
    Shell shell = styledText.getShell();
    FileDialog dialog = new FileDialog( shell, SWT.OPEN );
    String file = dialog.open();
    if( file != null ) {
      try {
        setText( IOUtil.readFromFileUtf8( file ) );
      } catch( IOException exception ) {
        String message = "Could not read from file " + file;
        MessageDialog.openError( shell, "Import Failed", message + "\nSee log for details." );
        Activator.logError( message, exception );
      }
    }
  }

  public void exportConfig() {
    Shell shell = styledText.getShell();
    FileDialog dialog = new FileDialog( shell, SWT.SAVE );
    dialog.setOverwrite( true );
    String file = dialog.open();
    if( file != null ) {
      String text = getText();
      try {
        IOUtil.writeToFileUtf8( file, text );
      } catch( IOException exception ) {
        String message = "Could not write to file " + file;
        MessageDialog.openError( shell, "Export Failed", message + "\nSee log for details." );
        Activator.logError( message, exception );
      }
    }
  }

  protected void handleError( String message ) {
  }

  private void drawError( GC gc ) {
    if( errorOffset >= 0 ) {
      int offset = Math.min( styledText.getCharCount(), errorOffset );
      Point location = styledText.getLocationAtOffset( offset );
      Point extent = gc.stringExtent( "x" );
      gc.setBackground( errorBorderColor );
      gc.fillRectangle( location.x, location.y + extent.y, extent.x, 2 );
    }
  }

  private void setErrorMarker( int line, int column ) {
    int errorLine = Math.max( 0, Math.min( styledText.getLineCount() - 1, line - 1 ) );
    errorOffset = styledText.getOffsetAtLine( errorLine ) + column;
    styledText.redraw();
  }

  private void removeErrorMarker() {
    errorOffset = -1;
    styledText.redraw();
  }

  private void dispose() {
    errorBorderColor.dispose();
  }

}
