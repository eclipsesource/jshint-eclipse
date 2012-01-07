package ralfstx.eclipse.jshint.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import ralfstx.eclipse.jshint.Activator;
import ralfstx.eclipse.jshint.Configuration;
import ralfstx.eclipse.jshint.ErrorHandler;
import ralfstx.eclipse.jshint.JSHint;
import ralfstx.eclipse.jshint.Text;
import ralfstx.eclipse.jshint.builder.JSHintBuilder.CoreExceptionWrapper;
import ralfstx.eclipse.jshint.properties.ProjectPreferences;


class JSHintBuilderVisitor implements IResourceVisitor, IResourceDeltaVisitor {

  private final JSHint checker;
  private final ProjectPreferences preferences;

  public JSHintBuilderVisitor( IProject project ) throws CoreException {
    preferences = new ProjectPreferences( project );
    checker = preferences.getEnabled() ? createJSHint( preferences.getConfiguration() ) : null;
  }

  public boolean visit( IResourceDelta delta ) throws CoreException {
    IResource resource = delta.getResource();
    return visit( resource );
  }

  public boolean visit( IResource resource ) throws CoreException {
    boolean descend = false;
    if( resource.exists() && preferences.getEnabled() ) {
      if( resource.getType() != IResource.FILE ) {
        descend = considerContainer( resource );
      } else {
        clean( resource );
        if( considerFile( resource ) ) {
          check( (IFile)resource );
        }
        descend = true;
      }
    }
    return descend;
  }

  private JSHint createJSHint( Configuration configuration ) throws CoreException {
    JSHint jshint = new JSHint();
    try {
      jshint.init();
      jshint.configure( configuration );
    } catch( IOException exception ) {
      String message = "Failed to intialize JSHint";
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception ) );
    }
    return jshint;
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
      throw (CoreException)wrapper.getCause();
    }
  }

  private boolean considerContainer( IResource resource ) throws CoreException {
    Path binPath = new Path( "bin" );
    if( binPath.isPrefixOf( resource.getProjectRelativePath() ) ) {
      return false;
    }
    return true;
  }

  private boolean considerFile( IResource resource ) throws CoreException {
    if( !"js".equals( resource.getFileExtension() ) ) {
      return false;
    }
    if( preferences.getExcluded( resource ) ) {
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
      throw new CoreException( new Status( IStatus.ERROR, Activator.PLUGIN_ID, message, exception ) );
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
