package org.svomz.apps.koobz.board.domain;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
