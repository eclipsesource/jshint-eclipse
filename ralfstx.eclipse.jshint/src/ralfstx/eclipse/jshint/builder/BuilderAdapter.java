package ralfstx.eclipse.jshint.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;


public class BuilderAdapter {

  private final IProject project;

  public BuilderAdapter( IProject project ) {
    this.project = project;
  }

  public void enableJSHint() throws CoreException {
    boolean changed = addBuilderToProject( project, JSHintBuilder.ID );
    if( changed ) {
      triggerBuild();
    }
  }

  public void disableJSHint() throws CoreException {
    boolean changed = removeBuilderFromProject( project, JSHintBuilder.ID );
    if( changed ) {
      triggerClean();
    }
  }

  public void triggerClean() throws CoreException {
    project.build( IncrementalProjectBuilder.CLEAN_BUILD, JSHintBuilder.ID, null, null );
  }

  public void triggerBuild() throws CoreException {
    project.build( IncrementalProjectBuilder.FULL_BUILD, JSHintBuilder.ID, null, null );
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
    ICommand[] newCommands = new ICommand[ oldCommands.length + 1 ];
    System.arraycopy( oldCommands, 0, newCommands, 0, oldCommands.length );
    newCommands[ newCommands.length - 1 ] = createBuildCommand( description, builderId );
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
    ICommand[] newCommands = new ICommand[ list.size() ];
    list.toArray( newCommands );
    description.setBuildSpec( newCommands );
  }

  private static ICommand createBuildCommand( IProjectDescription description, String builderId ) {
    ICommand command = description.newCommand();
    command.setBuilderName( builderId );
    return command;
  }

}
