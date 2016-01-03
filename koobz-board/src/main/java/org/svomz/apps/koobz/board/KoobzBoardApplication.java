package org.svomz.apps.koobz.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.svomz.apps.koobz.board.infrastructure.domain.KanbanRepositoryFactoryBean;

/**
 * Created by eric on 06/07/15.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableTransactionManagement
@EnableJpaRepositories(repositoryFactoryBeanClass = KanbanRepositoryFactoryBean.class, basePackages = "org.svomz.apps.koobz.board.domain")
public class KoobzBoardApplication {

    public static void main(String... args) {
      SpringApplication.run(KoobzBoardApplication.class, args);
    }

}