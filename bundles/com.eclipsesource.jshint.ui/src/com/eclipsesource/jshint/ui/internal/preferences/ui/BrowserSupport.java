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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.statushandlers.StatusManager;


public class BrowserSupport {

  private static final int BROWSER_STYLE = IWorkbenchBrowserSupport.AS_EDITOR
                                           | IWorkbenchBrowserSupport.LOCATION_BAR
                                           | IWorkbenchBrowserSupport.NAVIGATION_BAR
                                           | IWorkbenchBrowserSupport.STATUS;

  public static final BrowserSupport INSTANCE = new BrowserSupport();

  private final Listener selectionListener;

  private BrowserSupport() {
    selectionListener = createSelectionListener();
  }

  public void openUrl( String url ) {
    if( url == null ) {
      throw new NullPointerException( "url is null" );
    }
    IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
    try {
      IWebBrowser browser = support.createBrowser( BROWSER_STYLE, url, null, null );
      browser.openURL( new URL( url ) );
    } catch( MalformedURLException exception ) {
      throw new IllegalArgumentException( "Invalid URL: " + url );
    } catch( PartInitException exception ) {
      StatusManager.getManager().handle( exception.getStatus(), StatusManager.LOG );
    }
  }

  public void enableHyperlinks( Link link ) {
    link.addListener( SWT.Selection, selectionListener );
  }

  private Listener createSelectionListener() {
    return new Listener() {
      public void handleEvent( Event event ) {
        if( isSupportedUrl( event.text ) ) {
          openUrl( event.text );
        }
      }
    };
  }

  private static boolean isSupportedUrl( String text ) {
    return text.startsWith( "http://" ) || text.startsWith( "https://" );
  }

}
