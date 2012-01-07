/*******************************************************************************
 * Copyright (c) 2012 Ralf Sternberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package ralfstx.eclipse.jshint.builder;

import org.eclipse.core.runtime.CoreException;

import ralfstx.eclipse.jshint.ErrorHandler;
import ralfstx.eclipse.jshint.Text;
import ralfstx.eclipse.jshint.builder.JSHintBuilder.CoreExceptionWrapper;


final class MarkerErrorHandler implements ErrorHandler {

  private final MarkerAdapter markerAdapter;
  private final Text code;

  MarkerErrorHandler( MarkerAdapter markerAdapter, Text code ) {
    this.markerAdapter = markerAdapter;
    this.code = code;
  }

  public void handleError( int line, int character, String message ) {
    try {
      int start = code.getLineOffset( line - 1 ) + character - 1;
      int end = start;
      markerAdapter.createMarker( line, start, end, message );
    } catch( CoreException ce ) {
      throw new CoreExceptionWrapper( ce );
    }
  }

}
