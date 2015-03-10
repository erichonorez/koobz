package org.svomz.apps.kanban.infrastructure.domain;

import javax.persistence.EntityManager;

import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.repositories.BoardRepository;
import org.svomz.commons.persistence.jpa.AbstractJpaRepository;

import com.google.inject.Inject;

public class JpaBoardRepository extends AbstractJpaRepository<Board, Long> implements
    BoardRepository {

  @Inject
  public JpaBoardRepository(EntityManager entityManager) {
    super(entityManager);
  }

}
