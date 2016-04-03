package org.svomz.apps.koobz.board.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardUnitTest.ArchivingFeatures.class,
  BoardUnitTest.DefaultFeatures.class
})
public class BoardUnitTest {

  public static class ArchivingFeatures {

    @Test
    public void itShouldReturnsArchivedWorkItems()
      throws WorkItemNotInProcessException, StageNotInProcessException {
      // Given a board with a stage having one work item
      Board board = new Board(
        UUID.randomUUID().toString(),
        "A board"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      board.addStageToBoard(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem aWorkItem = board.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );

      // When I archive the work item
      board.archiveWorkItemWithId(aWorkItemId);

      // It should not be in the stage anymore
      assertThat(board.workItemsInStage(aStageIdentity)).doesNotContain(aWorkItem);
    }

    @Test
    public void itShouldSendBackWorkItemsToBoardInTheStageTheyWereBeforeArchiving()
      throws WorkItemNotInProcessException, WorkItemNotArchivedException,
             StageNotInProcessException {
      // Given a board with a stage having one work item archived
      Board board = new Board(
        UUID.randomUUID().toString(),
        "A board"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      board.addStageToBoard(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem aWorkItem = board.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );
      board.archiveWorkItemWithId(aWorkItemId);

      // When I send back the work item on the board
      board.sendBackToBoardWorkItemWithId(aWorkItemId);

      // Then the work item is in the To do stage
      assertThat(board.workItemsInStage(aStageIdentity))
        .contains(aWorkItem);
    }

    @Test
    public void itShouldConsiderArchivedWorkItemsWhenReordering()
      throws WorkItemNotInProcessException, WorkItemNotInStageException,
             WorkItemNotArchivedException, StageNotInProcessException {
      // Given a board with a stage having one work item archived and two work item non archived
      Board board = new Board(
        UUID.randomUUID().toString(),
        "A board"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      board.addStageToBoard(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem firstWorkItem = board.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );

      String aSecondWorkItemId = UUID.randomUUID().toString();
      WorkItem secondWorkItem = board.addWorkItemToStage(
        aStageIdentity,
        aSecondWorkItemId,
        "a",
        "a"
      );

      WorkItem thirdWorkItem = board.addWorkItemToStage(
        aStageIdentity,
        UUID.randomUUID().toString(),
        "a",
        "a"
      );

      board.archiveWorkItemWithId(aWorkItemId);

      // When I put the work item at the first position
      board.moveWorkItemWithIdToPosition(aSecondWorkItemId, 0);
      // And I send back to board the first work item
      board.sendBackToBoardWorkItemWithId(aWorkItemId);

      // Then the the first work item has position 1
      assertThat(firstWorkItem.getPosition()).isEqualTo(1);
      // And the second work item has position 0
      assertThat(secondWorkItem.getPosition()).isEqualTo(0);
      // And the third work item has position 2
      assertThat(thirdWorkItem.getPosition()).isEqualTo(2);
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

      board.addStageToBoard(
        UUID.randomUUID().toString(),
        "work in progress"
      );
      Assert.assertEquals(1, board.getStages().size());
    }

    @Test
    public void testRemoveStage() throws StageNotInProcessException, StageNotEmptyException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );

      board.removeStageWithId(aStageIdentity);
      Assert.assertTrue(board.getStages().isEmpty());
    }

    @Test(expected = StageNotInProcessException.class)
    public void testRemoveStageFailWithStageNotInProcessException()
      throws StageNotInProcessException, StageNotEmptyException {
      Board board = new Board("todo");

      board.removeStageWithId(UUID.randomUUID().toString());

      Assert.assertTrue(board.getStages().isEmpty());
    }

    @Test(expected = StageNotEmptyException.class)
    public void testRemoveStageFailWithStageNotEmptyException() throws StageNotInProcessException,
                                                                       StageNotEmptyException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );
      WorkItem workItem = board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item",
        "A description");

      board.removeStageWithId(column.getId());
      Assert.assertTrue(board.getStages().isEmpty());
    }

    @Test
    public void testAddPostIt() throws StageNotInProcessException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );

      Assert.assertTrue(board.getWorkItems().isEmpty());

      board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item",
        "A description");

      Assert.assertEquals(1, board.getWorkItems().size());

    }

    @Test(expected = StageNotInProcessException.class)
    public void testAddPostItFail() throws StageNotInProcessException {
      Board board = new Board("todo");

      String stageId = UUID.randomUUID().toString();
      Stage column = new Stage(stageId, "A column");

      board.addWorkItemToStage(stageId, UUID.randomUUID().toString(), "My first task", "A description");
    }

    @Test
    public void testRemovePostIt()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );

      WorkItem postIt =
        board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "My first task",
          "A description");
      board.removeWorkItem(postIt);
      Assert.assertTrue(board.getWorkItems().isEmpty());
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void testRemovePostItWithPostItNotOnBoardException()
      throws WorkItemNotInProcessException, StageNotInProcessException {
      Board board = new Board("todo");

      WorkItem postIt = new WorkItem(UUID.randomUUID().toString(), "A work item", "A description");
      board.removeWorkItem(postIt);
    }

    @Test
    public void testMovePostIt() throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );
      Stage columnDone = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "done"
      );
      WorkItem postIt =
        board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "My first task",
          "A description");
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

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );
      Stage columnDone = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "done"
      );

      Assert.assertTrue(board.getWorkItems().isEmpty());
      board.moveWorkItemToStage(new WorkItem(UUID.randomUUID().toString(), "a", "a"), columnDone);
    }

    @Test(expected = StageNotInProcessException.class)
    public void testMovePostItFailWithStageNotInProcessException() throws
                                                                   WorkItemNotInProcessException,
                                                                   StageNotInProcessException {
      Board board = new Board("todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = board.addStageToBoard(
        aStageIdentity,
        "work in progress"
      );
      WorkItem postIt =
        board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "My first task",
          "A description");

      Stage columnDone = new Stage(UUID.randomUUID().toString(), "done");

      board.moveWorkItemToStage(postIt, columnDone);
      Assert.assertEquals(1, board.getWorkItems());
      Assert.assertEquals(columnDone, postIt.getStage());
    }

    @Test
    public void addWorkItem_NewWorkItemIsTheLast() throws StageNotInProcessException {
      Board board = new Board("Project board");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = board.addStageToBoard(
        aStageIdentity,
        "To do"
      );

      WorkItem workItemA =
        board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item A",
          "A description");
      Assert.assertEquals(0, workItemA.getPosition());

      WorkItem workItemB =
        board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
          "A description");

      Assert.assertEquals(1, workItemB.getPosition());
    }

    @Test
    public void reorderWorkItem_WithExistingWorkItems() throws StageNotInProcessException,
                                                               WorkItemNotInProcessException,
                                                               WorkItemNotInStageException {
      Board board = new Board("Project board");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = board.addStageToBoard(
        aStageIdentity,
        "todo"
      );

      String workItemAId = UUID.randomUUID().toString();
      WorkItem workItemA = board.addWorkItemToStage(aStageIdentity, workItemAId, "Work item A",
        "A description");

      String workItemBId = UUID.randomUUID().toString();
      WorkItem workItemB = board.addWorkItemToStage(aStageIdentity, workItemBId, "Work item B",
        "A description");

      String workItemCId = UUID.randomUUID().toString();
      WorkItem workItemC = board.addWorkItemToStage(aStageIdentity, workItemCId, "Work item C",
        "A description");

      String workItemDId = UUID.randomUUID().toString();
      WorkItem workItemD = board.addWorkItemToStage(aStageIdentity, workItemDId, "Work item D",
        "A description");

      board.moveWorkItemWithIdToPosition(workItemCId, 0);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(1, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(3, workItemD.getPosition());

      board.moveWorkItemWithIdToPosition(workItemAId, 3);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(1, workItemB.getPosition());
      Assert.assertEquals(2, workItemD.getPosition());

      board.moveWorkItemWithIdToPosition(workItemBId, 2);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(1, workItemD.getPosition());

      board.moveWorkItemWithIdToPosition(workItemBId, 2);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(1, workItemD.getPosition());

    }

    @Test(expected = IllegalArgumentException.class)
    public void reorderWorkItem_WithNegativePositionThrowsIllegalArgumentException()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Board board = new Board("Project board");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = board.addStageToBoard(
        aStageIdentity,
        "todo"
      );

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem workItemA = board.addWorkItemToStage(aStageIdentity, aWorkItemId, "Work item A",
        "A description");

      board.moveWorkItemWithIdToPosition(aWorkItemId, -1);
    }

    @Test
    public void reorderWorkItem_WithBigPositionPutTheWorkItemAtTheEnd()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Board board = new Board("Project board");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = board.addStageToBoard(
        aStageIdentity,
        "todo"
      );

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem workItemA = board.addWorkItemToStage(aStageIdentity, aWorkItemId, "Work item A",
        "A description");

      WorkItem workItemB = board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
        "A description");

      board.moveWorkItemWithIdToPosition(aWorkItemId, Integer.MAX_VALUE);
      Assert.assertEquals(0, workItemB.getPosition());
      Assert.assertEquals(1, workItemA.getPosition());
    }

    @Test
    public void shouldIncrementStageOrderByOne() {
      Board board = new Board("new board");
      Stage todo = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "todo"
      );

      Stage wip = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage done = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "done"
      );

      Assert.assertEquals(0, todo.getPosition());
      Assert.assertEquals(1, wip.getPosition());
      Assert.assertEquals(2, done.getPosition());
    }

    @Test
    public void shouldReorder_SwapBetweenFirstAndLast() throws StageNotInProcessException {
      Board board = new Board("new board");
      Stage todo = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "todo"
      );

      Stage wip = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage validation = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "validation"
      );

      Stage done = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "done"
      );

      board.reorderStage(todo, 4);
      Assert.assertEquals(0, wip.getPosition());
      Assert.assertEquals(1, validation.getPosition());
      Assert.assertEquals(2, done.getPosition());
      Assert.assertEquals(3, todo.getPosition());
    }

    @Test
    public void shouldReorder_AllEmenetBetweenTwoElement() throws StageNotInProcessException {
      Board board = new Board("new board");
      Stage todo = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "todo"
      );

      Stage wip = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage validation = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "validation"
      );

      Stage done = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "done"
      );

      board.reorderStage(todo, 2);
      Assert.assertEquals(0, wip.getPosition());
      Assert.assertEquals(1, validation.getPosition());
      Assert.assertEquals(2, todo.getPosition());
      Assert.assertEquals(3, done.getPosition());
    }

    @Test
    public void shouldReorderWorkItemsIfMoved()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Board board = new Board("new board");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todo = board.addStageToBoard(
        aStageIdentity,
        "to do"
      );

      Stage wip = board.addStageToBoard(
        UUID.randomUUID().toString(),
        "wip"
      );

      WorkItem workItemA = board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item A",
        "A description");

      WorkItem workItemB = board.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
        "A description");

      board.moveWorkItemToStage(workItemA, wip);
      Assert.assertEquals(0, workItemA.getPosition());
      Assert.assertEquals(0, workItemB.getPosition());
    }
  }
}
