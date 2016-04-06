package org.svomz.apps.koobz.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  WorkflowUnitTest.ArchivingFeatures.class,
  WorkflowUnitTest.DefaultFeatures.class
})
public class WorkflowUnitTest {

  public static class ArchivingFeatures {

    @Test
    public void itShouldReturnsArchivedWorkItems()
      throws WorkItemNotInProcessException, StageNotInProcessException {
      // Given a board with a stage having one work item
      Workflow workflow = new Workflow(
        UUID.randomUUID().toString(),
        "A workflow"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      workflow.addStageToWorkflow(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem aWorkItem = workflow.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );

      // When I archive the work item
      workflow.archiveWorkItemWithId(aWorkItemId);

      // It should not be in the stage anymore
      assertThat(workflow.workItemsInStage(aStageIdentity)).doesNotContain(aWorkItem);
    }

    @Test
    public void itShouldSendBackWorkItemsToBoardInTheStageTheyWereBeforeArchiving()
      throws WorkItemNotInProcessException, WorkItemNotArchivedException,
             StageNotInProcessException {
      // Given a board with a stage having one work item archived
      Workflow workflow = new Workflow(
        UUID.randomUUID().toString(),
        "A workflow"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      workflow.addStageToWorkflow(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem aWorkItem = workflow.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );
      workflow.archiveWorkItemWithId(aWorkItemId);

      // When I send back the work item on the board
      workflow.sendBackToWorkflowWorkItemWithId(aWorkItemId);

      // Then the work item is in the To do stage
      assertThat(workflow.workItemsInStage(aStageIdentity))
        .contains(aWorkItem);
    }

    @Test
    public void itShouldConsiderArchivedWorkItemsWhenReordering()
      throws WorkItemNotInProcessException, WorkItemNotInStageException,
             WorkItemNotArchivedException, StageNotInProcessException {
      // Given a board with a stage having one work item archived and two work item non archived
      Workflow workflow = new Workflow(
        UUID.randomUUID().toString(),
        "A workflow"
      );

      String aStageIdentity = UUID.randomUUID().toString();
      workflow.addStageToWorkflow(
        aStageIdentity,
        "To do"
      );

      String aWorkItemName = "A work item";
      String aWorkItemDescription = "A work item description";
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem firstWorkItem = workflow.addWorkItemToStage(
        aStageIdentity,
        aWorkItemId,
        aWorkItemName,
        aWorkItemDescription
      );

      String aSecondWorkItemId = UUID.randomUUID().toString();
      WorkItem secondWorkItem = workflow.addWorkItemToStage(
        aStageIdentity,
        aSecondWorkItemId,
        "a",
        "a"
      );

      WorkItem thirdWorkItem = workflow.addWorkItemToStage(
        aStageIdentity,
        UUID.randomUUID().toString(),
        "a",
        "a"
      );

      workflow.archiveWorkItemWithId(aWorkItemId);

      // When I put the work item at the first position
      workflow.moveWorkItemWithIdToPosition(aSecondWorkItemId, 0);
      // And I send back to board the first work item
      workflow.sendBackToWorkflowWorkItemWithId(aWorkItemId);

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
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      Assert.assertEquals("todo", workflow.getName());
      Assert.assertTrue(workflow.workItems().isEmpty());
      Assert.assertTrue(workflow.stages().isEmpty());
    }

    @Test
    public void testAddColumns() {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "work in progress"
      );
      Assert.assertEquals(1, workflow.stages().size());
    }

    @Test
    public void testRemoveStage() throws StageNotInProcessException, StageNotEmptyException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );

      workflow.removeStageWithId(aStageIdentity);
      Assert.assertTrue(workflow.stages().isEmpty());
    }

    @Test(expected = StageNotInProcessException.class)
    public void testRemoveStageFailWithStageNotInProcessException()
      throws StageNotInProcessException, StageNotEmptyException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      workflow.removeStageWithId(UUID.randomUUID().toString());

      Assert.assertTrue(workflow.stages().isEmpty());
    }

    @Test(expected = StageNotEmptyException.class)
    public void testRemoveStageFailWithStageNotEmptyException() throws StageNotInProcessException,
                                                                       StageNotEmptyException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );
      WorkItem workItem = workflow
        .addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item",
        "A description");

      workflow.removeStageWithId(column.getId());
      Assert.assertTrue(workflow.stages().isEmpty());
    }

    @Test
    public void testAddPostIt() throws StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );

      Assert.assertTrue(workflow.workItems().isEmpty());

      workflow.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item",
        "A description");

      Assert.assertEquals(1, workflow.workItems().size());

    }

    @Test(expected = StageNotInProcessException.class)
    public void testAddPostItFail() throws StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String stageId = UUID.randomUUID().toString();
      Stage column = new Stage(stageId, "A column");

      workflow
        .addWorkItemToStage(stageId, UUID.randomUUID().toString(), "My first task", "A description");
    }

    @Test
    public void testRemovePostIt()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage column = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem postIt =
        workflow.addWorkItemToStage(aStageIdentity, aWorkItemId, "My first task",
          "A description");
      workflow.removeWorkItemWithId(aWorkItemId);
      Assert.assertTrue(workflow.workItems().isEmpty());
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void testRemovePostItWithPostItNotOnBoardException()
      throws WorkItemNotInProcessException, StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem postIt = new WorkItem(aWorkItemId, "A work item", "A description");
      workflow.removeWorkItemWithId(aWorkItemId);
    }

    @Test
    public void testMovePostIt() throws StageNotInProcessException, WorkItemNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );
      String doneColumnId = UUID.randomUUID().toString();
      Stage columnDone = workflow.addStageToWorkflow(
        doneColumnId,
        "done"
      );
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem postIt =
        workflow.addWorkItemToStage(aStageIdentity, aWorkItemId, "My first task",
          "A description");
      Assert.assertEquals(1, workflow.workItems().size());
      Assert.assertEquals(columnWIP, postIt.getStage());

      workflow.moveWorkItemWithIdToStageWithId(aWorkItemId, doneColumnId);
      Assert.assertEquals(1, workflow.workItems().size());
      Assert.assertEquals(columnDone, postIt.getStage());
    }

    @Test(expected = WorkItemNotInProcessException.class)
    public void testMovePostItFailWithPostItNotFoundException()
      throws WorkItemNotInProcessException,
             StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );
      String doneColumnId = UUID.randomUUID().toString();
      Stage columnDone = workflow.addStageToWorkflow(
        doneColumnId,
        "done"
      );

      Assert.assertTrue(workflow.workItems().isEmpty());
      String aWorkItemId = UUID.randomUUID().toString();
      workflow.moveWorkItemWithIdToStageWithId(aWorkItemId, doneColumnId);
    }

    @Test(expected = StageNotInProcessException.class)
    public void testMovePostItFailWithStageNotInProcessException() throws
                                                                   WorkItemNotInProcessException,
                                                                   StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");

      String aStageIdentity = UUID.randomUUID().toString();
      Stage columnWIP = workflow.addStageToWorkflow(
        aStageIdentity,
        "work in progress"
      );
      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem postIt =
        workflow.addWorkItemToStage(aStageIdentity, aWorkItemId, "My first task",
          "A description");

      String doneColumnId = UUID.randomUUID().toString();
      Stage columnDone = new Stage(doneColumnId, "done");

      workflow.moveWorkItemWithIdToStageWithId(aWorkItemId, doneColumnId);
      Assert.assertEquals(1, workflow.workItems());
      Assert.assertEquals(columnDone, postIt.getStage());
    }

    @Test
    public void addWorkItem_NewWorkItemIsTheLast() throws StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = workflow.addStageToWorkflow(
        aStageIdentity,
        "To do"
      );

      WorkItem workItemA =
        workflow.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item A",
          "A description");
      Assert.assertEquals(0, workItemA.getPosition());

      WorkItem workItemB =
        workflow.addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
          "A description");

      Assert.assertEquals(1, workItemB.getPosition());
    }

    @Test
    public void reorderWorkItem_WithExistingWorkItems() throws StageNotInProcessException,
                                                               WorkItemNotInProcessException,
                                                               WorkItemNotInStageException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = workflow.addStageToWorkflow(
        aStageIdentity,
        "todo"
      );

      String workItemAId = UUID.randomUUID().toString();
      WorkItem workItemA = workflow.addWorkItemToStage(aStageIdentity, workItemAId, "Work item A",
        "A description");

      String workItemBId = UUID.randomUUID().toString();
      WorkItem workItemB = workflow.addWorkItemToStage(aStageIdentity, workItemBId, "Work item B",
        "A description");

      String workItemCId = UUID.randomUUID().toString();
      WorkItem workItemC = workflow.addWorkItemToStage(aStageIdentity, workItemCId, "Work item C",
        "A description");

      String workItemDId = UUID.randomUUID().toString();
      WorkItem workItemD = workflow.addWorkItemToStage(aStageIdentity, workItemDId, "Work item D",
        "A description");

      workflow.moveWorkItemWithIdToPosition(workItemCId, 0);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(1, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(3, workItemD.getPosition());

      workflow.moveWorkItemWithIdToPosition(workItemAId, 3);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(1, workItemB.getPosition());
      Assert.assertEquals(2, workItemD.getPosition());

      workflow.moveWorkItemWithIdToPosition(workItemBId, 2);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(1, workItemD.getPosition());

      workflow.moveWorkItemWithIdToPosition(workItemBId, 2);
      Assert.assertEquals(0, workItemC.getPosition());
      Assert.assertEquals(3, workItemA.getPosition());
      Assert.assertEquals(2, workItemB.getPosition());
      Assert.assertEquals(1, workItemD.getPosition());

    }

    @Test(expected = IllegalArgumentException.class)
    public void reorderWorkItem_WithNegativePositionThrowsIllegalArgumentException()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = workflow.addStageToWorkflow(
        aStageIdentity,
        "todo"
      );

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem workItemA = workflow.addWorkItemToStage(aStageIdentity, aWorkItemId, "Work item A",
        "A description");

      workflow.moveWorkItemWithIdToPosition(aWorkItemId, -1);
    }

    @Test
    public void reorderWorkItem_WithBigPositionPutTheWorkItemAtTheEnd()
      throws StageNotInProcessException,
             WorkItemNotInProcessException, WorkItemNotInStageException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todoStage = workflow.addStageToWorkflow(
        aStageIdentity,
        "todo"
      );

      String aWorkItemId = UUID.randomUUID().toString();
      WorkItem workItemA = workflow.addWorkItemToStage(aStageIdentity, aWorkItemId, "Work item A",
        "A description");

      WorkItem workItemB = workflow
        .addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
        "A description");

      workflow.moveWorkItemWithIdToPosition(aWorkItemId, Integer.MAX_VALUE);
      Assert.assertEquals(0, workItemB.getPosition());
      Assert.assertEquals(1, workItemA.getPosition());
    }

    @Test
    public void shouldIncrementStageOrderByOne() {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      Stage todo = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "todo"
      );

      Stage wip = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage done = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "done"
      );

      Assert.assertEquals(0, todo.getPosition());
      Assert.assertEquals(1, wip.getPosition());
      Assert.assertEquals(2, done.getPosition());
    }

    @Test
    public void shouldReorder_SwapBetweenFirstAndLast() throws StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todo = workflow.addStageToWorkflow(
        aStageIdentity,
        "todo"
      );

      Stage wip = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage validation = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "validation"
      );

      Stage done = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "done"
      );

      workflow.moveStageWithIdToPosition(aStageIdentity, 4);
      Assert.assertEquals(0, wip.getPosition());
      Assert.assertEquals(1, validation.getPosition());
      Assert.assertEquals(2, done.getPosition());
      Assert.assertEquals(3, todo.getPosition());
    }

    @Test
    public void shouldReorder_AllEmenetBetweenTwoElement() throws StageNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todo = workflow.addStageToWorkflow(
        aStageIdentity,
        "todo"
      );

      Stage wip = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "work in progress"
      );

      Stage validation = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "validation"
      );

      Stage done = workflow.addStageToWorkflow(
        UUID.randomUUID().toString(),
        "done"
      );

      workflow.moveStageWithIdToPosition(aStageIdentity, 2);
      Assert.assertEquals(0, wip.getPosition());
      Assert.assertEquals(1, validation.getPosition());
      Assert.assertEquals(2, todo.getPosition());
      Assert.assertEquals(3, done.getPosition());
    }

    @Test
    public void shouldReorderWorkItemsIfMoved()
      throws StageNotInProcessException, WorkItemNotInProcessException {
      Workflow workflow = new Workflow(UUID.randomUUID().toString(), "todo");
      String aStageIdentity = UUID.randomUUID().toString();
      Stage todo = workflow.addStageToWorkflow(
        aStageIdentity,
        "to do"
      );

      String wipStageId = UUID.randomUUID().toString();
      Stage wip = workflow.addStageToWorkflow(
        wipStageId,
        "wip"
      );

      String workItemAId = UUID.randomUUID().toString();
      WorkItem workItemA = workflow.addWorkItemToStage(aStageIdentity, workItemAId, "Work item A",
        "A description");

      WorkItem workItemB = workflow
        .addWorkItemToStage(aStageIdentity, UUID.randomUUID().toString(), "Work item B",
        "A description");

      workflow.moveWorkItemWithIdToStageWithId(workItemAId, wipStageId);
      Assert.assertEquals(0, workItemA.getPosition());
      Assert.assertEquals(0, workItemB.getPosition());
    }
  }
}
