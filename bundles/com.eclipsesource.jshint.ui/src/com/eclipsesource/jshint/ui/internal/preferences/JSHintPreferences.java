/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;


/**
 * Instances of this class provide a copy of the global JSHint preferences. The preference values
 * are read from the preference store on creation. Modified values will be written back to the store
 * only when <code>save()</code> is called.
 * <p>
 * Instances of this class can be accessed concurrently from multiple threads. However, multiple
 * instances should not be used to concurrently write values to the backing store, otherwise updates
 * may be lost.
 * </p>
 */
public class JSHintPreferences {

  private static final String KEY_USE_CUSTOM_LIB = "useCustomJshint";
  private static final String KEY_CUSTOM_LIB_PATH = "customJshintPath";
  private static final String KEY_ENABLE_ERROR_MARKERS = "enableErrorMarkers";
  private static final boolean DEF_USE_CUSTOM_LIB = false;
  private static final String DEF_CUSTOM_LIB_PATH = "";
  private static final boolean DEF_ENABLE_ERROR_MARKERS = false;

  private final Lock readLock;
  private final Lock writeLock;
  private final Preferences node;
  private boolean useCustomLib;
  private String customLibPath;
  private boolean enableErrorMarkers;
  private boolean dirty;

  public JSHintPreferences() {
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    readLock = readWriteLock.readLock();
    writeLock = readWriteLock.writeLock();
    node = PreferencesFactory.getWorkspacePreferences();
    useCustomLib = node.getBoolean( KEY_USE_CUSTOM_LIB, DEF_USE_CUSTOM_LIB );
    customLibPath = node.get( KEY_CUSTOM_LIB_PATH, DEF_CUSTOM_LIB_PATH );
    enableErrorMarkers = node.getBoolean( KEY_ENABLE_ERROR_MARKERS, DEF_ENABLE_ERROR_MARKERS );
    dirty = false;
  }

  public void resetToDefaults() {
    setUseCustomLib( DEF_USE_CUSTOM_LIB );
    setCustomLibPath( DEF_CUSTOM_LIB_PATH );
    setEnableErrorMarkers( DEF_ENABLE_ERROR_MARKERS );
  }

  public boolean getUseCustomLib() {
    try {
      readLock.lock();
      return useCustomLib;
    } finally {
      readLock.unlock();
    }
  }

  public void setUseCustomLib( boolean useCustomLib ) {
    try {
      writeLock.lock();
      if( useCustomLib != this.useCustomLib ) {
        this.useCustomLib = useCustomLib;
        dirty = true;
      }
    } finally {
      writeLock.unlock();
    }
  }

  public String getCustomLibPath() {
    try {
      readLock.lock();
      return customLibPath;
    } finally {
      readLock.unlock();
    }
  }

  public void setCustomLibPath( String customLibPath ) {
    try {
      writeLock.lock();
      if( !customLibPath.equals( this.customLibPath ) ) {
        this.customLibPath = customLibPath;
        dirty = true;
      }
    } finally {
      writeLock.unlock();
    }
  }

  public boolean getEnableErrorMarkers() {
    try {
      readLock.lock();
      return enableErrorMarkers;
    } finally {
      readLock.unlock();
    }
  }

  public void setEnableErrorMarkers( boolean enableErrorMarkers ) {
    try {
      writeLock.lock();
      if( enableErrorMarkers != this.enableErrorMarkers ) {
        this.enableErrorMarkers = enableErrorMarkers;
        dirty = true;
      }
    } finally {
      writeLock.unlock();
    }
  }

  public boolean hasChanged() {
    try {
      readLock.lock();
      return dirty;
    } finally {
      readLock.unlock();
    }
  }

  public void save() throws CoreException {
    putUseCustomLib();
    putCustomLibPath();
    putEnableErrorMarkers();
    flushNode();
    try {
      writeLock.lock();
      dirty = false;
    } finally {
      writeLock.unlock();
    }
  }

  private void putUseCustomLib() {
    try {
      readLock.lock();
      if( useCustomLib == DEF_USE_CUSTOM_LIB ) {
        node.remove( KEY_USE_CUSTOM_LIB );
      } else {
        node.putBoolean( KEY_USE_CUSTOM_LIB, useCustomLib );
      }
    } finally {
      readLock.unlock();
    }
  }

  private void putCustomLibPath() {
    try {
      readLock.lock();
      if( customLibPath.equals( DEF_CUSTOM_LIB_PATH ) ) {
        node.remove( KEY_CUSTOM_LIB_PATH );
      } else {
        node.put( KEY_CUSTOM_LIB_PATH, customLibPath );
      }
    } finally {
      readLock.unlock();
    }
  }

  private void putEnableErrorMarkers() {
    try {
      readLock.lock();
      if( enableErrorMarkers == DEF_ENABLE_ERROR_MARKERS ) {
        node.remove( KEY_ENABLE_ERROR_MARKERS );
      } else {
        node.putBoolean( KEY_ENABLE_ERROR_MARKERS, enableErrorMarkers );
      }
    } finally {
      readLock.unlock();
    }
  }

  private void flushNode() throws CoreException {
    try {
      node.flush();
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

}
