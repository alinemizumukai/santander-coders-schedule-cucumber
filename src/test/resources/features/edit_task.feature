Feature: Edit existing tasks

    Scenario: I want to edit or add a description in an existing task
        Given I have a task registered
         | title                                | status    | userId    |
         | Lecture about Orthopedic Surgeries   | OPEN      | 1         |
        When I update the task
         | description      | Implantation of synthetic bone substitutes and bone graft implants  |
        Then The task is found in database
        And The task description should be "Implantation of synthetic bone substitutes and bone graft implants"
        And The response status is 200

    Scenario: I wanna change the user responsible for an existing task
        Given I have a task registered
         | title                | description                       | status    | userId    |
         | Committee meeting    | Approve the new chief of surgery  | OPEN      | 1         |
        When I update the task
         | userId      | 2        |
        Then The task is found in database
        And The userId should be 2
        And The response status is 200

    Scenario: I want to finish a task
        Given I have a task registered
         | title                | description          | status    | userId    |
         | Therapy Appointment  | Dr Katharine Wyatt   | OPEN      | 1         |
        When I update the task
         | status      | CLOSE        |
        Then The task is found in database
        And The task status should be "CLOSE"
        And The close date should be found
        And The response status is 200

    Scenario: I want to reopen a closed task
        Given I have a task registered
         | title                | description          | status    | userId    |
         | On-call shift        | On April 25          | CLOSE      | 2         |
        When I update the task
         | status      | OPEN        |
        Then The task is found in database
        And The task status should be "CLOSE"
        And The close date should be found
        And The response status is 422
        And The error message should be "Task already close"