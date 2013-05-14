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
import com.eclipsesource.jshint.Task;
import com.eclipsesource.jshint.TaskHandler;
import com.eclipsesource.jshint.TaskTag;
import com.eclipsesource.jshint.Text;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder.CoreExceptionWrapper;


final class MarkerHandler implements ProblemHandler, TaskHandler {

  private final MarkerAdapter markerAdapter;
  private final Text code;

  MarkerHandler( MarkerAdapter markerAdapter, Text code ) {
    this.markerAdapter = markerAdapter;
    this.code = code;
  }

  public void handleProblem( Problem problem ) {
    int line = problem.getLine();
    int startCharacter = problem.getStartCharacter();
    int stopCharacter = problem.getStopCharacter();
    String codeStr = problem.getCode();
    String message = problem.getMessage();
    if( isValidLine( line ) ) {
      int start = -1;
      int stop;
      if( isValidCharacter( line, startCharacter ) ) {
        start = code.getLineOffset( line - 1 ) + startCharacter;
      }
      if( isValidCharacter( line, stopCharacter ) ) {
    	  stop = code.getLineOffset( line - 1 ) + stopCharacter;
      }
      else
    	  stop = start;
      createProblemMarker( line, start, stop, message, codeStr );
    } else {
      createProblemMarker( -1, -1, -1, message, codeStr );
    }
  }

  private void createProblemMarker( int line, int start, int stop, String message, String codeStr ) throws CoreExceptionWrapper {
    try {
      markerAdapter.createProblemMarker( line, start, stop, message, codeStr );
    } catch( CoreException ce ) {
      throw new CoreExceptionWrapper( ce );
    }
  }

  public void handleTask( Task task ) {
	  int line = task.getLine();
	  int startCharacter = task.getStartCharacter();
	  int stopCharacter = task.getStopCharacter();
	  String message = task.getMessage();
	  TaskTag tag = task.getTag();

	  if( isValidLine( line ) && isValidCharacter(line, startCharacter) ) {
		  startCharacter = code.getLineOffset( line - 1 ) + startCharacter;
		  if( isValidCharacter(line, stopCharacter ) )
			  stopCharacter = code.getLineOffset( line - 1 ) + stopCharacter;
		  else
			  stopCharacter = startCharacter;
		  createTaskMarker( line, startCharacter, stopCharacter, tag, message );
	  }
	  else
		  createTaskMarker( -1, startCharacter, stopCharacter, tag, message );
  }

  private void createTaskMarker( int line, int startCharacter, int stopCharacter, TaskTag tag, String message ) throws CoreExceptionWrapper {
	  try {
		  markerAdapter.createTaskMarker(line, startCharacter, stopCharacter, tag, message);
	  } catch( CoreException ce ) {
		  throw new CoreExceptionWrapper( ce );
	  }
  }

  private boolean isValidLine( int line ) {
    return line >= 1 && line <= code.getLineCount();
  }

  private boolean isValidCharacter( int line, int character ) {
    return character >= 0 && character <= code.getLineLength( line - 1 );
  }

}
