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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.eclipsesource.jshint.ui.internal.builder.BuilderUtil;
import com.eclipsesource.jshint.ui.internal.builder.JSHintBuilder;


public class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.eclipsesource.jshint.ui"; //$NON-NLS-1$
  private static Activator instance;

  @Override
  public void start( BundleContext context ) throws Exception {
    super.start( context );
    repairObsoleteBuilderInProjects();
    instance = this;
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

  private static void repairObsoleteBuilderInProjects() throws CoreException {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    if( workspace != null ) {
      IProject[] projects = workspace.getRoot().getProjects();
      for( IProject project : projects ) {
        if( project.isAccessible() ) {
          if( BuilderUtil.removeBuilderFromProject( project, JSHintBuilder.ID_OLD ) ) {
            BuilderUtil.addBuilderToProject( project, JSHintBuilder.ID );
          }
        }
      }
    }
  }

}
