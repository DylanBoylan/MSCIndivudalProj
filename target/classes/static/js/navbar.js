$(document).ready(function () {
	// Load the navbar options only if the user is authenticated.
	if (AuthAPI.isLoggedIn()) {
    	updateNavbar();
	} else {
		$("#navbarNav").hide();
	}

	// Update the navbar so that only specific roles see their options.
	function updateNavbar() {
		hideAll(); // Hide all menu items first
		let role = AuthAPI.getUserRole();
		$("#homeNav").show(); // Home is always visible

		if (role === "ADMINISTRATOR") {
			$("#registerNav").show();
			$("#accountsNav").show();
		} else {
			switch (role) {
				case "COACH":
					$("#nextSessionNav").show();
					$("#playerTrainingNav").show();
					$("#seasonAnalyzerNav").show();
					// Fall through to Analyst permissions
				case "ANALYST":
					$("#getMatchesNav").show();
					$("#getActionsNav").show();
					$("#getPlayersNav").show();
					$("#getTeamsNav").show();
					$("#showMatchesNav").show();
					$("#showPlayerStatsNav").show();
					$("#goalsGraphNav").show();
					$("#pointsGraphNav").show();
					break;
				default:
					console.warn("Unknown role:", role);
			}
		}
	}

	// Hide all items initially
	function hideAll() {
		$("#homeNav, #registerNav, #accountsNav, #getMatchesNav, #getActionsNav, #getPlayersNav, #getTeamsNav, #showMatchesNav, #showPlayerStatsNav, #goalsGraphNav, #pointsGraphNav, #nextSessionNav, #playerTrainingNav, #seasonAnalyzerNav").hide();
	}

	// ✅ Fix: Redirect to `/#login` instead of `/login.html`
	$("#logoutButton").click(function (event) {
		event.preventDefault(); // Prevent default <a> behavior

		// ✅ Clear authentication tokens/session
		if (typeof AuthAPI.logout === "function") {
			AuthAPI.logout(); // Call your logout function
		}
		window.localStorage.clear(); // Clear local storage
		document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"; // Clear cookies

		// ✅ Redirect to `/#login`
		window.location.href = "/#login"; 
	});
});
