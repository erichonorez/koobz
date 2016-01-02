package org.svomz.apps.koobz.board.infrastructure.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by eric on 09/07/15.
 */
@NoRepositoryBean
public interface KanbanRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

  T findOrThrowException(ID primaryKey) throws EntityNotFoundException;

}
