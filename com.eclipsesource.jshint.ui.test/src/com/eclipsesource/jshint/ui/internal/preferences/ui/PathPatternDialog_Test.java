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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class PathPatternDialog_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    display = Display.getDefault();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    shell.dispose();
  }

  @Test
  public void testInitialSelection() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    openNonBlocking( dialog );

    assertTrue( getAllFilesRadio( dialog ).isSelected() );
    assertFalse( getSelectedFolderRadio( dialog ).isSelected() );
    assertEquals( "", getFilePatternText( dialog ).getText() );
    assertTrue( getAllFoldersRadio( dialog ).isSelected() );
    assertEquals( "", getFolderPatternText( dialog ).getText() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isChecked() );
  }

  @Test
  public void testInitialSelection_allFilesPattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*" );
    openNonBlocking( dialog );

    assertTrue( getAllFilesRadio( dialog ).isSelected() );
    assertEquals( "", getFilePatternText( dialog ).getText() );
    assertTrue( getAllFoldersRadio( dialog ).isSelected() );
    assertEquals( "", getFolderPatternText( dialog ).getText() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isChecked() );
  }

  @Test
  public void testInitialSelection_pathOnlyPattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    assertTrue( getAllFilesRadio( dialog ).isSelected() );
    assertEquals( "", getFilePatternText( dialog ).getText() );
    assertTrue( getSelectedFolderRadio( dialog ).isSelected() );
    assertEquals( "src/", getFolderPatternText( dialog ).getText() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isChecked() );
  }

  @Test
  public void testInitialSelection_simplePattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );

    assertTrue( getMatchingFilesRadio( dialog ).isSelected() );
    assertEquals( "*.js", getFilePatternText( dialog ).getText() );
    assertTrue( getAllFoldersRadio( dialog ).isSelected() );
    assertEquals( "", getFolderPatternText( dialog ).getText() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isChecked() );
  }

  @Test
  public void testInitialSelection_patternWithSubFolders() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/js//" );
    openNonBlocking( dialog );

    assertTrue( getAllFilesRadio( dialog ).isSelected() );
    assertEquals( "", getFilePatternText( dialog ).getText() );
    assertTrue( getSelectedFolderRadio( dialog ).isSelected() );
    assertEquals( "src/js/", getFolderPatternText( dialog ).getText() );
    assertTrue( getIncludeFolderCheckbox( dialog ).isChecked() );
  }

  @Test
  public void fileControls_initiallyDisabled() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    assertFalse( getFilePatternText( dialog ).isEnabled() );
  }

  @Test
  public void fileControls_enabledAfterRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    getMatchingFilesRadio( dialog ).click();

    assertTrue( getFilePatternText( dialog ).isEnabled() );
  }

  @Test
  public void fileControls_initiallyEnabled() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    assertTrue( getFilePatternText( dialog ).isEnabled() );
  }

  @Test
  public void fileControls_disabledAfterRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    getAllFilesRadio( dialog ).click();

    assertFalse( getFilePatternText( dialog ).isEnabled() );
  }

  @Test
  public void folderControls_initiallyDisabled() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );

    assertFalse( getFolderPatternText( dialog ).isEnabled() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isEnabled() );
  }

  @Test
  public void folderControls_enabledAfterRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );

    getSelectedFolderRadio( dialog ).click();

    assertTrue( getFolderPatternText( dialog ).isEnabled() );
    assertTrue( getIncludeFolderCheckbox( dialog ).isEnabled() );
  }

  @Test
  public void folderControls_initiallyEnabled() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/js//" );
    openNonBlocking( dialog );

    assertTrue( getFolderPatternText( dialog ).isEnabled() );
    assertTrue( getIncludeFolderCheckbox( dialog ).isEnabled() );
  }

  @Test
  public void folderControls_disabledAfterRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/js//" );
    openNonBlocking( dialog );

    getAllFoldersRadio( dialog ).click();

    assertFalse( getFolderPatternText( dialog ).isEnabled() );
    assertFalse( getIncludeFolderCheckbox( dialog ).isEnabled() );
  }

  @Test
  public void getValue_withNull() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    openNonBlocking( dialog );
    getOkButton( dialog ).click();

    assertEquals( "//*", dialog.getValue() );
  }

  @Test
  public void getValue_withOnlyFilePattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "*.js" );
    openNonBlocking( dialog );
    getOkButton( dialog ).click();

    assertEquals( "*.js", dialog.getValue() );
  }

  @Test
  public void getValue_withPathAndFilePattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/js//*.js" );
    openNonBlocking( dialog );
    getOkButton( dialog ).click();

    assertEquals( "src/js//*.js", dialog.getValue() );
  }

  @Test
  public void getValue_afterChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "" );
    openNonBlocking( dialog );

    getMatchingFilesRadio( dialog ).click();
    getFilePatternText( dialog ).setText( "*.js" );
    getSelectedFolderRadio( dialog ).click();
    getFolderPatternText( dialog ).setText( "src" );
    getIncludeFolderCheckbox( dialog ).select();

    getOkButton( dialog ).click();

    assertEquals( "src//*.js", dialog.getValue() );
  }

  @Test
  public void getValue_addsMissingTrailingSlashToPath() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    getFolderPatternText( dialog ).setText( "src" );
    getOkButton( dialog ).click();

    assertEquals( "src/*.js", dialog.getValue() );
  }

  @Test
  public void getValue_removesLeadingSlashFromPath() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    getFolderPatternText( dialog ).setText( "/src" );
    getOkButton( dialog ).click();

    assertEquals( "src/*.js", dialog.getValue() );
  }

  @Test
  public void getValue_removesEnclosingWhitespaceFromFile() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    getFilePatternText( dialog ).setText( " *.js " );
    getOkButton( dialog ).click();

    assertEquals( "src/*.js", dialog.getValue() );
  }

  @Test
  public void getValue_removesEnclosingWhitespaceFromPath() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    getFolderPatternText( dialog ).setText( " src/ " );
    getOkButton( dialog ).click();

    assertEquals( "src/*.js", dialog.getValue() );
  }

  // TODO remove leading slash

  @Test
  public void shellTextIsNew_withNullPattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    openNonBlocking( dialog );

    assertTrue( dialog.getShell().getText().startsWith( "New" ) );
  }

  @Test
  public void shellTextIsEdit_withPattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/js//*.js" );
    openNonBlocking( dialog );

    assertTrue( dialog.getShell().getText().startsWith( "Edit" ) );
  }

  @Test
  public void setTitle() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    dialog.setTitle( "foo" );

    assertEquals( "foo", dialog.getTitle() );
  }

  @Test
  public void setTitle_shownIfCalledBeforeOpen() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    dialog.setTitle( "foo" );
    openNonBlocking( dialog );

    assertNotNull( new SWTBot( dialog.getShell() ).label( "foo" ) );
  }

  @Test
  public void setTitle_shownIfCalledAfterOpen() {
    PathPatternDialog dialog = new PathPatternDialog( shell, null );
    openNonBlocking( dialog );
    dialog.setTitle( "foo" );

    assertNotNull( new SWTBot( dialog.getShell() ).label( "foo" ) );
  }

  @Test
  public void setErrorMessage() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    dialog.setErrorMessage( "error" );

    assertEquals( "error", dialog.getErrorMessage() );
  }

  @Test
  public void setErrorMessage_disablesOkButton() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );

    dialog.setErrorMessage( "error" );

    assertFalse( getOkButton( dialog ).isEnabled() );
  }

  @Test
  public void validateFilePattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFilePattern_errorOnChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );

    getFilePatternText( dialog ).setText( "*.js/" );

    assertEquals( "Illegal character in file pattern: '/'", dialog.getErrorMessage() );
  }

  @Test
  public void validateFilePattern_errorClearedOnChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );
    getFilePatternText( dialog ).setText( "*.js/" );

    getFilePatternText( dialog ).setText( "*.js" );

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFilePattern_errorClearedOnRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    openNonBlocking( dialog );
    getFilePatternText( dialog ).setText( "*.js/" );

    getAllFilesRadio( dialog ).click();

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFolderPattern() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFolderPattern_errorOnChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    getFolderPatternText( dialog ).setText( "src//" );

    assertEquals( "Illegal '//' in folder path", dialog.getErrorMessage() );
  }

  @Test
  public void validateFolderPattern_errorOnChange_illegalCharacter() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    getFolderPatternText( dialog ).setText( "src/[" );

    assertEquals( "Illegal character in folder path: '['", dialog.getErrorMessage() );
  }

  @Test
  public void validateFolderPattern_errorClearedOnChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );
    getFolderPatternText( dialog ).setText( "src//" );

    getFolderPatternText( dialog ).setText( "src/" );

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFolderPattern_errorClearedOnRadioChange() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );
    getFolderPatternText( dialog ).setText( "src//" );

    getAllFoldersRadio( dialog ).click();

    assertNull( dialog.getErrorMessage() );
  }

  @Test
  public void validateFileAndFolderPattern_firstErrorRestored() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/*.js" );
    openNonBlocking( dialog );
    getFilePatternText( dialog ).setText( "[foo]" );
    String originalErrorMessage = dialog.getErrorMessage();
    getFolderPatternText( dialog ).setText( "src//" );

    getFolderPatternText( dialog ).setText( "src/" );

    assertEquals( originalErrorMessage, dialog.getErrorMessage() );
  }

  @Test
  public void swtBot_ClickOnRadio_switchesRadioSelection() {
    PathPatternDialog dialog = new PathPatternDialog( shell, "src/" );
    openNonBlocking( dialog );

    assertTrue( getAllFilesRadio( dialog ).isSelected() );
    assertFalse( getMatchingFilesRadio( dialog ).isSelected() );

    getMatchingFilesRadio( dialog ).click();

    assertFalse( getAllFilesRadio( dialog ).isSelected() );
    assertTrue( getMatchingFilesRadio( dialog ).isSelected() );
  }

  private static SWTBotRadio getAllFilesRadio( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).radio( "All files" );
  }

  private static SWTBotRadio getMatchingFilesRadio( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).radio( "Files matching" );
  }

  private static SWTBotRadio getAllFoldersRadio( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).radio( "in all folders" );
  }

  private static SWTBotRadio getSelectedFolderRadio( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).radio( "in folder" );
  }

  private static SWTBotCheckBox getIncludeFolderCheckbox( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).checkBox( "including all subfolders" );
  }

  private static SWTBotText getFilePatternText( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).text( 0 );
  }

  private static SWTBotText getFolderPatternText( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).text( 1 );
  }

  private static SWTBotButton getOkButton( PathPatternDialog dialog ) {
    return new SWTBot( dialog.getShell() ).button( "OK" );
  }

  private static void openNonBlocking( Dialog dialog ) {
    dialog.setBlockOnOpen( false );
    dialog.open();
  }

  public static void main( String[] args ) {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setLayout( new GridLayout( 1, false ) );
    shell.setBounds( 20, 20, 600, 400 );
    shell.open();
    PathPatternDialog dialog = new PathPatternDialog( shell, "//*.js" );
    dialog.open();
    System.out.println( dialog.getValue() );
    display.dispose();
  }
}
