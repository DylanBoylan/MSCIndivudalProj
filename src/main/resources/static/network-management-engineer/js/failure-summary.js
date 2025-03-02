$(document).ready(function () {
	// Event Listener for search button
   $("#searchButton").off("click").on("click", function () {
	    let phoneModel = $("#phoneModelInput").val().trim();
		let resultDiv = $("#failuresResult");
		resultDiv.empty(); // Clear previous results
	    $("#errorMessage").hide();
	    // Hide the table container immediately on a new search
	    $("#tableWrapper").hide();
	    
	    if (!phoneModel) {
	        $("#errorMessage").html("⚠️ Please enter a phone model.").show();
	        return;
	    }
		
		console.log(`📡 Sending API request: /api/call-failures/summary?phoneModel=${encodeURIComponent(phoneModel)}`);
        
		$.ajax({
            url: `/api/call-failures/summary?phoneModel=${encodeURIComponent(phoneModel)}`,
            method: "GET",
            headers: {
                "Authorization": `Bearer ${AuthAPI.getToken()}`,
                "Content-Type": "application/json"
            },
            success: function (data) {
				console.log("📊 Failure summary received:", data.failures);
				let failures = data.failures || [];
				let resultDiv = $("#failuresResult");
			    let tableWrapper = $("#tableWrapper");
				
				$("#errorMessage").hide();
		        resultDiv.empty();
					
                if (failures.length > 0) {
                    let tableHtml = failures.map(failure => `
                        <tr>
                            <td>${failure.eventId}</td>
                            <td>${failure.causeCode}</td>
                            <td>${failure.occurrences}</td>
                        </tr>
                    `).join("");
					$("#failuresTable tbody").html(tableHtml);
					tableWrapper.show(); // ✅ Show table when results exist
					if (!$.fn.DataTable.isDataTable("#failuresTable")) {
			            $("#failuresTable").DataTable({
			                paging: false,
			                scrollY: "400px",
			                scrollCollapse: true,
			                order: [[2, "desc"]]
			            });
			        }
                } else {
                    $("#errorMessage").html("✅ No failures found for this phone model.").show();
					tableWrapper.hide(); // ❌ Hide table if no data
                }
            },
            error: function (xhr) {
                console.error("❌ Error fetching failure summary:", xhr);
                $("#errorMessage").html("❌ Failed to fetch failure summary.").show();
            }
        });
    });
});
