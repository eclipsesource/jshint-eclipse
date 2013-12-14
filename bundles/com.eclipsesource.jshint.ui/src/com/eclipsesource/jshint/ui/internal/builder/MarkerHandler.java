/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.builder;

import org.eclipse.core.runtime.CoreException;

import com.eclipsesource.jshint.Problem;
import com.eclipsesource.jshint.ProblemHandler;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder.CoreExceptionWrapper;
import com.eclipsesource.jshint.ui.internal.preferences.JSHintPreferences;


final class MarkerHandler implements ProblemHandler {

  private final MarkerAdapter markerAdapter;
  private final Text code;
  private final boolean enableErrorMarkers;

  MarkerHandler( MarkerAdapter markerAdapter, Text code ) {
    this.markerAdapter = markerAdapter;
    this.code = code;
    enableErrorMarkers = new JSHintPreferences().getEnableErrorMarkers();
  }

  public void handleProblem( Problem problem ) {
    int line = problem.getLine();
    int character = problem.getCharacter();
    if( isValidLine( line ) ) {
      int offset = -1;
      if( isValidCharacter( line, character ) ) {
        offset = code.getLineOffset( line - 1 ) + character;
      }
      createMarker( line, offset, problem.getMessage(), problem.isError() );
    } else {
      createMarker( -1, -1, problem.getMessage(), problem.isError() );
    }
  }

  private void createMarker( int line, int character, String message, boolean isError )
      throws CoreExceptionWrapper
  {
    try {
      if( enableErrorMarkers && isError ) {
        markerAdapter.createError( line, character, character, message );
      } else {
        markerAdapter.createWarning( line, character, character, message );
      }
    } catch( CoreException ce ) {
      throw new CoreExceptionWrapper( ce );
    }
  }

  private boolean isValidLine( int line ) {
    return line >= 1 && line <= code.getLineCount();
  }

  private boolean isValidCharacter( int line, int character ) {
    return character >= 0 && character < code.getLineLength( line - 1 );
  }

}
