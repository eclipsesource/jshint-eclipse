/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.jshint.ui.internal.preferences.PathPattern;
import com.eclipsesource.jshint.ui.internal.preferences.PathSegmentPattern;


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
    contentArea.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    GridLayout mainLayout = createMainLayout();
    contentArea.setLayout( mainLayout );
    createFileArea( contentArea );
    createFolderArea( contentArea );
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

  private void createFileArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    area.setLayout( new GridLayout() );
    createFileAreaControls( area );
    addFileRadioListeners();
    addFileTextListener();
  }

  private void createFileAreaControls( Composite area ) {
    allFilesRadiobox = new Button( area, SWT.RADIO );
    allFilesRadiobox.setText( "All files" );
    matchingFilesRadiobox = new Button( area, SWT.RADIO );
    matchingFilesRadiobox.setText( "Files matching" );
    filePatternText = new Text( area, SWT.BORDER );
    filePatternText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    addIndent( filePatternText );
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

  private void createFolderArea( Composite parent ) {
    Composite area = new Composite( parent, SWT.NONE );
    area.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    area.setLayout( new GridLayout() );
    createFolderAreaControls( area );
    addFolderRadioListeners();
    addFolderTextListener();
  }

  private void createFolderAreaControls( Composite area ) {
    allFoldersRadiobox = new Button( area, SWT.RADIO );
    allFoldersRadiobox.setText( "in all folders" );
    selectedFolderRadiobox = new Button( area, SWT.RADIO );
    selectedFolderRadiobox.setText( "in folder" );
    folderPatternText = new Text( area, SWT.BORDER );
    folderPatternText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    addIndent( folderPatternText );
    includeSubFoldersCheckbox = new Button( area, SWT.CHECK );
    includeSubFoldersCheckbox.setText( "including all subfolders" );
    addIndent( includeSubFoldersCheckbox );
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
      String folderPattern = folderPatternText.getText();
      builder.append( folderPattern );
      if( includeSubFoldersCheckbox.getSelection() ) {
        if( !folderPattern.endsWith( "/" ) ) {
          builder.append( "/" );
        }
        builder.append( "/" );
      }
    }
    if( allFilesRadiobox.getSelection() ) {
      builder.append( "*" );
    } else {
      builder.append( filePatternText.getText() );
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
    } else {
      return fileErrorMessage != null ? fileErrorMessage : folderErrorMessage;
    }
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

  private static void addIndent( Control control ) {
    GridData layoutData = (GridData)control.getLayoutData();
    if( layoutData == null ) {
      layoutData = new GridData();
    }
    layoutData.horizontalIndent = 20;
    control.setLayoutData( layoutData );
  }

  private static GridLayout createMainLayout() {
    GridLayout mainLayout = new GridLayout( 2, true );
    mainLayout.horizontalSpacing = 10;
    return mainLayout;
  }

}
