package org.svomz.apps.koobz.board.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.annotations.VisibleForTesting;
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
  @JoinColumn(name = "board_id")
  private Board board;

  Stage() {
    this.id = UUID.randomUUID().toString();
    this.workItems = new HashSet<WorkItem>();
  }

  public Stage(final String name) {
    this();
    StageValidation.checkStageName(name);


    this.name = name;
  }

  @VisibleForTesting
  Stage(final String name, final List<WorkItem> workItems) {
    this(name);
    workItems.forEach(workItem -> this.addWorkItem(workItem));
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    StageValidation.checkStageName(name);

    this.name = name;
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
    workItem.setOrder(this.workItems.size());
    this.workItems.add(workItem);
    return this;
  }

  Stage removeWorkItem(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    this.workItems.remove(workItem);
    int workItemOrder = workItem.getOrder();

    //reoder all items after the removed one
    this.workItems.forEach(item -> {
      if (item.getOrder() > workItemOrder) {
        int currentOrder = item.getOrder();
        item.setOrder(currentOrder - 1);
      }
    });

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

  void setOrder(int order) {
    this.order = order;
  }

  public int getOrder() {
    return this.order;
  }

  boolean hasWorkItems() {
    return this.getWorkItems().size() > 0;
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
