package org.svomz.apps.koobz.board.domain;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eric on 11/02/16.
 */
public class StageMaker {

  public static final Property<Stage, String> stageName = Property.newProperty();

  public static final Property<Stage, List<WorkItem>> workItems = Property.newProperty();

  public static final Instantiator<Stage> Stage = propertyLookup -> {
    Stage stage = new Stage(
      propertyLookup.valueOf(stageName, "A random name"),
      propertyLookup.valueOf(workItems, new ArrayList<>())
    );
    return stage;
  };

}
