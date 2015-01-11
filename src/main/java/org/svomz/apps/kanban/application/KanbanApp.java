package org.svomz.apps.kanban.application;

import javax.inject.Singleton;

import org.svomz.apps.kanban.domain.KanbanDomainModule;
import org.svomz.commons.application.AbstractApplication;
import org.svomz.commons.application.AppLauncher;
import org.svomz.commons.application.modules.HttpServerModule;
import org.svomz.commons.application.modules.JerseyModule;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

public class KanbanApp extends AbstractApplication {

  @Override
  public void run() {
    // do nothing
  }

  @Override
  public Iterable<? extends Module> getModules() {
    return ImmutableList.<Module>builder()
        .add(new HttpServerModule())
        .add(new JerseyModule(KanbanAppResourceConfig.class))
        .add(new JpaPersistModule("kanban"))
        .add(new KanbanDomainModule())
        .add(new ServletModule() {
          @Override
          protected void configureServlets() {
            this.bind(PersistFilter.class).in(Singleton.class);
            this.filter("/*").through(PersistFilter.class);}
        }).build();
  }
  
  public static void main(String... args) {
    AppLauncher.launch(KanbanApp.class);
  }

}
