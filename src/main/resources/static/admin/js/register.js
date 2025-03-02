$(document).ready(function () {
	// Event listener for register button
	$("#registerButton").off("click").on("click", function () {
		const email = $("#registerEmail").val().trim();
		const password = $("#registerPassword").val().trim();
		const role = $("#registerRole").val();
		registerUser(email, password, role);		
	});

	// FUNCTIONS ---
	function registerUser(email, password, role) {
		if (!email || !password || !role) {
	        $("#registerStatus").html(
	            `<div class="alert alert-warning"> ⚠️ Please fill in all fields. </div>`
	        );
	        return;
	    }
		
		$.ajax({
		    url: "/api/users",
		    method: "POST",
		    headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
		    contentType: "application/json",  // <-- Set JSON format
		    dataType: "json",
		    data: JSON.stringify({
		        email: email,
		        password: password,
		        role: role
		    }),
		    success: function () {
		        $("#registerStatus").html(registerTemplates.registerSuccess(email, role));
		    },
		    error: registrationFailure
		});
	
	}
	
	function registrationFailure(xhr) {
	    let responseText = xhr.responseText;
	    let errorMessage = "Something went wrong.";
	
	    try {
	        let contentType = xhr.getResponseHeader('Content-Type') || "";
	
	        if (contentType.includes('application/json')) {
	            let response = JSON.parse(responseText);
	            errorMessage = Object.values(response)[0]; // Extracts the first error message
	        } else {
	            errorMessage = responseText;
	        }
	
	        // Special case: User already exists
	        if (xhr.status === 409) { 
	            errorMessage = `Email already exists`;
	        }
	    } catch (e) {
	        console.error('Error parsing response:', e);
	    }
	
	    $("#registerStatus").html(registerTemplates.registerFailure(errorMessage));
	};
	
	
	
	// Templates
	let registerTemplates = {
		registerSuccess: function (email, role) {
			return `<div class="alert alert-success">✅ 
						${email} has been given the ${role} role.
					</div>`;
		},
		registerFailure: function (errorMessage) {
			return `<div class="alert alert-danger"> ❌ 
						${errorMessage} 
					</div>`;
		}
	}
});