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
package com.eclipsesource.jshint.ui.internal.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.Configuration;
import com.eclipsesource.jshint.JSHint;
import com.eclipsesource.jshint.ProblemHandler;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder.CoreExceptionWrapper;
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.JSHintPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;


class JSHintBuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

  private final JSHint checker;
  private final EnablementPreferences preferences;

  public JSHintBuilderVisitor( IProject project ) throws CoreException {
    Preferences node = PreferencesFactory.getProjectPreferences( project );
    preferences = new EnablementPreferences( node );
    OptionsPreferences optionsPreferences = new OptionsPreferences( node );
    checker = preferences.getEnabled() ? createJSHint( optionsPreferences.getConfiguration() ) : null;
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    IResource resource = delta.getResource();
    return visit( resource );
  }

  public boolean visit( IResource resource ) throws CoreException {
    boolean descend = false;
    if( resource.exists() && preferences.getEnabled() ) {
      if( resource.getType() != IResource.FILE ) {
        descend = considerContainer( resource );
      } else {
        clean( resource );
        if( considerFile( resource ) ) {
          check( (IFile)resource );
        }
        descend = true;
      }
    }
    return descend;
  }

  private JSHint createJSHint( Configuration configuration ) throws CoreException {
    JSHint jshint = new JSHint();
    try {
      InputStream inputStream = getCustomLib();
      if( inputStream != null ) {
        try {
          jshint.load( inputStream );
        } finally {
          inputStream.close();
        }
      } else {
        jshint.load();
      }
      jshint.configure( configuration );
    } catch( IOException exception ) {
      String message = "Failed to intialize JSHint";
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception ) );
    }
    return jshint;
  }

  private void check( IFile file ) throws CoreException {
    Text code = readContent( file );
    ProblemHandler handler = new MarkerHandler( new MarkerAdapter( file ), code );
    try {
      checker.check( code.getContent(), handler );
    } catch( CoreExceptionWrapper wrapper ) {
      throw (CoreException)wrapper.getCause();
    }
  }

  private boolean considerContainer( IResource resource ) {
    if( preferences.getExcluded( EnablementPreferences.getResourcePath( resource ) ) ) {
      return false;
    }
    Path binPath = new Path( "bin" );
    if( binPath.isPrefixOf( resource.getProjectRelativePath() ) ) {
      return false;
    }
    return true;
  }

  private boolean considerFile( IResource resource ) {
    if( !"js".equals( resource.getFileExtension() ) ) {
      return false;
    }
    if( preferences.getExcluded( EnablementPreferences.getResourcePath( resource ) ) ) {
      return false;
    }
    return true;
  }

  private static void clean( IResource resource ) throws CoreException {
    new MarkerAdapter( resource ).removeMarkers();
  }

  private static InputStream getCustomLib() throws FileNotFoundException {
    JSHintPreferences globalPrefs = new JSHintPreferences();
    if( globalPrefs.getUseCustomLib() ) {
      File file = new File( globalPrefs.getCustomLibPath() );
      return new FileInputStream( file );
    }
    return null;
  }

  private static Text readContent( IFile file ) throws CoreException {
    try {
      InputStream inputStream = file.getContents();
      String charset = file.getCharset();
      return readContent( inputStream, charset );
    } catch( IOException exception ) {
      String message = "Failed to read resource";
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception ) );
    }
  }

  private static Text readContent( InputStream inputStream, String charset )
      throws UnsupportedEncodingException, IOException
  {
    Text result;
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    try {
      result = new Text( reader );
    } finally {
      reader.close();
    }
    return result;
  }

}
