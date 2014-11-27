package org.svomz.apps.kanban.application;

import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;

public class KanbanServletModule extends ServletModule {
  
  protected void configureServlets() {
    this.filter("/*").through(PersistFilter.class);
  }
  
}
