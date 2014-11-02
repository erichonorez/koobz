package org.svomz.apps.kanban.domain.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Board {

	private String name;
	private Set<WorkItem> workItems;
	private Set<Stage> stages;
	
	public Board(final String name) {
		this.name = name;
		this.workItems = new HashSet<WorkItem>();
		this.stages = new HashSet<Stage>();
	}
	
	public String getName() {
		return this.name;
	}

	public Set<WorkItem> getWorkItems() {
		return Collections.unmodifiableSet(this.workItems);
	}

	public Set<Stage> getStages() {
		return Collections.unmodifiableSet(this.stages);
	}

	public Board addStage(final Stage stage) {
		this.stages.add(stage);
		return this;
	}

	public Board removeStage(Stage stage) throws StageNotInProcessException, StageNotEmptyException {
		if (!this.stages.contains(stage)) {
			throw new StageNotInProcessException();
		}
		
		if (!stage.getWorkItems().isEmpty()) {
			throw new StageNotEmptyException();
		}
		
		this.stages.remove(stage);
		return this;
		
	}

	public Board addWorkItem(WorkItem workItem, Stage stage) throws StageNotInProcessException {
		if (!this.stages.contains(stage)) {
			throw new StageNotInProcessException();
		}
		
		stage.addWorkItem(workItem);
		this.workItems.add(workItem);
		return this;
		
	}

	public Board removeWorkItem(final WorkItem workItem) throws WorkItemNotOnBoardException {
		if (!this.workItems.contains(workItem)) {
			throw new WorkItemNotOnBoardException();
		}
		
		workItem.getStage().removeWorkItem(workItem);
		this.workItems.remove(workItem);
		return this;
		
	}

	public Board moveWorkItem(WorkItem workItem, final Stage stage) throws WorkItemNotOnBoardException, StageNotInProcessException {
		if (!this.workItems.contains(workItem)) {
			throw new WorkItemNotOnBoardException();
		}
		if (!this.stages.contains(stage)) {
			throw new StageNotInProcessException();
		}
		
		workItem.getStage().removeWorkItem(workItem);
		stage.addWorkItem(workItem);
		return this;
	}

}
