package org.svomz.apps.kanban.domain.entities;

import java.util.HashSet;
import java.util.Set;

public class Stage {

	private String name;
	private Set<WorkItem> workItems;

	public Stage(final String name) {
		this.name = name;
		this.workItems = new HashSet<WorkItem>();
	}
	
	public String getName() {
		return this.name;
	}

	Set<WorkItem> getWorkItems() {
		return this.workItems;
	}

	Stage addWorkItem(WorkItem workItem) {
		workItem.setStage(this);
		this.workItems.add(workItem);
		return this;
	}

	Stage removeWorkItem(WorkItem workItem) {
		this.workItems.remove(workItem);
		workItem.setStage(null);
		return this;
	}
	
}
