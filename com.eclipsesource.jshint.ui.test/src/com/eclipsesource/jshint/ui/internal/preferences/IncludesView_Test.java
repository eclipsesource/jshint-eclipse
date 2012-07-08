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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.jshint.ui.internal.preferences.ui.IncludesView;

import static com.eclipsesource.jshint.ui.test.TestUtil.*;
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
    project = createProject( "test" );
    preferences = new EnablementPreferences( new PreferencesMock( "test" ) );
  }

  @After
  public void tearDown() {
    deleteProject( project );
    parent.dispose();
  }

  @Test
  public void tablesEmptyByDefault() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );

    SWTBotTable includeTable = findIncludeTable( view );
    SWTBotTable excludeTable = findExcludeTable( view );
    assertEquals( 0, includeTable.rowCount() );
    assertEquals( 0, excludeTable.rowCount() );
  }

  @Test
  public void loadDefaults() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    SWTBotTable includeTable = findIncludeTable( view );
    SWTBotTable excludeTable = findExcludeTable( view );
    new TableItem( includeTable.widget, SWT.NONE ).setText( "/foo/" );
    new TableItem( excludeTable.widget, SWT.NONE ).setText( "/bar/" );

    view.loadDefaults();

    assertEquals( 0, includeTable.rowCount() );
    assertEquals( 0, excludeTable.rowCount() );
  }

  @Test
  public void loadPreferences() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    preferences.setIncludePatterns( list( "/foo/" ) );
    preferences.setExcludePatterns( list( "/bar/" ) );

    view.loadPreferences( preferences );

    SWTBotTable includeTable = findIncludeTable( view );
    SWTBotTable excludeTable = findExcludeTable( view );
    assertEquals( 1, includeTable.rowCount() );
    assertEquals( 1, excludeTable.rowCount() );
    assertEquals( "/foo/", includeTable.getTableItem( 0 ).getText() );
    assertEquals( "/bar/", excludeTable.getTableItem( 0 ).getText() );
  }

  @Test
  public void loadPreferences_changed() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    preferences.setIncludePatterns( list( "/foo/" ) );
    view.loadPreferences( preferences );

    preferences.setIncludePatterns( list( "/bar/" ) );
    view.loadPreferences( preferences );

    SWTBotTable table = findIncludeTable( view );
    assertEquals( 1, table.rowCount() );
    assertEquals( "/bar/", table.getTableItem( 0 ).getText() );
  }

  @Test
  public void storePreferences() {
    IncludesView view = new IncludesView( parent, SWT.NONE, project );
    SWTBotTable includeTable = findIncludeTable( view );
    SWTBotTable excludeTable = findExcludeTable( view );
    new TableItem( includeTable.widget, SWT.NONE ).setText( "/foo/" );
    new TableItem( excludeTable.widget, SWT.NONE ).setText( "/bar/" );

    view.storePreferences( preferences );

    assertTrue( preferences.getIncludePatterns().contains( "/foo/" ) );
    assertTrue( preferences.getExcludePatterns().contains( "/bar/" ) );
  }

  private static SWTBotTable findIncludeTable( Composite parent ) {
    return new SWTBot( parent ).table( 0 );
  }

  private static SWTBotTable findExcludeTable( Composite parent ) {
    return new SWTBot( parent ).table( 1 );
  }

}
