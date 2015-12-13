package org.svomz.apps.kanban.application;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import org.svomz.apps.kanban.application.exceptions.EntityNotFoundMapper;
import org.svomz.apps.kanban.application.exceptions.IllegalArgumentMapper;
import org.svomz.apps.kanban.application.exceptions.StageNotEmptyMapper;
import org.svomz.apps.kanban.application.exceptions.StageNotInProcessMapper;
import org.svomz.apps.kanban.application.exceptions.WorkItemNotOnBoardMapper;
import org.svomz.apps.kanban.application.resources.BoardResource;
import org.svomz.apps.kanban.application.resources.StageResource;
import org.svomz.apps.kanban.application.resources.WorkItemResource;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/")
public class KanbanResourceConfig extends ResourceConfig {

  public KanbanResourceConfig() {
    this.register(BoardResource.class);
    this.register(StageResource.class);
    this.register(WorkItemResource.class);
    this.register(JacksonFeature.class);
    this.register(EntityNotFoundMapper.class);
    this.register(IllegalArgumentMapper.class);
    this.register(StageNotEmptyMapper.class);
    this.register(StageNotInProcessMapper.class);
    this.register(WorkItemNotOnBoardMapper.class);
  }

}
