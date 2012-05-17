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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.service.prefs.Preferences;


public class ResourceSelector {

  private final EnablementPreferences preferences;

  public ResourceSelector( IProject project ) {
    Preferences preferenceNode = PreferencesFactory.getProjectPreferences( project );
    this.preferences = new EnablementPreferences( preferenceNode );
  }

  public boolean isIncluded( IResource resource ) {
    boolean result = false;
    int type = resource.getType();
    if( type == IResource.PROJECT ) {
      result = isProjectIncluded();
    } else if( type ==  IResource.FOLDER ) {
      result = isPrefixPathIncluded( resource ) || isChildPathIncluded( resource );
    } else if( type ==  IResource.FILE ) {
      result = "js".equals( resource.getFileExtension() ) && isPrefixPathIncluded( resource );
    }
    return result;
  }

  public boolean isProjectIncluded() {
    return !preferences.getIncludedPaths().isEmpty();
  }

  private boolean isPrefixPathIncluded( IResource resource ) {
    IPath projectRelativePath = resource.getProjectRelativePath();
    List<String> includedPaths = preferences.getIncludedPaths();
    for( String path : includedPaths ) {
      if( new Path( path ).isPrefixOf( projectRelativePath ) ) {
        return true;
      }
    }
    return false;
  }

  private boolean isChildPathIncluded( IResource resource ) {
    IPath projectRelativePath = resource.getProjectRelativePath();
    List<String> includedPaths = preferences.getIncludedPaths();
    for( String path : includedPaths ) {
      if( projectRelativePath.isPrefixOf( new Path( path ) ) ) {
        return true;
      }
    }
    return false;
  }

}
