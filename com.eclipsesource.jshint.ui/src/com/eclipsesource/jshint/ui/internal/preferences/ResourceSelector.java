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

import org.eclipse.core.resources.IProject;
import org.osgi.service.prefs.Preferences;


public class ResourceSelector {

  private final EnablementPreferences preferences;

  public ResourceSelector( IProject project ) {
    Preferences preferenceNode = PreferencesFactory.getProjectPreferences( project );
    this.preferences = new EnablementPreferences( preferenceNode );
  }

  public boolean includeProject() {
    return !preferences.getIncludedPaths().isEmpty();
  }

}
