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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private Button enablementCheckbox;
  private Button projectSpecificCheckbox;
  private OptionsView optionsView;

  @Override
  public boolean performOk() {
    try {
      boolean preferencesChanged = storePreferences();
      boolean builderChanged = setBuilderEnablement();
      if( preferencesChanged || builderChanged ) {
        triggerRebuild();
      }
    } catch( CoreException exception ) {
      String message = "Failed to store settings";
      Activator.logError( message, exception );
      return false;
    }
    return true;
  }

  @Override
  protected void performDefaults() {
    super.performDefaults();
    enablementCheckbox.setSelection( false );
    projectSpecificCheckbox.setSelection( false );
    optionsView.loadDefaults();
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = LayoutUtil.createMainComposite( parent );
    addEnablementSection( composite );
    optionsView = new OptionsView( composite, SWT.NONE );
    optionsView.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    loadPreferences();
    updateEnablement();
    return composite;
  }

  private void loadPreferences() {
    Preferences node = getPreferences();
    EnablementPreferences enablePreferences = new EnablementPreferences( node );
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    enablementCheckbox.setSelection( enablePreferences.getEnabled() );
    projectSpecificCheckbox.setSelection( optionsPreferences.getProjectSpecific() );
    optionsView.loadPreferences( optionsPreferences );
  }

  private void addEnablementSection( Composite parent ) {
    Composite composite = LayoutUtil.createDefaultComposite( parent );
    enablementCheckbox = new Button( composite, SWT.CHECK );
    enablementCheckbox.setText( "Enable JSHint for this project" );
    enablementCheckbox.setLayoutData( createGridDataForCheckbox() );
    enablementCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        updateEnablement();
      }
    } );
    projectSpecificCheckbox = new Button( composite, SWT.CHECK );
    projectSpecificCheckbox.setText( "Enable project specific settings" );
    projectSpecificCheckbox.setLayoutData( createGridDataForCheckbox() );
    projectSpecificCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        updateEnablement();
      }
    } );
  }

  private void updateEnablement() {
    boolean enabled = enablementCheckbox.getSelection();
    boolean specific = projectSpecificCheckbox.getSelection();
    projectSpecificCheckbox.setEnabled( enabled );
    optionsView.setEnabled( enabled && specific );
  }

  private boolean storePreferences() throws CoreException {
    Preferences node = getPreferences();
    EnablementPreferences enablePreferences = new EnablementPreferences( node );
    enablePreferences.setEnabled( enablementCheckbox.getSelection() );
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    optionsPreferences.setProjectSpecific( projectSpecificCheckbox.getSelection() );
    optionsView.storePreferences( optionsPreferences );
    boolean changed = enablePreferences.hasChanged() || optionsPreferences.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
  }

  private boolean setBuilderEnablement() throws CoreException {
    return setBuilderEnabled( enablementCheckbox.getSelection() );
  }

  private boolean setBuilderEnabled( boolean enabled ) throws CoreException {
    IProject project = getResource().getProject();
    if( enabled ) {
      return BuilderUtil.addBuilderToProject( project, JSHintBuilder.ID );
    } else {
      return BuilderUtil.removeBuilderFromProject( project, JSHintBuilder.ID );
    }
  }

  private void triggerRebuild() throws CoreException {
    IProject project = getResource().getProject();
    BuilderUtil.triggerClean( project, JSHintBuilder.ID );
  }

  private static GridData createGridDataForCheckbox() {
    return new GridData( SWT.FILL, SWT.CENTER, true, false );
  }

}
