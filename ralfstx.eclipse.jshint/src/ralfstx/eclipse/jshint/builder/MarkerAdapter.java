package ralfstx.eclipse.jshint.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import ralfstx.eclipse.jshint.Activator;


public class MarkerAdapter {

  private static final String CUSTOM_TYPE = Activator.PLUGIN_ID;
  private final IResource resource;

  public MarkerAdapter( IResource resource ) {
    this.resource = resource;
  }

  public void removeMarkers() throws CoreException {
    IMarker[] markers = resource.findMarkers( IMarker.PROBLEM, true, IResource.DEPTH_INFINITE );
    for( IMarker marker : markers ) {
      if( marker.getAttribute( CUSTOM_TYPE ) != null ) {
        marker.delete();
      }
    }
  }

  public void createMarker( int lineNr, int start, int end, String message ) throws CoreException {
    IMarker marker = resource.createMarker( IMarker.PROBLEM );
    marker.setAttribute( CUSTOM_TYPE, "true" );
    marker.setAttribute( IMarker.SEVERITY, new Integer( IMarker.SEVERITY_WARNING ) );
    marker.setAttribute( IMarker.MESSAGE, message );
    marker.setAttribute( IMarker.LINE_NUMBER, new Integer( lineNr ) );
    System.out.println( "marker at line " + lineNr );
    if( start >= 0 ) {
      marker.setAttribute( IMarker.CHAR_START, new Integer( start ) );
      marker.setAttribute( IMarker.CHAR_END, new Integer( end >= 0 ? end : start ) );
    }
  }

}
