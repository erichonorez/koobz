package org.svomz.apps.koobz.board.ports.adapters.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.koobz.board.ports.adapters.rest.models.ErrorModel;
import org.svomz.apps.koobz.board.domain.model.StageNotEmptyException;

@Provider
public class StageNotEmptyMapper implements ExceptionMapper<StageNotEmptyException> {

  @Override
  public Response toResponse(StageNotEmptyException exception) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
