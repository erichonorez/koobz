package org.svomz.apps.kanban.application;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.google.inject.Injector;

@ApplicationPath("/")
public class KanbanApplication extends ResourceConfig {

  @Inject
  public KanbanApplication(ServiceLocator serviceLocator) {
    this.packages("org.svomz.apps.kanban.application.resources");
    this.register(JacksonFeature.class);
    // Guice bridge configuration
    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    GuiceIntoHK2Bridge bridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    Injector injectorInstance = KanbanServletConfig.getInjectorInstance();
    bridge.bridgeGuiceInjector(injectorInstance);
  }
  
}
