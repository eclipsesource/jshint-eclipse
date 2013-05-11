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
  private static final String TYPE_PROBLEM_OLD = "com.eclipsesource.jshint.problemmarker";
  private final IResource resource;

  public MarkerAdapter( IResource resource ) {
    this.resource = resource;
  }

  public void removeMarkers() throws CoreException {
    resource.deleteMarkers( TYPE_PROBLEM, true, IResource.DEPTH_INFINITE );
    resource.deleteMarkers( TYPE_PROBLEM_OLD, true, IResource.DEPTH_INFINITE );
  }

  public void createMarker( int line, int start, int end, String message, String code ) throws CoreException { 
    if( message == null ) {
      throw new NullPointerException( "message is null" );
    }
    IMarker marker = resource.createMarker( TYPE_PROBLEM );
    int severity;
    switch(code.charAt(0))
    {
		case 'I':
			severity = IMarker.SEVERITY_INFO;
			break;
    	case 'W':
    		severity = IMarker.SEVERITY_WARNING;
    		break;
    	case 'E':
    	default:
    		severity = IMarker.SEVERITY_ERROR;
    		break;
    }
    marker.setAttribute( IMarker.SEVERITY, new Integer( severity ) ); 
    marker.setAttribute( IMarker.MESSAGE, new StringBuilder(message).append("   (").append(code).append(")").toString() );
    if( line >= 1 ) {
      // needed to display line number in problems view location column
      marker.setAttribute( IMarker.LINE_NUMBER, line );
    }
    if( start >= 0 ) {
      marker.setAttribute( IMarker.CHAR_START, new Integer( start ) );
      marker.setAttribute( IMarker.CHAR_END, new Integer( end >= start ? end : start ) );
    }
  }

}
