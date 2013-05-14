/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource.
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptableObject;

import com.eclipsesource.jshint.internal.JSHintRunner;
import com.eclipsesource.jshint.internal.ProblemImpl;
import com.eclipsesource.jshint.internal.TaskImpl;
import com.eclipsesource.jshint.internal.TaskTagImpl;
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

  private static final String DEFAULT_JSHINT_VERSION = "1.1.0";
  private static final int DEFAULT_JSHINT_INDENT = 4;
  private ScriptableObject scope;
  private Function jshint;
  private Object opts;
  private int indent = DEFAULT_JSHINT_INDENT;
  private String defaultAnnotation;
  private Pattern commentSearch = Pattern.compile("(?:\"(?:[^\"\\n]*\\\\\")*[^\"\\n]*[\"\\n])|(/\\*(?:[^*]*\\*[^/])*[^*]*\\*/|//.*)");
  private final String[] TASK_TYPES = new String[] {"TODO", "FIXME", "XXX"};
  private List<TaskTag> taskTags;

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
  public void configure( JsonObject configuration, String defaultAnnotation, List<TaskTag> taskTags ) {
    if( configuration == null ) {
      throw new NullPointerException( "configuration is null" );
    }
    Context context = Context.enter();
    try {
      ScriptableObject scope = context.initStandardObjects();
      String optionsString = configuration.toString();
      opts = context.evaluateString( scope, "opts = " + optionsString + ";", "[options]", 1, null );
      indent = determineIndent( configuration );
    } finally {
      Context.exit();
    }
    this.taskTags = taskTags;
    this.defaultAnnotation = defaultAnnotation != null && defaultAnnotation.length() > 0 ? new StringBuilder("/* jshint ").append(defaultAnnotation).append(" */\n").toString() : "";
    //TODO Set up task tags here.
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
  public boolean check( String code, ProblemHandler problem, TaskHandler task ) {
    if( code == null ) {
      throw new NullPointerException( "code is null" );
    }
    return check( new Text( code ), problem, task );
  }

  public boolean check( Text text, ProblemHandler problem, TaskHandler task ) {
    if( text == null ) {
      throw new NullPointerException( "code is null" );
    }
    if( jshint == null ) {
      throw new IllegalStateException( "JSHint is not loaded" );
    }
    boolean result = true;
    String code = new StringBuilder(defaultAnnotation).append(text.getContent()).toString();
    // Don't feed jshint with empty strings, see https://github.com/jshint/jshint/issues/615
    // However, consider an empty string valid
    if( code.trim().length() != 0 ) {
      Context context = Context.enter();
      try {
        result = checkCode( context, code );
        if( !result && problem != null ) {
          handleProblems( problem, text );
        }
        generateTasks(task, text);
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
      context.evaluateReader( scope, reader, "jshint library", 1, null );
      String code = "console = {log:function(){},error:function(){},trace:function(){}};";
      context.evaluateString( scope, code, "fake console", 1, null );
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
    int startCharacter = getPropertyAsInt( error, "character", -1 );
    int offset = text.getLineOffset( line - (defaultAnnotation.length() > 0 ? 2 : 1) );  // Adjust for the annotation comment at the top of the file.
    int stopCharacter;
    String code = getPropertyAsString( error, "code", "W000" );
    String token = coalesceToken( getPropertyAsString( error, "a", null ), reason );
	String content = text.getContent();
    if( startCharacter > 0 ) {
      startCharacter = fixStartPosition( content, line, startCharacter, offset );
    }
    if(token != null && offset + startCharacter > -1 && offset + startCharacter < content.length())
    {
	  	if(offset + startCharacter + token.length() < content.length() && content.substring(offset + startCharacter, offset + startCharacter + token.length()).equals(token))
	  	{
	  		stopCharacter = startCharacter + token.length();
	  	}
	  	else
	  		if(offset + startCharacter - token.length() > -1 && content.substring(offset + startCharacter - token.length(), offset + startCharacter).equals(token))
	  		{
	  			stopCharacter = startCharacter;
	  			startCharacter -= token.length();
	  		}
	  		else
	  			stopCharacter = startCharacter;
    }
    else
    	stopCharacter = startCharacter;
    if(defaultAnnotation != null && defaultAnnotation.length() > 0)
    	line--; // Adjust for the annotation comment at the top of the file.
    String message = reason.endsWith( "." ) ? reason.substring( 0, reason.length() - 1 ) : reason;
    return new ProblemImpl( line, startCharacter, stopCharacter, message, code );
  }

  private int fixStartPosition( String content, int line, int character, int offset ) {
    // JSHint reports physical character positions instead of a character index,
    // i.e. every tab character is multiplied with the indent.
    int indentIndex = 0;
    int charIndex = 0;
    int maxIndex = Math.min( character, content.length() - offset ) - 1;
    while( indentIndex < maxIndex ) {
      boolean isTab = content.charAt( offset + charIndex ) == '\t';
      indentIndex += isTab ? indent : 1;
      charIndex++;
    }
    return charIndex;
  }

  private String coalesceToken( String token, String reason ) {
	  String coalesced;
	  int index;

	  if(token == null && reason.length() > 0 && reason.charAt(0) == '\'')
	  {
		  index = reason.indexOf('\'', 1);
		  coalesced = index != -1 ? reason.substring(1, index) : token;
	  }
	  else
		  coalesced = token;

	  return coalesced;
  }

  private TaskImpl createTask( int line, int startCharacter, int stopCharacter, TaskTag tag, String message ) {
	  return new TaskImpl( line, startCharacter, stopCharacter, tag, message );
  }

  public static TaskTagImpl createTaskTag( String keyword, int priority ) {
	  return new TaskTagImpl ( keyword, priority );
  }

  // Generates a list of todo/fixme/xxx tasks found in the document
  // TODO: Make this use the central preferences store
  private void generateTasks(TaskHandler handler, Text text) {
	  String content;
	  String message;
	  int line;
	  int startCharacter;
	  int stopCharacter;
	  int offset;
	  int taskCount;
	  int taskIndex;
	  Matcher commentMatch;
	  Matcher taskMatch;

	  if(this.taskTags != null) {
		  taskCount = this.taskTags.size();
		  Pattern[] taskPattern = new Pattern[taskCount];
		  for( taskIndex = 0; taskIndex < taskCount; taskIndex++ )
			  taskPattern[taskIndex] = Pattern.compile(Pattern.quote(this.taskTags.get(taskIndex).getKeyword()) + ":?[ \t]+.*\\b[^ \t\n]*");
		  String comment;
		  content = text.getContent();
		  for( commentMatch = commentSearch.matcher(content); commentMatch.find(); ) {
			  comment = commentMatch.group(1);
			  // Prevent task expressions matching the closing mark for a comment
			  if(comment != null) {
				  if(comment.endsWith("*/"))
					  comment = comment.substring(0, comment.length() - 2);
				  for( taskIndex = 0; taskIndex < taskCount; taskIndex++ ) {
					  for(taskMatch = taskPattern[taskIndex].matcher(comment); taskMatch.find(); ) {
						  message = taskMatch.group();
						  startCharacter = taskMatch.start() + commentMatch.start();
						  stopCharacter = taskMatch.end() + commentMatch.start();
				    	  line = text.getOffsetLine(startCharacter);
				    	  offset = text.getLineOffset(line);
				    	  stopCharacter -= offset;
				    	  startCharacter -= offset;
				    	  handler.handleTask( createTask( line + 1, startCharacter, stopCharacter, this.taskTags.get(taskIndex), message ) );
					  }
				  }
			  }
		  }
	  }
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
