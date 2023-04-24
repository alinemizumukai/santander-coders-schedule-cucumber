Feature: Register new tasks

    Scenario: I want to register new a task
        Given I have a new task
         | title                | description   | status    | userId    |
         | Orthopedic Surgery   | Mr Steven     | OPEN      | 1         |
        When I register the task
        Then The task is found in database
        And The response status is 201