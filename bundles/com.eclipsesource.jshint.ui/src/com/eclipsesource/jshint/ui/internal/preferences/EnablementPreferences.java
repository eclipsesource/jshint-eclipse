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

  private static final String KEY_EXCLUDED = "excluded";
  private static final String KEY_INCLUDED = "included";
  private static final String DEF_EXCLUDED = "";
  private static final String DEF_INCLUDED = "";

  private final Preferences node;
  private boolean changed;

  public EnablementPreferences( Preferences node ) {
    this.node = node;
    changed = false;
  }

  public void setIncludePatterns( List<String> patterns ) {
    String value = PathEncoder.encodePaths( patterns );
    if( !value.equals( node.get( KEY_INCLUDED, DEF_INCLUDED ) ) ) {
      if( DEF_INCLUDED.equals( value ) ) {
        node.remove( KEY_INCLUDED );
      } else {
        node.put( KEY_INCLUDED, value );
      }
      changed = true;
    }
  }

  public List<String> getIncludePatterns() {
    String value = node.get( KEY_INCLUDED, DEF_INCLUDED );
    return PathEncoder.decodePaths( value );
  }

  public void setExcludePatterns( List<String> patterns ) {
    String value = PathEncoder.encodePaths( patterns );
    if( !value.equals( node.get( KEY_EXCLUDED, DEF_EXCLUDED ) ) ) {
      if( DEF_EXCLUDED.equals( value ) ) {
        node.remove( KEY_EXCLUDED );
      } else {
        node.put( KEY_EXCLUDED, value );
      }
      changed = true;
    }
  }

  public List<String> getExcludePatterns() {
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
