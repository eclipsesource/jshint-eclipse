/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.eclipse.jshint.properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PropertyPage;

import ralfstx.eclipse.jshint.Activator;


public abstract class AbstractPropertyPage extends PropertyPage {

  private ProjectPreferences projectPreferences;

  protected static Composite createMainComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( createGridLayout( 1, false ) );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    return composite;
  }

  protected static GridLayout createGridLayout( int numColumns, boolean makeColumnsEqualWidth ) {
    GridLayout layout = new GridLayout( numColumns, makeColumnsEqualWidth );
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    return layout;
  }

  protected static Composite createDefaultComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    composite.setLayoutData( new GridData( SWT.FILL, SWT.TOP, true, false ) );
    return composite;
  }

  protected GridData createSpanGridData() {
    GridData labelData = new GridData( SWT.FILL, SWT.TOP, true, false );
    labelData.horizontalSpan = 2;
    return labelData;
  }

  protected IResource getResource() {
    IAdaptable element = getElement();
    if( element instanceof IResource ) {
      return ( IResource )element;
    }
    return ( IResource )element.getAdapter( IResource.class );
  }

  protected void hideButtons() {
    getDefaultsButton().setVisible( false );
    getApplyButton().setVisible( false );
  }

  protected ProjectPreferences getProjectPreferences() {
    if( projectPreferences == null ) {
      projectPreferences = new ProjectPreferences( getResource().getProject() );
    }
    return projectPreferences;
  }

  protected void logError( String message, CoreException exception ) {
    Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
    Platform.getLog( Activator.getDefault().getBundle() ).log( status );
  }

}
