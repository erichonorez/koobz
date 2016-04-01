package org.svomz.apps.koobz.board.ports.adapters.rest.models;

import java.util.ArrayList;
import java.util.List;

import org.svomz.apps.koobz.board.domain.Board;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Preconditions;

/**
 * Represents how a {@link Board} should be JSONified.
 */
public class BoardViewModel {

  private final Board board;

  public BoardViewModel(final Board board) {
    Preconditions.checkNotNull(board);
    
    this.board = board;
  }
  
  @JsonProperty("id")
  @JsonView(SimpleView.class)
  public String getId() {
    return this.board.getId();
  }
  
  @JsonProperty("name")
  @JsonView(SimpleView.class)
  public String getName() {
    return this.board.getName();
  }
  
  @JsonProperty("stages")
  @JsonView(FullView.class)
  public List<StageViewModel> getStages() {
    ArrayList<StageViewModel> models = new ArrayList<>();

    this.board.getStages().forEach(stage -> {
      models.add(new StageViewModel(stage));
    });

    return models;
  }
  
  /**
   * Specifies {@link BoardViewModel}'s attributes that must be JSONified to have a simple representation. 
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class SimpleView { }
  
  /**
   * Specifies {@link BoardViewModel}'s attributes that must be JSONified to have a full representation. 
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class FullView extends SimpleView { }
  
}
