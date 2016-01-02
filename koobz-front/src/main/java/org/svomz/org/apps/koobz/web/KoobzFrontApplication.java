package org.svomz.org.apps.koobz.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by eric on 02/01/16.
 */
@SpringBootApplication
@EnableZuulProxy
public class KoobzFrontApplication {

  public static void main(String[] args) {
    SpringApplication.run(KoobzFrontApplication.class, args);
  }



}
