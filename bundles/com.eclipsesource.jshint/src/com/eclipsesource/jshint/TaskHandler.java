package com.eclipsesource.jshint;


/**
 * Implementations of this class are used to handle tasks found by JSHint.
 */
public interface TaskHandler {

  /**
   * Handles a task found during the task scan.
   *
   * @param task
   *          the task
   */
  void handleTask( Task task );

}
