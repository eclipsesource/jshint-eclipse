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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private IncludesView includesView;

  @Override
  public boolean performOk() {
    try {
      if( storePreferences() ) {
        boolean enabled = new EnablementPreferences( getPreferences() ).getEnabled();
        setBuilderEnabled( enabled );
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
    includesView.loadDefaults();
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = LayoutUtil.createMainComposite( parent );
    includesView = new IncludesView( composite, SWT.NONE, getResource().getProject() );
    includesView.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    loadPreferences();
    return composite;
  }

  private void loadPreferences() {
    Preferences node = getPreferences();
    EnablementPreferences enablePreferences = new EnablementPreferences( node );
    includesView.loadPreferences( enablePreferences );
  }

  private boolean storePreferences() throws CoreException {
    Preferences node = getPreferences();
    EnablementPreferences enablePreferences = new EnablementPreferences( node );
    includesView.storePreferences( enablePreferences );
    if( enablePreferences.hasChanged() ) {
      savePreferences();
      return true;
    }
    return false;
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
