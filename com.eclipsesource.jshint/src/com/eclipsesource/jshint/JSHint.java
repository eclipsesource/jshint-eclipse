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
import java.io.UnsupportedEncodingException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

import com.eclipsesource.jshint.internal.JSHintRunner;
import com.eclipsesource.jshint.internal.ProblemImpl;


/**
 * JSHint code analysis tool written in Java.
 * <p>
 * Usage:
 * <pre>
 * JSHint jshint = new JSHint();
 * jshint.load();
 * jshint.configure( new Configuration() );
 * jshint.check( jsCode, new ProblemHandler() { ... } );
 * </pre>
 * </p>
 */
public class JSHint {

  private static final String JSHINT_JS = "com/jshint/jshint-r05.js";
  private Function jshint;
  private Object opts;

  /**
   * Loads the JSHint library.
   */
  public void load() throws IOException {
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
      String optionsString = configuration.getOptionsString();
      opts = context.evaluateString( scope, "opts = " + optionsString + ";", "[options]", 1, null );
    } finally {
      Context.exit();
    }
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
    if( jshint == null ) {
      throw new IllegalStateException( "JSHint is not loaded" );
    }
    boolean result;
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      Object[] args = new Object[] { code, opts };
      try {
        result = ( (Boolean)jshint.call( context, scope, null, args ) ).booleanValue();
      } catch( JavaScriptException exception ) {
        if( handler != null ) {
          String message = "Could not parse JavaScript: " + exception.getMessage();
          handler.handleProblem( new ProblemImpl( 0, 0, message ) );
        }
        return false;
      }
      if( !result && handler != null ) {
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

  private void handleError( ProblemHandler handler, ScriptableObject error ) {
    String reason = getPropertyAsString( error, "reason", "" );
    int line = getPropertyAsInt( error, "line", -1 );
    int character = getPropertyAsInt( error, "character", -1 );
    String message = reason.endsWith( "." ) ? reason.substring( 0, reason.length() - 1 ) : reason;
    handler.handleProblem( new ProblemImpl( line, character, message ) );
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

  public static void main( String[] args ) {
    JSHintRunner runner = new JSHintRunner();
    runner.run( args );
  }

}
