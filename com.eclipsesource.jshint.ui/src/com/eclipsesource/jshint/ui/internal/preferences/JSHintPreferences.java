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
package com.eclipsesource.jshint.ui.internal.preferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.eclipsesource.jshint.ui.internal.Activator;


public class JSHintPreferences {

  private static final String KEY_USE_CUSTOM_LIB = "useCustomJshint";
  private static final String KEY_CUSTOM_LIB_PATH = "customJshintPath";
  private static final boolean DEF_USE_CUSTOM_LIB = false;
  private static final String DEF_CUSTOM_LIB_PATH = "";

  private final IEclipsePreferences node;
  private boolean dirty;
  private boolean useCustomLib;
  private String customLibPath;

  public JSHintPreferences() {
    node = InstanceScope.INSTANCE.getNode( Activator.PLUGIN_ID );
    useCustomLib = node.getBoolean( KEY_USE_CUSTOM_LIB, DEF_USE_CUSTOM_LIB );
    customLibPath = node.get( KEY_CUSTOM_LIB_PATH, DEF_CUSTOM_LIB_PATH );
    dirty = false;
  }

  public void resetToDefaults() {
    setUseCustomLib( DEF_USE_CUSTOM_LIB );
    setCustomLibPath( DEF_CUSTOM_LIB_PATH );
  }

  public boolean getUseCustomLib() {
    return useCustomLib;
  }

  public void setUseCustomLib( boolean useCustomLib ) {
    if( useCustomLib != this.useCustomLib ) {
      this.useCustomLib = useCustomLib;
      dirty = true;
    }
  }

  public String getCustomLibPath() {
    return customLibPath;
  }

  public void setCustomLibPath( String customLibPath ) {
    if( !customLibPath.equals( this.customLibPath ) ) {
      this.customLibPath = customLibPath;
      dirty = true;
    }
  }

  public boolean hasChanged() {
    return dirty;
  }

  public void save() throws CoreException {
    putUseCustomLib();
    putCustomLibPath();
    try {
      node.flush();
      dirty = false;
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

  private void putUseCustomLib() {
    if( useCustomLib == DEF_USE_CUSTOM_LIB ) {
      node.remove( KEY_USE_CUSTOM_LIB );
    } else {
      node.putBoolean( KEY_USE_CUSTOM_LIB, useCustomLib );
    }
  }

  private void putCustomLibPath() {
    if( customLibPath.equals( DEF_CUSTOM_LIB_PATH ) ) {
      node.remove( KEY_CUSTOM_LIB_PATH );
    } else {
      node.put( KEY_CUSTOM_LIB_PATH, customLibPath );
    }
  }

}
