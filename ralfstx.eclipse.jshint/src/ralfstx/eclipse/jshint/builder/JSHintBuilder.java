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
import ralfstx.eclipse.jshint.JavaScriptChecker;
import ralfstx.eclipse.jshint.properties.StatusHelper;


public class JSHintBuilder extends IncrementalProjectBuilder {

  private static final String PLUGIN_ID = "ralfstx.eclipse.jshint";

  public static final String ID = PLUGIN_ID + ".builder";

  @Override
  protected IProject[] build( int kind, Map<String, String> args, IProgressMonitor monitor )
    throws CoreException
  {
    if( kind == IncrementalProjectBuilder.FULL_BUILD ) {
      System.out.println( "full build " + getProject() );
      fullBuild( monitor );
    } else {
      IResourceDelta delta = getDelta( getProject() );
      if( delta == null ) {
        System.out.println( "build " + kind + " " + getProject() );
        fullBuild( monitor );
      } else {
        System.out.println( "incremental build " + delta.getResource() );
        incrementalBuild( delta, monitor );
      }
    }
    return null;
  }

  @Override
  protected void clean( IProgressMonitor monitor ) throws CoreException {
    System.out.println( "clean " + getProject() );
    getProject().accept( new IResourceVisitor() {
      public boolean visit( IResource resource ) throws CoreException {
        new MarkerAdapter( resource ).removeMarkers();
        return true;
      }
    } );
  }

  private void fullBuild( IProgressMonitor monitor ) throws CoreException {
    getProject().accept( new MyBuilderVisitor() );
  }

  private void incrementalBuild( IResourceDelta delta, IProgressMonitor monitor )
    throws CoreException
  {
    delta.accept( new MyBuilderVisitor() );
  }

  private static class MyBuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

    private final JavaScriptChecker checker;

    public MyBuilderVisitor() throws CoreException {
      checker = new JavaScriptChecker();
      try {
        checker.init();
        checker.setOptions( new Configuration() );
      } catch( IOException exception ) {
        String message = "Failed to intialize JSHint";
        throw new CoreException( new Status( IStatus.ERROR, PLUGIN_ID, message, exception ) );
      }
    }

    public boolean visit( IResourceDelta delta ) throws CoreException {
      IResource resource = delta.getResource();
      return visit( resource );
    }

    public boolean visit( IResource resource ) throws CoreException {
      System.out.println( "   visit " + resource.getFullPath() );
      if( resource.exists() && resource.getType() == IResource.FILE ) {
        clean( resource );
        if( mustCheck( resource ) ) {
          check( ( IFile )resource );
        }
      }
      return true;
    }

    private static boolean mustCheck( IResource resource ) throws CoreException {
      Path binPath = new Path( "bin" );
      if( binPath.isPrefixOf( resource.getProjectRelativePath() ) ) {
        return false;
      }
      if( !StatusHelper.getProjectEnabled( resource ) ) {
        return false;
      }
      if( !"js".equals( resource.getFileExtension() ) ) {
        return false;
      }
      if( StatusHelper.getFileExcluded( resource ) ) {
        return false;
      }
      return true;
    }

    private void clean( IResource resource ) throws CoreException {
      new MarkerAdapter( resource ).removeMarkers();
    }

    private void check( IFile file ) throws CoreException {
      String code = readContent( file );
      ErrorHandler handler = new MarkerErrorHandler( new MarkerAdapter( file ) );
      try {
        checker.check( code, handler );
      } catch( CoreExceptionWrapper wrapper ) {
        throw ( CoreException )wrapper.getCause();
      }
    }

    private static String readContent( IFile file ) throws CoreException {
      try {
        InputStream inputStream = file.getContents();
        String charset = file.getCharset();
        return readContent( inputStream, charset );
      } catch( IOException exception ) {
        String message = "Failed to read resource";
        throw new CoreException( new Status( IStatus.ERROR, PLUGIN_ID, message, exception ) );
      }
    }

    private static String readContent( InputStream inputStream, String charset )
      throws UnsupportedEncodingException, IOException
    {
      String result;
      BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
      try {
        StringBuilder builder = new StringBuilder();
        String line = reader.readLine();
        while( line != null ) {
          builder.append( line );
          builder.append( '\n' );
          line = reader.readLine();
        }
        result = builder.toString();
      } finally {
        reader.close();
      }
      return result;
    }

  }

  private static final class MarkerErrorHandler implements ErrorHandler {

    private final MarkerAdapter markerAdapter;

    private MarkerErrorHandler( MarkerAdapter markerAdapter ) {
      this.markerAdapter = markerAdapter;
    }

    public void handleError( int line, int character, String message ) {
      try {
        // TODO Character is line-relative, we need absolute position in the text
        // int start = getAbsolutePosition( line, character );
        // int end = start;
        int start = -1;
        int end = -1;
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
