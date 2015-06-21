package org.svomz.apps.kanban.infrastructure.application;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class EntityManagerProducer {

  @PersistenceUnit(unitName = "kanban")
  private EntityManagerFactory em;
  
  @RequestScoped
  @Produces
  public EntityManager create() {
    return this.em.createEntityManager();
  }
  
  public void close(@Disposes EntityManager entityManager) {
    if (entityManager.isOpen()) {
      entityManager.close();
    }
  }
  
}
