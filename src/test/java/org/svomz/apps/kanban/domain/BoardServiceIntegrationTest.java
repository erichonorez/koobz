package org.svomz.apps.kanban.domain;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.services.KanbanService;
import org.svomz.commons.infrastructure.persistence.EntityNotFoundException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class BoardServiceIntegrationTest {
  
  @Test
  public void testCreateBoard() {
    Injector injector = Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();
    
    KanbanService boardService = injector.getInstance(KanbanService.class);
    Assert.assertNotNull(boardService.getAll());
    Assert.assertEquals(0, boardService.getAll().size());
    
    Board board = new Board("hello, world!");
    boardService.create(board);
    Assert.assertTrue(boardService.getAll().size() == 1);
    
    persistService.stop();
  }
  
  @Test
  public void testUpdateBoard() throws EntityNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Injector injector = Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();
    
    KanbanService boardService = injector.getInstance(KanbanService.class);
    Board board = new Board("hello, world!");
    long newId = boardService.create(board).getId();
    
    Board updatedBoard = new Board("World! Hello,");
    // use reflection to set the id of the board
    Field field = Board.class.getDeclaredField("id");
    field.setAccessible(true);
    field.set(updatedBoard, newId);
    boardService.update(updatedBoard);
    

    List<Board> boards = boardService.getAll();
    Assert.assertTrue(boards.size() == 1);
    Board existingBoard = boardService.get(updatedBoard.getId());
    Assert.assertEquals(updatedBoard.getId(), existingBoard.getId());
    Assert.assertEquals(updatedBoard.getId(), existingBoard.getId());
    Assert.assertEquals("World! Hello,", existingBoard.getName());
    
    persistService.stop();
  }
  
  @Test(expected = EntityNotFoundException.class)
  public void testUpdateBoardFailsWithEntityNotFoundException() throws EntityNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    Injector injector = Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();
    
    KanbanService boardService = injector.getInstance(KanbanService.class);
    Board board = new Board("hello, world!");
    boardService.create(board);
    
    Board boardToUpdate = new Board("World! Hello,");
    // use reflection to set the id of the board
    Field field = Board.class.getDeclaredField("id");
    field.setAccessible(true);
    field.set(boardToUpdate, 666);
    boardService.update(boardToUpdate);
    
    persistService.stop();
  }
  
  @Test
  public void testDeleteBoard() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, EntityNotFoundException {
    Injector injector = Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();
    
    KanbanService boardService = injector.getInstance(KanbanService.class);
    Board board = new Board("hello, world!");
    long newId = boardService.create(board).getId();
    
    boardService.delete(newId);
    
    persistService.stop();
  }
  
  @Test(expected = EntityNotFoundException.class)
  public void testDeleteFailsWithEntityNotFoundException() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, EntityNotFoundException {
    Injector injector = Guice.createInjector(new JpaPersistModule("kanban"), new KanbanDomainModule());
    PersistService persistService = injector.getInstance(PersistService.class);
    persistService.start();
    
    KanbanService boardService = injector.getInstance(KanbanService.class);
    Board board = new Board("hello, world!");
    boardService.create(board);
    boardService.delete(666);
    
    persistService.stop();
  }

}
