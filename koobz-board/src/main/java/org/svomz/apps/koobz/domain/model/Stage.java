package org.svomz.apps.koobz.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.Preconditions;


@Entity
@Table(name = "stages")
public class Stage {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "position")
  private int order;

  @OneToMany(mappedBy = "stage", fetch = FetchType.EAGER)
  private Set<WorkItem> workItems;
  
  @ManyToOne
  @JoinColumn(name = "workflow_id")
  private Workflow workflow;

  /** Needed by JPA */
  private Stage() {}

  Stage(final String stageId, final String name) {
    this.id = Preconditions.checkNotNull(stageId);
    this.setName(name);
    this.workItems = new HashSet<>();
  }

  public void setName(String name) {
    this.name = Preconditions.checkNotNull(name);
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  /**
   * @return the list of work items on the board. This list does not contains archived items.
   */
  public Set<WorkItem> getWorkItems() {
    Set<WorkItem>
      notArchivedWorkItems = this.workItems.stream()
      .filter(workItem -> !workItem.isArchived())
      .collect(Collectors.toSet());

    return Collections.unmodifiableSet(notArchivedWorkItems);
  }

  Stage addWorkItem(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    workItem.setStage(this);
    workItem.setPriority(this.workItems.size());
    this.workItems.add(workItem);
    return this;
  }

  Stage removeWorkItem(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    this.workItems.remove(workItem);
    int workItemOrder = workItem.getPriority();

    //reoder all items after the removed one
    this.workItems.forEach(item -> {
      if (item.getPriority() > workItemOrder) {
        int currentOrder = item.getPriority();
        item.setPriority(currentOrder - 1);
      }
    });

    return this;
  }
  
  Stage setWorkflow(final Workflow workflow) {
    Preconditions.checkNotNull(workflow, "The given board must not be null.");
    
    this.workflow = workflow;
    return this;
  }
  
  Stage moveWorkItemToPosition(WorkItem workItem, int position) throws WorkItemNotInStageException {
    Preconditions.checkNotNull(workItem);
    Preconditions.checkArgument(position >= 0);
    
    if (!this.workItems.contains(workItem)) {
      throw new WorkItemNotInStageException();
    }
    
    if (position == workItem.getPriority()) {
      return this;
    }
    
    if (position > this.workItems.size()) {
      position = this.workItems.size() - 1;
    }
    
    if (position > workItem.getPriority()) {
      for (WorkItem item : this.workItems) {
        if (item.getPriority() > workItem.getPriority() && item.getPriority() <= position) {
          item.setPriority(item.getPriority() - 1);
        }
      }
    } else {
      for (WorkItem item : this.workItems) {
        if (item.getPriority() >= position && item.getPriority() < workItem.getPriority()) {
          item.setPriority(item.getPriority() + 1);
        }
      }
    }
    
    workItem.setPriority(position);
    return this;
  }

  void setPosition(int order) {
    this.order = order;
  }

  public int getPosition() {
    return this.order;
  }

  boolean hasWorkItems() {
    return this.getWorkItems().size() > 0;
  }

}
