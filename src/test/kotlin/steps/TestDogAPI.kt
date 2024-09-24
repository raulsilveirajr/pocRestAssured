package steps

import io.cucumber.java.Before
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.module.jsv.JsonSchemaValidator
import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import org.apache.http.HttpStatus
import java.net.URL

import kotlin.random.Random

class TestDogAPI {
    var xApiKey = "YOUR_API_KEY";
    var breed : Map<String, Any> = mapOf();
    var image: String = "";

    lateinit var response : Response;

    @Before
    fun setup(): Unit {
        RestAssured.baseURI = "https://api.thedogapi.com"
        RestAssured.basePath = "/v1"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }

    @When("I send a request to the {string} endpoint")
    fun shouldGetBreeds(endpoint: String) {
        logStep("shouldGetBreeds - Processing the request to the endpoint: $endpoint")
        response  = RestAssured
            .given()
                .header("Content-Type", "application/json")
                .header("x-api-key", xApiKey)
            .get("breeds")
    }

    @Then("should return status code {string}")
    fun shouldReturnStatusCode(statusCode: String) {
        logStep("shouldReturnStatusCode - Should return status code: $statusCode")
        response.then()
            .statusCode(statusCode.toInt())

        val breedsList = JsonPath.with(response.body().asString()).getObject("", List::class.java)
        breed = breedsList[Random.nextInt(breedsList.size)] as Map<String, Any>
        image = (breed.get("image") as Map<*, *>).get("id").toString()
    }

    @Then("should receive a list of dog breeds")
    fun shouldReceiveListOfDogBreeds() {
        logStep("shouldReturnStatusCode - Should receive a list of dog breeds")
        response.then().statusCode(HttpStatus.SC_OK).body(
            JsonSchemaValidator.matchesJsonSchemaInClasspath(
                "json-schema/breed-response-schema.json"
            )
        )
    }
}

private fun logStep(step: String) {
    println(
        """
            =================================================================
            $step
            =================================================================
        """.trimIndent()
    )
}

private fun URL.findParameterValue(parameterName: String): String? {
    return query.split('&').map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }.firstOrNull{it.first == parameterName}?.second
}

