package com.eclipsesource.jshint.internal;

import com.eclipsesource.jshint.Task;
import com.eclipsesource.jshint.TaskTag;


public class TaskImpl implements Task {
  private final int line;
  private final int startCharacter;
  private final int stopCharacter;
  private final String message;
  private final TaskTag tag;

  public TaskImpl( int line, int startCharacter, int stopCharacter, TaskTag tag, String message ) {
    this.line = line;
    this.startCharacter = startCharacter;
    this.stopCharacter = stopCharacter;
    this.message = message;
    this.tag = tag;
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

  public TaskTag getTag() {
	  return tag;
  }
}
