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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;


public class IncludesView extends Composite {

  private ContainerCheckedTreeViewer treeViewer;
  private final IProject project;

  public IncludesView( Composite parent, int style, IProject project ) {
    super( parent, style );
    super.setLayout( createGridLayout( 1, false ) );
    this.project = project;
    createIncludeControls();
  }

  public void loadDefaults() {
    treeViewer.refresh();
    uncheckAllElements();
  }

  public void loadPreferences( EnablementPreferences preferences ) {
    treeViewer.refresh();
    uncheckAllElements();
    List<String> includes = preferences.getIncludedPaths();
    checkIncludedElements( includes );
  }

  public void storePreferences( EnablementPreferences preferences ) {
    List<String> selectedPaths = getSelectedPaths();
    preferences.setIncludedPaths( selectedPaths );
  }

  @Override
  public void setLayout( Layout layout ) {
    // prevent changing the default layout
  }

  private void createIncludeControls() {
    new Label( this, SWT.NONE ).setText( "Enable JSHint for the selected directories:" );
    treeViewer = new ContainerCheckedTreeViewer( this, SWT.SINGLE | SWT.BORDER );
    treeViewer.getTree().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    treeViewer.setContentProvider( new FoldersWorkbenchContentProvider() );
    treeViewer.setLabelProvider( new WorkbenchLabelProvider() );
    treeViewer.setInput( project );
    treeViewer.expandToLevel( 2 );
  }

  private void uncheckAllElements() {
    ITreeContentProvider contentProvider = getContentProvider();
    for( Object element : contentProvider.getElements( project ) ) {
      uncheckAllElements( contentProvider, element );
    }
  }

  private void uncheckAllElements( ITreeContentProvider contentProvider, Object element ) {
    treeViewer.setChecked( element, false );
    for( Object child : contentProvider.getChildren( element ) ) {
      uncheckAllElements( contentProvider, child );
    }
  }

  private void checkIncludedElements( List<String> includes ) {
    ITreeContentProvider contentProvider = getContentProvider();
    for( Object element : contentProvider.getElements( project ) ) {
      checkIncludedElements( contentProvider, includes, element );
    }
  }

  private void checkIncludedElements( ITreeContentProvider contentProvider,
                                      List<String> includes,
                                      Object element )
  {
    String path = getElementPath( element );
    if( includes.contains( path ) ) {
      treeViewer.setChecked( element, true );
    }
    for( Object child : contentProvider.getChildren( element ) ) {
      checkIncludedElements( contentProvider, includes, child );
    }
  }

  private List<String> getSelectedPaths() {
    ITreeContentProvider contentProvider = getContentProvider();
    ArrayList<String> selected = new ArrayList<String>();
    for( Object element : contentProvider.getElements( project ) ) {
      getSelectedPaths( contentProvider, selected, element );
    }
    return selected;
  }

  private void getSelectedPaths( ITreeContentProvider contentProvider,
                                 ArrayList<String> selected,
                                 Object element )
  {
    if( treeViewer.getChecked( element ) && !treeViewer.getGrayed( element ) ) {
      selected.add( getElementPath( element ) );
    } else {
      for( Object child : contentProvider.getChildren( element ) ) {
        getSelectedPaths( contentProvider, selected, child );
      }
    }
  }

  private ITreeContentProvider getContentProvider() {
    return (ITreeContentProvider)treeViewer.getContentProvider();
  }

  private static String getElementPath( Object element ) {
    return EnablementPreferences.getResourcePath( (IResource)element );
  }

  private static GridLayout createGridLayout( int numColumns, boolean makeColumnsEqualWidth ) {
    GridLayout layout = new GridLayout( numColumns, makeColumnsEqualWidth );
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    return layout;
  }

  private final class FoldersWorkbenchContentProvider extends BaseWorkbenchContentProvider {

    @Override
    public Object[] getChildren( Object element ) {
      List<Object> result = new ArrayList<Object>();
      Object[] children = super.getChildren( element );
      for( Object child : children ) {
        if( child instanceof IFolder ) {
          result.add( child );
        }
      }
      return result.toArray();
    }
  }

}
