package org.svomz.apps.kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.svomz.apps.kanban.infrastructure.domain.KanbanRepositoryFactoryBean;

/**
 * Created by eric on 06/07/15.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableJpaRepositories(repositoryFactoryBeanClass = KanbanRepositoryFactoryBean.class, basePackages = "org.svomz.apps.kanban.domain")
public class KanbanApplication {

    public static void main(String... args) {
      SpringApplication.run(KanbanApplication.class, args);
    }

}
