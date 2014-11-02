package org.svomz.apps.kanban.domain;

import org.junit.Assert;

import org.junit.Test;

public class BoardTest {

	@Test
	public void testBoardCreation() {
		Board board = new Board("todo");
		
		Assert.assertEquals("todo", board.getName());
		Assert.assertTrue(board.getPostIts().isEmpty());
		Assert.assertTrue(board.getColumns().isEmpty());
		
	}
	
	@Test
	public void testAddColumns() {
		Board board = new Board("todo");
		
		Column column = new Column("work in progress");
		board.addColumn(column);
		Assert.assertEquals(1, board.getColumns().size());
		
	}
	
	@Test
	public void testRemoveColumns() throws ColumnNotOnBoardException {
		Board board = new Board("todo");
		
		Column column = new Column("work in progress");
		board.addColumn(column);
		try {
			board.removeColumn(column);
		} catch (ColumnNotOnBoardException e) {
			throw e;
		}
		Assert.assertTrue(board.getColumns().isEmpty());
		
	}
	
	@Test
	public void testAddPostIt() throws ColumnNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		Column column = new Column("work in progress");
		board.addColumn(column);
		
		Assert.assertTrue(board.getPostIts().isEmpty());
		board.addPostIt(postIt, column);
		Assert.assertEquals(1, board.getPostIts().size());
		
	}
	
	@Test(expected = ColumnNotOnBoardException.class)
	public void testAddPostItFail() throws ColumnNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		Column column = new Column("work in progress");
		
		board.addPostIt(postIt, column);
	}
	
	@Test
	public void testRemovePostIt() throws ColumnNotOnBoardException, PostItNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		Column column = new Column("work in progress");
		board.addColumn(column);
		
		board.addPostIt(postIt, column);
		board.removePostIt(postIt);
		Assert.assertTrue(board.getPostIts().isEmpty());
	}
	
	@Test(expected = PostItNotOnBoardException.class)
	public void testRemovePostItWithPostItNotOnBoardException() throws PostItNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		board.removePostIt(postIt);
	}
	
	public void testMovePostIt() throws ColumnNotOnBoardException, PostItNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		Column columnWIP = new Column("work in progress");
		board.addColumn(columnWIP);
		Column columnDone = new Column("done");
		board.addColumn(columnDone);
		board.addPostIt(postIt, columnWIP);
		Assert.assertEquals(1, board.getPostIts());
		Assert.assertEquals(columnWIP, postIt.getColumn());
		
		Assert.assertTrue(board.getPostIts().isEmpty());
		board.movePostIt(postIt, columnDone);
		Assert.assertEquals(1, board.getPostIts());
		Assert.assertEquals(columnDone, postIt.getColumn());
	}
	
	@Test(expected = PostItNotOnBoardException.class)
	public void testMovePostItFailWithPostItNotFound() throws PostItNotOnBoardException, ColumnNotOnBoardException {
		Board board = new Board("todo");
		
		PostIt postIt = new PostIt("My first task");
		Column columnWIP = new Column("work in progress");
		board.addColumn(columnWIP);
		Column columnDone = new Column("done");
		board.addColumn(columnDone);
		
		Assert.assertTrue(board.getPostIts().isEmpty());
		board.movePostIt(postIt, columnDone);
		Assert.assertEquals(1, board.getPostIts());
		Assert.assertEquals(columnDone, postIt.getColumn());
	}
	
}
