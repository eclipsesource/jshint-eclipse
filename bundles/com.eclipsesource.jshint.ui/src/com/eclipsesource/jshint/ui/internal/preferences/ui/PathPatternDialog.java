/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.jshint.ui.internal.preferences.PathPattern;
import com.eclipsesource.jshint.ui.internal.preferences.PathSegmentPattern;

import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridData;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridLayout;


public class PathPatternDialog extends TitleAreaDialog {

  private Button allFilesRadiobox;
  private Button matchingFilesRadiobox;
  private Text filePatternText;
  private Button allFoldersRadiobox;
  private Button selectedFolderRadiobox;
  private Button includeSubFoldersCheckbox;
  private Text folderPatternText;
  private final PathPattern pattern;
  private String value;
  private String title;

  public PathPatternDialog( Shell parent, String pattern ) {
    super( parent );
    this.pattern = pattern == null ? null : PathPattern.create( pattern );
  }

  public String getValue() {
    return value;
  }

  @Override
  public void setTitle( String title ) {
    this.title = title;
    super.setTitle( title );
  }

  @Override
  public void setErrorMessage( String newErrorMessage ) {
    super.setErrorMessage( newErrorMessage );
    setOkEnabled( newErrorMessage == null );
  }

  @Override
  protected void configureShell( Shell shell ) {
    super.configureShell( shell );
    shell.setText( pattern != null ? "Edit path pattern" : "New path pattern" );
  }

  @Override
  protected Control createDialogArea( Composite parent ) {
    Control composite = super.createDialogArea( parent );
    Composite contentArea = new Composite( parent, SWT.NONE );
    Control fileArea = createFileArea( contentArea );
    Control folderArea = createFolderArea( contentArea );
    gridData( contentArea ).fillBoth();
    gridLayout( contentArea ).columns( 2, true ).margin( 10 ).spacing( 10 );
    gridData( fileArea ).fillBoth();
    gridData( folderArea ).fillBoth();
    initializeUI();
    return composite;
  }

  @Override
  protected void okPressed() {
    createPatternFromUI();
    super.okPressed();
  }

  String getTitle() {
    return title;
  }

  private Control createFileArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    gridLayout( area ).spacing( 3 );
    createFileAreaControls( area );
    addFileRadioListeners();
    addFileTextListener();
    return area;
  }

  private void createFileAreaControls( Composite area ) {
    allFilesRadiobox = new Button( area, SWT.RADIO );
    allFilesRadiobox.setText( "All files" );
    matchingFilesRadiobox = new Button( area, SWT.RADIO );
    matchingFilesRadiobox.setText( "Files matching" );
    filePatternText = new Text( area, SWT.BORDER );
    gridData( filePatternText ).fillHorizontal().indent( 20, 0 );
    Label label = new Label( area, SWT.NONE );
    label.setText( "(* = any string, ? = any character)" );
    gridData( label ).indent( 20, 0 );
  }

  private void addFileRadioListeners() {
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        updateFileDetailsEnablementFromSelection();
        validate();
      }
    };
    allFilesRadiobox.addListener( SWT.Selection, listener );
    matchingFilesRadiobox.addListener( SWT.Selection, listener );
  }

  private void addFileTextListener() {
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        validate();
      }
    };
    filePatternText.addListener( SWT.Modify, listener );
  }

  private Control createFolderArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    gridLayout( area ).spacing( 3 );
    createFolderAreaControls( area );
    addFolderRadioListeners();
    addFolderTextListener();
    return area;
  }

  private void createFolderAreaControls( Composite area ) {
    allFoldersRadiobox = new Button( area, SWT.RADIO );
    allFoldersRadiobox.setText( "in all folders" );
    selectedFolderRadiobox = new Button( area, SWT.RADIO );
    selectedFolderRadiobox.setText( "in folder" );
    folderPatternText = new Text( area, SWT.BORDER );
    gridData( folderPatternText ).fillHorizontal().indent( 20, 0 );
    Label label = new Label( area, SWT.NONE );
    label.setText( "(empty for project root folder)" );
    gridData( label ).indent( 20, 0 );
    includeSubFoldersCheckbox = new Button( area, SWT.CHECK );
    includeSubFoldersCheckbox.setText( "including all subfolders" );
    gridData( includeSubFoldersCheckbox ).indent( 20, 0 );
  }

  private void addFolderRadioListeners() {
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        updateFolderDetailsEnablementFromSelection();
        validate();
      }
    };
    allFoldersRadiobox.addListener( SWT.Selection, listener );
    selectedFolderRadiobox.addListener( SWT.Selection, listener );
  }

  private void addFolderTextListener() {
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        validate();
      }
    };
    folderPatternText.addListener( SWT.Modify, listener );
  }

  private void initializeUI() {
    if( pattern == null || pattern.matchesAllFiles() ) {
      allFilesRadiobox.setSelection( true );
    } else {
      matchingFilesRadiobox.setSelection( true );
      filePatternText.setText( pattern.getFilePattern() );
    }
    if( pattern == null || pattern.matchesAllFolders() ) {
      allFoldersRadiobox.setSelection( true );
      includeSubFoldersCheckbox.setSelection( false );
    } else {
      selectedFolderRadiobox.setSelection( true );
      String folderPattern = pattern.getPathPattern();
      folderPatternText.setText( folderPattern.replace( "//", "/" ) );
      includeSubFoldersCheckbox.setSelection( folderPattern.endsWith( "//" ) );
    }
    updateFileDetailsEnablementFromSelection();
    updateFolderDetailsEnablementFromSelection();
    super.setTitle( title );
  }

  private void createPatternFromUI() {
    StringBuilder builder = new StringBuilder();
    if( allFoldersRadiobox.getSelection() ) {
      builder.append( "//" );
    } else {
      String folderPattern = folderPatternText.getText().trim();
      builder.append( trimSlashes( folderPattern ) );
      if( includeSubFoldersCheckbox.getSelection() ) {
        builder.append( "//" );
      } else if( folderPattern.length() > 0 ) {
        builder.append( "/" );
      }
    }
    if( allFilesRadiobox.getSelection() ) {
      builder.append( "*" );
    } else {
      builder.append( filePatternText.getText().trim() );
    }
    value = builder.toString();
  }

  private void updateFileDetailsEnablementFromSelection() {
    filePatternText.setEnabled( matchingFilesRadiobox.getSelection() );
  }

  private void updateFolderDetailsEnablementFromSelection() {
    folderPatternText.setEnabled( selectedFolderRadiobox.getSelection() );
    includeSubFoldersCheckbox.setEnabled( selectedFolderRadiobox.getSelection() );
  }

  private void validate() {
    String errorMessage = getCombinedErrorMessage();
    setErrorMessage( errorMessage );
  }

  private String getCombinedErrorMessage() {
    String fileErrorMessage = getFileErrorMessage();
    String folderErrorMessage = getFolderErrorMessage();
    if( fileErrorMessage != null && folderErrorMessage != null ) {
      return fileErrorMessage + ", " + folderErrorMessage;
    }
    return fileErrorMessage != null ? fileErrorMessage : folderErrorMessage;
  }

  private String getFolderErrorMessage() {
    String errorMessage = null;
    if( selectedFolderRadiobox.getSelection() ) {
      String text = folderPatternText.getText();
      errorMessage = checkFolderPattern( text );
    }
    return errorMessage;
  }

  private String getFileErrorMessage() {
    String errorMessage = null;
    if( matchingFilesRadiobox.getSelection() ) {
      String text = filePatternText.getText();
      errorMessage = checkFilePattern( text );
    }
    return errorMessage;
  }

  private void setOkEnabled( boolean enabled ) {
    Button okButton = getButton( IDialogConstants.OK_ID );
    if( okButton != null ) {
      okButton.setEnabled( enabled );
    }
  }

  private static String trimSlashes( String string ) {
    String result = string;
    while( result.startsWith( "/" ) ) {
      result = result.substring( 1 );
    }
    while( result.endsWith( "/" ) ) {
      result = result.substring( 0, result.length() - 1 );
    }
    return result;
  }

  private static String checkFilePattern( String pattern ) {
    String errorMessage = null;
    try {
      PathSegmentPattern.create( pattern );
    } catch( IllegalArgumentException exception ) {
      errorMessage = exception.getMessage().replace( "in expression", "in file pattern" );
    }
    return errorMessage;
  }

  private static String checkFolderPattern( String pattern ) {
    String errorMessage = null;
    if( pattern.contains( "//" ) ) {
      errorMessage = "Illegal '//' in folder path";
    } else {
      try {
        PathPattern.create( pattern );
      } catch( IllegalArgumentException exception ) {
        errorMessage = exception.getMessage().replace( "in expression", "in folder path" );
      }
    }
    return errorMessage;
  }

}
