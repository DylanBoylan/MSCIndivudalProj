// js/components/navbar.js
$(document).ready(function () {
	// Always load the navbar
	if (AuthAPI.isLoggedIn()) {
		const userName = AuthAPI.getUsername() || "User";
		$("#loggedUserName").text(userName);
	    // Unhide the authentication info on the right.
	    $("#welcomeText").removeClass("d-none");
	    $("#logoutButton").removeClass("d-none");
	} else {
	    // Ensure that if not authenticated, the auth info remains hidden.
	    $("#welcomeText").addClass("d-none");
	    $("#logoutButton").addClass("d-none");
	}
	
	// Remove previous listener before adding new one -- avoids duplication
	$(document).off("click", "#logoutButton").on("click", "#logoutButton", function () {   
		AuthAPI.removeToken();
		alert("✅ Logged out!"); 
		location.reload(); // Refresh to update navbar
	});
});
	
