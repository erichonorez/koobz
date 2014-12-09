package org.svomz.apps.kanban.domain.entities;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import jersey.repackaged.com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "work_items")
public class WorkItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty
  private long id;

  @Column(name = "text")
  @JsonProperty
  private String text;

  @ManyToOne
  @JoinColumn(name = "stage_id")
  @JsonProperty
  private Stage stage;

  @SuppressWarnings("unused")
  private WorkItem() {}

  public WorkItem(final String text) {
    WorkItemValidation.checkWorkItemText(text);

    this.text = text;
  }

  public long getId() {
    return this.id;
  }

  public String getText() {
    return this.text;
  }

  public Stage getStage() {
    return this.stage;
  }

  public WorkItem setText(String text) {
    WorkItemValidation.checkWorkItemText(text);

    this.text = text;
    return this;
  }

  WorkItem setStage(@Nullable final Stage stage) {
    this.stage = stage;
    return this;
  }

  private static class WorkItemValidation {

    private final static int TEXT_MIN_SIZE = 1;
    private final static int TEXT_MAX_SIZE = 255;
    private final static String TEXT_IS_NULL_ERR_MSG = "You must give a text to the work item";
    private final static String TEXT_SIZE_ERR_MSG = "The text length must be between %1s and %2s";

    private static void checkWorkItemText(@Nullable final String text) {
      Preconditions.checkArgument(text != null, TEXT_IS_NULL_ERR_MSG);

      int textLength = text.length();
      Preconditions.checkArgument(textLength >= 1 && textLength <= 255,
          String.format(TEXT_SIZE_ERR_MSG, TEXT_MIN_SIZE, TEXT_MAX_SIZE));
    }

  }

}
