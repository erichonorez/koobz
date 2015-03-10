package org.svomz.apps.kanban.presentation.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.kanban.domain.exceptions.StageNotEmptyException;
import org.svomz.apps.kanban.presentation.models.ErrorModel;

@Provider
public class StageNotEmptyMapper implements ExceptionMapper<StageNotEmptyException> {

  @Override
  public Response toResponse(StageNotEmptyException exception) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
