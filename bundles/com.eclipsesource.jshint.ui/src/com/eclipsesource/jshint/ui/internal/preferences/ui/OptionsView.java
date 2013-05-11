/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;


public class OptionsView extends Composite {

  private Text predefinedText;
  private Text optionsText;
  private Text annotationText;

  public OptionsView( Composite parent, int style ) {
    super( parent, style );
    super.setLayout( createGridLayout( 1, false ) );
    createPredefControls();
    createOptionsControls();
    createAnnotationControls();
  }

  public void loadDefaults() {
    predefinedText.setText( "" );
    optionsText.setText( "" );
    annotationText.setText( "" );
  }

  public void loadPreferences( OptionsPreferences preferences ) {
    predefinedText.setText( preferences.getGlobals() );
    optionsText.setText( preferences.getOptions() );
    annotationText.setText( preferences.getAnnotation() );
  }

  public void storePreferences( OptionsPreferences preferences ) {
    preferences.setGlobals( predefinedText.getText() );
    preferences.setOptions( optionsText.getText() );
    preferences.setAnnotation( annotationText.getText() );
  }

  @Override
  public void setLayout( Layout layout ) {
    // prevent changing the default layout
  }

  private void createPredefControls() {
    new Label( this, SWT.NONE ).setText( "Predefined Globals:" );
    predefinedText = new Text( this, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    predefinedText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    createSubText( this ).setText( "Example: \"org: true, com: true, ...\"\n"
                                   + "use false for read-only identifiers" );
  }

  private void createOptionsControls() {
    new Label( this, SWT.NONE ).setText( "JSHint Options:" );
    optionsText = new Text( this, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    optionsText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    createSubText( this ).setText( "Example: \"strict: false, sub: true, ...\"\n"
                                   + "see http://www.jshint.com/options/" );
  }

  private void createAnnotationControls() {
    new Label( this, SWT.NONE ).setText( "Default JSHint Annotation:" );
    annotationText = new Text( this, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    annotationText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    createSubText( this ).setText( "Example: \"-W086, -W078\"" );
  }

  private static Text createSubText( Composite parent ) {
    Text subText = new Text( parent, SWT.READ_ONLY | SWT.WRAP );
    subText.setLayoutData( createGridDataWithIndent( 20 ) );
    subText.setBackground( parent.getBackground() );
    return subText;
  }

  private static GridLayout createGridLayout( int numColumns, boolean makeColumnsEqualWidth ) {
    GridLayout layout = new GridLayout( numColumns, makeColumnsEqualWidth );
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    return layout;
  }

  private static GridData createGridDataWithIndent( int indent ) {
    GridData predefinedSubData = new GridData();
    predefinedSubData.horizontalIndent = indent;
    return predefinedSubData;
  }

}
