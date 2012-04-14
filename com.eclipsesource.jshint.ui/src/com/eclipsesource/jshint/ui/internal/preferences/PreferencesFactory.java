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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import com.eclipsesource.jshint.ui.internal.Activator;


public class PreferencesFactory {

  public static Preferences getProjectPreferences( IProject project ) {
    return new ProjectScope( project ).getNode( Activator.PLUGIN_ID );
  }

  @SuppressWarnings( "deprecation" )
  public static Preferences getWorkspacePreferences() {
    // InstanceScope.INSTANCE does not yet exist in Eclipse 3.6
    return new InstanceScope().getNode( Activator.PLUGIN_ID );
  }

}
