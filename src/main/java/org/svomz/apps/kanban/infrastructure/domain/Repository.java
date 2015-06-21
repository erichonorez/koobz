package org.svomz.apps.kanban.infrastructure.domain;

import java.util.List;

public interface Repository<T, PK> {
    
    public T find(PK primaryKey) throws EntityNotFoundException;
    
    public List<T> findAll();
    
    public T create(T entity);
    
    public T update(T entity);
    
    public void delete(T entity);

}
