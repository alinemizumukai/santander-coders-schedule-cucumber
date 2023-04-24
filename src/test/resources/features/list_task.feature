Feature: List registered tasks

    Scenario: I have registered tasks
        Given I have a task registered
         | title            | description   | status    | userId    |
         | Lunch Meeting    | At Joe's Bar  | OPEN      | 1         |
        When I search the task by id
        Then The task is found in database
        And The response status is 200
