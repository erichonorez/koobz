Feature: Edit work item
	In order to update a work item text
	As a user
	I want to edit the work item text
	
	Rules:
	* The text of a work item should be between 1 and 255
	
	Scenario: Update the text of a work item with input
		Given I am on a board
		And a stage exists
		And a work item exits
		When I edit the text of the work item with "Hello, World"
		Then the text of the work item is updated
		
	Scenario: Do not update the text of a work item with an invalid input
		Given I am on a board
		And a stage exists
		And a work item exists
		When I edit the text of the work item with <invalid_text>
		Then I get an error message
		And the text of the work item is not updated
		
		