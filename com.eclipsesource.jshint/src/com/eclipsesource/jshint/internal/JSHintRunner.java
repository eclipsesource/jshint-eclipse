package com.eclipsesource.jshint.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.jshint.Configuration;
import com.eclipsesource.jshint.JSHint;
import com.eclipsesource.jshint.Problem;
import com.eclipsesource.jshint.ProblemHandler;


public class JSHintRunner {

  private List<File> files;
  private Charset charset;
  private JSHint jshint;

  public void run( String... args ) {
    try {
      readArgs( args );
      ensureCharset();
      ensureInputFiles();
      loadJSHint();
      configureJSHint();
      processFiles();
    } catch( Exception e ) {
      System.out.println( e.getMessage() );
    }
  }

  private void readArgs( String[] args ) {
    files = new ArrayList<File>();
    String lastArg = null;
    for( String arg : args ) {
      if( "--charset".equals( lastArg ) ) {
        setCharset( arg );
      } else if( "--charset".equals( arg ) ) {
        // continue
      } else {
        File file = new File( arg );
        if( !file.exists() ) {
          throw new IllegalArgumentException( "No such file: " + file.getAbsolutePath() );
        }
        files.add( file );
      }
      lastArg = arg;
    }
  }

  private void ensureCharset() {
    if( charset == null ) {
      setCharset( "UTF-8" );
    }
  }

  private void setCharset( String name ) {
    try {
      charset = Charset.forName( name );
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Unknown or unsupported charset: " + name );
    }
  }

  private void ensureInputFiles() {
    if( files.isEmpty() ) {
      throw new IllegalArgumentException( "No input files" );
    }
  }

  private void loadJSHint() {
    jshint = new JSHint();
    try {
      jshint.load();
    } catch( IOException exception ) {
      String message = "Failed to load JSHint library: " + exception.getMessage();
      throw new IllegalArgumentException( message );
    }
  }

  private void processFiles() throws IOException {
    for( File file : files ) {
      ProblemHandler handler = new SysoutProblemHandler( file.getAbsolutePath() );
      String code = readFileContents( file );
      jshint.check( code, handler );
    }
  }

  private void configureJSHint() {
    jshint.configure( new Configuration() );
  }

  private String readFileContents( File file ) throws FileNotFoundException, IOException {
    FileInputStream inputStream = new FileInputStream( file );
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    StringBuilder builder = new StringBuilder();
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

  private static final class SysoutProblemHandler implements ProblemHandler {

    private final String fileName;

    public SysoutProblemHandler( String fileName ) {
      this.fileName = fileName;
    }

    public void handleProblem( Problem problem ) {
      int line = problem.getLine();
      String message = problem.getMessage();
      System.out.println( "Problem in file " + fileName + " at line " + line + ": " + message );
    }

  }

}
