$(document).ready(function () {
    // Load the accounts as soon as the accounts page is loaded.
    loadAccounts();

	// ******** EVENT LISTENERS **********
    // Attach event listener for the edit form submission.
    $("#editUserForm").off("submit").on("submit", function (e) {
        e.preventDefault();
        updateUser();
    });
	
	// Close the modal when the "Close" button is clicked
	$('#closeEditModal').off("submit").on('click', function() {
	    $('#editUserModal').hide();
	});
	
	// Event Listeners for user buttons
	// Edit user button
	$(document).off("click", ".editUserBtn").on("click", ".editUserBtn", function () {
    	const email = $(this).data("email");
    	openEditModal(email);
	});
	
	// Delete user button
	$(document).off("click", ".deleteUserBtn").on("click", ".deleteUserBtn", function () {
    	const email = $(this).data("email");
    	if (confirm("Are you sure you want to delete this account?")) {
        	deleteUser(email);
    	}
	});

	
	// -- FUNCTIONS --
	function loadAccounts() {
	    // Clear any previous status and table rows.
	    $("#accountsStatus").html("");
	    $("#accountsTable tbody").empty();
	
	    $.ajax({
	        url: "/api/users",
	        method: "GET",
	        headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        success: displayUsers,
	        error: function (xhr) {
	            console.error("Error loading accounts:", xhr.responseText);
	            displayErrorMessage("Failed to load accounts.");
	        }
	    });
	}
	
	function displayErrorMessage(errorMessage) {
		$("#accountsStatus").html(`<div class='alert alert-danger'>${errorMessage}</div>`);
	}
	
	function displayUsers(users) {
		console.log("Displaying users");
		users.forEach(user => {
	        const row = user_templates.userCard(user);
	        $("#accountsTable tbody").append(row);
	    });
	};
	
	
	function openEditModal(email) {
	    // Fetch user details from the backend.
	    $.ajax({
	        url: "/api/users/" + encodeURIComponent(email),
	        method: "GET",
	        headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        success: populateEditModal,
	        error: function (xhr) {
	            alert("Failed to fetch user details: " + xhr.responseText);
	        }
	    });
	}
	
	function populateEditModal(user) {
	    $("#editEmail").val(user.email);
	    $("#editRole").val(user.role);
	    $("#editPassword").val(""); // Clear the password field
	
	    if (user.email.toLowerCase() === "admin@networksys.com") {
	        $("#editEmail").prop("disabled", true); // Disable email editing
	        $("#editRole").prop("disabled", true); // Disable role editing
	    } else {
	        $("#editEmail").prop("disabled", false); // Enable email editing
	        $("#editRole").prop("disabled", false); // Enable role editing
	    }
	
	    // Show the modal
		$("#editUserModal").show();
	}
	
	
	// Update User
	function updateUser() {
	    const email = $("#editEmail").val().toLowerCase();
	    const role = $("#editRole").val();
	    const password = $("#editPassword").val().trim();
	
	    // Determine what to send in the request based on the user
	    let data = {};
	    if (email.toLowerCase() === "admin@networksys.com") {
	        if (!password) {
	            alert("⚠️ Please enter a new password.");
	            return;
	        }
	        data = { password: password }; // Only update password
	    } else {
	        data = { email: email, password: password, role: role }; // Update all fields
	    }
	
	    $.ajax({
	        url: "/api/users/" + encodeURIComponent(email),
	        method: "PUT",
	        contentType: "application/json",
			headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        data: JSON.stringify(data),
	        success: function () {
	            alert("✅ User updated successfully!");
	            document.getElementById('editUserModal').style.display = 'none'; // Hide modal
	            loadAccounts(); // Refresh the user list
	        },
	        error: function (xhr) {
	            alert("❌ Failed to update user: " + xhr.responseText);
	        }
	    });
	}
	
	// Delete User
	function deleteUser(email) {
	    const protectedEmail = "admin@networksys.com"; // Hardcoded user that cannot be deleted
	
	    if (email === protectedEmail) {
	        alert("❌ This user cannot be deleted!");
	        return; // Stop the function from proceeding
	    }
	
	    $.ajax({
	        url: "/api/users/" + encodeURIComponent(email),
	        method: "DELETE",
			headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        success: function () {
	            alert("✅ User deleted successfully!");
	            loadAccounts(); // Refresh the table.
	        },
	        error: function (xhr) {
	            alert("❌ " + xhr.responseText);
	        }
	    });
	}

	// Templates
	const user_templates = {
		userCard: function (user) {
			return `<tr>
				      <td>${user.email}</td>
				      <td>${user.role}</td>
				      <td>
				        <button class=" btn btn-warning btn-sm editUserBtn" data-email="${user.email}">Edit</button>
				        <button class="btn btn-danger btn-sm deleteUserBtn" data-email="${user.email}">Delete</button>
				      </td>
				    </tr>`;
		}
	}
});
