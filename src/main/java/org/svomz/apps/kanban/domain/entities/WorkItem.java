package org.svomz.apps.kanban.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import jersey.repackaged.com.google.common.base.Preconditions;

@Entity
@Table(name = "work_items")
public class WorkItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "text")
  private String text;

  @ManyToOne
  @JoinColumn(name = "stage_id")
  private Stage stage;

  public long getId() {
    return this.id;
  }
  
  @SuppressWarnings("unused")
  private WorkItem() {
  }
  
  public WorkItem(final String text) {
    Preconditions.checkNotNull(text, "The work item must have a text.");
    
    this.text = text;
  }

  public String getText() {
    return this.text;
  }

  public Stage getStage() {
    return this.stage;
  }
  
  public WorkItem setText(String text) {
    Preconditions.checkNotNull(text);
    
    this.text = text;
    return this;
  }
  
  WorkItem setStage(final Stage stage) {
    this.stage = stage;
    return this;
  }

}
