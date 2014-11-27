package org.svomz.apps.kanban.domain.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.Preconditions;

/**
 * Description: The board object is the aggregated root to manipulate the board itself and its
 * related objects : {@link WorkItem} and {@link Stage}.
 * 
 * @author Eric Honorez
 */
@Entity
@Table(name = "boards")
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
  @JoinColumn(name = "board_id")
  private Set<WorkItem> workItems;

  @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
  @JoinColumn(name = "board_id")
  private Set<Stage> stages;

  private Board() {
    this.workItems = new HashSet<WorkItem>();
    this.stages = new HashSet<Stage>();
  }

  public Board(final String name) {
    this();
    Preconditions.checkNotNull(name, "You must give a name to your board.");

    this.name = name;
  }

  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    Preconditions.checkNotNull(name, "The supplied board name must not be null.");

    this.name = name;
  }

  /**
   * @return the list of work items on the board. This list is a unmodifiable Set to prevent direct
   *         manipulations (add / remove) of the set. If you want to add or remove work items you
   *         must use {@link #removeWorkItem(WorkItem)} and {@link #addWorkItem(WorkItem, Stage)}.
   */
  public Set<WorkItem> getWorkItems() {
    return Collections.unmodifiableSet(this.workItems);
  }

  /**
   * @return the list of stages on the board. This list is a unmodifiable Set to prevent direct
   *         manipulations (add / remove) of the set. If you want to add or remove stages you must
   *         use {@link #removeStage(Stage)} and {@link #addStage(Stage)}.
   */
  public Set<Stage> getStages() {
    return Collections.unmodifiableSet(this.stages);
  }

  public Board addStage(final Stage stage) {
    Preconditions.checkNotNull(stage, "The given stage must not be null.");

    this.stages.add(stage);
    return this;
  }

  public Board removeStage(final Stage stage) throws StageNotInProcessException,
      StageNotEmptyException {
    Preconditions.checkNotNull(stage, "The given stage must not be null.");

    if (!this.stages.contains(stage)) {
      throw new StageNotInProcessException();
    }

    if (!stage.getWorkItems().isEmpty()) {
      throw new StageNotEmptyException();
    }

    this.stages.remove(stage);
    return this;
  }

  public Board addWorkItem(final WorkItem workItem, final Stage stage)
      throws StageNotInProcessException {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");
    Preconditions.checkNotNull(stage, "The given stage must not be null");

    if (!this.stages.contains(stage)) {
      throw new StageNotInProcessException();
    }

    stage.addWorkItem(workItem);
    this.workItems.add(workItem);
    return this;

  }

  public Board removeWorkItem(final WorkItem workItem) throws WorkItemNotOnBoardException {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    if (!this.workItems.contains(workItem)) {
      throw new WorkItemNotOnBoardException();
    }
    
    workItem.getStage().removeWorkItem(workItem);
    this.workItems.remove(workItem);
    return this;

  }

  public Board moveWorkItem(final WorkItem workItem, final Stage stage)
      throws WorkItemNotOnBoardException, StageNotInProcessException {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");
    Preconditions.checkNotNull(stage, "The given stage must not be null");

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
