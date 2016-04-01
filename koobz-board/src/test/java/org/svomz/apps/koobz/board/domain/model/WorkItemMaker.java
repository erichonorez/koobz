package org.svomz.apps.koobz.board.domain.model;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

/**
 * Created by eric on 11/02/16.
 */
public class WorkItemMaker {

  public static final Property<WorkItem, String> workItemName = Property.newProperty();

  public static final Property<WorkItem, String> workItemDescription = Property.newProperty();

  public static final Instantiator<WorkItem> WorkItem = propertyLookup -> {
    WorkItem workItem = new WorkItem(
      propertyLookup.valueOf(workItemName, "A random name"),
      propertyLookup.valueOf(workItemDescription, "")
    );
    return workItem;
  };

}
