package org.svomz.apps.kanban.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Board {

	private String name;
	private Set<PostIt> postIts;
	private Set<Column> columns;
	
	public Board(final String name) {
		this.name = name;
		this.postIts = new HashSet<PostIt>();
		this.columns = new HashSet<Column>();
	}
	
	public String getName() {
		return this.name;
	}

	public Set<PostIt> getPostIts() {
		return Collections.unmodifiableSet(this.postIts);
	}

	public Set<Column> getColumns() {
		return Collections.unmodifiableSet(this.columns);
	}

	public Board addColumn(final Column column) {
		this.columns.add(column);
		return this;
	}

	public Board removeColumn(Column column) throws ColumnNotOnBoardException {
		if (!this.columns.contains(column)) {
			throw new ColumnNotOnBoardException();
		}
		this.columns.remove(column);
		return this;
		
	}

	public Board addPostIt(PostIt postIt, Column column) throws ColumnNotOnBoardException {
		if (!this.columns.contains(column)) {
			throw new ColumnNotOnBoardException();
		}
		
		postIt.setColumn(column);
		this.postIts.add(postIt);
		return this;
		
	}

	public Board removePostIt(final PostIt postIt) throws PostItNotOnBoardException {
		if (!this.postIts.contains(postIt)) {
			throw new PostItNotOnBoardException();
		}
		this.postIts.remove(postIt);
		return this;
		
	}

	public Board movePostIt(PostIt postIt, final Column column) throws PostItNotOnBoardException, ColumnNotOnBoardException {
		if (!this.postIts.contains(postIt)) {
			throw new PostItNotOnBoardException();
		}
		if (!this.columns.contains(column)) {
			throw new ColumnNotOnBoardException();
		}
		
		postIt.setColumn(column);
		return this;
	}

}
