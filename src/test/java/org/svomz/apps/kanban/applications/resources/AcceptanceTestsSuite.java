package org.svomz.apps.kanban.applications.resources;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by eric on 09/07/15.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  BoardResourceAcceptanceTest.class,
  StageResourceAcceptanceTest.class,
  WorkItemResourceAcceptanceTest.class
})
public class AcceptanceTestsSuite {

}
