package org.svomz.apps.koobz.board.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Iterator;
import java.util.Set;

import static org.svomz.apps.koobz.board.domain.model.BoardMaker.*;
import static org.svomz.apps.koobz.board.domain.model.StageMaker.*;
import static org.svomz.apps.koobz.board.domain.model.WorkItemMaker.*;
import static com.natpryce.makeiteasy.MakeItEasy.*;

import static org.assertj.core.api.Assertions.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardUnitTest.ArchivingFeatures.class,
  BoardUnitTest.DefaultFeatures.class
})
public class BoardUnitTest {

  public static class ArchivingFeatures {

    @Test
    public void itShouldReturnsArchivedWorkItems() throws WorkItemNotInProcessException {
      Board board = make(a(Board,
        with(name, "Hello"),
        with(stages, listOf(
          a(Stage,
            with(stageName, "TODO"),
            with(workItems, listOf(
              a(WorkItem, with(workItemName, "Un")),
              a(WorkItem, with(workItemName, "Deux"))
            ))),
          a(Stage, with(stageName, "WIP")),
          a(Stage, with(stageName, "DONE"))
        ))
      ));

      Iterator<WorkItem> workItemsOnTheBoard = board.getWorkItems().iterator();
      WorkItem workItem = workItemsOnTheBoard.next();
      board.archive(workItem);

      Set<WorkItem> workItemsReferencedByTheTodoStage
        = board.getStages()
        .stream()
        .filter(stage -> "TODO".equals(stage.getName()))
        .findFirst()
        .get()
        .getWorkItems();

      assertThat(workItemsReferencedByTheTodoStage)
        .hasSize(1)
        .doesNotContain(workItem);

      assertThat(board.getWorkItems())
        .hasSize(1)
        .doesNotContain(workItem);
    }

    @Test
    public void itShouldUnarchiveWorkItemsInTheStageTheyWereBeforeArchving()
      throws WorkItemNotInProcessException {
      Board board = make(a(Board,
        with(name, "Hello"),
        with(stages, listOf(
          a(Stage,
            with(stageName, "TODO"),
            with(workItems, listOf(
              a(WorkItem, with(workItemName, "Deux")),
              a(WorkItem, with(workItemName, "Un"))
            ))),
          a(Stage, with(stageName, "WIP")),
          a(Stage, with(stageName, "DONE"))
        ))
      ));

      WorkItem workItem = board.getWorkItems().iterator().next();
      board.archive(workItem);
      board.unarchive(workItem.getId());

      Set<WorkItem> workItemsReferencedByTheTodoStage
        = board.getStages()
        .stream()
        .filter(stage -> "TODO".equals(stage.getName()))
        .findFirst()
        .get()
        .getWorkItems();

      assertThat(workItemsReferencedByTheTodoStage)
        .hasSize(2)
        .contains(workItem);

      assertThat(board.getWorkItems())
        .hasSize(2)
        .contains(workItem);
    }

    @Test
    public void itShouldConsiderArchivedWorkItemsWhenReordering()
      throws WorkItemNotInProcessException, WorkItemNotInStageException {
      Board board = make(a(Board,
        with(name, "Hello"),
        with(stages, listOf(
          a(Stage,
            with(stageName, "TODO"),
            with(workItems, listOf(
              a(WorkItem, with(workItemName, "One")),
              a(WorkItem, with(workItemName, "Two")),
              a(WorkItem, with(workItemName, "Tree"))
            ))),
          a(Stage, with(stageName, "WIP")),
          a(Stage, with(stageName, "DONE"))
        ))
      ));

      //Archive the work item with the title "One"
      WorkItem one = board.getWorkItems()
        .stream()
        .filter(workItem -> "One".equals(workItem.getTitle()))
        .findFirst()
        .get();

      board.archive(one);

      //Move tree to the first position
      WorkItem tree = board.getWorkItems()
        .stream()
        .filter(workItem -> "Tree".equals(workItem.getTitle()))
        .findFirst()
        .get();

      board.putWorkItemAtPosition(tree, 0);

      //When unarchiving "One" it should be in second position
      board.unarchive(one.getId());

      assertThat(tree.getOrder())
        .isEqualTo(0);

      assertThat(one.getOrder())
        .isEqualTo(1);

      WorkItem two = board.getWorkItems()
        .stream()
        .filter(workItem -> "Two".equals(workItem.getTitle()))
        .findFirst()
        .get();

      assertThat(two.getOrder())
        .isEqualTo(2);
    }

  }

  public static class DefaultFeatures {

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
    public void testRemovePostIt()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem("My first task");
      Stage column = new Stage("work in progress");
      board.addStage(column);

      board.addWorkItem(postIt, column);
      board.removeWorkItem(postIt);
      Assert.assertTrue(board.getWorkItems().isEmpty());
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void testRemovePostItWithPostItNotOnBoardException()
      throws WorkItemNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem("My first task");
      board.removeWorkItem(postIt);
    }

    @Test
    public void testMovePostIt() throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem("My first task");
      Stage columnWIP = new Stage("work in progress");
      board.addStage(columnWIP);
      Stage columnDone = new Stage("done");
      board.addStage(columnDone);
      board.addWorkItem(postIt, columnWIP);
      Assert.assertEquals(1, board.getWorkItems().size());
      Assert.assertEquals(columnWIP, postIt.getStage());

      board.moveWorkItemToStage(postIt, columnDone);
      Assert.assertEquals(1, board.getWorkItems().size());
      Assert.assertEquals(columnDone, postIt.getStage());
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void testMovePostItFailWithPostItNotFoundException()
      throws WorkItemNotInProcessException,
             StageNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem("My first task");
      Stage columnWIP = new Stage("work in progress");
      board.addStage(columnWIP);
      Stage columnDone = new Stage("done");
      board.addStage(columnDone);

      Assert.assertTrue(board.getWorkItems().isEmpty());
      board.moveWorkItemToStage(postIt, columnDone);
      Assert.assertEquals(1, board.getWorkItems());
      Assert.assertEquals(columnDone, postIt.getStage());
    }

    @Test(expected = StageNotInProcessException.class)
    public void testMovePostItFailWithStageNotInProcessException() throws
                                                                   WorkItemNotInProcessException,
                                                                   StageNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem("My first task");
      Stage columnWIP = new Stage("work in progress");
      board.addStage(columnWIP);
      board.addWorkItem(postIt, columnWIP);
      Stage columnDone = new Stage("done");

      board.moveWorkItemToStage(postIt, columnDone);
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
    public void reorderWorkItem_WithExistingWorkItems() throws StageNotInProcessException,
                                                               WorkItemNotInProcessException,
                                                               WorkItemNotInStageException {
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

      board.putWorkItemAtPosition(workItemC, 0);
      Assert.assertEquals(0, workItemC.getOrder());
      Assert.assertEquals(1, workItemA.getOrder());
      Assert.assertEquals(2, workItemB.getOrder());
      Assert.assertEquals(3, workItemD.getOrder());

      board.putWorkItemAtPosition(workItemA, 3);
      Assert.assertEquals(0, workItemC.getOrder());
      Assert.assertEquals(3, workItemA.getOrder());
      Assert.assertEquals(1, workItemB.getOrder());
      Assert.assertEquals(2, workItemD.getOrder());

      board.putWorkItemAtPosition(workItemB, 2);
      Assert.assertEquals(0, workItemC.getOrder());
      Assert.assertEquals(3, workItemA.getOrder());
      Assert.assertEquals(2, workItemB.getOrder());
      Assert.assertEquals(1, workItemD.getOrder());

      board.putWorkItemAtPosition(workItemB, 2);
      Assert.assertEquals(0, workItemC.getOrder());
      Assert.assertEquals(3, workItemA.getOrder());
      Assert.assertEquals(2, workItemB.getOrder());
      Assert.assertEquals(1, workItemD.getOrder());

    }

    @Test(expected = IllegalArgumentException.class)
    public void reorderWorkItem_WithNegativePositionThrowsIllegalArgumentException()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Board board = new Board("Project board");
      Stage todoStage = new Stage("Todo");
      board.addStage(todoStage);

      WorkItem workItemA = new WorkItem("Work item A");
      board.addWorkItem(workItemA, todoStage);

      WorkItem workItemB = new WorkItem("Work item B");
      board.addWorkItem(workItemB, todoStage);

      board.putWorkItemAtPosition(workItemA, -1);
    }

    @Test
    public void reorderWorkItem_WithBigPositionPutTheWorkItemAtTheEnd()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Board board = new Board("Project board");
      Stage todoStage = new Stage("Todo");
      board.addStage(todoStage);

      WorkItem workItemA = new WorkItem("Work item A");
      board.addWorkItem(workItemA, todoStage);

      WorkItem workItemB = new WorkItem("Work item B");
      board.addWorkItem(workItemB, todoStage);

      board.putWorkItemAtPosition(workItemA, Integer.MAX_VALUE);
      Assert.assertEquals(0, workItemB.getOrder());
      Assert.assertEquals(1, workItemA.getOrder());
    }

    @Test
    public void shouldIncrementStageOrderByOne() {
      Board board = new Board("new board");
      Stage todo = new Stage("todo");
      board.addStage(todo);

      Stage wip = new Stage("wip");
      board.addStage(wip);

      Stage done = new Stage("done");
      board.addStage(done);

      Assert.assertEquals(0, todo.getOrder());
      Assert.assertEquals(1, wip.getOrder());
      Assert.assertEquals(2, done.getOrder());
    }

    @Test
    public void shouldReorder_SwapBetweenFirstAndLast() throws StageNotInProcessException {
      Board board = new Board("new board");
      Stage todo = new Stage("todo");
      board.addStage(todo);

      Stage wip = new Stage("wip");
      board.addStage(wip);

      Stage validation = new Stage("validation");
      board.addStage(validation);

      Stage done = new Stage("done");
      board.addStage(done);

      board.reorderStage(todo, 4);
      Assert.assertEquals(0, wip.getOrder());
      Assert.assertEquals(1, validation.getOrder());
      Assert.assertEquals(2, done.getOrder());
      Assert.assertEquals(3, todo.getOrder());
    }

    @Test
    public void shouldReorder_AllEmenetBetweenTwoElement() throws StageNotInProcessException {
      Board board = new Board("new board");
      Stage todo = new Stage("todo");
      board.addStage(todo);

      Stage wip = new Stage("wip");
      board.addStage(wip);

      Stage validation = new Stage("validation");
      board.addStage(validation);

      Stage done = new Stage("done");
      board.addStage(done);

      board.reorderStage(todo, 2);
      Assert.assertEquals(0, wip.getOrder());
      Assert.assertEquals(1, validation.getOrder());
      Assert.assertEquals(2, todo.getOrder());
      Assert.assertEquals(3, done.getOrder());
    }

    @Test
    public void shouldReorderWorkItemsIfMoved()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("new board");
      Stage todo = new Stage("todo");
      board.addStage(todo);

      Stage wip = new Stage("wip");
      board.addStage(wip);

      WorkItem workItemA = new WorkItem("Work item A");
      board.addWorkItem(workItemA, todo);

      WorkItem workItemB = new WorkItem("Work item B");
      board.addWorkItem(workItemB, todo);

      board.moveWorkItemToStage(workItemA, wip);
      Assert.assertEquals(0, workItemA.getOrder());
      Assert.assertEquals(0, workItemB.getOrder());
    }

    @Test
    public void itShouldAddStageWorkItemsIfStageContainsWorkItemWhenAddedToBoard() {
      Board board = make(a(Board,
        with(name, "Hello"),
        with(stages, listOf(
          a(Stage,
            with(stageName, "TODO"),
            with(workItems, listOf(
              a(WorkItem, with(workItemName, "Un")),
              a(WorkItem, with(workItemName, "Deux"))
            ))),
          a(Stage, with(stageName, "WIP")),
          a(Stage, with(stageName, "DONE"))
        ))
      ));

      assertThat(board.getWorkItems())
        .hasSize(2);
    }
  }
}
