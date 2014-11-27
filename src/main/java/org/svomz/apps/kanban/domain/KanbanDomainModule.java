package org.svomz.apps.kanban.domain;

import org.svomz.apps.kanban.domain.repositories.BoardRepository;
import org.svomz.apps.kanban.domain.repositories.BoardRepositoryImpl;
import org.svomz.apps.kanban.domain.repositories.StageRepository;
import org.svomz.apps.kanban.domain.repositories.StageRepositoryImpl;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepository;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepositoryImpl;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.apps.kanban.domain.services.KanbanServiceImpl;

import com.google.inject.AbstractModule;

public class KanbanDomainModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bind(BoardRepository.class).to(BoardRepositoryImpl.class);
    this.bind(StageRepository.class).to(StageRepositoryImpl.class);
    this.bind(WorkItemRepository.class).to(WorkItemRepositoryImpl.class);
    this.bind(KanbanService.class).to(KanbanServiceImpl.class);
  }

}
