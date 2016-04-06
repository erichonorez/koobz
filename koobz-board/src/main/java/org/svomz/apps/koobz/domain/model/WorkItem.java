package org.svomz.apps.koobz.domain.model;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "work_items")
public class WorkItem {

  @Id
  private String id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;
  
  @Column(name = "position")
  private int priority;

  @ManyToOne
  @JoinColumn(name = "stage_id")
  private Stage stage;

  @ManyToOne
  @JoinColumn(name = "workflow_id")
  private Workflow workflow;

  @Transient
  private boolean archived;

  /**
   * No-args constructor required by JPA.
   */
  private WorkItem() {
  }

  WorkItem(String aWorkItemId, String aWorkItemTitle, String aWorkItemDescription) {
    this.id = Preconditions.checkNotNull(aWorkItemId);
    this.setTitle(aWorkItemTitle);
    this.setDescription(aWorkItemDescription);
  }

  public String getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public Stage getStage() {
    return this.stage;
  }

  @Nullable
  public String getDescription() {
    return this.description;
  }

  public WorkItem setTitle(String title) {
    WorkItemValidation.checkTitle(title);

    this.title = title;
    return this;
  }

  public WorkItem setDescription(@Nullable String description) {
    WorkItemValidation.checkDescription(description);

    this.description = description;
    return this;
  }

  WorkItem setStage(@Nullable final Stage stage) {
    this.stage = stage;
    return this;
  }

  WorkItem setPriority(final int priority) {
    this.priority = priority;
    return this;
  }

  @Nullable
  public int getPriority() {
    return this.priority;
  }

  WorkItem setWorkflow(final Workflow workflow) {
    this.workflow = workflow;
    return this;
  }

  void setArchived(boolean archived) {
    this.archived = archived;
  }

  boolean isArchived() {
    return this.archived;
  }

  private static class WorkItemValidation {

    private final static int TITLE_MIN_SIZE = 1;
    private final static int TITLE_MAX_SIZE = 255;
    private final static int DESCRIPTION_MAX_SIZE = 2048;
    
    private final static String TITLE_IS_NULL_ERR_MSG = "You must give a text to the work item";
    private final static String TITLE_SIZE_ERR_MSG = "The title length must be between %1s and %2s";
    private static final String DESCRIPTION_SIZE_ERR_MESG = "The description length must be lower or equal to %1s";

    private static void checkTitle(@Nullable final String title) {
      Preconditions.checkArgument(title != null, TITLE_IS_NULL_ERR_MSG);

      int titleLength = title.length();
      Preconditions.checkArgument(titleLength >= TITLE_MIN_SIZE && titleLength <= TITLE_MAX_SIZE,
          String.format(TITLE_SIZE_ERR_MSG, TITLE_MIN_SIZE, TITLE_MAX_SIZE));
    }

    public static void checkDescription(@Nullable String description) {
      if (description == null) {
        return;
      }

      Preconditions.checkArgument(description.length() <= 2048,
        String.format(DESCRIPTION_SIZE_ERR_MESG, DESCRIPTION_MAX_SIZE));
    }
  }
  
}
