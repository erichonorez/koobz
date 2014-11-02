package org.svomz.apps.kanban.domain;

public class PostIt {
	
	private String text;
	private Column column;

	public PostIt(final String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public Column getColumn() {
		return this.column;
	}

	PostIt setColumn(final Column column) {
		this.column = column;
		return this;
	}
	
}
