Feature: Rename a board
  In order to give a meaning to my board
  As a user
  I want to be able to change to name of my board

  Rules:
  - The name of the board should be between 1 and 255 char

  Scenario: Update the name of the board
    Given I am on a board
    When I rename the board with "Sprint 1"
    Then the name of the stage is changed

  Scenario Outline: Do not update the name of the board with an invalid name
    Given I am on a board
    When I rename the board with <invalid_name>
    Then I get an error message
    And the name is not changed

    Examples:
    | |
    | todotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodo |