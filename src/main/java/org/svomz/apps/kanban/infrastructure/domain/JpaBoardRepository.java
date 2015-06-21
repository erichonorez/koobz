package org.svomz.apps.kanban.infrastructure.domain;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.Board;
import org.svomz.apps.kanban.domain.BoardRepository;

public class JpaBoardRepository extends AbstractJpaRepository<Board, Long> implements
    BoardRepository {

  @Inject
  public JpaBoardRepository(EntityManager entityManager) {
    super(entityManager);
  }

}
