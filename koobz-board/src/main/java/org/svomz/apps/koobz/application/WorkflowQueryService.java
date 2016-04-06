package org.svomz.apps.koobz.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Service;
import org.svomz.apps.koobz.domain.model.Workflow;
import org.svomz.apps.koobz.domain.model.WorkflowRepository;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
public class WorkflowQueryService {

  private final WorkflowRepository workflowRepository;

  @Inject
  public WorkflowQueryService(final WorkflowRepository aWorkflowRepository) {
    this.workflowRepository = aWorkflowRepository;
  }

  @Transactional
  public Optional<Workflow> findBoard(final String aBoardId) throws WorkflowNotFoundException {
    Preconditions.checkNotNull(aBoardId);

    return Optional.ofNullable(this.boardRepository().findOne(aBoardId));
  }

  private WorkflowRepository boardRepository() {
    return workflowRepository;
  }

}
