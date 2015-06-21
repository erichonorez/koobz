package org.svomz.apps.kanban.infrastructure.application;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonProducer implements ContextResolver<ObjectMapper> {

  private final ObjectMapper mapper;

  public JacksonProducer() {
      this.mapper = new ObjectMapper();
      this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
      return this.mapper;
  }

}