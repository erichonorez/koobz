Feature: Add work item
  In order to track item on the board
  As a user
  I want to create new items

  Scenario: Add a new work item
    Given I am on a board
    And a stage exists
    When I create a work item in the existing stage
    Then I see the work item in the stage