$(document).ready(function () {
	// Load the sidebar only if the user is authenticated.
	if (AuthAPI.isLoggedIn()) {
    	updateSidebar();
	} else {
		$("#sidebar").hide();
	}

	// Update the sidebar so that only admin users see admin-only options.
	function updateSidebar() {
		hideAll();
		let role = AuthAPI.getUserRole();
		$("#homeNav").show();
		if (role == "ADMINISTRATOR") {
	    	// Show both the "Upload File" and "Register" options for admins.
			$("#homeNav").show();
	    	$("#fileUploadNav").show();
	    	$("#registerNav").show();
			$("#accountsNav").show();
	    } else {
			switch (AuthAPI.getUserRole()) {
			    case "COACH":
			        $("#nextSessionNav").show();
					$("#playerTrainingNav").show();
					$("#seasonAnalyzerNav").show();
					// Fall through
			    case "ANALYST":
			        $("#getMatchesNav").show();
					$("#getActionsNav").show();
					$("#getPlayersNav").show();
					$("#getTeamsNav").show();
					$("#goalsGraphNav").show();
					$("#pointsGraphNav").show();
			        break;
			    default:
			        console.warn("Unknown role:", userRole);
			}
		}
	}
  
	function hideAll() {
		$("#homeNav").hide();
		$("#fileUploadNav").hide();
		$("#registerNav").hide();
	    $("#accountsNav").hide();
		$("#getActionsNav").hide();
	    $("#nextSessionNav").hide();
		$("#playerTrainingNav").hide();
		$("#getMatchesNav").hide();
		$("#getPlayersNav").hide();
		$("#getTeamsNav").hide();
		$("#goalsGraphNav").hide();
		$("#pointsGraphNav").hide();
		$("#seasonAnalyzerNav").hide();
	}
	
});

