package org.svomz.apps.koobz.ports.adapters.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.svomz.apps.koobz.ports.adapters.rest.models.WorkflowInputModel;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class WorkflowResourceAcceptanceTest extends AbstractAcceptanceTest {

  @Test
  public void shouldSuccessfullyCreateNewBoard() {
    WorkflowInputModel workflow = new WorkflowInputModel("Test1");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workflow)
    .when()
      .post(this.workflowsUrl());
  }
  
  /**
   * As an api user
   * When I GET /boards
   * Then I have the board with its corresponding stages and workitems
   */  
  @Test
  public void testGetBoard() {
    WorkflowInputModel workflowInputModel = new WorkflowInputModel("Test1");
    JsonPath response = this.createWorkflow(workflowInputModel);
    
    String boardId = response.get("id");
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .get("/workflows/" + boardId)
    .then()
      .statusCode(200)
      .body("id", equalTo(boardId))
      .body("name", equalTo(workflowInputModel.getName()))
      .body("stages", emptyIterable());
  }
  
  /**
   * As an api user
   * When I POST /boards
   * Then I receive the newly created boards without its relations to stage and workItems
   */
  @Test
  public void testPostBoard() {
    WorkflowInputModel board = new WorkflowInputModel("Test1");
    this.createWorkflow(board);
  }

  private JsonPath createWorkflow(WorkflowInputModel board) {
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post(this.workflowsUrl())
    .then()
      .statusCode(201)
      .body("name", equalTo(board.getName()))
      .body("id", isA(String.class))
      .body("workItems", nullValue())
      .body("stages", nullValue())
    .extract().jsonPath();
  }
  
  /**
   * As an api user
   * When I put /boards/{id}
   * Then I receive the updated board without its relations to stage and workItems
   */
  @Test
  public void testPutBoard() {
    WorkflowInputModel board1 = new WorkflowInputModel("Test1");
    String boardId = this.createWorkflow(board1).get("id");
    
    WorkflowInputModel updatedBoard1 = new WorkflowInputModel("Test2");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(updatedBoard1)
    .expect()
      .statusCode(200)
    .when()
      .put("/workflows/" + String.valueOf(boardId));
  }
  
  /**
   * As an api user
   * When I GET a non existing board
   * Then I receive a 404
   */
  @Test
  public void testGetNonExistingBoard() {
    given()
      .accept(ContentType.JSON)
    .when()
      .get("/workflows/" + Integer.MAX_VALUE)
    .then()
      .statusCode(404)
      .body("message", not(equalTo(StringUtils.EMPTY)));
  }
  
  /**
   * As an api user
   * When I want to create an invalid board
   * Then I receice a 400
   */
  @Test
  public void testInvalidRequestWithEmptyBody() {
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(StringUtils.EMPTY)
    .when()
      .post(this.workflowsUrl())
    .then()
      .statusCode(400);
  }
  
  @Test
  public void testInvalidRequestWithEmptyName() {
    WorkflowInputModel board = new WorkflowInputModel("");
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post(this.workflowsUrl())
    .then()
      .statusCode(400);
  }

  private String workflowsUrl() {
    return "/workflows";
  }

}
