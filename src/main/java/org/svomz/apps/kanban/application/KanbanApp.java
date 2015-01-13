package org.svomz.apps.kanban.application;

import javax.inject.Singleton;

import org.svomz.apps.kanban.domain.KanbanDomainModule;
import org.svomz.commons.application.AbstractApplication;
import org.svomz.commons.application.AppLauncher;
import org.svomz.commons.application.modules.HttpServerModule;
import org.svomz.commons.application.modules.JerseyModule;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
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
            this.filter("/*").through(PersistFilter.class);
            
            this.bind(MetricRegistry.class).in(Singleton.class);
            
            HttpServerModule.addContextListener(this.binder(), new InstrumentedFilterContextListener() {
              @Inject
              private MetricRegistry metricRegistry;
              
              @Override
              protected MetricRegistry getMetricRegistry() {
                return this.metricRegistry;
              }
            });
            
            HttpServerModule.addContextListener(this.binder(), new MetricsServlet.ContextListener() {
              @Inject
              private MetricRegistry metricRegistry;
              
              @Override
              protected MetricRegistry getMetricRegistry() {
                return this.metricRegistry;
              }
            });
            
            this.bind(InstrumentedFilter.class).in(Singleton.class);
            this.filter("/*").through(InstrumentedFilter.class);
            
            this.bind(MetricsServlet.class).in(Singleton.class);
            this.serve("/metrics").with(MetricsServlet.class);
            
            this.bind(PingServlet.class).in(Singleton.class);
            this.serve("/ping").with(PingServlet.class);
            
            this.bind(ThreadDumpServlet.class).in(Singleton.class);
            this.serve("/threads").with(ThreadDumpServlet.class);

          }
        }).build();
  }
  
  public static void main(String... args) {
    AppLauncher.launch(KanbanApp.class);
  }

}
