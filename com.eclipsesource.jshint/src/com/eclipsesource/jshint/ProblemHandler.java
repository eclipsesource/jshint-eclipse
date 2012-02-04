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
package com.eclipsesource.jshint;


/**
 * Implementations of this class are used to handle problems returned from JSHint.
 */
public interface ProblemHandler {

  /**
   * Handles a problem occurred during the code check.
   *
   * @param problem
   *          the problem
   */
  void handleProblem( Problem problem );

}
