package org.svomz.apps.kanban.application.resources;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.svomz.apps.kanban.application.KanbanApp;
import org.svomz.apps.kanban.application.models.BoardInputModel;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.commons.application.Lifecycle;
import org.svomz.commons.application.modules.LifecycleModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ConnectionConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

public class BoardResourceIntegrationTest {

  
  @BeforeClass
  public static void bootstrap() throws InstantiationException, IllegalAccessException, InterruptedException {
    Set<Module> modules = ImmutableSet.<Module>builder()
        .add(new LifecycleModule())
        .addAll(KanbanApp.class.newInstance().getModules())
        .build();
    Injector injector = Guice.createInjector(modules);
    final Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
    lifecycle.start();
  }
  
  @Before
  public void setUp() {
    RestAssured.config = config().connectionConfig(new ConnectionConfig().closeIdleConnectionsAfterEachResponse());
  }
  
  @After
  public void tearDown() {
    RestAssured.reset();
  }
  
  /**
   * As an api user
   * When I GET /boards
   * Then I have the list of boards without their corresponding stages and workitems
   */
  @Test
  public void testGetBoards() {
    BoardInputModel board1 = new BoardInputModel("Test1");
    createBoard(board1);
    BoardInputModel board2 = new BoardInputModel("Test2");
    createBoard(board2);
    
    JsonPath json = given()
      .accept(ContentType.JSON)
    .when()
      .get("/boards/")
    .then()
      .statusCode(200)
      .body(not(emptyIterable()))
      .extract().response().jsonPath();
    
    List<WorkItem> workItems = json.get("workItems");
    for (WorkItem workItem : workItems) {
      Assert.assertNull(workItem);
    }
    
    List<Stage> stages = json.get("stages");
    for (Stage stage : stages) {
      Assert.assertNull(stage);
    }
  }
  
  /**
   * As an api user
   * When I GET /boards
   * Then I have the board with its corresponding stages and workitems
   */  
  @Test
  public void testGetBoard() {
    BoardInputModel board = new BoardInputModel("Test1");
    JsonPath response = createBoard(board);
    
    int boardId = response.get("id");
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
    .when()
      .get("/boards/" + String.valueOf(boardId))
    .then()
      .statusCode(200)
      .body("id", equalTo(boardId))
      .body("name", equalTo(board.getName()))
      .body("workItems", emptyIterable())
      .body("stages", emptyIterable());
  }
  
  /**
   * As an api user
   * When I POST /boards
   * Then I receive the newly created boards without its relations to stage and workItems
   */
  @Test
  public void testPostBoard() {
    BoardInputModel board = new BoardInputModel("Test1");
    createBoard(board);
  }

  private JsonPath createBoard(BoardInputModel board) {
    return given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post("/boards")
    .then()
      .statusCode(201)
      .body("name", equalTo(board.getName()))
      .body("id", isA(Integer.class))
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
    BoardInputModel board1 = new BoardInputModel("Test1");
    int boardId = createBoard(board1).get("id");
    
    BoardInputModel updatedBoard1 = new BoardInputModel("Test2");
    
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(updatedBoard1)
    .expect()
      .statusCode(200)
    .when()
      .put("/boards/" + String.valueOf(boardId))
    .then()
      .body("name", equalTo(updatedBoard1.getName()))
      .body("id", equalTo(boardId))
      .body("workItems", nullValue())
      .body("stages", nullValue());
  }
  
  /**
   * As an api user
   * When I create a board
   * Then I delete this board
   * I expect to receive not content
   */
  @Test
  public void testDeleteBoard() {
    BoardInputModel board1 = new BoardInputModel("Test1");
    int boardId = createBoard(board1).get("id");
    
    given()
      .accept(ContentType.JSON)
    .when()
      .delete("/boards/" + String.valueOf(boardId))
    .then()
      .statusCode(204)
      .body(equalTo(StringUtils.EMPTY));
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
      .get("/boards/" + Integer.MAX_VALUE)
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
      .post("/boards")
    .then()
      .statusCode(400);
  }
  
  @Test
  public void testInvalidRequestWithEmptyName() {
    BoardInputModel board = new BoardInputModel("");
    given()
      .contentType(ContentType.JSON)
      .accept(ContentType.JSON)
      .body(board)
    .when()
      .post("/boards")
    .then()
      .statusCode(400);
  }

}
