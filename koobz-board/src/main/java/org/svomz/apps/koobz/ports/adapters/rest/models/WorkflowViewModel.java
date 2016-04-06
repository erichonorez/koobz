package org.svomz.apps.koobz.ports.adapters.rest.models;

import java.util.ArrayList;
import java.util.List;

import org.svomz.apps.koobz.domain.model.Workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

/**
 * Represents how a {@link Workflow} should be JSONified.
 */
public class WorkflowViewModel {

  private final Workflow workflow;

  public WorkflowViewModel(final Workflow workflow) {
    Preconditions.checkNotNull(workflow);
    
    this.workflow = workflow;
  }
  
  @JsonProperty("id")
  @JsonView(SimpleView.class)
  public String getId() {
    return this.workflow.getId();
  }
  
  @JsonProperty("name")
  @JsonView(SimpleView.class)
  public String getName() {
    return this.workflow.getName();
  }
  
  @JsonProperty("stages")
  @JsonView(FullView.class)
  public List<StageViewModel> getStages() {
    ArrayList<StageViewModel> models = new ArrayList<>();

    this.workflow.stages().forEach(stage -> {
      models.add(new StageViewModel(stage));
    });

    return models;
  }
  
  /**
   * Specifies {@link WorkflowViewModel}'s attributes that must be JSONified to have a simple representation.
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class SimpleView { }
  
  /**
   * Specifies {@link WorkflowViewModel}'s attributes that must be JSONified to have a full representation.
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class FullView extends SimpleView { }
  
}
