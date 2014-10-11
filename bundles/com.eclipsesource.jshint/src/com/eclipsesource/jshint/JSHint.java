/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.jshint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;

import com.eclipsesource.jshint.internal.JSHintRunner;
import com.eclipsesource.jshint.internal.ProblemImpl;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * Lightweight Java wrapper for the JSHint code analysis tool.
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 * JSHint jshint = new JSHint();
 * jshint.load();
 * jshint.configure( new Configuration() );
 * jshint.check( jsCode, new ProblemHandler() { ... } );
 * </pre>
 *
 * @see http://www.jshint.com/
 */
public class JSHint {

  private static final String DEFAULT_JSHINT_VERSION = "2.5.6";
  private static final int DEFAULT_JSHINT_INDENT = 4;
  private ScriptableObject scope;
  private Function jshint;
  private Object options;
  private Object globals;
  private int indent = DEFAULT_JSHINT_INDENT;

  /**
   * Loads the default JSHint library.
   * @see #getDefaultLibraryVersion()
   */
  public void load() throws IOException {
    Reader reader = getJsHintReader();
    try {
      load( reader );
    } finally {
      reader.close();
    }
  }

  /**
   * Loads a custom JSHint library. The input stream must provide the contents of the
   * file <code>jshint.js</code> found in the JSHint distribution.
   *
   * @param inputStream
   *          an input stream to load the the JSHint library from
   * @throws IOException
   *           if an I/O error occurs while reading from the input stream
   * @throws IllegalArgumentException
   *           if the given input is not a proper JSHint library file
   */
  public void load( InputStream inputStream ) throws IOException {
    Reader reader = new InputStreamReader( inputStream );
    try {
      load( reader );
    } finally {
      reader.close();
    }
  }

  /**
   * Sets the configuration to use for all subsequent checks.
   *
   * @param configuration
   *          the configuration to use, must not be null
   */
  public void configure( JsonObject configuration ) {
    if( configuration == null ) {
      throw new NullPointerException( "configuration is null" );
    }
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      JsonValue globalsValue = configuration.get( "globals" );
      if( globalsValue != null ) {
        String globalsExpr = "globals = " + globalsValue.toString() + ";";
        globals = context.evaluateString( scope, globalsExpr, "[globals]", 1, null );
      }
      configuration.remove( "globals" );
      String optionsExpr = "options = " + configuration.toString() + ";";
      options = context.evaluateString( scope, optionsExpr, "[options]", 1, null );
      indent = determineIndent( configuration );
    } finally {
      Context.exit();
    }
  }

  private int determineIndent( JsonObject configuration ) {
    JsonValue value = configuration.get( "indent" );
    if( value != null && value.isNumber() ) {
      return value.asInt();
    }
    return DEFAULT_JSHINT_INDENT;
  }

  /**
   * Checks the given JavaScript code. All problems will be reported to the given problem handler.
   *
   * @param code
   *          the JavaScript code to check, must not be null
   * @param handler
   *          the handler to report problems to or <code>null</code>
   * @return <code>true</code> if no problems have been found, otherwise <code>false</code>
   */
  public boolean check( String code, ProblemHandler handler ) {
    if( code == null ) {
      throw new NullPointerException( "code is null" );
    }
    return check( new Text( code ), handler );
  }

  public boolean check( Text text, ProblemHandler handler ) {
    if( text == null ) {
      throw new NullPointerException( "code is null" );
    }
    if( jshint == null ) {
      throw new IllegalStateException( "JSHint is not loaded" );
    }
    boolean result = true;
    String code = text.getContent();
    // Don't feed jshint with empty strings, see https://github.com/jshint/jshint/issues/615
    // However, consider an empty string valid
    if( code.trim().length() != 0 ) {
      Context context = Context.enter();
      try {
        result = checkCode( context, code );
        if( !result && handler != null ) {
          handleProblems( handler, text );
        }
      } finally {
        Context.exit();
      }
    }
    return result;
  }

  /**
   * Returns the version of the built-in JSHint library that is used when <code>load()</code> is
   * called without a parameter.
   *
   * @return the version name of the default JSHint version
   */
  public static String getDefaultLibraryVersion() {
    return DEFAULT_JSHINT_VERSION;
  }

  private void load( Reader reader ) throws IOException {
    Context context = Context.enter();
    try {
      context.setOptimizationLevel( 9 );
      context.setLanguageVersion( Context.VERSION_1_5 );
      scope = context.initStandardObjects();
      context.evaluateString( scope, createShimCode(), "shim", 1, null );
      context.evaluateReader( scope, reader, "jshint library", 1, null );
      jshint = findJSHintFunction( scope );
    } catch( RhinoException exception ) {
      throw new IllegalArgumentException( "Could not evaluate JavaScript input", exception );
    } finally {
      Context.exit();
    }
  }

  private boolean checkCode( Context context, String code ) {
    try {
      Object[] args = new Object[] { code, options, globals };
      return ( (Boolean)jshint.call( context, scope, null, args ) ).booleanValue();
    } catch( JavaScriptException exception ) {
      String message = "JavaScript exception thrown by JSHint: " + exception.getMessage();
      throw new RuntimeException( message, exception );
    } catch( RhinoException exception ) {
      String message = "JavaScript exception caused by JSHint: " + exception.getMessage();
      throw new RuntimeException( message, exception );
    }
  }

  private void handleProblems( ProblemHandler handler, Text text ) {
    NativeArray errors = (NativeArray)jshint.get( "errors", jshint );
    long length = errors.getLength();
    for( int i = 0; i < length; i++ ) {
      Object object = errors.get( i, errors );
      ScriptableObject error = (ScriptableObject)object;
      if( error != null ) {
        Problem problem = createProblem( error, text );
        handler.handleProblem( problem );
      }
    }
  }

  ProblemImpl createProblem( ScriptableObject error, Text text ) {
    String reason = getPropertyAsString( error, "reason", "" );
    int line = getPropertyAsInt( error, "line", -1 );
    int character = getPropertyAsInt( error, "character", -1 );
    String code = getPropertyAsString( error, "code", "" );
    if( line <= 0 || line > text.getLineCount() ) {
      line = -1;
      character = -1;
    } else if( character > 0 ) {
      character = visualToCharIndex( text, line, character );
    }
    String message = reason.endsWith( "." ) ? reason.substring( 0, reason.length() - 1 ) : reason;
    return new ProblemImpl( line, character, message, code );
  }

  /*
   * JSHint reports "visual" character positions instead of a character index, i.e. the first
   * character is 1 and every tab character is multiplied by the indent with.
   *
   * Example: "a\tb\tc"
   *
   *   index:  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10|
   *   char:   | a | » | b | » | c |
   *   visual:     | a | »             | b | »             | c |
   */
  int visualToCharIndex( Text text, int line, int character ) {
    String string = text.getContent();
    int offset = text.getLineOffset( line - 1 );
    int charIndex = 0;
    int visualIndex = 1;
    int maxCharIndex = string.length() - offset - 1;
    while( visualIndex != character && charIndex < maxCharIndex ) {
      boolean isTab = string.charAt( offset + charIndex ) == '\t';
      visualIndex += isTab ? indent : 1;
      charIndex++;
    }
    return charIndex;
  }

  private static String createShimCode() {
    // Create shims to prevent problems with JSHint accessing objects that are not available in
    // Rhino, e.g. https://github.com/jshint/jshint/issues/1038
    return "console = {log:function(){},error:function(){},trace:function(){}};"
         + "window = {};";
  }

  private static Function findJSHintFunction( ScriptableObject scope )
      throws IllegalArgumentException
  {
    Object object;
    if( ScriptableObject.hasProperty( scope, "JSHINT" ) ) {
      object = scope.get( "JSHINT", scope );
    } else {
      throw new IllegalArgumentException( "Global JSHINT function missing in input" );
    }
    if( !( object instanceof Function ) ) {
      throw new IllegalArgumentException( "Global JSHINT is not a function" );
    }
    return (Function)object;
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
    if( property instanceof Number ) {
      result = ( ( Number )property ).intValue();
    }
    return result;
  }

  private static BufferedReader getJsHintReader() throws UnsupportedEncodingException {
    ClassLoader classLoader = JSHint.class.getClassLoader();
    // Include DEFAULT_JSHINT_VERSION in name to ensure the constant matches the actual version
    String name = "com/jshint/jshint-" + DEFAULT_JSHINT_VERSION + ".js";
    InputStream inputStream = classLoader.getResourceAsStream( name );
    return new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
  }

  public static void main( String[] args ) {
    JSHintRunner runner = new JSHintRunner();
    runner.run( args );
  }

}
