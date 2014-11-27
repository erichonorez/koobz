package org.svomz.apps.kanban.application;

import org.svomz.apps.kanban.domain.KanbanDomainModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;

public class KanbanServletConfig extends GuiceServletContextListener {

  private static Injector INSTANCE = null;

  @Override
  protected Injector getInjector() {
    return KanbanServletConfig.getInjectorInstance();
  }

  public static Injector getInjectorInstance() {
    if (INSTANCE == null) {
      INSTANCE =
          Guice.createInjector(new JpaPersistModule("kanban"), new KanbanServletModule(),
              new KanbanDomainModule());
    }
    return INSTANCE;
  }

}
