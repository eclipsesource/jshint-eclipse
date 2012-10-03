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
package com.eclipsesource.jshint.internal;

import com.eclipsesource.jshint.Problem;


public class ProblemImpl implements Problem {
  private final int line;
  private final int character;
  private final String message;

  public ProblemImpl( int line, int character, String message ) {
    this.line = line;
    this.character = character;
    this.message = message;
  }

  public int getLine() {
    return line;
  }

  public int getCharacter() {
    return character;
  }

  public String getMessage() {
    return message;
  }

}
