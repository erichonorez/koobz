Feature: Rename a stage

  In order to be able to change to meaning of the stage on my board
  As a user
  I want to edit the name of the stage

  Rules:
  * the name of the stage should be between 1 and 255 char

  Scenario: Update the stage name
    Given I am on a board
    And a stage exists
    When rename the stage with "TODO"
    Then the name of the stage is changed

  Scenario Outline: Do not update stage with an invalid name
    Given I am on a board 
    And a stage exists
    When I edit the name of the stage with <invalid_name>
    Then I get an error message
    And the name of the stage is not changed

    Examples:
    |  |
    | todotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodotodo |
