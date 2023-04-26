Feature: Register new tasks

    Scenario: I want to register new a task
        Given I have a new task
         | title                | description   | status    | userId    |
         | Orthopedic Surgery   | Mr Steven     | OPEN      | 1         |
        When I register the task
        Then The task is found in database
        And The task status should be "OPEN"
        And The response status is 201

    Scenario: I want to register new a task with CLOSE status - There's a bug in Api that allows to create tasks with close status
        Given I have a new task
         | title                | description       | status     | userId    |
         | Neonatal Surgery     | Ms Tia            | CLOSE      | 2         |
        When I register the task
        Then The task is not found in database
        And The response status is 400

    Scenario: I want to register new a task without user - There's a bug in Api that allows to create tasks without userId
        Given I have a new task
         | title                    | description       | status     | userId    |
         | Take Sophia to daycare   | Before surgery    | OPEN       |           |
        When I register the task
        Then The task is not found in database
        And The response status is 400

    Scenario: I want to register new a task without title
        Given I have a new task
         | description       | status     | userId    |
         | After surgery     | OPEN       | 2         |
        When I register the task
        Then The task is not found in database
        And The response status is 400

    Scenario: I want to register new a task without description
        Given I have a new task
         | title                            | status    | userId    |
         | Lecture at Grey Sloan Memorial   | OPEN      | 2         |
        When I register the task
        Then The task is found in database
        And The task status should be "OPEN"
        And The response status is 201