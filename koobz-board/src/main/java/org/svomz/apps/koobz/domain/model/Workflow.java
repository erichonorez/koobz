package org.svomz.apps.koobz.domain.model;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Description: The board object is the aggregated root to manipulate the board itself and its
 * related objects : {@link WorkItem} and {@link Stage}.
 */
@Entity
@Table(name = "workflows")
public class Workflow {

  @Id
  private String id;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "workflow", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch =
    FetchType.EAGER)
  private Set<WorkItem> workItems;

  @OneToMany(mappedBy = "workflow", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch =
    FetchType.EAGER)
  private Set<Stage> stages;

  /**
   * Needed by JPA
   */
  private Workflow() {
  }

  public Workflow(final String workflowId, final String aBoardName) {
    this.workItems = new HashSet<WorkItem>();
    this.stages = new HashSet<Stage>();
    this.setId(workflowId);
    this.setName(aBoardName);
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    WorkflowValidation.checkBoardName(name);

    this.name = name;
  }

  /**
   * @return the list of visible work items on the board. This list does not contains archived items.
   *
   * This list is a unmodifiable Set to prevent direct manipulations (add / remove) of the set. If
   * you want to add or remove work items you must use {@link #removeWorkItemWithId(String)} and
   * {@link #addWorkItemToStage(String, String, String, String)}.
   */
  public Set<WorkItem> workItems() {
    Set<WorkItem>
      notArchivedWorkItems = this.allWorkItems().stream()
      .filter(workItem -> !workItem.isArchived())
      .collect(Collectors.toSet());

    return Collections.unmodifiableSet(notArchivedWorkItems);
  }

  /**
   * @return the list of stages on the board. This list is a unmodifiable Set to prevent direct
   * manipulations (add / remove) of the set. If you want to add or remove stages you must use
   * {@link #removeStageWithId(String)} and {@link #addStageToWorkflow(String, String)}}.
   */
  public Set<Stage> stages() {
    return Collections.unmodifiableSet(this.stages);
  }

  public Optional<Stage> stageOfId(String stageId) {
    Preconditions.checkNotNull(stageId);

    return this.stages().stream()
      .filter(stage -> {
        return stageId.equals(stage.getId());
      }).findFirst();
  }

  public Stage addStageToWorkflow(final String aStageIdentity, final String aStageTitle) {
    Preconditions.checkNotNull(aStageIdentity);
    Preconditions.checkNotNull(aStageTitle);

    Stage stage = new Stage(aStageIdentity, aStageTitle);
    stage.setWorkflow(this);
    stage.setPosition(this.stages.size());
    this.stages.add(stage);

    return stage;
  }

  public Workflow removeStageWithId(final String aStageId) throws StageNotInProcessException,
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

  public Workflow moveStageWithIdToPosition(final String aStageId, final int newPosition)
    throws StageNotInProcessException {
    Preconditions.checkNotNull(aStageId, "The given stage must not be null");

    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    Stage stage = optionalStage.get();

    if (newPosition == stage.getPosition()) {
      return this;
    }

    int position = newPosition;
    if (position >= this.stages.size()) {
      position = this.stages.size() - 1;
    }

    if (position > stage.getPosition()) {
      for (Stage item : this.stages) {
        if (item.getPosition() > stage.getPosition() && item.getPosition() <= position) {
          item.setPosition(item.getPosition() - 1);
        }
      }
    } else {
      for (Stage item : this.stages) {
        if (item.getPosition() >= newPosition && item.getPosition() < stage.getPosition()) {
          item.setPosition(item.getPosition() + 1);
        }
      }
    }

    stage.setPosition(position);

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
    workItem.setWorkflow(this);
    this.workItems.add(workItem);
    return workItem;
  }

  public Workflow removeWorkItemWithId(final String aWorkItemId) throws WorkItemNotInProcessException {
    Preconditions.checkNotNull(aWorkItemId, "The given workItem must not be null.");

    Optional<WorkItem> optionalWorkItem = this.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.getStage().removeWorkItem(workItem);
    this.workItems.remove(workItem);
    return this;

  }

  /**
   * Move a work item to a stage. The work item is added as the last element in the stage.
   *
   * @param aWorkItemId the work item to move
   * @param aStageId    the destination stage
   * @return the board to which belong the work item and the stage
   */
  public Workflow moveWorkItemWithIdToStageWithId(final String aWorkItemId, final String aStageId)
    throws WorkItemNotInProcessException, StageNotInProcessException {
    Preconditions.checkNotNull(aWorkItemId, "The given workItem must not be null.");
    Preconditions.checkNotNull(aStageId, "The given stage must not be null");

    Optional<WorkItem> optionalWorkItem = this.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    Optional<Stage> optionalStage = this.stageOfId(aStageId);
    if (!optionalStage.isPresent()) {
      throw new StageNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.getStage().removeWorkItem(workItem);

    Stage stage = optionalStage.get();
    stage.addWorkItem(workItem);
    return this;
  }

  public WorkItem moveWorkItemWithIdToPosition(final String aWorkItemId, int newPosition)
    throws WorkItemNotInProcessException, WorkItemNotInStageException {
    Preconditions.checkNotNull(aWorkItemId);

    Optional<WorkItem> optionalWorkItem = this.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.getStage().moveWorkItemToPosition(workItem, newPosition);
    return workItem;
  }

  public Workflow archiveWorkItemWithId(final String aWorkItemId)
    throws WorkItemNotInProcessException {
    Preconditions.checkNotNull(aWorkItemId);

    Optional<WorkItem> optionalWorkItem = this.workItemOfId(aWorkItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotInProcessException();
    }

    WorkItem workItem = optionalWorkItem.get();
    workItem.setArchived(true);
    return this;
  }

  public Workflow sendBackToWorkflowWorkItemWithId(final String workItemId)
    throws WorkItemNotArchivedException {
    Preconditions.checkNotNull(workItemId);

    Optional<WorkItem> optionalWorkItem = this.archivedWorkItemOfId(workItemId);
    if (!optionalWorkItem.isPresent()) {
      throw new WorkItemNotArchivedException();
    }

    optionalWorkItem.get().setArchived(false);
    return this;
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
   */
  public Optional<WorkItem> workItemOfId(final String aWorkItemId) {
    Preconditions.checkNotNull(aWorkItemId);

    return this.workItems()
      .stream()
      .filter(workItem -> aWorkItemId.equals(workItem.getId()))
      .findFirst();
  }

  private void setId(String boardId) {
    Preconditions.checkNotNull(boardId);
    this.id = boardId;
  }

  private Optional<WorkItem> archivedWorkItemOfId(final String workItemId) {
    return this.allWorkItems()
      .stream()
      .filter(workItem -> workItem.isArchived() && workItem.getId().equals(workItemId))
      .findFirst();
  }

  /**
   * @return the list of work items even the archived ones.
   */
  private Set<WorkItem> allWorkItems() {
    return this.workItems;
  }

  private static class WorkflowValidation {

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
