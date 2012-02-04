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

import org.eclipse.core.runtime.CoreException;

import com.eclipsesource.jshint.Problem;
import com.eclipsesource.jshint.ProblemHandler;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder.CoreExceptionWrapper;


final class MarkerHandler implements ProblemHandler {

  private final MarkerAdapter markerAdapter;
  private final Text code;

  MarkerHandler( MarkerAdapter markerAdapter, Text code ) {
    this.markerAdapter = markerAdapter;
    this.code = code;
  }

  public void handleProblem( Problem problem ) {
    int line = problem.getLine();
    int character = problem.getCharacter();
    int start = code.getLineOffset( line - 1 ) + character - 1;
    int end = start;
    String message = problem.getMessage();
    try {
      markerAdapter.createMarker( line, start, end, message );
    } catch( CoreException ce ) {
      throw new CoreExceptionWrapper( ce );
    }
  }

}
