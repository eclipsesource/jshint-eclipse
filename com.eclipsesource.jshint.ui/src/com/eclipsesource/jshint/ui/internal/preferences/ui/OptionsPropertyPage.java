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
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;


public class OptionsPropertyPage extends AbstractPropertyPage {

  private Button projectSpecificCheckbox;
  private OptionsView optionsView;

  @Override
  public boolean performOk() {
    try {
      boolean preferencesChanged = storePreferences();
      if( preferencesChanged ) {
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
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    projectSpecificCheckbox.setSelection( optionsPreferences.getProjectSpecific() );
    optionsView.loadPreferences( optionsPreferences );
  }

  private void addEnablementSection( Composite parent ) {
    Composite composite = LayoutUtil.createDefaultComposite( parent );
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
    boolean specific = projectSpecificCheckbox.getSelection();
    optionsView.setEnabled( specific );
  }

  private boolean storePreferences() throws CoreException {
    Preferences node = getPreferences();
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    optionsPreferences.setProjectSpecific( projectSpecificCheckbox.getSelection() );
    optionsView.storePreferences( optionsPreferences );
    boolean changed = optionsPreferences.hasChanged();
    if( changed ) {
      savePreferences();
    }
    return changed;
  }

  private void triggerRebuild() throws CoreException {
    IProject project = getResource().getProject();
    BuilderUtil.triggerClean( project, JSHintBuilder.ID );
  }

  private static GridData createGridDataForCheckbox() {
    return new GridData( SWT.FILL, SWT.CENTER, true, false );
  }

}
