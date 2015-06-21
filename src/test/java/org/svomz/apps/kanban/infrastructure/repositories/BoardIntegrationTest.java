package org.svomz.apps.kanban.infrastructure.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.svomz.apps.kanban.domain.Board;
import org.svomz.apps.kanban.domain.BoardRepository;
import org.svomz.apps.kanban.domain.Stage;
import org.svomz.apps.kanban.domain.StageNotEmptyException;
import org.svomz.apps.kanban.domain.StageNotInProcessException;
import org.svomz.apps.kanban.domain.StageRepository;
import org.svomz.apps.kanban.domain.WorkItem;
import org.svomz.apps.kanban.domain.WorkItemNotOnBoardException;
import org.svomz.apps.kanban.domain.WorkItemRepository;
import org.svomz.apps.kanban.infrastructure.domain.EntityNotFoundException;
import org.svomz.apps.kanban.infrastructure.domain.JpaBoardRepository;
import org.svomz.apps.kanban.infrastructure.domain.JpaStageRepository;
import org.svomz.apps.kanban.infrastructure.domain.JpaWorkItemRepository;

public class BoardIntegrationTest {

  private EntityManagerFactory emf;
  private EntityManager em;

  @Before
  public void setUp() throws ClassNotFoundException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    this.emf = Persistence.createEntityManagerFactory("kanban");
    this.em = this.emf.createEntityManager();
  }

  @Test
  public void creatNewBoard() {
    Board board = new Board("todo");

    BoardRepository repository = new JpaBoardRepository(this.em);
    repository.create(board);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    List<Board> boards = repository.findAll();
    Assert.assertTrue(boards.size() == 1);
    Board persistedBoard = boards.get(0);
    Assert.assertEquals(board.getName(), persistedBoard.getName());
  }

  @Test
  public void testAddAndRemoveStageAndWorkItem() throws StageNotInProcessException,
      WorkItemNotOnBoardException, EntityNotFoundException, StageNotEmptyException {
    Board board = new Board("todo");
    WorkItem postIt = new WorkItem("My first task");
    Stage column = new Stage("work in progress");
    board.addStage(column);
    board.addWorkItem(postIt, column);

    BoardRepository boardRepository = new JpaBoardRepository(this.em);
    boardRepository.create(board);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    List<Board> boards = boardRepository.findAll();
    Assert.assertTrue(boards.size() == 1);

    StageRepository stageRepository = new JpaStageRepository(this.em);
    Assert.assertTrue(stageRepository.findAll().size() == 1);

    WorkItemRepository workItemRepository = new JpaWorkItemRepository(this.em);
    Assert.assertTrue(workItemRepository.findAll().size() == 1);

    board.removeWorkItem(postIt);
    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertTrue(stageRepository.findAll().size() == 1);
    Assert.assertTrue(workItemRepository.findAll().size() == 0);

    board.removeStage(column);
    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertTrue(stageRepository.findAll().size() == 0);
    Assert.assertTrue(workItemRepository.findAll().size() == 0);
  }

  @Test
  public void testMoveWorkItem() throws StageNotInProcessException, WorkItemNotOnBoardException,
      EntityNotFoundException, StageNotEmptyException {
    Board board = new Board("todo");
    WorkItem postIt = new WorkItem("My first task");
    Stage columnWIP = new Stage("work in progress");
    board.addStage(columnWIP);
    board.addWorkItem(postIt, columnWIP);
    Stage columnDone = new Stage("done");
    board.addStage(columnDone);

    BoardRepository boardRepository = new JpaBoardRepository(this.em);
    boardRepository.create(board);

    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    List<Board> boards = boardRepository.findAll();
    Assert.assertTrue(boards.size() == 1);

    StageRepository stageRepository = new JpaStageRepository(this.em);
    Assert.assertTrue(stageRepository.findAll().size() == 2);

    WorkItemRepository workItemRepository = new JpaWorkItemRepository(this.em);
    List<WorkItem> workItems = workItemRepository.findAll();
    Assert.assertTrue(workItems.size() == 1);

    board.moveWorkItem(postIt, columnDone);
    this.em.getTransaction().begin();
    this.em.flush();
    this.em.getTransaction().commit();

    Assert.assertTrue(stageRepository.findAll().size() == 2);
    workItems = workItemRepository.findAll();
    Assert.assertTrue(workItems.size() == 1);
    WorkItem persistedWorkItem = workItems.get(0);
    Assert.assertTrue(persistedWorkItem.getStage().getId() == columnDone.getId());
  }

  @After
  public void tearDown() {
    this.em.close();
    this.emf.close();
  }

}
