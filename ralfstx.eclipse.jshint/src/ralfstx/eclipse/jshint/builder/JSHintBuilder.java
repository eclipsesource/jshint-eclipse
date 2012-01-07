package ralfstx.eclipse.jshint.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import ralfstx.eclipse.jshint.Configuration;
import ralfstx.eclipse.jshint.ErrorHandler;
import ralfstx.eclipse.jshint.JSHint;
import ralfstx.eclipse.jshint.Text;
import ralfstx.eclipse.jshint.properties.ProjectPreferences;
import ralfstx.eclipse.jshint.properties.StatusHelper;


public class JSHintBuilder extends IncrementalProjectBuilder {

  private static final String PLUGIN_ID = "ralfstx.eclipse.jshint";

  public static final String ID = PLUGIN_ID + ".builder";

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
    getProject().accept( new IResourceVisitor() {
      public boolean visit( IResource resource ) throws CoreException {
        new MarkerAdapter( resource ).removeMarkers();
        return true;
      }
    } );
  }

  private void fullBuild( IProgressMonitor monitor ) throws CoreException {
    IProject project = getProject();
    getProject().accept( new MyBuilderVisitor( project ) );
  }

  private void incrementalBuild( IResourceDelta delta, IProgressMonitor monitor )
    throws CoreException
  {
    IProject project = getProject();
    delta.accept( new MyBuilderVisitor( project ) );
  }

  private static class MyBuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

    private final JSHint checker;

    public MyBuilderVisitor( IProject project ) throws CoreException {
      checker = new JSHint();
      try {
        checker.init();
        Configuration configuration = getConfiguration( project );
        checker.configure( configuration );
      } catch( IOException exception ) {
        String message = "Failed to intialize JSHint";
        throw new CoreException( new Status( IStatus.ERROR, PLUGIN_ID, message, exception ) );
      }
    }

    private static Configuration getConfiguration( IProject project ) {
      ProjectPreferences preferences = new ProjectPreferences( project );
      return preferences.getConfiguration();
    }

    public boolean visit( IResourceDelta delta ) throws CoreException {
      IResource resource = delta.getResource();
      return visit( resource );
    }

    public boolean visit( IResource resource ) throws CoreException {
      boolean descend = true;
      if( resource.exists() ) {
        if( resource.getType() != IResource.FILE ) {
          descend = allowContainer( resource );
        } else {
          clean( resource );
          if( allowFile( resource ) ) {
            check( ( IFile )resource );
          }
        }
      }
      return descend;
    }

    private void clean( IResource resource ) throws CoreException {
      new MarkerAdapter( resource ).removeMarkers();
    }

    private void check( IFile file ) throws CoreException {
      Text code = readContent( file );
      ErrorHandler handler = new MarkerErrorHandler( new MarkerAdapter( file ), code );
      try {
        checker.check( code.getContent(), handler );
      } catch( CoreExceptionWrapper wrapper ) {
        throw ( CoreException )wrapper.getCause();
      }
    }

    private static boolean allowContainer( IResource resource ) throws CoreException {
      Path binPath = new Path( "bin" );
      if( binPath.isPrefixOf( resource.getProjectRelativePath() ) ) {
        return false;
      }
      if( !StatusHelper.getProjectEnabled( resource ) ) {
        return false;
      }
      return true;
    }

    private static boolean allowFile( IResource resource ) throws CoreException {
      if( !"js".equals( resource.getFileExtension() ) ) {
        return false;
      }
      if( StatusHelper.getFileExcluded( resource ) ) {
        return false;
      }
      return true;
    }

    private static Text readContent( IFile file ) throws CoreException {
      try {
        InputStream inputStream = file.getContents();
        String charset = file.getCharset();
        return readContent( inputStream, charset );
      } catch( IOException exception ) {
        String message = "Failed to read resource";
        throw new CoreException( new Status( IStatus.ERROR, PLUGIN_ID, message, exception ) );
      }
    }

    private static Text readContent( InputStream inputStream, String charset )
      throws UnsupportedEncodingException, IOException
    {
      Text result;
      BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
      try {
        result = new Text( reader );
      } finally {
        reader.close();
      }
      return result;
    }

  }

  private static final class MarkerErrorHandler implements ErrorHandler {

    private final MarkerAdapter markerAdapter;
    private final Text code;

    private MarkerErrorHandler( MarkerAdapter markerAdapter, Text code ) {
      this.markerAdapter = markerAdapter;
      this.code = code;
    }

    public void handleError( int line, int character, String message ) {
      try {
        int start = code.getLineOffset( line - 1 ) + character - 1;
        int end = start;
        markerAdapter.createMarker( line, start, end, message );
      } catch( CoreException ce ) {
        throw new CoreExceptionWrapper( ce );
      }
    }
  }

  private static class CoreExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = 2267576736168605043L;

    public CoreExceptionWrapper( CoreException wrapped ) {
      super( wrapped );
    }

  }

}
