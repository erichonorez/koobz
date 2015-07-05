package org.svomz.apps.kanban.presentation.resources;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber"},
    features = "src/test/resources/features",
    snippets = SnippetType.CAMELCASE
)
public class KanbanAcceptanceTest {

}
