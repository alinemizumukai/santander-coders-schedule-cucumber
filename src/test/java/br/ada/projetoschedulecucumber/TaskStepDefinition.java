package br.ada.projetoschedulecucumber;

import br.ada.projetoschedulecucumber.model.Task;
import br.ada.projetoschedulecucumber.model.TaskStatus;
import br.ada.projetoschedulecucumber.model.User;
import br.ada.projetoschedulecucumber.util.DatabaseUtil;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    private Gson gson = new GsonBuilder().serializeNulls().create();
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
        String jsonBody = gson.toJson(task);
        response = request.body(jsonBody).when().post("/tasks");
    }

    @When("I search the task by id")
    public void searchTheTask() {
        response = request.when().get("/tasks/" + task.getId());
    }

    @Then("The task is found in database")
    public void searchInDatabase_Success() throws SQLException {
        var responseId = response.jsonPath().get("id");
        if (responseId != null) {
           task.setId(Long.valueOf(responseId.toString()));
        }
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertNotNull(found);
    }

    @Then("The task is not found in database")
    public void searchInDatabase_notFound() throws SQLException {
        var responseId = response.jsonPath().get("id");
        if (responseId != null) {
            task.setId(Long.valueOf(responseId.toString()));
        }
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertNull(found);
    }

    @And("The task status is {string}")
    public void taskStatusEquals(String taskStatus) throws SQLException {
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertEquals(taskStatus, found.getStatus().name());
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
            User user = null;
            if (it.get("userId") != null){
                try {
                    user = DatabaseUtil.findUserById(Long.valueOf(it.get("userId")));
                    if (user == null && it.get("userId").equals("1")) {
                        user = new User();
                        user.setName("Callie Torres");
                        user.setUsername("ctorres");
                        user.setPassword("ct-123");
                        DatabaseUtil.insertUser(user);
                    } else if (user == null && it.get("userId").equals("2")){
                        user = new User();
                        user.setName("Addison Montgomery");
                        user.setUsername("addie");
                        user.setPassword("am-123");
                        DatabaseUtil.insertUser(user);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            task.setUser(user);
        });
        return task;
    }

}
