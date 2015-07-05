Feature: Add a stage

  In order to modelise the several stages of my process
  As a user
  I want to be able to add a new stage to a board

  Scenario: Create a new stage
    Given I am on a board
    When I create a new stage
    Then I see the stage on the board
    And the name of the stage is "untitled"