package org.svomz.apps.koobz.board.ports.adapters.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.koobz.board.ports.adapters.rest.models.ErrorModel;
import org.svomz.apps.koobz.board.domain.model.StageNotInProcessException;

@Provider
public class StageNotInProcessMapper implements ExceptionMapper<StageNotInProcessException> {

  @Override
  public Response toResponse(StageNotInProcessException exception) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
