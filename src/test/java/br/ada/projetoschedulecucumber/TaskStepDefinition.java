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
import java.time.LocalDate;

public class TaskStepDefinition {

    private Gson gson = new GsonBuilder().serializeNulls().create();
    private Task task = null;
    private RequestSpecification request = RestAssured.given()
            .baseUri("http://localhost:8080/api")
            .contentType(ContentType.JSON);
    private Response response;

    @Before
    public void setup() throws SQLException {
        createUsersForTests();
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

    @When("I update the task")
    public void updateMovie(DataTable data) throws SQLException {
        task = DatabaseUtil.findTaskById(task.getId());
        String title = data.asMap().get("title");
        if (title != null) {
            task.setTitle(title);
        }
        String description = data.asMap().get("description");
        if (description != null) {
            task.setDescription(description);
        }
        String userId =  data.asMap().get("userId");
        if(userId != null){
            task.setUser(DatabaseUtil.findUserById(Long.valueOf(userId)));
        }
        String status = data.asMap().get("status");
        if (status != null) {
            task.setStatus(TaskStatus.valueOf(status));
        }
        if (status != null && status.equals("CLOSE")) {
            task.setClosedAt(LocalDate.now());
        }
        String jsonBody = gson.toJson(task);
        task.setClosedAt(null);
        response = request.body(jsonBody).when().put("/tasks/" + task.getId());
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

    @And("The task status should be {string}")
    public void taskStatusEquals(String taskStatus) throws SQLException {
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertEquals(taskStatus, found.getStatus().name());
    }

    @And("The task description should be {string}")
    public void taskDescriptionEquals(String description) throws SQLException {
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertEquals(description, found.getDescription());
    }

    @And("The userId should be {long}")
    public void taskUserEquals(Long userId) throws SQLException {
        Task found = DatabaseUtil.findTaskById(task.getId());
        assertEquals(userId, found.getUser().getId());
    }

    @And("The response status is {int}")
    public void statusEquals(Integer status) {
        response.then().statusCode(status);
    }

    private void createUsersForTests() throws SQLException {
        User user1 = DatabaseUtil.findUserById(1L);
        if (user1 == null) {
            user1 = new User();
            user1.setName("Callie Torres");
            user1.setUsername("ctorres");
            user1.setPassword("ct-123");
            DatabaseUtil.insertUser(user1);
        }
        User user2 = DatabaseUtil.findUserById(2L);
        if (user2 == null) {
            user2 = new User();
            user2.setName("Addison Montgomery");
            user2.setUsername("addie");
            user2.setPassword("am-123");
            DatabaseUtil.insertUser(user2);
        }
    }

    private Task createTaskFromDataTable(DataTable data) throws RuntimeException {
        Task task = new Task();
        data.asMaps().forEach( it -> {
            task.setTitle(it.get("title"));
            task.setDescription(it.get("description"));
            task.setStatus(TaskStatus.valueOf(it.get("status")));
            if (it.get("status").equals("OPEN"))
                task.setClosedAt(null);
            User user = null;
            if (it.get("userId") != null){
                try {
                    user = DatabaseUtil.findUserById(Long.valueOf(it.get("userId")));
                    if (user == null) {
                        user = new User();
                        user.setName("Arizona Robins");
                        user.setUsername("arobins");
                        user.setPassword("ar-123");
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
