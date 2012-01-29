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
package com.eclipsesource.jshint.ui.internal.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;


public class ProjectPropertyPage extends AbstractPropertyPage {

  private Button enablementCheckbox;
  private Text predefinedText;
  private Text optionsText;
  private Composite configSection;

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
    predefinedText.setText( "" );
    optionsText.setText( "" );
  }

  @Override
  protected Control createContents( Composite parent ) {
    Composite composite = createMainComposite( parent );
    boolean enabled = getProjectPreferences().getEnabled();
    addEnablementSection( composite, enabled );
    addConfigSection( composite, enabled );
    return composite;
  }

  private void addEnablementSection( Composite parent, boolean enabled ) {
    Composite composite = createDefaultComposite( parent );
    enablementCheckbox = new Button( composite, SWT.CHECK );
    enablementCheckbox.setText( "Enable JSHint for this project" );
    enablementCheckbox.setSelection( enabled );
    enablementCheckbox.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        configSection.setEnabled( enablementCheckbox.getSelection() );
      }
    } );
  }

  private void addConfigSection( Composite parent, boolean enabled ) {
    ProjectPreferences preferences = getProjectPreferences();
    configSection = new Composite( parent, SWT.NONE );
    configSection.setLayout( new GridLayout() );
    configSection.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    configSection.setEnabled( enabled );

    Label predefinedLabel = new Label( configSection, SWT.NONE );
    predefinedLabel.setText( "Predefined globals:" );

    predefinedText = new Text( configSection, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    predefinedText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    predefinedText.setText( preferences.getGlobals() );

    Text predefinedSubLabel = new Text( configSection, SWT.READ_ONLY | SWT.WRAP );
    predefinedSubLabel.setText( "Example: \"org: true, com: true, ...\"\n"
                                + "use false for read-only identifiers" );
    predefinedSubLabel.setLayoutData( createGridDataWithIndent( 20 ) );
    predefinedSubLabel.setBackground( configSection.getBackground() );

    Label optionsLabel = new Label( configSection, SWT.NONE );
    optionsLabel.setText( "JSHint Options:" );

    optionsText = new Text( configSection, SWT.BORDER | SWT.MULTI | SWT.WRAP );
    optionsText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    optionsText.setText( preferences.getOptions() );

    Text optionsSubLabel = new Text( configSection, SWT.READ_ONLY | SWT.WRAP );
    optionsSubLabel.setText( "Example: \"strict: false, sub: true, ...\"\n"
                             + "see http://www.jshint.com/options/" );
    optionsSubLabel.setLayoutData( createGridDataWithIndent( 20 ) );
    optionsSubLabel.setBackground( configSection.getBackground() );
  }

  private GridData createGridDataWithIndent( int indent ) {
    GridData predefinedSubData = new GridData();
    predefinedSubData.horizontalIndent = indent;
    return predefinedSubData;
  }

  private boolean storePreferences() throws CoreException {
    ProjectPreferences preferences = getProjectPreferences();
    preferences.setEnabled( enablementCheckbox.getSelection() );
    preferences.setGlobals( predefinedText.getText() );
    preferences.setOptions( optionsText.getText() );
    boolean changed = preferences.hasChanged();
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
