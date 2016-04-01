package org.svomz.apps.koobz.board.domain.model;

import com.google.common.base.Preconditions;

public final class BoardId {

  private final String boardId;

  public BoardId(final String aBoardId) {
    Preconditions.checkNotNull(aBoardId);

    this.boardId = aBoardId;
  }

  public String value() {
    return this.boardId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BoardId boardId1 = (BoardId) o;

    return boardId.equals(boardId1.boardId);

  }

  @Override
  public int hashCode() {
    return boardId.hashCode();
  }
}
