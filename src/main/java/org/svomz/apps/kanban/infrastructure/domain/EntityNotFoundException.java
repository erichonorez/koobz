package org.svomz.apps.kanban.infrastructure.domain;

import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class EntityNotFoundException extends Exception {

  private static final String MESSAGE_TPL = "%s entity with the identifier %s not found";

  private final Class<?> entityClass;
  private final String primaryKey;

  public EntityNotFoundException(final Class<?> entityClass, final String primaryKey) {
    Preconditions.checkNotNull(entityClass);
    Preconditions.checkNotNull(primaryKey);

    this.entityClass = entityClass;
    this.primaryKey = primaryKey;
  }

  @Override
  public String getMessage() {
    return String.format(MESSAGE_TPL, this.getEntityClass().getSimpleName(), this.getPrimaryKey());
  }

  public Class<?> getEntityClass() {
    return this.entityClass;
  }

  public String getPrimaryKey() {
    return this.primaryKey;
  }

}
