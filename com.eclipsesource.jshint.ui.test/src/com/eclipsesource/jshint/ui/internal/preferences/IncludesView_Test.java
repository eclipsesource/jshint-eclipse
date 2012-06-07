/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.preferences.ui.IncludesView;
import com.eclipsesource.jshint.ui.test.TestUtil;

import static com.eclipsesource.jshint.ui.test.TestUtil.list;
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
  public void tablesEmptyByDefault() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    Table includeTable = findIncludeTable( view );
    Table excludeTable = findExcludeTable( view );
    assertEquals( 0, includeTable.getItemCount() );
    assertEquals( 0, excludeTable.getItemCount() );
  }

  @Test
  public void loadDefaults() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    Table includeTable = findIncludeTable( view );
    Table excludeTable = findExcludeTable( view );
    new TableItem( includeTable, SWT.NONE ).setText( "/foo/" );
    new TableItem( excludeTable, SWT.NONE ).setText( "/bar/" );

    view.loadDefaults();

    assertEquals( 0, includeTable.getItemCount() );
    assertEquals( 0, excludeTable.getItemCount() );
  }

  @Test
  public void loadPreferences() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    preferences.setIncludePatterns( list( "/foo/" ) );
    preferences.setExcludePatterns( list( "/bar/" ) );

    view.loadPreferences( preferences );

    Table includeTable = findIncludeTable( view );
    Table excludeTable = findExcludeTable( view );
    assertEquals( 1, includeTable.getItemCount() );
    assertEquals( 1, excludeTable.getItemCount() );
    assertEquals( "/foo/", includeTable.getItem( 0 ).getText() );
    assertEquals( "/bar/", excludeTable.getItem( 0 ).getText() );
  }

  @Test
  public void loadPreferences_changed() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    preferences.setIncludePatterns( list( "/foo/" ) );
    view.loadPreferences( preferences );

    preferences.setIncludePatterns( list( "/bar/" ) );
    view.loadPreferences( preferences );

    Table table = findIncludeTable( view );
    assertEquals( 1, table.getItemCount() );
    assertEquals( "/bar/", table.getItem( 0 ).getText() );
  }

  @Test
  public void storePreferences() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    Table includeTable = findIncludeTable( view );
    Table excludeTable = findExcludeTable( view );
    new TableItem( includeTable, SWT.NONE ).setText( "/foo/" );
    new TableItem( excludeTable, SWT.NONE ).setText( "/bar/" );

    view.storePreferences( preferences );

    assertTrue( preferences.getIncludePatterns().contains( "/foo/" ) );
    assertTrue( preferences.getExcludePatterns().contains( "/bar/" ) );
  }

  private static Table findIncludeTable( Composite parent ) {
    return findTable( parent, 0 );
  }

  private static Table findExcludeTable( Composite parent ) {
    return findTable( parent, 1 );
  }

  private static Table findTable( Composite parent, int number ) {
    for( Control child : parent.getChildren() ) {
      if( child instanceof Table && number-- == 0 ) {
        return (Table)child;
      }
    }
    return null;
  }

}
