package org.svomz.apps.kanban.applications.resources;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.isA;

import org.junit.After;
import org.junit.Before;
import org.svomz.apps.kanban.application.models.BoardInputModel;
import org.svomz.apps.kanban.application.models.StageInputModel;
import org.svomz.apps.kanban.application.models.WorkItemInputModel;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ConnectionConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class AbstractAcceptanceTest {

  @Before
  public void setUp() {
    RestAssured.config = config().connectionConfig(new ConnectionConfig());

    String targetUri = System.getProperty("restassured.baseURI");
    RestAssured.baseURI = targetUri != null ? targetUri : "http://localhost";

    String port = System.getProperty("restassured.port");
    RestAssured.port = port != null ? Integer.valueOf(port) : 8080;
  }
  
  @After
  public void tearDown() {
    RestAssured.reset();
  }

  protected JsonPath createStage(String boardId, final String stageName) {
    StageInputModel stage = new StageInputModel(stageName);
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(stage)
    .when()
      .post("/boards/" + boardId + "/stages")
    .then()
      .statusCode(201)
      .body("id", isA(String.class))
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

  protected JsonPath createWorkItem(final String boardId, final String stageId,
    final String workItemTitle, String workItemDescription) {
    WorkItemInputModel workItem = new WorkItemInputModel(workItemTitle, stageId, 0, workItemDescription);
    
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workItem)
    .when()
      .post("/boards/" + boardId + "/workitems")
    .then()
      .statusCode(201)
      .body("id", isA(String.class))
      .body("title", equalTo(workItemTitle))
      .body("description", equalTo(workItemDescription))
    .extract().response().jsonPath();
  }

}
