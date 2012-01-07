package ralfstx.eclipse.jshint.builder;

import org.eclipse.core.runtime.CoreException;

import ralfstx.eclipse.jshint.ErrorHandler;
import ralfstx.eclipse.jshint.Text;
import ralfstx.eclipse.jshint.builder.JSHintBuilder.CoreExceptionWrapper;


final class MarkerErrorHandler implements ErrorHandler {

  private final MarkerAdapter markerAdapter;
  private final Text code;

  MarkerErrorHandler( MarkerAdapter markerAdapter, Text code ) {
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
