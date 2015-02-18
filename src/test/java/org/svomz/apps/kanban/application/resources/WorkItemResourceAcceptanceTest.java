package org.svomz.apps.kanban.application.resources;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.svomz.apps.kanban.application.models.WorkItemInputModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class WorkItemResourceAcceptanceTest extends AbstractAcceptanceTest {

  /**
   * As an api user
   * Given a board with workitems exists
   * When I get boards/{boardId}/workitems
   * Then I have the list of work items
   * @throws IOException 
   * @throws JsonMappingException 
   * @throws JsonParseException 
   */
  @Test
  public void testGetAllWorkItemsOfABoard() throws JsonParseException, JsonMappingException, IOException {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    String firstWorkItemText = "My first work item";
    this.createWorkItem(boardId, stageId, firstWorkItemText);
    String secondWorkItemText = "My second work item";
    this.createWorkItem(boardId, stageId, secondWorkItemText);  
    
    JsonPath json = given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .get("/boards/" + boardId + "/workitems")
    .then()
      .body(not(empty()))
    .extract()
    .response().jsonPath();
    
    List<String> texts = json.get("text");
    Assert.assertThat(texts, allOf(
        not(emptyIterable()),
        containsInAnyOrder(
            equalTo("My first work item"), equalTo("My second work item")
        )
    ));
    
  }
  
  /**
   * As an api user
   * Given the board with id == Integer.MAX_VALUE doesn't exist
   * When I get the related work items 
   * Then I get a 404
   */
  @Test
  public void testGetWorkItemsOfNonExistingBoard() {
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .get("/boards/" + Integer.MAX_VALUE + "/workitems")
    .then()
      .statusCode(404);
  }
  
  /**
   * As an api user
   * Given a board exists
   * When I post a new work item
   * Then I receive the newly created work item in response
   */
  @Test
  public void testCreateWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    this.createWorkItem(boardId, stageId, "My first work item");
  }
  
  /**
   * As an api user
   * Given a board exists
   * When I put a existing work item
   * Then I receive the updated work item in response
   */
  @Test
  public void testUpdateWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    String text = "My first work item";
    int workItemId = this.createWorkItem(boardId, stageId, text).getInt("id");
    
    String editedText = "My first edited work item";
    WorkItemInputModel updateRequest = new WorkItemInputModel(editedText, stageId);
    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(updateRequest)
      .when()
        .put("/boards/" + boardId + "/workitems/" + workItemId)
      .then()
        .statusCode(200)
        .body("id", allOf(isA(Integer.class), equalTo(workItemId)))
        .body("text", equalTo(editedText))
        .body("stageId", allOf(isA(Integer.class), equalTo(stageId)))
      .extract().response().jsonPath();
  }
  
  /**
   * As an api user
   * Given a workitem exists
   * When I delete it
   * I get a 204 in response
   */
  @Test
  public void testDeleteWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    String text = "My first work item";
    int workItemId = this.createWorkItem(boardId, stageId, text).getInt("id");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .delete("/boards/" + boardId + "/workitems/" + workItemId)
    .then()
      .statusCode(204)
      .body(equalTo(StringUtils.EMPTY));
  }
  
  /**
   * As an api user
   * Given a board with two stage exists
   * When I update it to change its stage
   * Then I receive the updated work item with the correct stage
   */
  @Test
  public void testUpdateStageOfAWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    String text = "My first work item";
    int workItemId = this.createWorkItem(boardId, stageId, text).getInt("id");
    JsonPath jsonStageWIP = this.createStage(boardId, "Work in progress");
    
    int wipStageId = jsonStageWIP.getInt("id");
    WorkItemInputModel updateRequest = new WorkItemInputModel("My first work item", wipStageId);
    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(updateRequest)
      .when()
        .put("/boards/" + boardId + "/workitems/" + workItemId)
      .then()
        .statusCode(200)
        .body("id", allOf(isA(Integer.class), equalTo(workItemId)))
        .body("text", equalTo(text))
        .body("stageId", allOf(isA(Integer.class), equalTo(wipStageId)))
      .extract().response().jsonPath();
  }
  
  /**
   * As an api user
   * Given 2 boards with each two stage and one with a workitem
   * When I want to move the workitem from board A stage 1 to board B stage 1
   * The I receive a 400 
   * Then the work item is not moved
   */
  @Test
  public void testInvalidMoveOfAWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonBoard2 = this.createBoard("Test board 2");
    int boardId2 = jsonBoard2.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    JsonPath jsonStageBoard2 = this.createStage(boardId2, "Test stage in board 2");
    int stageId = jsonStage.getInt("id");
    String text = "My first work item";
    int workItemId = this.createWorkItem(boardId, stageId, text).getInt("id");

    WorkItemInputModel updateRequest = new WorkItemInputModel("My first work item", jsonStageBoard2.getInt("id"));
    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(updateRequest)
      .when()
        .put("/boards/" + boardId + "/workitems/" + workItemId)
      .then()
        .statusCode(404);
  }
  
  /**
   * As an api user
   * Given a board with a stage exists
   * When I create a invalid work item
   * Then I receive a 400
   * Then the work item is not created
   */
  @Test
  public void testCreateInvalidWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    int boardId = jsonBoard.getInt("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    int stageId = jsonStage.getInt("id");
    WorkItemInputModel workItem = new WorkItemInputModel(StringUtils.EMPTY, stageId);
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workItem)
    .when()
      .post("/boards/" + boardId + "/workitems")
    .then()
      .statusCode(400);
  }
  
}
