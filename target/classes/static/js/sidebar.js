// js/sidebar.js
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
			    case "NETWORK_MANAGEMENT_ENGINEER":
			        $("#summaryNav").show();
			        $("#countByImsiNav").show();
					$("#top10operatorsNav").show();
					$("#top10imsisNav").show();
					// Fall through
			    case "SUPPORT_ENGINEER":
			        $("#failuresNav").show();
			        $("#imsisNav").show();
					// Fall through
			    case "CUSTOMER_SERVICE":
			        $("#byimsisNav").show();
			        $("#byimsitimeNav").show();
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
		$("#byimsisNav").hide();
	    $("#imsisNav").hide();
		$("#failuresNav").hide();
	    $("#countByImsiNav").hide();
	    $("#summaryNav").hide();
		$("#byimsitimeNav").hide();
		$("#top10imsisNav").hide();
	}
  
	// Used only for debugging purposes
	function showAll() {
		$("#homeNav").hide();
		$("#fileUploadNav").show();
		$("#registerNav").show();
	    $("#accountsNav").show();
		$("#byimsisNav").show();
	    $("#imsisNav").show();
		$("#failuresNav").show();
	    $("#countByImsiNav").show();
	    $("#summaryNav").show();
		$("#byimsitimeNav").show();
		$("#top10imsisNav").show();

	}
	
});
