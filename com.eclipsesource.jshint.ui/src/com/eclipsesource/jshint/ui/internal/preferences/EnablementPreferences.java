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

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.osgi.service.prefs.Preferences;


public class EnablementPreferences {

  private static final String KEY_ENABLED = "enabled";
  private static final String KEY_EXCLUDED = "excluded";
  private static final String KEY_INCLUDED = "included";
  private static final boolean DEF_ENABLED = false;
  private static final String DEF_EXCLUDED = "";
  private static final String DEF_INCLUDED = "";

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

  public void setIncluded( String resourcePath, boolean included ) {
    List<String> includedPaths = getIncludedPaths();
    if( included && !includedPaths.contains( resourcePath ) ) {
      includedPaths.add( resourcePath );
    } else if( !included && includedPaths.contains( resourcePath ) ) {
      includedPaths.remove( resourcePath );
    }
    setIncludedPaths( includedPaths );
  }

  public boolean getIncluded( String resourcePath ) {
    List<String> enabledPaths = getIncludedPaths();
    return enabledPaths.contains( resourcePath );
  }

  public void setIncludedPaths( List<String> includedPaths ) {
    String value = PathEncoder.encodePaths( includedPaths );
    if( !value.equals( node.get( KEY_INCLUDED, DEF_INCLUDED ) ) ) {
      if( DEF_INCLUDED.equals( value ) ) {
        node.remove( KEY_INCLUDED );
      } else {
        node.put( KEY_INCLUDED, value );
      }
      changed = true;
    }
  }

  public List<String> getIncludedPaths() {
    String value = node.get( KEY_INCLUDED, DEF_INCLUDED );
    return PathEncoder.decodePaths( value );
  }

  public void setExcluded( String resourcePath, boolean excluded ) {
    List<String> excludedPaths = getExcludedPaths();
    if( excluded && !excludedPaths.contains( resourcePath ) ) {
      excludedPaths.add( resourcePath );
    } else if( !excluded && excludedPaths.contains( resourcePath ) ) {
      excludedPaths.remove( resourcePath );
    }
    setExcludedPaths( excludedPaths );
  }

  public boolean getExcluded( String resourcePath ) {
    List<String> excludedFiles = getExcludedPaths();
    return excludedFiles.contains( resourcePath );
  }

  public void setExcludedPaths( List<String> excluded ) {
    String value = PathEncoder.encodePaths( excluded );
    if( !value.equals( node.get( KEY_EXCLUDED, DEF_EXCLUDED ) ) ) {
      if( DEF_EXCLUDED.equals( value ) ) {
        node.remove( KEY_EXCLUDED );
      } else {
        node.put( KEY_EXCLUDED, value );
      }
      changed = true;
    }
  }

  public List<String> getExcludedPaths() {
    String value = node.get( KEY_EXCLUDED, DEF_EXCLUDED );
    return PathEncoder.decodePaths( value );
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

}
