package org.svomz.apps.kanban.domain.repositories;

import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

import com.google.inject.Inject;

public class BoardRepositoryImpl extends AbstractJpaRepository<Board, Long> implements
    BoardRepository {

  @Inject
  public BoardRepositoryImpl(EntityManager entityManager) {
    super(entityManager);
  }

}
