package com.eclipsesource.jshint;

/**
 * Holds information about a task found by JSHint.
 */
public interface Task {

  /**
   * Returns the line number where the task was found.
   *
   * @return the line number, beginning with 1
   */
  int getLine();

  /**
   * Returns the character offset within the line in which the task is located.
   * 
   * @return the character offset, beginning with 0
   */
  int getStartCharacter();

  /**
   * Returns the character offset at the end of the task (if present), otherwise the same as getStartCharacter() above.
   * 
   * @return the character offset, beginning with 0
   */
  int getStopCharacter();

  /**
   * The tag for the task (usu. todo, fixme or xxx)
   *
   * @return the code
   */
  TaskTag getTag();

  /**
   * The task message found in the scan.
   *
   * @return the message
   */
  String getMessage();
}
