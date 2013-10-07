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
   * The problem message returned by JSHint.
   *
   * @return the message
   */
  String getMessage();

  /**
   * The error code returned by JSHint.
   *
   * @return the error code
   */
  String getCode();

  /**
   * Returns whether this problem represents an error.
   *
   * @return <code>true</code> if this is an error
   */
  boolean isError();

}
