package br.ada.projetoschedulecucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
        plugin = {"pretty", "html:allure-results/cucumber-reports.html"}
)
class ProjetoSchedulerCucumberApplicationTests {
}
