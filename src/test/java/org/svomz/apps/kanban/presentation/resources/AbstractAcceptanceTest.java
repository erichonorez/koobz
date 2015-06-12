package org.svomz.apps.kanban.presentation.resources;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;

import org.junit.After;
import org.junit.Before;
import org.svomz.apps.kanban.presentation.models.BoardInputModel;
import org.svomz.apps.kanban.presentation.models.StageInputModel;
import org.svomz.apps.kanban.presentation.models.WorkItemInputModel;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ConnectionConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class AbstractAcceptanceTest {
  
  @Before
  public void setUp() {
    RestAssured.config = config().connectionConfig(new ConnectionConfig().closeIdleConnectionsAfterEachResponse());
  }
  
  @After
  public void tearDown() {
    RestAssured.reset();
  }

  protected JsonPath createStage(int boardId, final String stageName) {
    StageInputModel stage = new StageInputModel(stageName);
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(stage)
    .when()
      .post("/boards/" + boardId + "/stages")
    .then()
      .statusCode(201)
      .body("id", isA(Integer.class))
      .body("name", equalTo(stage.getName()))
    .extract().response().jsonPath();
  }

  protected JsonPath createBoard(final String name) {
    BoardInputModel board = new BoardInputModel(name);
    JsonPath boardJsonPath = given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post("/boards")
    .then().extract().response().jsonPath();
    return boardJsonPath;
  }

  protected JsonPath createWorkItem(final int boardId, final int stageId, final String workItemText) {
    WorkItemInputModel workItem = new WorkItemInputModel(workItemText, stageId, 0);
    
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workItem)
    .when()
      .post("/boards/" + boardId + "/workitems")
    .then()
      .statusCode(201)
      .body("id", isA(Integer.class))
      .body("text", equalTo(workItemText))
      .body("stageId", isA(Integer.class))
    .extract().response().jsonPath();
  }

}
