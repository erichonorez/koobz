package org.svomz.apps.koobz.board.domain.model;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 11/02/16.
 */
public class BoardMaker {

  public static final Property<Board, String> name = Property.newProperty();

  public static final Property<Board, List<Stage>> stages = Property.newProperty();

  public static final Instantiator<Board> Board = propertyLookup -> {
    Board board = new Board(
      propertyLookup.valueOf(name, "A random name"),
      propertyLookup.valueOf(stages, new ArrayList<>())
    );
    return board;
  };

}
