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
      .post(this.baseUrl() + "/boards/" + boardId + "/stages")
    .then()
      .statusCode(201)
      .body("id", allOf(isA(Integer.class), is(greaterThan(0))))
      .body("name", equalTo(stage.getName()))
    .extract().response().jsonPath();
  }

  protected String baseUrl() {
    return "http://localhost:8080";
  }

  protected JsonPath createBoard(final String name) {
    BoardInputModel board = new BoardInputModel(name);
    JsonPath boardJsonPath = given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post(this.baseUrl() + "/boards")
    .then().extract().response().jsonPath();
    return boardJsonPath;
  }

  protected JsonPath createWorkItem(final int boardId, final int stageId, final String workItemTitle) {
    WorkItemInputModel workItem = new WorkItemInputModel(workItemTitle, stageId, 0);
    
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(workItem)
    .when()
      .post(this.baseUrl() + "/boards/" + boardId + "/workitems")
    .then()
      .statusCode(201)
      .body("id", isA(Integer.class))
      .body("title", equalTo(workItemTitle))
    .extract().response().jsonPath();
  }

}
