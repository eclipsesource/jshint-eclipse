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
package com.eclipsesource.jshint.ui.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;


public class TestUtil {

  public static final String BUILDER_ID = "com.eclipsesource.jshint.ui.builder";
  public static final String OLD_BUILDER_ID = "com.eclipsesource.jshint.builder";
  public static final String SETTINGS_FOLDER_PATH = "/.settings";
  public static final String OLD_SETTINGS_FILE = "com.eclipsesource.jshint.prefs";
  public static final String NEW_SETTINGS_FILE = "com.eclipsesource.jshint.ui.prefs";

  private TestUtil() {
  }

  public static IProject createProject( String name ) {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IProject project = workspace.getRoot().getProject( name );
    try {
      project.create( null );
      project.open( null );
    } catch( CoreException exception ) {
      throw new RuntimeException( exception );
    }
    return project;
  }

  public static void deleteProject( IProject project ) {
    if( project != null && project.exists() ) {
      try {
        project.delete( true, null );
      } catch( CoreException exception ) {
        throw new RuntimeException( exception );
      }
    }
  }

  public static IFolder createFolder( IProject project, String path ) {
    IFolder folder = project.getFolder( path );
    try {
      folder.create( false, false, null );
    } catch( CoreException exception ) {
      throw new RuntimeException( exception );
    }
    return folder;
  }

  public static IFile createFile( IProject project, String name, String content )
      throws CoreException
  {
    IFile file = project.getFile( name );
    file.create( new ByteArrayInputStream( content.getBytes() ), true, null );
    return file;
  }

  public static String readContent( IFile resource ) throws CoreException, IOException {
    String content = null;
    InputStream inputStream = resource.getContents();
    if( inputStream != null ) {
      try {
        content = readContent( inputStream, "UTF-8" );
      } finally {
        inputStream.close();
      }
    }
    return content;
  }

  public static String getExampleContent( String resourceName ) throws IOException {
    String content = null;
    InputStream inputStream = TestUtil.class.getResourceAsStream( resourceName );
    if( inputStream != null ) {
      try {
        content = readContent( inputStream, "UTF-8" );
      } finally {
        inputStream.close();
      }
    }
    return content;
  }

  public static void createExampleSettingsFile( IProject project, String settingsFileName )
      throws CoreException, IOException
  {
    IFolder folder = createSettingsFolder( project );
    IFile settingsFile = folder.getFile( settingsFileName );
    InputStream inputStream = TestUtil.class.getResourceAsStream( settingsFileName );
    if( inputStream != null ) {
      try {
        settingsFile.create( inputStream, true, null );
      } finally {
        inputStream.close();
      }
    }
  }

  public static IFolder createSettingsFolder( IProject project ) throws CoreException {
    IFolder folder = project.getFolder( SETTINGS_FOLDER_PATH );
    if( !folder.exists() ) {
      folder.create( true, true, null );
    }
    return folder;
  }

  private static String readContent( InputStream inputStream, String charset )
      throws UnsupportedEncodingException, IOException
  {
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    StringBuilder builder = new StringBuilder();
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

}
