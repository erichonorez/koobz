package org.svomz.apps.kanban.domain.entities;

public class WorkItem {
	
	private String text;
	private Stage stage;

	public WorkItem(final String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public Stage getStage() {
		return this.stage;
	}

	WorkItem setStage(final Stage stage) {
		this.stage = stage;
		return this;
	}
	
}
