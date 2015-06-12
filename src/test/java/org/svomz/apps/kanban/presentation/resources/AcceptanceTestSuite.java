package org.svomz.apps.kanban.presentation.resources;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.svomz.apps.kanban.presentation.KanbanApp;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.LifecycleModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardResourceAcceptanceTest.class,
  StageResourceAcceptanceTest.class,
  WorkItemResourceAcceptanceTest.class
})
public class AcceptanceTestSuite {

  @BeforeClass
  public static void bootstrap() throws InstantiationException, IllegalAccessException, InterruptedException {
    Set<Module> modules = ImmutableSet.<Module>builder()
        .add(new LifecycleModule())
        .addAll(KanbanApp.class.newInstance().getModules())
        .build();
    Injector injector = Guice.createInjector(modules);
    final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
    lifecycle.start();
    Thread.sleep(10000);
  }
  
}
