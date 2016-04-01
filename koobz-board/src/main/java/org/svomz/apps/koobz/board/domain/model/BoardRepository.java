package org.svomz.apps.koobz.board.domain.model;


import org.svomz.apps.koobz.board.infrastructure.domain.KanbanRepository;

import java.util.Optional;

public interface BoardRepository extends KanbanRepository<Board, String> {

}
