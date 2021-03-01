package com.codenjoy.dojo.cucumber;

import com.codenjoy.dojo.services.dao.Registration;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {

    @Autowired
    private WebDriverWrapper web;

    @Autowired
    private Registration registration;

    @When("Login page opened in browser")
    public void loginPage() {
        web.open("/login");
    }

    @When("Try to login as {string} with {string} password in game {string}")
    public void login(String email, String password, String game) {
        web.text("#email input", email);
        web.text("#password input", password);
        web.select("#game select", game);
        web.click("#submit-button");
    }

    @Then("See {string} login error")
    public void login(String error) {
        assertEquals(error, web.element("#error-message").getText());
    }

    @When("Press register button")
    public void pressRegisterButton() {
        web.element("#register-button").click();
    }

    @When("Try to register with: name {string}, email {string}, " +
            "password {string}, city {string}, " +
            "tech skills {string}, company {string}, " +
            "experience {string}, game {string}")
    public void tryToRegister(String name, String email,
                              String password, String country,
                              String techSkills, String company,
                              String experience, String game)
    {
        web.text("#readableName input", name);
        web.text("#email input", email);
        web.text("#password input", password);
        web.text("#passwordConfirmation input", password);
        web.text("#data1 input", country);
        web.text("#data2 input", techSkills);
        web.text("#data3 input", company);
        web.text("#data4 input", experience);
        web.select("#game select", game);
        web.click("#submit-button");
    }

    @SneakyThrows
    @Then("On game board with url {string}")
    public void onGameBoard(String url) {
        url = replaceAll(url);

        assertEquals(url, web.url());
        Thread.sleep(2000);
    }

    public String replaceAll(String url) {
        url = replace(url, "<PLAYER_ID>", "playerId");
        url = replace(url, "<CODE>", "code");
        return url;
    }

    public String replace(String data, String key, String attribute) {
        if (data.contains(key)) {
            String playerId = web.get("#settings", attribute);
            data = data.replaceAll(key, playerId);
        }
        return data;
    }

    @Given("Clean all registration data")
    public void cleanAllRegistrationData() {
        registration.removeAll();
    }
}