/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;

import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridData;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridLayout;


public class IncludesView extends Composite {

  private Table includeTable;
  private Table excludeTable;
  private Image fileImage;

  public IncludesView( Composite parent, int style, IProject project ) {
    super( parent, style );
    gridLayout( this ).columns( 2 ).spacing( 5, 3 );
    createImages();
    createIncludeControls();
    createExcludeControls();
  }

  public void loadDefaults() {
    setPatterns( includeTable, Collections.<String>emptyList() );
    setPatterns( excludeTable, Collections.<String>emptyList() );
  }

  public void loadPreferences( EnablementPreferences preferences ) {
    List<String> includePatterns = preferences.getIncludePatterns();
    List<String> excludePatterns = preferences.getExcludePatterns();
    setPatterns( includeTable, includePatterns );
    setPatterns( excludeTable, excludePatterns );
  }

  public void storePreferences( EnablementPreferences preferences ) {
    ArrayList<String> includePatterns = getPatterns( includeTable );
    preferences.setIncludePatterns( includePatterns );
    ArrayList<String> excludePatterns = getPatterns( excludeTable );
    preferences.setExcludePatterns( excludePatterns );
  }

  private void createImages() {
    Display display = getDisplay();
    ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
    fileImage = images.getImageDescriptor( ISharedImages.IMG_OBJ_FILE ).createImage( display );
  }

  private void createIncludeControls() {
    Label label = new Label( this, SWT.NONE );
    label.setText( "Enable JSHint for these files and folders:" );
    gridData( label ).span( 2, 1 );
    includeTable = new Table( this, SWT.BORDER );
    gridData( includeTable ).fillBoth();
    createButtonsBar( includeTable );
    addListeners( includeTable );
  }

  private void createExcludeControls() {
    Label label = new Label( this, SWT.NONE );
    label.setText( "But exclude these files and folders from validation:" );
    gridData( label ).span( 2, 1 );
    excludeTable = new Table( this, SWT.BORDER );
    gridData( excludeTable ).fillBoth();
    createButtonsBar( excludeTable );
    addListeners( excludeTable );
  }

  private void addListeners( final Table table ) {
    table.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        editSelectedPattern( table );
      }
    } );
    table.addListener( SWT.Traverse, new Listener() {
      public void handleEvent( Event event ) {
        if( event.detail == SWT.TRAVERSE_RETURN ) {
          event.doit = false;
        }
      }
    } );
  }

  private void createButtonsBar( final Table table ) {
    ButtonBar buttonBar = new ButtonBar( this, SWT.NONE );
    createAddButton( table, buttonBar );
    createEditButton( table, buttonBar );
    createRemoveButton( table, buttonBar );
  }

  private void createAddButton( final Table table, ButtonBar buttonBar ) {
    buttonBar.addButton( "Add", new Listener() {
      public void handleEvent( Event event ) {
        addPattern( table );
      }
    } );
  }

  private void createEditButton( final Table table, ButtonBar buttonBar ) {
    final Button button = buttonBar.addButton( "Edit", new Listener() {
      public void handleEvent( Event event ) {
        editSelectedPattern( table );
      }
    } );
    button.setEnabled( false );
    table.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        button.setEnabled( table.getSelectionCount() > 0 );
      }
    } );
  }

  private void createRemoveButton( final Table table, ButtonBar buttonBar ) {
    final Button button = buttonBar.addButton( "Remove", new Listener() {
      public void handleEvent( Event event ) {
        removeSelectedPattern( table );
      }
    } );
    button.setEnabled( false );
    table.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        button.setEnabled( table.getSelectionCount() > 0 );
      }
    } );
  }

  private void addPattern( Table table ) {
    String defaultPattern = table == includeTable ? "//*.js" : "//*.min.js";
    String pattern = showPatternDialogForTable( table, defaultPattern );
    if( pattern != null ) {
      ArrayList<String> patterns = getPatterns( table );
      if( !patterns.contains( pattern ) ) {
        patterns.add( pattern );
      }
      setPatterns( table, patterns );
      select( table, pattern );
    }
  }

  private void editSelectedPattern( Table table ) {
    TableItem[] selection = table.getSelection();
    if( selection.length != 0 ) {
      String oldPattern = selection[ 0 ].getText();
      String newPattern = showPatternDialogForTable( table, oldPattern );
      if( newPattern != null ) {
        ArrayList<String> patterns = getPatterns( table );
        patterns.remove( oldPattern );
        patterns.add( newPattern );
        setPatterns( table, patterns );
        select( table, newPattern );
      }
    }
  }

  private void removeSelectedPattern( Table table ) {
    int selection = table.getSelectionIndex();
    if( selection != -1 ) {
      String pattern = table.getItem( selection ).getText();
      ArrayList<String> patterns = getPatterns( table );
      patterns.remove( pattern );
      setPatterns( table, patterns );
      select( table, selection );
    }
  }

  private String showPatternDialogForTable( Table table, String origPattern ) {
    String pattern = null;
    PathPatternDialog dialog = new PathPatternDialog( table.getShell(), origPattern );
    configurePatternDialog( dialog, table );
    if( dialog.open() == Window.OK ) {
      pattern = dialog.getValue();
    }
    return pattern;
  }

  private void configurePatternDialog( PathPatternDialog dialog, Table table ) {
    if( table == excludeTable ) {
      dialog.setTitle( "Select files to exclude" );
    } else {
      dialog.setTitle( "Select files to include" );
    }
  }

  private static void select( Table table, String pattern ) {
    TableItem[] items = table.getItems();
    for( int i = 0; i < items.length; i++ ) {
      if( items[ i ].getText().equals( pattern ) ) {
        table.select( i );
      }
    }
  }

  private static void select( Table table, int selection ) {
    int itemCount = table.getItemCount();
    if( selection < itemCount ) {
      table.select( selection );
    } else if( itemCount > 0 ) {
      table.select( itemCount - 1 );
    }
  }

  private void setPatterns( Table table, List<String> patterns ) {
    Collections.sort( patterns );
    table.removeAll();
    for( String pattern : patterns ) {
      TableItem item = new TableItem( table, SWT.NONE );
      item.setText( pattern );
      item.setImage( fileImage );
    }
  }

  private static ArrayList<String> getPatterns( Table table ) {
    TableItem[] items = table.getItems();
    ArrayList<String> result = new ArrayList<String>();
    for( TableItem item : items ) {
      result.add( item.getText() );
    }
    return result;
  }

}
