package org.svomz.apps.koobz.board.ports.adapters.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.svomz.apps.koobz.board.application.BoardApplicationService;
import org.svomz.apps.koobz.board.infrastructure.domain.KanbanRepositoryFactoryBean;

/**
 * Created by eric on 06/07/15.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
  "org.svomz.apps.koobz.board.application",
  "org.svomz.apps.koobz.board.ports.adapters.rest"
})
@EnableTransactionManagement
@EnableJpaRepositories(repositoryFactoryBeanClass = KanbanRepositoryFactoryBean.class, basePackages = "org.svomz.apps.koobz.board.domain.model")
@EntityScan(basePackages = "org.svomz.apps.koobz.board.domain.model")
public class KoobzBoardApplication {

    public static void main(String... args) {
      SpringApplication.run(KoobzBoardApplication.class, args);
    }

}
