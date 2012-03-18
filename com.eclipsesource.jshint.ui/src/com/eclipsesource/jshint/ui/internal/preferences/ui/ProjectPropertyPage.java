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

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private Button enablementCheckbox;
  private OptionsView configView;

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
    configView.loadDefaults();
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = createMainComposite( parent );
    EnablementPreferences preferences = new EnablementPreferences( getPreferences() );
    addEnablementSection( composite, preferences );
    OptionsPreferences optionsPreferences = new OptionsPreferences( getPreferences() );
    configView = new OptionsView( composite, SWT.NONE );
    configView.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    configView.loadPreferences( optionsPreferences );
    return composite;
  }

  private void addEnablementSection( Composite parent, EnablementPreferences preferences ) {
    Composite composite = createDefaultComposite( parent );
    enablementCheckbox = new Button( composite, SWT.CHECK );
    enablementCheckbox.setText( "Enable JSHint for this project" );
    enablementCheckbox.setSelection( preferences.getEnabled() );
    enablementCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        configView.setEnabled( enablementCheckbox.getSelection() );
      }
    } );
  }

  private boolean storePreferences() throws CoreException {
    EnablementPreferences preferences = new EnablementPreferences( getPreferences() );
    preferences.setEnabled( enablementCheckbox.getSelection() );
    boolean changed1 = preferences.hasChanged();
    OptionsPreferences jsHintPreferences = new OptionsPreferences( getPreferences() );
    boolean changed2 = configView.storePreferences( jsHintPreferences );
    boolean changed = changed1 || changed2;
    if( changed ) {
      preferences.save();
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

}
