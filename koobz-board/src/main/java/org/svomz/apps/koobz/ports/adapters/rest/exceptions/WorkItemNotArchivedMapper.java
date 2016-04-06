package org.svomz.apps.koobz.ports.adapters.rest.exceptions;

import org.svomz.apps.koobz.domain.model.WorkItemNotArchivedException;
import org.svomz.apps.koobz.ports.adapters.rest.models.ErrorModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WorkItemNotArchivedMapper implements ExceptionMapper<WorkItemNotArchivedException> {

  @Override
  public Response toResponse(WorkItemNotArchivedException exception) {
    return Response.status(Response.Status.BAD_REQUEST)
      .entity(new ErrorModel(exception.getMessage()))
      .type(MediaType.APPLICATION_JSON).build();
  }

}
