package org.svomz.apps.kanban.application.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.kanban.application.models.ErrorModel;
import org.svomz.apps.kanban.infrastructure.domain.EntityNotFoundException;

@Provider
public class EntityNotFoundMapper implements ExceptionMapper<EntityNotFoundException> {

  @Override
  public Response toResponse(EntityNotFoundException exception) {
    return Response.status(Status.NOT_FOUND).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
