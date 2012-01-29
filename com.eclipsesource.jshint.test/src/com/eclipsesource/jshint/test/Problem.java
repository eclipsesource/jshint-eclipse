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
package com.eclipsesource.jshint.test;


public class Problem {
  public final int line;
  public final int character;
  public final String message;

  public Problem( int line, int character, String message ) {
    this.line = line;
    this.character = character;
    this.message = message;
  }

  public String getPosition() {
    return line + "." + character;
  }
}
