package org.svomz.apps.koobz.board.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.annotations.VisibleForTesting;
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
  private String id;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy="board", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<WorkItem> workItems;

  @OneToMany(mappedBy="board", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<Stage> stages;

  Board() {
    this.id = UUID.randomUUID().toString();
    this.workItems = new HashSet<WorkItem>();
    this.stages = new HashSet<Stage>();
  }

  public Board(final String name) {
    this();
    BoardValidation.checkBoardName(name);

    this.name = name;
  }

  @VisibleForTesting
  public Board(final String name, final List<Stage> stages) {
    this(name);
    for (Stage stage : stages) {
      this.addStage(stage);
    }
  }

  public Board(final String boardId, final String aBoardName) {
    Preconditions.checkNotNull(boardId);
    this.id = boardId;
    this.workItems = new HashSet<WorkItem>();
    this.stages = new HashSet<Stage>();
    this.setName(aBoardName);
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    BoardValidation.checkBoardName(name);

    this.name = name;
  }

  /**
   * @return the list of work items on the board. This list does not contains archived items.
   *
   * This list is a unmodifiable Set to prevent direct manipulations (add / remove) of the set.
   * If you want to add or remove work items you must use {@link #removeWorkItem(WorkItem)}
   * and {@link #addWorkItem(WorkItem, Stage)}.
   */
  public Set<WorkItem> getWorkItems() {
    Set<WorkItem>
      notArchivedWorkItems = this.getAllWorkItems().stream()
        .filter(workItem -> !workItem.isArchived())
        .collect(Collectors.toSet());

    return Collections.unmodifiableSet(notArchivedWorkItems);
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

    stage.setBoard(this);
    stage.setOrder(this.stages.size());
    this.stages.add(stage);

    if (stage.hasWorkItems()) {
      stage.getWorkItems().forEach(workItem -> {
        try {
          this.addWorkItem(workItem, stage);
        } catch (StageNotInProcessException e) {
          // This should never happen
          throw new IllegalStateException(e);
        }
      });
    }

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

  public Board reorderStage(final Stage stage,final int position)
    throws StageNotInProcessException {
    Preconditions.checkNotNull(stage, "The given stage must not be null");

    if (!this.stages.contains(stage)) {
      throw new StageNotInProcessException();
    }

    if (position == stage.getOrder()) {
      return this;
    }

    int order = position;
    if (order >= this.stages.size()) {
      order = this.stages.size() - 1;
    }

    if (order > stage.getOrder()) {
      for (Stage item : this.stages) {
        if (item.getOrder() > stage.getOrder() && item.getOrder() <= order) {
          item.setOrder(item.getOrder() - 1);
        }
      }
    } else {
      for (Stage item : this.stages) {
        if (item.getOrder() >= position && item.getOrder() < stage.getOrder()) {
          item.setOrder(item.getOrder() + 1);
        }
      }
    }

    stage.setOrder(order);

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
    workItem.setBoard(this);
    this.workItems.add(workItem);
    return this;

  }

  public Board removeWorkItem(final WorkItem workItem) throws WorkItemNotInProcessException {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    if (!this.workItems.contains(workItem)) {
      throw new WorkItemNotInProcessException();
    }

    workItem.getStage().removeWorkItem(workItem);
    this.workItems.remove(workItem);
    return this;

  }

  /**
   * Move a work item to a stage. The work item is added as the last element in the stage.
   *
   * @param workItem the work item to move
   * @param stage the destination stage
   * @return the board to which belong the work item and the stage
   *
   * @throws WorkItemNotInProcessException
   * @throws StageNotInProcessException
   */
  public Board moveWorkItemToStage(final WorkItem workItem, final Stage stage)
    throws WorkItemNotInProcessException, StageNotInProcessException {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");
    Preconditions.checkNotNull(stage, "The given stage must not be null");

    if (!this.workItems.contains(workItem)) {
      throw new WorkItemNotInProcessException();
    }
    if (!this.stages.contains(stage)) {
      throw new StageNotInProcessException();
    }

    workItem.getStage().removeWorkItem(workItem);
    stage.addWorkItem(workItem);
    return this;
  }
  
  public WorkItem putWorkItemAtPosition(WorkItem workItem, int i) throws WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(workItem);
    
    if (!this.getAllWorkItems().contains(workItem)) {
      throw new WorkItemNotInProcessException();
    }
    
    workItem.getStage().putWorkItemAtPosition(workItem, i);
    return workItem;
  }

  public Board archive(WorkItem workItem) throws WorkItemNotInProcessException {
    Preconditions.checkNotNull(workItem);

    if (!this.getWorkItems().contains(workItem)) {
      throw new WorkItemNotInProcessException();
    }

    workItem.setArchived(true);
    return this;
  }

  public Board unarchive(final String workItemId) throws WorkItemNotArchivedException {
    Preconditions.checkNotNull(workItemId);

    Optional<WorkItem> optionalWorkItem = this.archivedWorkItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotArchivedException();
    }

    optionalWorkItem.get().setArchived(false);
    return this;
  }

  /**
   * @return the list of work items event the archived ones.
   */
  private Set<WorkItem> getAllWorkItems() {
    return this.workItems;
  }

  public Optional<Stage> stageOfId(String stageId) {
    Preconditions.checkNotNull(stageId);

    return this.getStages().stream()
      .filter(stage -> {
        return stageId.equals(stage.getId());
      }).findFirst();
  }

  /**
   * Get the ordered list of non-archived work items in the stage having the specified id.
   *
   * @param aStageId the id of the stage
   * @return the ordered list of work items
   * @throws StageNotInProcessException if a stage with the specified id is not found in the board.
   */
  public List<WorkItem> getWorkItemsInStage(final String aStageId) throws StageNotInProcessException {
    Preconditions.checkNotNull(aStageId);

    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();
    return stage.getWorkItems()
      .stream()
      .sorted((workItem1, workItem2) -> {
        return workItem1.getOrder() - workItem2.getOrder();
      }).collect(Collectors.toList());
  }

  /**
   * Get the non archived work item by its id
   *
   * @param aWorkItemId
   * @return
   * @throws WorkItemNotInProcessException
   */
  public Optional<WorkItem> workItemOfId(final String aWorkItemId) {
    Preconditions.checkNotNull(aWorkItemId);

    return this.getWorkItems()
      .stream()
      .filter(workItem -> aWorkItemId.equals(workItem.getId()))
      .findFirst();
  }

  private Optional<WorkItem> archivedWorkItemOfId(final String workItemId) {
    return this.getAllWorkItems()
      .stream()
      .filter(workItem -> workItem.isArchived() && workItem.getId().equals(workItemId))
      .findFirst();
  }

  private static class BoardValidation {

    private final static int NAME_MIN_SIZE = 1;
    private final static int NAME_MAX_SIZE = 255;
    private final static String NAME_IS_NULL_ERR_MSG = "You must give a name to the board";
    private final static String NAME_SIZE_ERR_MSG = "The name length must be between %1s and %2s";

    private static void checkBoardName(@Nullable final String name) {
      Preconditions.checkArgument(name != null, NAME_IS_NULL_ERR_MSG);

      int nameLength = name.length();
      Preconditions.checkArgument(nameLength >= 1 && nameLength <= 255,
          String.format(NAME_SIZE_ERR_MSG, NAME_MIN_SIZE, NAME_MAX_SIZE));
    }

  }

}
