package org.svomz.apps.kanban.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "stage")
  private Set<WorkItem> workItems;
  
  @ManyToOne
  @JoinColumn(name = "board_id")
  private Board board;

  Stage() {
    this.workItems = new HashSet<WorkItem>();
  }

  public Stage(final String name) {
    this();
    StageValidation.checkStageName(name);

    this.name = name;
  }

  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    StageValidation.checkStageName(name);

    this.name = name;
  }

  public Set<WorkItem> getWorkItems() {
    return Collections.unmodifiableSet(this.workItems);
  }

  Stage addWorkItem(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    workItem.setStage(this);
    workItem.setOrder(this.workItems.size());
    this.workItems.add(workItem);
    return this;
  }

  Stage removeWorkItem(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    this.workItems.remove(workItem);
    workItem.setStage(null);
    return this;
  }
  
  Stage setBoard(final Board board) {
    Preconditions.checkNotNull(board, "The given board must not be null.");
    
    this.board = board;
    return this;
  }
  
  Stage reoderWorkItem(WorkItem workItem, int order) throws WorkItemNotInStageException {
    Preconditions.checkNotNull(workItem);
    Preconditions.checkArgument(order >= 0);
    
    if (!this.workItems.contains(workItem)) {
      throw new WorkItemNotInStageException();
    }
    
    if (order == workItem.getOrder()) {
      return this;
    }
    
    if (order > this.workItems.size()) {
      order = this.workItems.size() - 1;
    }
    
    if (order > workItem.getOrder()) {
      for (WorkItem item : this.workItems) {
        if (item.getOrder() > workItem.getOrder() && item.getOrder() <= order) {
          item.setOrder(item.getOrder() - 1);
        }
      }
    } else {
      for (WorkItem item : this.workItems) {
        if (item.getOrder() >= order && item.getOrder() < workItem.getOrder()) {
          item.setOrder(item.getOrder() + 1);
        }
      }
    }
    
    workItem.setOrder(order);
    return this;
  }

  private static class StageValidation {

    private final static int NAME_MIN_SIZE = 1;
    private final static int NAME_MAX_SIZE = 255;
    private final static String NAME_IS_NULL_ERR_MSG = "You must give a name to the stage";
    private final static String NAME_SIZE_ERR_MSG = "The name length must be between %1s and %2s";

    private static void checkStageName(@Nullable final String name) {
      Preconditions.checkArgument(name != null, NAME_IS_NULL_ERR_MSG);

      int nameLength = name.length();
      Preconditions.checkArgument(nameLength >= 1 && nameLength <= 255,
          String.format(NAME_SIZE_ERR_MSG, NAME_MIN_SIZE, NAME_MAX_SIZE));
    }

  }

}
