/*******************************************************************************
 * Copyright (c) 2012 EclipseSource.
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

  private static final String DEFAULT_JSHINT_VERSION = "r12";
  private static final int DEFAULT_JSHINT_INDENT = 4;
  private Function jshint;
  private Object opts;
  private ScriptableObject scope;
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
   * <p>
   * JSLint is also supported. In this case the file to provide is <code>jslint.js</code>.
   * </p>
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
  public void configure( Configuration configuration ) {
    if( configuration == null ) {
      throw new NullPointerException( "configuration is null" );
    }
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      String optionsString = configuration.toJson();
      opts = context.evaluateString( scope, "opts = " + optionsString + ";", "[options]", 1, null );
      indent = determineIndent( configuration );
    } finally {
      Context.exit();
    }
  }

  private int determineIndent( Configuration configuration ) {
    Object indent = configuration.getOption( "indent" );
    if( indent instanceof Integer ) {
      return ( (Integer)indent ).intValue();
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
      scope = context.initStandardObjects();
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
      Object[] args = new Object[] { code, opts };
      return ( (Boolean)jshint.call( context, scope, null, args ) ).booleanValue();
    } catch( JavaScriptException exception ) {
      String message = "JavaScript exception thrown by JSHint: " + exception.getMessage();
      throw new RuntimeException( message, exception );
    } catch( RhinoException exception ) {
      String message = "JavaScript exception caused by JSHint: " + exception.getMessage();
      throw new RuntimeException( message, exception );
    }
  }

  private Function findJSHintFunction( ScriptableObject scope ) throws IllegalArgumentException {
    Object object;
    if( ScriptableObject.hasProperty( scope, "JSHINT" ) ) {
      object = scope.get( "JSHINT", scope );
    } else if( ScriptableObject.hasProperty( scope, "JSLINT" ) ) {
      object = scope.get( "JSLINT", scope );
    } else {
      throw new IllegalArgumentException( "Global JSHINT or JSLINT function missing in input" );
    }
    if( !( object instanceof Function ) ) {
      throw new IllegalArgumentException( "Global JSHINT or JSLINT is not a function" );
    }
    return (Function)object;
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

  private ProblemImpl createProblem( ScriptableObject error, Text text ) {
    String reason = getPropertyAsString( error, "reason", "" );
    int line = getPropertyAsInt( error, "line", -1 );
    int character = getPropertyAsInt( error, "character", -1 );
    if( character > 0 ) {
      character = fixPosition( text, line, character );
    }
    String message = reason.endsWith( "." ) ? reason.substring( 0, reason.length() - 1 ) : reason;
    return new ProblemImpl( line, character, message );
  }

  private int fixPosition( Text text, int line, int character ) {
    // JSHint reports physical character positions instead of a character index,
    // i.e. every tab character is multiplied with the indent.
    String string = text.getContent();
    int offset = text.getLineOffset( line - 1 );
    int indentIndex = 0;
    int charIndex = 0;
    while( indentIndex < character - 1 ) {
      boolean isTab = string.charAt( offset + indentIndex ) == '\t';
      indentIndex += isTab ? indent : 1;
      charIndex++;
    }
    return charIndex;
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
    String name = "com/jshint/jshint-" + DEFAULT_JSHINT_VERSION + ".min.js";
    InputStream inputStream = classLoader.getResourceAsStream( name );
    return new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
  }

  public static void main( String[] args ) {
    JSHintRunner runner = new JSHintRunner();
    runner.run( args );
  }

}
