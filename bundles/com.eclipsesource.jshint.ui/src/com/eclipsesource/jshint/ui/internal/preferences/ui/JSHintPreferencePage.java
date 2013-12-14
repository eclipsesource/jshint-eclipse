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

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.eclipsesource.jshint.JSHint;
import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.JSHintPreferences;

import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridData;
import static com.eclipsesource.jshint.ui.internal.util.LayoutUtil.gridLayout;


public class JSHintPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private final JSHintPreferences preferences;
  private Button defaultLibRadio;
  private Button customLibRadio;
  private Text customLibPathText;
  private Button customLibPathButton;
  private Button enableErrorsCheckbox;

  public JSHintPreferencePage() {
    setPreferenceStore( Activator.getDefault().getPreferenceStore() );
    setDescription( "General settings for JSHint" );
    preferences = new JSHintPreferences();
  }

  public void init( IWorkbench workbench ) {
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return null;
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    gridLayout( composite ).columns( 3 ).spacing( 3 ).marginTop( 10 );
    createCustomJSHintArea( composite );
    createEnableErrorMarkersArea( composite );
    updateControlsFromPrefs();
    updateControlsEnabled();
    return composite;
  }

  @Override
  public boolean performOk() {
    try {
      if( preferences.hasChanged() ) {
        preferences.save();
        triggerRebuild();
      }
    } catch( CoreException exception ) {
      Activator.logError( "Failed to save preferences", exception );
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    preferences.resetToDefaults();
    updateControlsFromPrefs();
    updateControlsEnabled();
    super.performDefaults();
  }

  private void createCustomJSHintArea( Composite parent ) {
    defaultLibRadio = new Button( parent, SWT.RADIO );
    String version = JSHint.getDefaultLibraryVersion();
    defaultLibRadio.setText( "Use the &built-in JSHint library (version " + version + ")" );
    gridData( defaultLibRadio ).fillHorizontal().span( 3, 1 );
    customLibRadio = new Button( parent, SWT.RADIO );
    customLibRadio.setText( "Provide a &custom JSHint library file" );
    gridData( customLibRadio ).fillHorizontal().span( 3, 1 );
    customLibRadio.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        preferences.setUseCustomLib( customLibRadio.getSelection() );
        validate();
        updateControlsEnabled();
      }
    } );
    customLibPathText = new Text( parent, SWT.BORDER );
    gridData( customLibPathText ).fillHorizontal().span( 2, 1 ).indent( 25, 0 );
    customLibPathText.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        preferences.setCustomLibPath( customLibPathText.getText() );
        validate();
      }
    } );
    customLibPathButton = new Button( parent, SWT.PUSH );
    customLibPathButton.setText( "Select" );
    customLibPathButton.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        selectFile();
      }
    } );
    Text customLibPathLabelText = new Text( parent, SWT.READ_ONLY | SWT.WRAP );
    customLibPathLabelText.setText( "This file is usually named 'jshint.js'." );
    customLibPathLabelText.setBackground( parent.getBackground() );
    gridData( customLibPathLabelText ).fillHorizontal().span( 3, 1 ).indent( 25, 1 );
  }

  private void createEnableErrorMarkersArea( Composite parent ) {
    enableErrorsCheckbox = new Button( parent, SWT.CHECK );
    enableErrorsCheckbox.setText( "Enable JSHint errors" );
    enableErrorsCheckbox.setToolTipText( "If unchecked, errors will be shown as warnings" );
    gridData( enableErrorsCheckbox ).fillHorizontal().span( 3, 1 );
    enableErrorsCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        preferences.setEnableErrorMarkers( enableErrorsCheckbox.getSelection() );
        validate();
      }
    } );
  }

  private void selectFile() {
    FileDialog fileDialog = new FileDialog( getShell(), SWT.OPEN );
    fileDialog.setText( "Select JSHint library file" );
    File file = new File( preferences.getCustomLibPath() );
    fileDialog.setFileName( file.getName() );
    fileDialog.setFilterPath( file.getParent() );
    fileDialog.setFilterNames( new String[] { "JavaScript files" } );
    fileDialog.setFilterExtensions( new String[] { "*.js", "" } );
    String selectedPath = fileDialog.open();
    if( selectedPath != null ) {
      customLibPathText.setText( selectedPath );
    }
  }

  private void validate() {
    setErrorMessage( null );
    setValid( false );
    final Display display = getShell().getDisplay();
    Job validator = new Job( "JSHint preferences validation" ) {
      @Override
      protected IStatus run( IProgressMonitor monitor ) {
        try {
          monitor.beginTask( "checking preferences", 1 );
          validatePrefs();
          display.asyncExec( new Runnable() {
            public void run() {
              setValid( true );
            }
          } );
        } catch( final IllegalArgumentException exception ) {
          display.asyncExec( new Runnable() {
            public void run() {
              setErrorMessage( exception.getMessage() );
            }
          } );
        } finally {
          monitor.done();
        }
        return Status.OK_STATUS;
      }
    };
    validator.schedule();
  }

  private void validatePrefs() {
    if( preferences.getUseCustomLib() ) {
      String path = preferences.getCustomLibPath();
      validateFile( new File( path ) );
    }
  }

  private static void validateFile( File file ) throws IllegalArgumentException {
    if( !file.isFile() ) {
      throw new IllegalArgumentException( "File does not exist" );
    }
    if( !file.canRead() ) {
      throw new IllegalArgumentException( "File is not readable" );
    }
    try {
      FileInputStream inputStream = new FileInputStream( file );
      try {
        JSHint jsHint = new JSHint();
        jsHint.load( inputStream );
      } finally {
        inputStream.close();
      }
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "File is not a valid JSHint library", exception );
    }
  }

  private void updateControlsFromPrefs() {
    customLibRadio.setSelection( preferences.getUseCustomLib() );
    defaultLibRadio.setSelection( !customLibRadio.getSelection() );
    customLibPathText.setText( preferences.getCustomLibPath() );
    enableErrorsCheckbox.setSelection( preferences.getEnableErrorMarkers() );
  }

  private void updateControlsEnabled() {
    boolean enabled = customLibRadio.getSelection();
    customLibPathText.setEnabled( enabled );
    customLibPathButton.setEnabled( enabled );
  }

  private void triggerRebuild() throws CoreException {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for( IProject project : projects ) {
      if( project.isAccessible() ) {
        BuilderUtil.triggerClean( project, JSHintBuilder.ID );
      }
    }
  }

}
