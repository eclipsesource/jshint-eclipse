package com.eclipsesource.jshint.internal;

import com.eclipsesource.jshint.TaskTag;

public class TaskTagImpl implements TaskTag {
  private final int priority;
  private final String keyword;

  public TaskTagImpl( String keyword, int priority ) {
    this.priority = priority;
    this.keyword = keyword;
  }

	public int getPriority() {
		return priority;
	}
	
	public String getKeyword() {
		return keyword;
	}
}
