/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ralf - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.preferences.ui.IncludesView;
import com.eclipsesource.jshint.ui.test.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class IncludesView_Test {

  private Composite parent;
  private IProject project;
  private EnablementPreferences preferences;

  @Before
  public void setUp() {
    Display display = Display.getDefault();
    parent = new Shell( display );
    project = TestUtil.createProject( "test" );
    preferences = new EnablementPreferences( new PreferencesMock( "test" ) );
  }

  @After
  public void tearDown() {
    TestUtil.deleteProject( project );
    parent.dispose();
  }

  @Test
  public void treeEmptyByDefault() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    Tree tree = findTree( view );
    assertEquals( 0, tree.getItemCount() );
  }

  @Test
  public void treeItemTexts() {
    createSampleFolders();

    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    Tree tree = findTree( view );
    assertEquals( 2, tree.getItemCount() );
    assertEquals( "a", tree.getItem( 0 ).getText() );
    assertEquals( "1", tree.getItem( 0 ).getItem( 0 ).getText() );
    assertEquals( "2", tree.getItem( 0 ).getItem( 1 ).getText() );
    assertEquals( "b", tree.getItem( 1 ).getText() );
  }

  @Test
  public void treeItemsUncheckedByDefault() {
    createSampleFolders();

    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    Tree tree = findTree( view );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ).getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 1 ) ) );
  }

  @Test
  public void loadDefaults() {
    createSampleFolders();
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    Tree tree = findTree( view );
    tree.getItem( 0 ).setChecked( true );
    tree.getItem( 1 ).getItem( 0 ).setChecked( true );

    view.loadDefaults();

    assertEquals( "[ ]", getItemState( tree.getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ).getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 1 ) ) );
  }

  @Test
  public void loadPreferences() {
    createSampleFolders();
    preferences.setIncluded( "a", true );
    preferences.setIncluded( "b/1", true );
    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    view.loadPreferences( preferences );

    Tree tree = findTree( view );
    assertEquals( "[x]", getItemState( tree.getItem( 0 ) ) );
    assertEquals( "[x]", getItemState( tree.getItem( 0 ).getItem( 0 ) ) );
    assertEquals( "[x]", getItemState( tree.getItem( 0 ).getItem( 1 ) ) );
    assertEquals( "[-]", getItemState( tree.getItem( 1 ) ) );
    assertEquals( "[x]", getItemState( tree.getItem( 1 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 1 ) ) );
  }

  @Test
  public void loadPreferences_changed() {
    createSampleFolders();
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    preferences.setIncluded( "a", true );
    preferences.setIncluded( "b/1", true );
    view.loadPreferences( preferences );

    preferences.setIncluded( "a", false );
    preferences.setIncluded( "b/1", false );
    preferences.setIncluded( "a/1", true );
    view.loadPreferences( preferences );

    Tree tree = findTree( view );
    assertEquals( "[-]", getItemState( tree.getItem( 0 ) ) );
    assertEquals( "[x]", getItemState( tree.getItem( 0 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 0 ).getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 0 ) ) );
    assertEquals( "[ ]", getItemState( tree.getItem( 1 ).getItem( 1 ) ) );
  }

  @Test
  public void storePreferences() {
    createSampleFolders();
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    Tree tree = findTree( view );
    tree.getItem( 0 ).setChecked( true );
    tree.getItem( 1 ).getItem( 0 ).setChecked( true );

    view.storePreferences( preferences );

    List<String> includedPaths = preferences.getIncludedPaths();
    assertTrue( includedPaths.contains( "a" ) );
    assertTrue( includedPaths.contains( "b/1" ) );
  }

  private void createSampleFolders() {
    TestUtil.createFolder( project, "/a" );
    TestUtil.createFolder( project, "/a/1" );
    TestUtil.createFolder( project, "/a/2" );
    TestUtil.createFolder( project, "/b" );
    TestUtil.createFolder( project, "/b/1" );
    TestUtil.createFolder( project, "/b/2" );
  }

  private static Tree findTree( Composite parent ) {
    Control[] children = parent.getChildren();
    for( Control child : children ) {
      if( child instanceof Tree ) {
        Tree tree = (Tree)child;
        return tree;
      }
    }
    return null;
  }

  private static String getItemState( TreeItem item ) {
    if( item.getChecked() ) {
      if( item.getGrayed() ) {
        return "[-]";
      } else {
        return "[x]";
      }
    } else {
      return "[ ]";
    }
  }

}
