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
   * and {@link #addWorkItemToStage(String, String, String, String)}.
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
   *         use {@link #removeStageWithId(String)} and {@link #addStageToBoard(String, String)}}.
   */
  public Set<Stage> getStages() {
    return Collections.unmodifiableSet(this.stages);
  }

  public Stage addStageToBoard(final String aStageIdentity, final String aStageTitle) {
    Preconditions.checkNotNull(aStageIdentity);
    Preconditions.checkNotNull(aStageTitle);

    Stage stage = new Stage(aStageIdentity, aStageTitle);
    stage.setBoard(this);
    stage.setPosition(this.stages.size());
    this.stages.add(stage);

    return stage;
  }

  public Board removeStageWithId(final String aStageId) throws StageNotInProcessException,
                                                               StageNotEmptyException {
    Preconditions.checkNotNull(aStageId, "The given stage must not be null.");


    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();
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

    if (position == stage.getPosition()) {
      return this;
    }

    int order = position;
    if (order >= this.stages.size()) {
      order = this.stages.size() - 1;
    }

    if (order > stage.getPosition()) {
      for (Stage item : this.stages) {
        if (item.getPosition() > stage.getPosition() && item.getPosition() <= order) {
          item.setPosition(item.getPosition() - 1);
        }
      }
    } else {
      for (Stage item : this.stages) {
        if (item.getPosition() >= position && item.getPosition() < stage.getPosition()) {
          item.setPosition(item.getPosition() + 1);
        }
      }
    }

    stage.setPosition(order);

    return this;
  }

  public WorkItem addWorkItemToStage(final String aStageId, final String aWorkItemId,
    String aWorkItemTitle, String aWorkItemDescription)
      throws StageNotInProcessException {
    Preconditions.checkNotNull(aWorkItemId, "The given workItem must not be null.");
    Preconditions.checkNotNull(aStageId, "The given stage must not be null");

    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();
    WorkItem workItem = new WorkItem(aWorkItemId, aWorkItemTitle, aWorkItemDescription);
    stage.addWorkItem(workItem);
    workItem.setBoard(this);
    this.workItems.add(workItem);
    return workItem;
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
  
  public WorkItem putWorkItemAtPosition(final WorkItem workItem, int i)
    throws WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(workItem);
    
    if (!this.getAllWorkItems().contains(workItem)) {
      throw new WorkItemNotInProcessException();
    }
    
    workItem.getStage().putWorkItemAtPosition(workItem, i);
    return workItem;
  }

  public Board archiveWorkItemWithId(final String aWorkItemId) throws WorkItemNotInProcessException {
    Preconditions.checkNotNull(aWorkItemId);

    Optional<WorkItem> optionalWorkItem = this.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.setArchived(true);
    return this;
  }

  public Board sendBackToBoardWorkItemWithId(final String workItemId) throws WorkItemNotArchivedException {
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
  public List<WorkItem> workItemsInStage(final String aStageId) throws StageNotInProcessException {
    Preconditions.checkNotNull(aStageId);

    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();
    return stage.getWorkItems()
      .stream()
      .sorted((workItem1, workItem2) -> {
        return workItem1.getPosition() - workItem2.getPosition();
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
