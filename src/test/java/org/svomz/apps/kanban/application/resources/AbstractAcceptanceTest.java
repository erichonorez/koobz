package org.svomz.apps.kanban.application.resources;

import static com.jayway.restassured.config.RestAssuredConfig.config;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.svomz.apps.kanban.application.KanbanApp;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.LifecycleModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ConnectionConfig;

public class AbstractAcceptanceTest {
  
  @BeforeClass
  public static void bootstrap() throws InstantiationException, IllegalAccessException, InterruptedException {
    Set<Module> modules = ImmutableSet.<Module>builder()
        .add(new LifecycleModule())
        .addAll(KanbanApp.class.newInstance().getModules())
        .build();
    Injector injector = Guice.createInjector(modules);
    final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
    lifecycle.start();
  }
  
  @Before
  public void setUp() {
    RestAssured.config = config().connectionConfig(new ConnectionConfig().closeIdleConnectionsAfterEachResponse());
  }
  
  @After
  public void tearDown() {
    RestAssured.reset();
  }

}
