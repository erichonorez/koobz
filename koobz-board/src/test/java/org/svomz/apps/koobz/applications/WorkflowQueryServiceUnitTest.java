package org.svomz.apps.koobz.applications;

import org.junit.Test;
import org.svomz.apps.koobz.application.WorkflowNotFoundException;
import org.svomz.apps.koobz.application.WorkflowQueryService;
import org.svomz.apps.koobz.domain.model.Workflow;
import org.svomz.apps.koobz.domain.model.WorkflowRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkflowQueryServiceUnitTest {

  @Test
  public void itShouldSuccessfullyFindAnExistingBoard() throws WorkflowNotFoundException {
    // Given a persisted workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
    String workflowId = "35a45cd4-f81f-11e5-9ce9-5e5517507c66";
    // And with "A name" as name
    String aBoardName = "A name";

    WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
    WorkflowQueryService workflowService = new WorkflowQueryService(workflowRepository);

    when(workflowRepository.findOne(workflowId)).thenReturn(new Workflow(workflowId, aBoardName));

    // When a user makes a query to retrive workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66"
    Optional<Workflow> optionalBoard = workflowService.findBoard(workflowId);

    // Then the user gets the corresponding workflow
    assertThat(optionalBoard.isPresent()).isNotNull();

    Workflow workflow = optionalBoard.get();
    assertThat(workflow.getName()).isEqualTo(aBoardName);
    assertThat(workflow.workItems()).isEmpty();
    assertThat(workflow.stages()).isEmpty();
    assertThat(workflow.getId()).isNotNull();
  }

  @Test(expected = WorkflowNotFoundException.class)
  public void itShouldThrowBoardNotFoundExceptionIfBoardDoesNotExist()
    throws WorkflowNotFoundException {
    // Given the workflow with id "35a45cd4-f81f-11e5-9ce9-5e5517507c66" does not exist
    WorkflowRepository workflowRepository = mock(WorkflowRepository.class);
    when(workflowRepository.findOne("35a45cd4-f81f-11e5-9ce9-5e5517507c66"))
      .thenReturn(null);

    // When I query the for this id
    WorkflowQueryService workflowService = new WorkflowQueryService(workflowRepository);
    workflowService.findBoard("35a45cd4-f81f-11e5-9ce9-5e5517507c66");

    //then I get a BoardNotFoundException
  }

}
