package org.svomz.apps.koobz.board.application;

import com.google.common.base.Preconditions;

import org.springframework.stereotype.Service;
import org.svomz.apps.koobz.board.domain.model.Board;
import org.svomz.apps.koobz.board.domain.model.BoardRepository;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Service
public class BoardQueryService {

  private final BoardRepository boardRepository;

  @Inject
  public BoardQueryService(final BoardRepository aBoardRepository) {
    this.boardRepository = aBoardRepository;
  }

  @Transactional
  public Optional<Board> findBoard(final String aBoardId) throws BoardNotFoundException {
    Preconditions.checkNotNull(aBoardId);

    return Optional.ofNullable(this.boardRepository().findOne(aBoardId));
  }

  private BoardRepository boardRepository() {
    return boardRepository;
  }

}
