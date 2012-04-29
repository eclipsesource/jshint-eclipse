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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.osgi.service.prefs.Preferences;


public class EnablementPreferences {

  private static final String KEY_ENABLED = "enabled";
  private static final String KEY_EXCLUDED = "excluded";
  private static final boolean DEF_ENABLED = false;
  private static final String DEF_EXCLUDED = "";

  private final Preferences node;
  private boolean changed;

  public EnablementPreferences( Preferences node ) {
    this.node = node;
    changed = false;
  }

  public void setEnabled( boolean enabled ) {
    if( enabled != node.getBoolean( KEY_ENABLED, DEF_ENABLED ) ) {
      if( enabled == DEF_ENABLED ) {
        node.remove( KEY_ENABLED );
      } else {
        node.putBoolean( KEY_ENABLED, enabled );
      }
      changed = true;
    }
  }

  public boolean getEnabled() {
    return node.getBoolean( KEY_ENABLED, DEF_ENABLED );
  }

  public void setExcluded( String resourcePath, boolean exclude ) {
    List<String> excluded = getExcluded();
    if( exclude && !excluded.contains( resourcePath ) ) {
      excluded.add( resourcePath );
    } else if( !exclude && excluded.contains( resourcePath ) ) {
      excluded.remove( resourcePath );
    }
    setExcluded( excluded );
  }

  public boolean getExcluded( String resourcePath ) {
    List<String> excludedFiles = getExcluded();
    return excludedFiles.contains( resourcePath );
  }

  public List<String> getExcluded() {
    String value = node.get( KEY_EXCLUDED, DEF_EXCLUDED );
    return decodePaths( value );
  }

  public void setExcluded( List<String> excluded ) {
    String value = encodePaths( excluded );
    if( !value.equals( node.get( KEY_EXCLUDED, DEF_EXCLUDED ) ) ) {
      if( DEF_EXCLUDED.equals( value ) ) {
        node.remove( KEY_EXCLUDED );
      } else {
        node.put( KEY_EXCLUDED, value );
      }
      changed = true;
    }
  }

  public boolean hasChanged() {
    return changed;
  }

  public void clearChanged() {
    changed = false;
  }

  public static String getResourcePath( IResource resource ) {
    return resource.getProjectRelativePath().toPortableString();
  }

  private static String encodePaths( List<String> paths ) {
    StringBuilder builder = new StringBuilder();
    for( String path : paths ) {
      if( builder.length() > 0 ) {
        builder.append( ':' );
      }
      builder.append( path );
    }
    return builder.toString();
  }

  private static ArrayList<String> decodePaths( String encodedPaths ) {
    ArrayList<String> list = new ArrayList<String>();
    for( String path : encodedPaths.split( ":" ) ) {
      if( !path.isEmpty() ) {
        list.add( path );
      }
    }
    return list;
  }

}
