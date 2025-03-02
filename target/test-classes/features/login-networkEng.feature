Feature: Log in as NETWORK_MANAGEMENT_ENGINEER
	
	Background: Ensure Network Engineer Account exists
    * def adminResponse = call read('login-admin.feature')
    * def adminToken = adminResponse.token
 
    Given url baseUrl + '/api/users'
    * header Authorization = 'Bearer ' + adminToken
    And request
    """
    {
      "email": "networkEng@networksys.com",
      "password": "NetworkEng123!",
      "role": "NETWORK_MANAGEMENT_ENGINEER"
    }
    """
    When method post
    Then status 201
    
  Scenario: Authenticate Network Engineer User and Get Token
    Given url baseUrl + '/api/auth/login'
    * header Content-Type = 'application/json'
    And request { email: "networkEng@networksys.com", password: "NetworkEng123!" }
    When method post
    Then status 200
    * def token = response.token
