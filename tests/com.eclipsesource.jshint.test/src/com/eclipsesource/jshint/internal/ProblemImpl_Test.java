/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.internal;

import org.junit.Test;

import com.eclipsesource.jshint.Problem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ProblemImpl_Test {

  @Test
  public void isError_falseForNull() {
    Problem problem = new ProblemImpl( 0, 0, null, null );

    assertFalse( problem.isError() );
  }

  @Test
  public void isError_falseForEmptyString() {
    Problem problem = new ProblemImpl( 0, 0, null, "" );

    assertFalse( problem.isError() );
  }

  @Test
  public void isError_falseForAnythingElse() {
    Problem problem = new ProblemImpl( 0, 0, null, "foo" );

    assertFalse( problem.isError() );
  }

  @Test
  public void isError_trueForErrors() {
    Problem problem = new ProblemImpl( 0, 0, null, "E000" );

    assertTrue( problem.isError() );
  }

}
