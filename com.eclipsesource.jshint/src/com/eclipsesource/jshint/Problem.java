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
 * Holds information about a problem found by JSHint.
 */
public interface Problem {

  /**
   * Returns the line number in which the problem occurred.
   *
   * @return the line number, beginning with 1
   */
  int getLine();

  /**
   * Returns the character offset within the line in which the character occurred.
   * 
   * @return the character offset, beginning with 0
   */
  int getCharacter();

  /**
   * The problem message returned from JSHint.
   *
   * @return the message
   */
  String getMessage();

}
