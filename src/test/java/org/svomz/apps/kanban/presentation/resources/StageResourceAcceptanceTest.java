package org.svomz.apps.kanban.presentation.resources;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.svomz.apps.kanban.presentation.models.StageInputModel;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class StageResourceAcceptanceTest extends AbstractAcceptanceTest {
  
  /**
   * As an api user
   * Given a board exits
   * When 1 add a new stage to this board
   * Then I receive 201
   */
  @Test
  public void testCreateStage() {
    String boardName = "Test1";
    JsonPath boardJsonPath = createBoard(boardName);
    
    int boardId = boardJsonPath.getInt("id");
    createStage(boardId, "To do");
  }
  
  /**
   * As an api user
   * Given a board exists with some stages
   * When I list all boards
   * Then I receive them all
   */
  @Test
  public void testGetAllStages() {
    int boardId = this.createBoard("Test 1").getInt("id");
    this.createStage(boardId, "Stage 1");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .get("/boards/" + boardId + "/stages")
    .then()
      .statusCode(200)
      .body(not(emptyIterable()));
  }
  
  /**
   * As an api user
   * Given a board exists with a stage
   * When I update this stage
   * Then I receive the updated stage
   */
  @Test
  public void testUpdateStage() {
    int boardId = this.createBoard("Test 1").getInt("id");
    JsonPath json = this.createStage(boardId, "Stage 1");
    String stageNewName = "New name for stage 1";
    StageInputModel updateRequest = new StageInputModel(stageNewName);
    
    int stageId = json.getInt("id");
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(updateRequest)
    .when()
      .put("/boards/" + boardId + "/stages/" + stageId)
    .then()
      .statusCode(200)
      .body("id", equalTo(stageId))
      .body("name", equalTo(stageNewName));
  }
  
  /**
   * As an api user
   * Given a stage exists without workitems
   * When I delete it
   * Then I receive 204
   */
  @Test
  public void testDeleteStageWithoutWorkItems() {
    int boardId = this.createBoard("Test 1").getInt("id");
    JsonPath json = this.createStage(boardId, "Stage 1");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .delete("/boards/" + boardId + "/stages/" + json.getInt("id"))
    .then()
      .statusCode(204);
  }
  
  /**
   * As an api user
   * Given a stage exists with workitems
   * When I delete it
   * Then I receive 400
   */
  @Test
  public void testDeleteStageWithWorkItems() {
    int boardId = this.createBoard("Test 1").getInt("id");
    JsonPath stageJson = this.createStage(boardId, "Stage 1");
    int stageId = stageJson.getInt("id");
    this.createWorkItem(boardId, stageId, "WorkItem 1");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .delete("/boards/" + boardId + "/stages/" + stageId)
    .then()
      .statusCode(400);
  }
  
  /**
   * As an api user
   * Given a board exists
   * When I create a new stage with invalid parameters
   * Then I get a 400
   * Then the stage is not created
   */
  @Test
  public void testCreateInvalidStage() {
    int boardId = this.createBoard("Test").getInt("id");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(StringUtils.EMPTY)
    .when()
      .post("/boards/" + boardId + "/stages")
    .then()
      .statusCode(400);
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new StageInputModel(StringUtils.EMPTY))
    .when()
      .post("/boards/" + boardId + "/stages")
    .then()
      .statusCode(400);
    
    String stageName = StringUtils.right("", 256);
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new StageInputModel(stageName))
    .when()
      .post("/boards/" + boardId + "/stages")
    .then()
      .statusCode(400);
  }
  
  /**
   * As an api user
   * Given a board with a stage exists
   * When I update this stage with invalid parameters
   * Then I receive a 400
   * Then the stage is not updated
   */
  @Test
  public void testUpdateInvalidStage() {
    int boardId = this.createBoard("Test").getInt("id");
    int stageId = this.createStage(boardId, "Test").getInt("id");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(StringUtils.EMPTY)
    .when()
      .put("/boards/" + boardId + "/stages/" + stageId)
    .then()
      .statusCode(400);
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new StageInputModel(StringUtils.EMPTY))
    .when()
      .put("/boards/" + boardId + "/stages/" + stageId)
    .then()
      .statusCode(400);
    
    String stageName = StringUtils.right("", 256);
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(new StageInputModel(stageName))
    .when()
      .put("/boards/" + boardId + "/stages/" + stageId)
    .then()
      .statusCode(400);
  }

}
