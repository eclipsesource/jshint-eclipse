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
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.Configuration;
import com.eclipsesource.jshint.ui.internal.Activator;
import com.eclipsesource.jshint.ui.internal.properties.OptionParserUtil;


public class OptionsPreferences {

  private static final String KEY_GLOBALS = "globals";
  private static final String KEY_OPTIONS = "options";
  private static final String DEF_GLOBALS = "";
  private static final String DEF_OPTIONS = "";

  private final Preferences node;
  private boolean changed;

  public OptionsPreferences( Preferences node ) {
    this.node = node;
  }

  public String getGlobals() {
    return node.get( KEY_GLOBALS, DEF_GLOBALS );
  }

  public void setGlobals( String value ) {
    if( !value.equals( node.get( KEY_GLOBALS, DEF_GLOBALS ) ) ) {
      if( DEF_GLOBALS.equals( value ) ) {
        node.remove( KEY_GLOBALS );
      } else {
        node.put( KEY_GLOBALS, value );
      }
      changed = true;
    }
  }

  public String getOptions() {
    return node.get( KEY_OPTIONS, DEF_OPTIONS );
  }

  public void setOptions( String value ) {
    if( !value.equals( node.get( KEY_OPTIONS, DEF_OPTIONS ) ) ) {
      if( value.equals( DEF_OPTIONS ) ) {
        node.remove( KEY_OPTIONS );
      } else {
        node.put( KEY_OPTIONS, value );
      }
      changed = true;
    }
  }

  public Configuration getConfiguration() {
    String options = getOptions();
    String globals = getGlobals();
    return OptionParserUtil.createConfiguration( options, globals );
  }

  public boolean hasChanged() {
    return changed;
  }

  public void save() throws CoreException {
    try {
      node.flush();
      changed = false;
    } catch( BackingStoreException exception ) {
      String message = "Failed to store preferences";
      Status status = new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception );
      throw new CoreException( status );
    }
  }

}
