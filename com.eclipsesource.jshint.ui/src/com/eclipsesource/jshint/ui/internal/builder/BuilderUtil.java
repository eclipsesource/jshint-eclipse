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
package com.eclipsesource.jshint.ui.internal.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;


public class BuilderUtil {

  private BuilderUtil() {
    // prevent instantiation
  }

  public static void triggerClean( IProject project, String builderName ) throws CoreException {
    project.build( IncrementalProjectBuilder.CLEAN_BUILD, builderName, null, null );
  }

  public static void triggerBuild( IProject project, String builderName ) throws CoreException {
    project.build( IncrementalProjectBuilder.FULL_BUILD, builderName, null, null );
  }

  public static boolean addBuilderToProject( IProject project, String builderId )
      throws CoreException
  {
    IProjectDescription description = project.getDescription();
    if( !containsBuildCommand( description, builderId ) ) {
      addBuildCommand( description, builderId );
      project.setDescription( description, null );
      return true;
    }
    return false;
  }

  public static boolean removeBuilderFromProject( IProject project, String builderId )
      throws CoreException
  {
    IProjectDescription description = project.getDescription();
    if( containsBuildCommand( description, builderId ) ) {
      removeBuildCommands( description, builderId );
      project.setDescription( description, null );
      return true;
    }
    return false;
  }

  private static boolean containsBuildCommand( IProjectDescription description, String builderId ) {
    for( ICommand command : description.getBuildSpec() ) {
      if( command.getBuilderName().equals( builderId ) ) {
        return true;
      }
    }
    return false;
  }

  private static void addBuildCommand( IProjectDescription description, String builderId ) {
    ICommand[] oldCommands = description.getBuildSpec();
    ICommand[] newCommands = new ICommand[oldCommands.length + 1];
    System.arraycopy( oldCommands, 0, newCommands, 0, oldCommands.length );
    newCommands[newCommands.length - 1] = createBuildCommand( description, builderId );
    description.setBuildSpec( newCommands );
  }

  private static void removeBuildCommands( IProjectDescription description, String builderId ) {
    ICommand[] oldCommands = description.getBuildSpec();
    List<ICommand> list = new ArrayList<ICommand>();
    for( ICommand command : oldCommands ) {
      if( !command.getBuilderName().equals( builderId ) ) {
        list.add( command );
      }
    }
    ICommand[] newCommands = new ICommand[list.size()];
    list.toArray( newCommands );
    description.setBuildSpec( newCommands );
  }

  private static ICommand createBuildCommand( IProjectDescription description, String builderId ) {
    ICommand command = description.newCommand();
    command.setBuilderName( builderId );
    return command;
  }

}
