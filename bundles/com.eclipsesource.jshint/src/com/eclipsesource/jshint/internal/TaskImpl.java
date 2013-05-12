package com.eclipsesource.jshint.internal;

import com.eclipsesource.jshint.Task;


public class TaskImpl implements Task {
  private final int line;
  private final int startCharacter;
  private final int stopCharacter;
  private final String message;
  private final String code;

  public TaskImpl( int line, int startCharacter, int stopCharacter, String code, String message ) {
    this.line = line;
    this.startCharacter = startCharacter;
    this.stopCharacter = stopCharacter;
    this.message = message;
    this.code = code;
  }

  public int getLine() {
    return line;
  }

  public int getStartCharacter() {
	  return startCharacter;
  }

  public int getStopCharacter() {
	  return stopCharacter;
  }

  public String getMessage() {
    return message;
  }

  public String getCode() {
	  return code;
  }
}
