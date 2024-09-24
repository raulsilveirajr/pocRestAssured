Feature: The Dog API

 Scenario Outline: Get the list of dog breeds
    When I send a request to the "<ENDPOINT>" endpoint
    Then should return status code "<STATUS_CODE>"
    Then should receive a list of dog breeds

   Examples:
     | ENDPOINT | STATUS_CODE |
     | breeds   | 200         |
