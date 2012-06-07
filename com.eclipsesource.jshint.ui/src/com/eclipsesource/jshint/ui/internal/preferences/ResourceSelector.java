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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.osgi.service.prefs.Preferences;


public class ResourceSelector {

  private final List<PathPattern> includePatterns;
  private final List<PathPattern> excludePatterns;

  public ResourceSelector( IProject project ) {
    Preferences preferenceNode = PreferencesFactory.getProjectPreferences( project );
    EnablementPreferences preferences = new EnablementPreferences( preferenceNode );
    includePatterns = createPatterns( preferences.getIncludePatterns() );
    excludePatterns = createPatterns( preferences.getExcludePatterns() );
  }

  public boolean allowVisitProject() {
    return !includePatterns.isEmpty();
  }

  public boolean allowVisitFolder( IResource resource ) {
    return !includePatterns.isEmpty();
  }

  public boolean allowVisitFile( IResource resource ) {
    String[] pathSegments = resource.getParent().getProjectRelativePath().segments();
    String fileName = resource.getName();
    return isFileIncluded( pathSegments, fileName ) && !isFileExcluded( pathSegments, fileName );
  }

  private boolean isFileIncluded( String[] parentSegments, String fileName ) {
    for( PathPattern pattern : includePatterns ) {
      if( pattern.matchesFolder( parentSegments ) ) {
        if( pattern.matchesFile( fileName ) ) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isFileExcluded( String[] parentSegments, String fileName ) {
    for( PathPattern pattern : excludePatterns ) {
      if( pattern.matchesFolder( parentSegments ) ) {
        if( pattern.matchesFile( fileName ) ) {
          return true;
        }
      }
    }
    return false;
  }

  private static List<PathPattern> createPatterns( List<String> expressions ) {
    List<PathPattern> patterns = new ArrayList<PathPattern>( expressions.size() );
    for( String expression : expressions ) {
      patterns.add( PathPattern.create( expression ) );
    }
    return patterns;
  }

}
