package org.svomz.org.apps.koobz.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.stereotype.Controller;

/**
 * Created by eric on 02/01/16.
 */
@SpringBootApplication
@EnableZuulProxy
@Controller
public class KoobzApplication {

  public static void main(String[] args) {
    SpringApplication.run(KoobzApplication.class, args);
  }

}
