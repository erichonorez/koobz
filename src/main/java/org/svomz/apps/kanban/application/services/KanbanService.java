package org.svomz.apps.kanban.application.services;

import java.util.List;

import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.WorkItem;
import org.svomz.apps.kanban.domain.exceptions.StageNotEmptyException;
import org.svomz.apps.kanban.domain.exceptions.StageNotInProcessException;
import org.svomz.apps.kanban.domain.exceptions.WorkItemNotInStageException;
import org.svomz.apps.kanban.domain.exceptions.WorkItemNotOnBoardException;
import org.svomz.commons.persistence.EntityNotFoundException;

/**
 * Description: Facade service for Kanban application.
 * 
 * For each method which involves a write operation in the persistence storage you are guaranteed
 * that change is applied at the return of the method.
 */
public interface KanbanService {

  List<Board> getAll();

  Board get(final long boardId) throws EntityNotFoundException;

  /**
   * Creates a board with the given name and make it persistent.
   * 
   * @return the persisted board.
   * 
   *         TODO the supplied object should not be a Board but a specific object representing the
   *         request. Why? Because we want to prevent user to directly add stages and workItems by
   *         just settings fields without passing by a method call. This method will be typically
   *         called by the REST application layer where marshalling / unmarshalling process does not
   *         call object method but access and set fields directly.
   */
  Board createBoard(final String name);

  /**
   * Updates the supplied board. The returned Board is the same instance than the one supplied.
   * 
   * @return the updated instance of the board.
   * @throws EntityNotFoundException if the entity is not found in the persistence storage.
   * 
   *         TODO same as for createBoard method. The update should take two parameters: the id of
   *         the board to update and the data to update.
   */
  Board updateBoard(final long boardId, final String name) throws EntityNotFoundException;

  /**
   * Removes the board identified by the given boardId.
   * 
   * @throws EntityNotFoundException if the board identified by the given boardId is not found in
   *         the back-end persistence storage.
   * 
   *         TODO throw an exception when the board contains stage(s) and / or workitem(s).
   */
  void deleteBoard(final long boardId) throws EntityNotFoundException;

  /**
   * Creates a stage with the given name and attaches it to the board identified by the supplied
   * board id and makes the stage persistent.
   * 
   * @param boardId
   * @param stage
   * @return the stage persisted instance
   * @throws EntityNotFoundException if the board identified by the given boardId is not found in
   *         the back-end persistence storage.
   * 
   *         TODO same as for the createBoard method. This method should take two parameters: the
   *         board identifier and an object which represent the request.
   */
  Stage addStageToBoard(final long boardId, final String name) throws EntityNotFoundException;

  /**
   * Removes the stage identified by the supplied by stageId from the board identified by the given
   * boardId.
   * 
   * @throws EntityNotFoundException if the board identified by the boardId or if the stage
   *         identified by the stageId are not found in the back-end persistence storage.
   * @throws StageNotInProcessException if the stage identified by the supplied stageId is not on
   *         the board.
   * @throws StageNotEmptyException if the stage contains workitems.
   */
  void removeStageFromBoard(final long boardId, final long stageId) throws EntityNotFoundException,
      StageNotInProcessException, StageNotEmptyException;

  /**
   * Updates the stage identified by the supplied stageId with the given name.
   * 
   * @return the updated instance of the stage
   * @throws EntityNotFoundException if the stage identified by the given stageId is not found in
   *         the persistence storage.
   * 
   *         TODO as described above don't use entities here.
   */
  Stage updateStage(final long stageId, final String namae) throws EntityNotFoundException;

  /**
   * @throws EntityNotFoundException if the board identified by the supplied boardId is not found in
   *         the persistence storage.
   */
  List<Stage> getStages(final long boardId) throws EntityNotFoundException;

  /**
   * @throws EntityNotFoundException if the board identified by the supplied boardId is not found in
   *         the persistence storage.
   */
  List<WorkItem> getWorkItems(final long boardId) throws EntityNotFoundException;

  /**
   * Creates a workItem instance with the given name, adds it on the board in the given stage.
   * 
   * @return the workItem persisted instance
   * @throws EntityNotFoundException if the board identified by the supplied boardId or the stage
   *         identified by the given stageId are not found in the persistence storage.
   * @throws StageNotInProcessException if the stage is not identified by the given stageId is not
   *         found in back-end persistence storage.
   * 
   *         TODO as described above.
   */
  WorkItem addWorkItemToBoard(final long boardId, final long stageId, final String text)
      throws EntityNotFoundException, StageNotInProcessException;

  /**
   * @throws EntityNotFoundException if the board identified by the supplied boardId or the workItem
   *         identified by the given workItemId are not found in the persistence storage.
   * @throws WorkItemNotOnBoardException if the work item is not on the board.
   */
  void removeWorkItemFromBoard(final long boardId, final long workItemId)
      throws EntityNotFoundException, WorkItemNotOnBoardException;

  /**
   * @return the updated workItem instance
   * @throws EntityNotFoundException if the board identified by the given boardId or the workItem
   *         identified by the given workItemId are not found in the persistence storage. This
   *         exception me be thrown if the workItem is moved from one stage to another and the dest
   *         stage is not on the board.
   * @throws WorkItemNotOnBoardException if the work item is not on the board.
   * @throws StageNotInProcessException if the stage is moved from one stage to another the dest
   *         stage is not on the board.
   * @throws WorkItemNotInStageException 
   */
  WorkItem updateWorkItem(final long boardId, final long workItemId, final String text,
      final long stageId, final Integer order) throws EntityNotFoundException, WorkItemNotOnBoardException,
      StageNotInProcessException, WorkItemNotInStageException;

}
