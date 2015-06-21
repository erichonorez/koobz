package org.svomz.apps.kanban.domain.entities;

import org.junit.Assert;
import org.junit.Test;
import org.svomz.apps.kanban.domain.Board;
import org.svomz.apps.kanban.domain.Stage;
import org.svomz.apps.kanban.domain.StageNotEmptyException;
import org.svomz.apps.kanban.domain.StageNotInProcessException;
import org.svomz.apps.kanban.domain.WorkItem;
import org.svomz.apps.kanban.domain.WorkItemNotInStageException;
import org.svomz.apps.kanban.domain.WorkItemNotOnBoardException;

public class BoardUnitTest {

  @Test
  public void testBoardCreation() {
    Board board = new Board("todo");

    Assert.assertEquals("todo", board.getName());
    Assert.assertTrue(board.getWorkItems().isEmpty());
    Assert.assertTrue(board.getStages().isEmpty());
  }

  @Test
  public void testAddColumns() {
    Board board = new Board("todo");

    Stage column = new Stage("work in progress");
    board.addStage(column);
    Assert.assertEquals(1, board.getStages().size());
  }

  @Test
  public void testRemoveStage() throws StageNotInProcessException, StageNotEmptyException {
    Board board = new Board("todo");

    Stage column = new Stage("work in progress");
    board.addStage(column);
    board.removeStage(column);
    Assert.assertTrue(board.getStages().isEmpty());
  }

  @Test(expected = StageNotInProcessException.class)
  public void testRemoveStageFailWithStageNotInProcessException()
      throws StageNotInProcessException, StageNotEmptyException {
    Board board = new Board("todo");

    Stage column = new Stage("work in progress");
    board.removeStage(column);
    
    Assert.assertTrue(board.getStages().isEmpty());
  }

  @Test(expected = StageNotEmptyException.class)
  public void testRemoveStageFailWithStageNotEmptyException() throws StageNotInProcessException,
      StageNotEmptyException {
    Board board = new Board("todo");

    Stage column = new Stage("work in progress");
    board.addStage(column);
    WorkItem workItem = new WorkItem("Make coffee");
    board.addWorkItem(workItem, column);
    board.removeStage(column);
    Assert.assertTrue(board.getStages().isEmpty());
  }

  @Test
  public void testAddPostIt() throws StageNotInProcessException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage column = new Stage("work in progress");
    board.addStage(column);

    Assert.assertTrue(board.getWorkItems().isEmpty());
    board.addWorkItem(postIt, column);
    Assert.assertEquals(1, board.getWorkItems().size());

  }

  @Test(expected = StageNotInProcessException.class)
  public void testAddPostItFail() throws StageNotInProcessException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage column = new Stage("work in progress");

    board.addWorkItem(postIt, column);
  }

  @Test
  public void testRemovePostIt() throws StageNotInProcessException, WorkItemNotOnBoardException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage column = new Stage("work in progress");
    board.addStage(column);

    board.addWorkItem(postIt, column);
    board.removeWorkItem(postIt);
    Assert.assertTrue(board.getWorkItems().isEmpty());
  }

  @Test(expected = WorkItemNotOnBoardException.class)
  public void testRemovePostItWithPostItNotOnBoardException() throws WorkItemNotOnBoardException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    board.removeWorkItem(postIt);
  }

  @Test
  public void testMovePostIt() throws StageNotInProcessException, WorkItemNotOnBoardException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage columnWIP = new Stage("work in progress");
    board.addStage(columnWIP);
    Stage columnDone = new Stage("done");
    board.addStage(columnDone);
    board.addWorkItem(postIt, columnWIP);
    Assert.assertEquals(1, board.getWorkItems().size());
    Assert.assertEquals(columnWIP, postIt.getStage());

    board.moveWorkItem(postIt, columnDone);
    Assert.assertEquals(1, board.getWorkItems().size());
    Assert.assertEquals(columnDone, postIt.getStage());
  }

  @Test(expected = WorkItemNotOnBoardException.class)
  public void testMovePostItFailWithPostItNotFoundException() throws WorkItemNotOnBoardException,
      StageNotInProcessException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage columnWIP = new Stage("work in progress");
    board.addStage(columnWIP);
    Stage columnDone = new Stage("done");
    board.addStage(columnDone);

    Assert.assertTrue(board.getWorkItems().isEmpty());
    board.moveWorkItem(postIt, columnDone);
    Assert.assertEquals(1, board.getWorkItems());
    Assert.assertEquals(columnDone, postIt.getStage());
  }
  
  @Test(expected = StageNotInProcessException.class)
  public void testMovePostItFailWithStageNotInProcessException() throws WorkItemNotOnBoardException,
      StageNotInProcessException {
    Board board = new Board("todo");

    WorkItem postIt = new WorkItem("My first task");
    Stage columnWIP = new Stage("work in progress");
    board.addStage(columnWIP);
    board.addWorkItem(postIt, columnWIP);
    Stage columnDone = new Stage("done");

    board.moveWorkItem(postIt, columnDone);
    Assert.assertEquals(1, board.getWorkItems());
    Assert.assertEquals(columnDone, postIt.getStage());
  }
  
  @Test
  public void addWorkItem_NewWorkItemIsTheLast() throws StageNotInProcessException {
    Board board = new Board("Project board");
    Stage todoStage = new Stage("Todo");
    board.addStage(todoStage);
    
    WorkItem workItemA = new WorkItem("Work item A");
    board.addWorkItem(workItemA, todoStage);
    Assert.assertEquals(0, workItemA.getOrder());
    
    WorkItem workItemB = new WorkItem("Work item B");
    board.addWorkItem(workItemB, todoStage);
    Assert.assertEquals(1, workItemB.getOrder());
  }
  
  @Test
  public void reorderWorkItem_WithExistingWorkItems() throws StageNotInProcessException, WorkItemNotOnBoardException, WorkItemNotInStageException {
    Board board = new Board("Project board");
    Stage todoStage = new Stage("Todo");
    board.addStage(todoStage);
    
    WorkItem workItemA = new WorkItem("Work item A");
    board.addWorkItem(workItemA, todoStage);
    
    WorkItem workItemB = new WorkItem("Work item B");
    board.addWorkItem(workItemB, todoStage);
    
    WorkItem workItemC = new WorkItem("Work item C");
    board.addWorkItem(workItemC, todoStage);
    
    WorkItem workItemD = new WorkItem("Work item D");
    board.addWorkItem(workItemD, todoStage);
    
    board.reoderWorkItem(workItemC, 0);
    Assert.assertEquals(0, workItemC.getOrder());
    Assert.assertEquals(1, workItemA.getOrder());
    Assert.assertEquals(2, workItemB.getOrder());
    Assert.assertEquals(3, workItemD.getOrder());
    
    board.reoderWorkItem(workItemA, 3);
    Assert.assertEquals(0, workItemC.getOrder());
    Assert.assertEquals(3, workItemA.getOrder());
    Assert.assertEquals(1, workItemB.getOrder());
    Assert.assertEquals(2, workItemD.getOrder());
    
    board.reoderWorkItem(workItemB, 2);
    Assert.assertEquals(0, workItemC.getOrder());
    Assert.assertEquals(3, workItemA.getOrder());
    Assert.assertEquals(2, workItemB.getOrder());
    Assert.assertEquals(1, workItemD.getOrder());
    
    board.reoderWorkItem(workItemB, 2);
    Assert.assertEquals(0, workItemC.getOrder());
    Assert.assertEquals(3, workItemA.getOrder());
    Assert.assertEquals(2, workItemB.getOrder());
    Assert.assertEquals(1, workItemD.getOrder());
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void reorderWorkItem_WithNegativePositionThrowsIllegalArgumentException() throws StageNotInProcessException, WorkItemNotOnBoardException, WorkItemNotInStageException {
    Board board = new Board("Project board");
    Stage todoStage = new Stage("Todo");
    board.addStage(todoStage);
    
    WorkItem workItemA = new WorkItem("Work item A");
    board.addWorkItem(workItemA, todoStage);
    
    WorkItem workItemB = new WorkItem("Work item B");
    board.addWorkItem(workItemB, todoStage);
    
    board.reoderWorkItem(workItemA, -1);
  }
  
  @Test
  public void reorderWorkItem_WithBigPositionPutTheWotkItemAtTheEnd() throws StageNotInProcessException, WorkItemNotOnBoardException, WorkItemNotInStageException {
    Board board = new Board("Project board");
    Stage todoStage = new Stage("Todo");
    board.addStage(todoStage);
    
    WorkItem workItemA = new WorkItem("Work item A");
    board.addWorkItem(workItemA, todoStage);
    
    WorkItem workItemB = new WorkItem("Work item B");
    board.addWorkItem(workItemB, todoStage);
    
    board.reoderWorkItem(workItemA, Integer.MAX_VALUE);
    Assert.assertEquals(0, workItemB.getOrder());
    Assert.assertEquals(1, workItemA.getOrder());
  }

}
