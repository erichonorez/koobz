package org.svomz.apps.koobz.ports.adapters.rest.exceptions;

import org.svomz.apps.koobz.application.WorkflowNotFoundException;
import org.svomz.apps.koobz.ports.adapters.rest.models.ErrorModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WorkflowNotFoundMapper implements ExceptionMapper<WorkflowNotFoundException> {

  @Override
  public Response toResponse(WorkflowNotFoundException exception) {
    return Response.status(Response.Status.NOT_FOUND)
        .entity(new ErrorModel(exception.getMessage()))
        .type(MediaType.APPLICATION_JSON).build();
  }

}
