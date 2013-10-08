/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint.ui.internal.preferences.ui;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class BrowserSupport_Test {

  @Test
  public void instance_isNotNull() {
    assertNotNull( BrowserSupport.INSTANCE );
  }

  @Test( expected = NullPointerException.class )
  public void openUrl_rejectsNullUrl() {
    BrowserSupport.INSTANCE.openUrl( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void openUrl_rejectsInvalidUrl() {
    BrowserSupport.INSTANCE.openUrl( "eclipse.org" );
  }

}
