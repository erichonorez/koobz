package org.svomz.apps.kanban.application.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.svomz.apps.kanban.application.models.ErrorModel;
import org.svomz.apps.kanban.domain.WorkItemNotInProcessException;

@Provider
public class WorkItemNotInProcessMapper implements ExceptionMapper<WorkItemNotInProcessException> {

  @Override
  public Response toResponse(WorkItemNotInProcessException exception) {
    return Response.status(Status.BAD_REQUEST).entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }
}
