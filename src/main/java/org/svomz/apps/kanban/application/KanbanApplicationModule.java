package org.svomz.apps.kanban.application;

import org.svomz.apps.kanban.application.services.KanbanService;
import org.svomz.apps.kanban.application.services.KanbanServiceImpl;
import org.svomz.apps.kanban.domain.repositories.BoardRepository;
import org.svomz.apps.kanban.domain.repositories.StageRepository;
import org.svomz.apps.kanban.domain.repositories.WorkItemRepository;
import org.svomz.apps.kanban.infrastructure.domain.JpaBoardRepository;
import org.svomz.apps.kanban.infrastructure.domain.JpaStageRepository;
import org.svomz.apps.kanban.infrastructure.domain.JpaWorkItemRepository;

import com.google.inject.AbstractModule;

public class KanbanApplicationModule extends AbstractModule {

  @Override
  public void configure() {
    this.bind(BoardRepository.class).to(JpaBoardRepository.class);
    this.bind(StageRepository.class).to(JpaStageRepository.class);
    this.bind(WorkItemRepository.class).to(JpaWorkItemRepository.class);
    this.bind(KanbanService.class).to(KanbanServiceImpl.class);
  }

}
