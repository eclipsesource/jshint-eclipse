package ralfstx.eclipse.jshint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;


public class JSHint {

  private static final String JSHINT_JS = "com/jshint/jshint.js";
  private Function jshint;
  private Object opts;

  public void init() throws IOException {
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      BufferedReader reader = getJsHintReader();
      try {
        context.evaluateReader( scope, reader, "jshint.js", 1, null );
      } finally {
        reader.close();
      }
      jshint = ( Function )scope.get( "JSHINT", scope );
    } finally {
      Context.exit();
    }
  }

  public void configure( Configuration configuration ) {
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      String optionsString = configuration.getOptionsString();
      opts = context.evaluateString( scope, "opts = " + optionsString + ";", "[options]", 1, null );
    } finally {
      Context.exit();
    }
  }

  public boolean check( String code, ErrorHandler handler ) {
    Context context = Context.enter();
    boolean result;
    try {
      ScriptableObject scope = context.initStandardObjects();
      Object[] args = new Object[] { code, opts };
      try {
        result = ( (Boolean)jshint.call( context, scope, null, args ) ).booleanValue();
      } catch( JavaScriptException exception ) {
        handler.handleError( 0, -1, "Could not parse JavaScript: " + exception.getMessage() );
        return false;
      }
      if( result == Boolean.FALSE ) {
        NativeArray errors = (NativeArray)jshint.get( "errors", jshint );
        for( Object object : errors ) {
          ScriptableObject error = (ScriptableObject)object;
          if( error != null ) {
            handleError( handler, error );
          }
        }
      }
    } finally {
      Context.exit();
    }
    return result;
  }

  private void handleError( ErrorHandler handler, ScriptableObject error ) {
    String reason = getPropertyAsString( error, "reason", "" );
    int line = getPropertyAsInt( error, "line", -1 );
    int character = getPropertyAsInt( error, "character", -1 );
    String message = reason.endsWith( "." ) ? reason.substring( 0, reason.length() - 1 ) : reason;
    handler.handleError( line, character, message );
  }

  private static String getPropertyAsString( ScriptableObject object,
                                             String name,
                                             String defaultValue )
  {
    String result = defaultValue;
    Object property = ScriptableObject.getProperty( object, name );
    if( property instanceof String ) {
      result = ( String )property;
    }
    return result;
  }

  private static int getPropertyAsInt( ScriptableObject object, String name, int defaultValue ) {
    int result = defaultValue;
    Object property = ScriptableObject.getProperty( object, name );
    if( property instanceof Double ) {
      Double doule = ( Double )property;
      result = ( int )doule.doubleValue();
    }
    return result;
  }

  private static BufferedReader getJsHintReader() throws UnsupportedEncodingException {
    ClassLoader classLoader = JSHint.class.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream( JSHINT_JS );
    return new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
  }

  ////////////////////////////////////////////////
  // Usage example

  public static void main( String[] args ) {
    try {
      JSHint checker = new JSHint();
      checker.init();
      checker.configure( new Configuration() );
      String code = "foo = { bar : 23, bar : 42 };\nif( foo == null )\n  bar = x";
      checker.check( code, new SysoutErrorHandler() );
    } catch( Exception e ) {
      e.printStackTrace();
    }
  }

  private static final class SysoutErrorHandler implements ErrorHandler {

    public void handleError( int line, int character, String message ) {
      System.out.println( line + "," + character + ": " + message );
    }

  }

}
