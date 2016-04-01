package org.svomz.apps.koobz.board.ports.adapters.rest.exceptions;

import org.svomz.apps.koobz.board.application.BoardNotFoundException;
import org.svomz.apps.koobz.board.infrastructure.domain.EntityNotFoundException;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.ErrorModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BoardNotFoundMapper implements ExceptionMapper<BoardNotFoundException> {

  @Override
  public Response toResponse(BoardNotFoundException exception) {
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
