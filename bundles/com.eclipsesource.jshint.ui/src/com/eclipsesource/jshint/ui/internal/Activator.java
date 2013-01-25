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
package com.eclipsesource.jshint.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.eclipsesource.jshint.ui"; //$NON-NLS-1$
  private static Activator instance;

  @Override
  public void start( BundleContext context ) throws Exception {
    super.start( context );
    instance = this;
    CompatibilityUtil.run();
  }

  @Override
  public void stop( BundleContext context ) throws Exception {
    instance = null;
    super.stop( context );
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getDefault() {
    return instance;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in
   * relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor( String path ) {
    return imageDescriptorFromPlugin( PLUGIN_ID, path );
  }

  public static void logError( String message, CoreException exception ) {
    Status status = new Status( IStatus.ERROR, PLUGIN_ID, message, exception );
    Platform.getLog( getDefault().getBundle() ).log( status );
  }

  public static void logError( String message, Throwable t ) {
    if (!(t instanceof CoreException)) {
      t = new CoreException(createStatus(message, 0, IStatus.ERROR, t));
    }
    logError(message, (CoreException)t);
  }

  private static IStatus createStatus(String msg, int code, int severity, Throwable ex) {
    return new Status(severity, PLUGIN_ID, code, msg, ex);
  }
}
