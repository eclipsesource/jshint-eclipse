package ralfstx.eclipse.jshint.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ralfstx.eclipse.jshint.Activator;


public class JSHintBuilder extends IncrementalProjectBuilder {

  public static final String ID = Activator.PLUGIN_ID + ".builder";

  @Override
  protected IProject[] build( int kind, Map<String, String> args, IProgressMonitor monitor )
      throws CoreException
  {
    if( kind == IncrementalProjectBuilder.FULL_BUILD ) {
      fullBuild( monitor );
    } else {
      IResourceDelta delta = getDelta( getProject() );
      if( delta == null ) {
        fullBuild( monitor );
      } else {
        incrementalBuild( delta, monitor );
      }
    }
    return null;
  }

  @Override
  protected void clean( IProgressMonitor monitor ) throws CoreException {
    new MarkerAdapter( getProject() ).removeMarkers();
  }

  private void fullBuild( IProgressMonitor monitor ) throws CoreException {
    IProject project = getProject();
    getProject().accept( new JSHintBuilderVisitor( project ) );
  }

  private void incrementalBuild( IResourceDelta delta, IProgressMonitor monitor )
      throws CoreException
  {
    IProject project = getProject();
    delta.accept( new JSHintBuilderVisitor( project ) );
  }

  static class CoreExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = 2267576736168605043L;

    public CoreExceptionWrapper( CoreException wrapped ) {
      super( wrapped );
    }

  }

}
