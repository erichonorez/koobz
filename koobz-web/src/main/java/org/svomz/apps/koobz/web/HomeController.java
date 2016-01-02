package org.svomz.apps.koobz.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by eric on 02/01/16.
 */
@Controller
public class HomeController {

  @RequestMapping("/")
  public String index() {
    return "index";
  }

}
