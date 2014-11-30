package org.svomz.apps.kanban.domain.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import jersey.repackaged.com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "stages")
@JsonIgnoreProperties({"workItems"})
public class Stage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty
  private long id;

  @Column(name = "name")
  @JsonProperty
  private String name;

  @OneToMany(mappedBy = "stage")
  private Set<WorkItem> workItems;

  private Stage() {
    this.workItems = new HashSet<WorkItem>();
  }

  public Stage(final String name) {
    this();
    Preconditions.checkNotNull(name, "The stage must have a name.");

    this.name = name;
  }

  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  Set<WorkItem> getWorkItems() {
    return Collections.unmodifiableSet(this.workItems);
  }

  Stage addWorkItem(WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    workItem.setStage(this);
    this.workItems.add(workItem);
    return this;
  }

  Stage removeWorkItem(WorkItem workItem) {
    Preconditions.checkNotNull(workItem, "The given workItem must not be null.");

    this.workItems.remove(workItem);
    workItem.setStage(null);
    return this;
  }

}
