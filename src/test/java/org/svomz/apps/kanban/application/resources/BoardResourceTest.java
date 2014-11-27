package org.svomz.apps.kanban.application.resources;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.Assert;

public class BoardResourceTest extends JerseyTest {

  @Override
  protected Application configure() {
      return new ResourceConfig(BoardResource.class);
  }

  @Test
  public void getBoards() {
      Response response = target("boards").request().get();
      Assert.assertEquals(response.getMediaType().toString(), MediaType.APPLICATION_JSON);
  }
  
}
