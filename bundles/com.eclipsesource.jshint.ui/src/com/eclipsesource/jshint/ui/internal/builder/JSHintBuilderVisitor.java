/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.JSHint;
import com.eclipsesource.jshint.ProblemHandler;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder.CoreExceptionWrapper;
import com.eclipsesource.jshint.ui.internal.preferences.EnablementPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.JSHintPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.OptionsPreferences;
import com.eclipsesource.jshint.ui.internal.preferences.PreferencesFactory;
import com.eclipsesource.jshint.ui.internal.preferences.ResourceSelector;
import com.eclipsesource.json.JsonObject;


class JSHintBuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

  private final JSHint checker;
  private final ResourceSelector selector;
  private IProgressMonitor monitor;
  private static Pattern errorSwitch = Pattern.compile("\\B[-+][EWI]\\d{3}");

  public JSHintBuilderVisitor( IProject project, IProgressMonitor monitor ) throws CoreException {
    Preferences node = PreferencesFactory.getProjectPreferences( project );
    new EnablementPreferences( node );
    selector = new ResourceSelector( project );
    checker = selector.allowVisitProject() ? createJSHint( getConfiguration( project ), getDefaultAnnotation( project ) ) : null;
    this.monitor = monitor;
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    IResource resource = delta.getResource();
    return visit( resource );
  }

  public boolean visit( IResource resource ) throws CoreException {
    boolean descend = false;
    if( resource.exists() && selector.allowVisitProject() && !monitor.isCanceled() ) {
      if( resource.getType() != IResource.FILE ) {
        descend = selector.allowVisitFolder( resource );
      } else {
        clean( resource );
        if( selector.allowVisitFile( resource ) ) {
          check( (IFile)resource );
        }
        descend = true;
      }
    }
    return descend;
  }

  private JSHint createJSHint( JsonObject configuration, String defaultAnnotation ) throws CoreException {
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
      jshint.configure( configuration, defaultAnnotation );
    } catch( IOException exception ) {
      String message = "Failed to intialize JSHint";
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception ) );
    }
    return jshint;
  }

  private void check( IFile file ) throws CoreException {
    Text code = readContent( file );
    MarkerHandler handler = new MarkerHandler( new MarkerAdapter( file ), code );
    try {
      checker.check( code, handler, handler );
    } catch( CoreExceptionWrapper wrapper ) {
      throw (CoreException)wrapper.getCause();
    } catch( RuntimeException exception ) {
      String message = "Failed checking file " + file.getFullPath().toPortableString();
      throw new RuntimeException( message, exception );
    }
  }

  private static JsonObject getConfiguration( IProject project ) {
    JsonObject configuration;
    Preferences projectNode = PreferencesFactory.getProjectPreferences( project );
    OptionsPreferences projectPreferences = new OptionsPreferences( projectNode );
    if( projectPreferences.getProjectSpecific() ) {
      configuration = projectPreferences.getConfiguration();
    } else {
      Preferences workspaceNode = PreferencesFactory.getWorkspacePreferences();
      OptionsPreferences workspacePreferences = new OptionsPreferences( workspaceNode );
      configuration = workspacePreferences.getConfiguration();
    }
    return configuration;
  }

  private static String getDefaultAnnotation( IProject project ) {
    String defaultAnnotation;
    Matcher match;
    StringBuilder cleaned;
    Preferences projectNode = PreferencesFactory.getProjectPreferences( project );
    OptionsPreferences projectPreferences = new OptionsPreferences( projectNode );
    if( projectPreferences.getProjectSpecific() ) {
	  defaultAnnotation = projectPreferences.getAnnotation();
    } else {
      Preferences workspaceNode = PreferencesFactory.getWorkspacePreferences();
      OptionsPreferences workspacePreferences = new OptionsPreferences( workspaceNode );
      defaultAnnotation = workspacePreferences.getAnnotation();
    }
    if(defaultAnnotation != null) {
    	match = errorSwitch.matcher(defaultAnnotation);
    	if(match.find()) {
    		cleaned = new StringBuilder(match.group());
    		while(match.find())
    			cleaned.append(", ").append(match.group());
    		defaultAnnotation = cleaned.toString();
    	}
    	else
    		defaultAnnotation = null;
    }
    return defaultAnnotation;
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
