package br.ada.projetoschedulecucumber;

import br.ada.projetoschedulecucumber.model.Task;
import br.ada.projetoschedulecucumber.model.TaskStatus;
import br.ada.projetoschedulecucumber.model.User;
import br.ada.projetoschedulecucumber.util.DatabaseUtil;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class TaskStepDefinition {

    private Gson gson = new Gson();
    private Task task = null;
    private RequestSpecification request = RestAssured.given()
            .baseUri("http://localhost:8080/api")
            .contentType(ContentType.JSON);
    private Response response;

    @Before
    public void setup(){
        request.header(HttpHeaders.ACCEPT_LANGUAGE, "en-US");
    }

    @Given("I have a new task")
    public void iHaveANewTask(DataTable data) {
        task = createTaskFromDataTable(data);
    }

    @Given("I have a task registered")
    public void iHaveTaskRegistered(DataTable data) throws SQLException {
        task = createTaskFromDataTable(data);
        DatabaseUtil.insertTask(task);
    }

    @When("I register the task")
    public void registerNewTask(){
        String jsonBody = "{\"title\": \"" + task.getTitle() + "\", \"description\": \"" + task.getDescription() + "\", \"status\": \"" + task.getStatus() + "\", \"user\": { \"id\": " + task.getUser().getId() + " } }";
        response = request.body(jsonBody).when().post("/tasks");
    }

    @When("I search the task by id")
    public void searchTheTask() {
        response = request.when().get("/tasks/" + task.getId());
    }

    @Then("The task is found in database")
    public void searchInDatabase() throws SQLException {
        Long id = task.getId();
        if (id == null) {
           id = Long.valueOf(response.jsonPath().get("id").toString());
        }
        Task found = DatabaseUtil.findTaskById(id);
        assertNotNull(found);
    }

    @And("The response status is {int}")
    public void statusEquals(Integer status) {
        response.then().statusCode(status);
    }

    private Task createTaskFromDataTable(DataTable data) throws RuntimeException {
        Task task = new Task();
        data.asMaps().forEach( it -> {
            task.setTitle(it.get("title"));
            task.setDescription(it.get("description"));
            task.setStatus(TaskStatus.valueOf(it.get("status")));
            task.setClosedAt(null);
            User user;
            try {
                user = DatabaseUtil.findUserById(Long.valueOf(it.get("userId")));
                if (user == null) {
                    user = new User();
                    user.setId(1L);
                    user.setName("Callie Torres");
                    user.setUsername("ctorres");
                    user.setPassword("ct-123");
                    DatabaseUtil.insertUser(user);
                }
                task.setUser(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return task;
    }

}
