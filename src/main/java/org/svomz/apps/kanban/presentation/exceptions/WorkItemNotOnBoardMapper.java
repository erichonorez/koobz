package org.svomz.apps.kanban.presentation.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.kanban.domain.exceptions.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.presentation.models.ErrorModel;

@Provider
public class WorkItemNotOnBoardMapper implements ExceptionMapper<WorkItemNotOnBoardException> {

  @Override
  public Response toResponse(WorkItemNotOnBoardException exception) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }
}
