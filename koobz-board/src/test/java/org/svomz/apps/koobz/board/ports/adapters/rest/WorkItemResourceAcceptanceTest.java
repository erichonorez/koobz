package org.svomz.apps.koobz.board.ports.adapters.rest;

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
import org.junit.Ignore;
import org.junit.Test;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemMoveInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemArchivingInputModel;
import org.svomz.apps.koobz.board.ports.adapters.rest.models.WorkItemPositionInputModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class WorkItemResourceAcceptanceTest extends AbstractAcceptanceTest {
  
  /**
   * As an api user
   * Given a board exists
   * When I post a new work item
   * Then I receive the newly created work item in response
   */
  @Test
  public void testCreateWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    String stageId = jsonStage.get("id");
    this.createWorkItem(boardId, stageId, "My first work item", "a description");
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
    String boardId = jsonBoard.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    String stageId = jsonStage.get("id");
    String text = "My first work item";
    String workItemId = this.createWorkItem(boardId, stageId, text, StringUtils.EMPTY).get("id");
    
    String editedText = "My first edited work item";
    String editedDescription = "a description";
    WorkItemInputModel updateRequest = new WorkItemInputModel(editedText, stageId, 0, editedDescription);
    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(updateRequest)
      .when()
        .put("/boards/" + boardId + "/workitems/" + workItemId)
      .then()
        .statusCode(200);
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
    String boardId = jsonBoard.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    String stageId = jsonStage.get("id");
    String text = "My first work item";
    String workItemId = this.createWorkItem(boardId, stageId, text, StringUtils.EMPTY).get("id");
    
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
    String boardId = jsonBoard.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    String stageId = jsonStage.get("id");
    String text = "My first work item";
    String workItemId = this.createWorkItem(boardId, stageId, text, StringUtils.EMPTY).get("id");
    JsonPath jsonStageWIP = this.createStage(boardId, "Work in progress");
    
    String wipStageId = jsonStageWIP.get("id");

    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(new WorkItemMoveInputModel(wipStageId))
      .when()
        .post("/boards/" + boardId + "/workitems/" + workItemId + "/move")
      .then()
        .statusCode(200);
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
    String boardId = jsonBoard.get("id");
    JsonPath jsonBoard2 = this.createBoard("Test board 2");
    String boardId2 = jsonBoard2.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    JsonPath jsonStageBoard2 = this.createStage(boardId2, "Test stage in board 2");
    String stageId = jsonStage.get("id");
    String text = "My first work item";
    String workItemId = this.createWorkItem(boardId, stageId, text, StringUtils.EMPTY).get("id");

    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .body(new WorkItemMoveInputModel(jsonStageBoard2.get("id")))
      .when()
        .post("/boards/" + boardId + "/workitems/" + workItemId + "/move")
      .then()
        .statusCode(400);
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
    String boardId = jsonBoard.get("id");
    JsonPath jsonStage = this.createStage(boardId, "Test stage");
    String stageId = jsonStage.get("id");
    WorkItemInputModel workItem = new WorkItemInputModel(StringUtils.EMPTY, stageId, null, null);
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workItem)
    .when()
      .post("/boards/" + boardId + "/workitems")
    .then()
      .statusCode(400);
  }

  @Test
  public void itShouldMoveWorkItemFromAStageToAnother() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonTodoStage = this.createStage(boardId, "To do");
    String stageId = jsonTodoStage.get("id");
    JsonPath jsonDoneStage = this.createStage(boardId, "Done");

    JsonPath jsonWorkItem =
      this.createWorkItem(boardId, stageId, "My first work item", "a description");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemMoveInputModel(jsonDoneStage.get("id")))
    .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/move")
    .then()
      .statusCode(200);
  }

  @Test
  public void itShouldChangeThePositionOfWorkItemsInAStage() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonTodoStage = this.createStage(boardId, "To do");
    String stageId = jsonTodoStage.get("id");

    JsonPath jsonWorkItem =
      this.createWorkItem(boardId, stageId, "My first work item", "a description");
    this.createWorkItem(boardId, stageId, "My second work item", "a description");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemPositionInputModel(2))
    .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/position")
    .then()
      .statusCode(200);
  }

  @Ignore
  @Test
  public void itShouldArchiveAWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonTodoStage = this.createStage(boardId, "To do");
    String stageId = jsonTodoStage.get("id");

    JsonPath jsonWorkItem =
      this.createWorkItem(boardId, stageId, "My first work item", "a description");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemArchivingInputModel(true))
      .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/archiving")
      .then()
      .statusCode(200);
  }

  @Ignore
  @Test
  public void itShouldFailIfSendBackToBoardOnANotArchivedWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonTodoStage = this.createStage(boardId, "To do");
    String stageId = jsonTodoStage.get("id");

    JsonPath jsonWorkItem =
      this.createWorkItem(boardId, stageId, "My first work item", "a description");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemArchivingInputModel(false))
      .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/archiving")
      .then()
      .statusCode(400);
  }

  @Ignore
  @Test
  public void itShouldSendBackToBoardArchivedWorkItem() {
    JsonPath jsonBoard = this.createBoard("Test board");
    String boardId = jsonBoard.get("id");
    JsonPath jsonTodoStage = this.createStage(boardId, "To do");
    String stageId = jsonTodoStage.get("id");

    JsonPath jsonWorkItem =
      this.createWorkItem(boardId, stageId, "My first work item", "a description");

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemArchivingInputModel(true))
      .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/archiving")
      .then()
      .statusCode(200);

    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new WorkItemArchivingInputModel(false))
    .when()
      .post("/boards/" + boardId + "/workitems/" + jsonWorkItem.get("id") + "/archiving")
    .then()
      .statusCode(200);
  }

}
