package ralfstx.eclipse.jshint;


public interface ErrorHandler {

  void handleError( int line, int character, String message );

}
