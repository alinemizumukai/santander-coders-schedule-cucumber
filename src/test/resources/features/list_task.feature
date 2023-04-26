Feature: List registered tasks

    Scenario: I have registered tasks
        Given I have a task registered
         | title            | description   | status    | userId    |
         | Lunch Meeting    | At Joe's Bar  | OPEN      | 1         |
        When I search the task by id
        Then The task is found in database
        And The response status is 200

    Scenario: I don't have a task registered
        Given I don't have a task registered
         | id            | 99         |
        When I search the task by id equals 99
        Then The task is not found in database
        And The response status is 404
        And The error message should be "Resource was not found"