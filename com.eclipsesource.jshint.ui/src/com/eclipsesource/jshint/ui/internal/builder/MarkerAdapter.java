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
package com.eclipsesource.jshint.ui.internal.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


public class MarkerAdapter {

  private static final String TYPE_PROBLEM = "com.eclipsesource.jshint.ui.problemmarker";
  private final IResource resource;

  public MarkerAdapter( IResource resource ) {
    this.resource = resource;
  }

  public void removeMarkers() throws CoreException {
    resource.deleteMarkers( TYPE_PROBLEM, true, IResource.DEPTH_INFINITE );
  }

  public void createMarker( int lineNr, int start, int end, String message ) throws CoreException {
    IMarker marker = resource.createMarker( TYPE_PROBLEM );
    marker.setAttribute( IMarker.SEVERITY, new Integer( IMarker.SEVERITY_WARNING ) );
    marker.setAttribute( IMarker.MESSAGE, message );
    marker.setAttribute( IMarker.LINE_NUMBER, new Integer( lineNr ) );
    if( start >= 0 ) {
      marker.setAttribute( IMarker.CHAR_START, new Integer( start ) );
      marker.setAttribute( IMarker.CHAR_END, new Integer( end >= 0 ? end : start ) );
    }
  }

}
