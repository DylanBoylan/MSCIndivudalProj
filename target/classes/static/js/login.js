$(document).ready(function () {
	console.log("login.js loaded...");
	
	$("#loginButton").off("click").on("click", function (e) {
        e.preventDefault();
        const email = $("#loginEmail").val();
        const password = $("#loginPassword").val();
		
		$.ajax({
		    url: "/api/auth/login", // Replace with actual backend URL if needed
		    type: "POST",
		    contentType: "application/json",
		    data: JSON.stringify({
		        email: email,
		        password: password
		    }),
		    dataType: "json",
		    success: function (response) {
		        alert("✅ Login Successful!");
				// Save token
				AuthAPI.saveToken(response.token);
		        Router.navigate("home");
		    },
		    error: function (xhr) {
		        alert("❌ Login Failed: " + (xhr.responseText || "Unknown error"));
		    }
		});
    });
	
	// Hit enter key to log in - easier than moving mouse to login button
	$("#loginEmail, #loginPassword").off("keypress").on("keypress", function (e) {
        // Check if the Enter key is pressed.
        if (e.key === "Enter" || e.which === 13) {
            e.preventDefault();
            $("#loginButton").click();
        }
	});
});