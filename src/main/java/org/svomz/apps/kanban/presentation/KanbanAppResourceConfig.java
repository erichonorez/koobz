package org.svomz.apps.kanban.presentation;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.svomz.commons.application.modules.JerseyModule.AppInjector;

@ApplicationPath("/")
public class KanbanAppResourceConfig extends ResourceConfig {

  public KanbanAppResourceConfig() {
    this.packages("org.svomz.apps.kanban.presentation.resources",
        "org.svomz.apps.kanban.presentation.exceptions");
    this.register(ValidationFeature.class);
    this.register(JacksonFeature.class);
    this.register(AppInjector.class);
    
    this.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
  }

}
