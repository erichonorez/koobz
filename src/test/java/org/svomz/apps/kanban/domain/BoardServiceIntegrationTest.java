package org.svomz.apps.kanban.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.persistence.EntityNotFoundException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class BoardServiceIntegrationTest {

  @Test
  public void testCreateBoard() {
    Injector injector =
        Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();

    KanbanService boardService = injector.getInstance(KanbanService.class);
    Assert.assertNotNull(boardService.getAll());
    Assert.assertEquals(0, boardService.getAll().size());

    boardService.createBoard("hello, world!");
    Assert.assertTrue(boardService.getAll().size() == 1);

    persistService.stop();
  }

  @Test
  public void testUpdateBoard() throws EntityNotFoundException, NoSuchFieldException,
      SecurityException, IllegalArgumentException, IllegalAccessException {
    Injector injector =
        Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();

    KanbanService boardService = injector.getInstance(KanbanService.class);
    long newId = boardService.createBoard("hello, world!").getId();

    Board updatedBoard = boardService.updateBoard(newId, "World! Hello,");


    List<Board> boards = boardService.getAll();
    Assert.assertTrue(boards.size() == 1);
    Board existingBoard = boardService.get(updatedBoard.getId());
    Assert.assertEquals(updatedBoard.getId(), existingBoard.getId());
    Assert.assertEquals(updatedBoard.getId(), existingBoard.getId());
    Assert.assertEquals("World! Hello,", existingBoard.getName());

    persistService.stop();
  }

  @Test(expected = EntityNotFoundException.class)
  public void testUpdateBoardFailsWithEntityNotFoundException() throws EntityNotFoundException,
      NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Injector injector =
        Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();

    KanbanService boardService = injector.getInstance(KanbanService.class);
    boardService.createBoard("hello, world!");

    boardService.updateBoard(666, "Work!, hello");

    persistService.stop();
  }

  @Test
  public void testDeleteBoard() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException, EntityNotFoundException {
    Injector injector =
        Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();

    KanbanService boardService = injector.getInstance(KanbanService.class);
    long newId = boardService.createBoard("hello, world!").getId();

    boardService.deleteBoard(newId);

    persistService.stop();
  }

  @Test(expected = EntityNotFoundException.class)
  public void testDeleteFailsWithEntityNotFoundException() throws NoSuchFieldException,
      SecurityException, IllegalArgumentException, IllegalAccessException, EntityNotFoundException {
    Injector injector =
        Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();

    KanbanService boardService = injector.getInstance(KanbanService.class);
    boardService.createBoard("hello, world!");
    boardService.deleteBoard(666);

    persistService.stop();
  }

}
