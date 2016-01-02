Feature: Delete a stage
  In order to keep my board clean
  As a user
  I want to be able to delete useless stages

  Scenario: Delete empty stage
    Given I am on a board
    And an empty stage exists
    When I delete the the empty stage
    Then the stage is not on the board anymore

  Scenario: Delete stage containing work items
    Given I am on a board
    And a stage exists with work items
    When I delete the stage
    Then I get an error message
    And the stage with its work items is still on the board