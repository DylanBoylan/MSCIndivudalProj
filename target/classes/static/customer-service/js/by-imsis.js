$(document).ready(function () {
    console.log("✅ by-imsi.js script loaded!");

    $("#fetchFailures").off("click").on("click", function () {
        console.log("🔄 Fetch Failures button clicked!");
        fetchFailuresByImsi();
    });
	
	// -- FUNCTIONS --
	function fetchFailuresByImsi() {
	    let imsi = $("#imsiInput").val().trim();
	    let resultDiv = $("#failuresResult");
	
	    resultDiv.html(""); // Clear previous results
	
	    if (!imsi) {
	        resultDiv.html(byImsiTemplates.failureMessage("❌ Please enter a valid IMSI.</div>"));
	        return;
	    }
	
	    console.log(`📡 Sending API request: /api/call-failures/by-imsi?imsi=${imsi}`);
	
	    $.ajax({
	        url: `/api/call-failures/by-imsi?imsi=${encodeURIComponent(imsi)}`,
	        method: "GET",
	        headers: {
	            "Authorization": `Bearer ${AuthAPI.getToken()}`,
	            "Content-Type": "application/json"
	        },
			success: displayFailures,
	        error: function (xhr) {
	            console.error("❌ Error fetching failures:", xhr);
	            resultDiv.html(byImsiTemplates.failureMessage("❌ Failed to fetch failures."));
	        }
	    });
	}
	
	function displayFailures(data) {
	    let failures = data.failures || [];
	
	    let resultDiv = $("#failuresResult");
	    let tableWrapper = $("#tableWrapper");
	    let failuresCount = $("#failuresCount");
		
		let numFailures = failures.length;
		if (numFailures > 0) {
			let tableHtml = "";
			failures.forEach(failure => {
				tableHtml += byImsiTemplates.failureEntry(failure) + "\n";
			});
			$("#failuresTable tbody").html(tableHtml);
		    failuresCount.html(byImsiTemplates.failuresFoundMessage(numFailures)).show();
	        tableWrapper.show(); // ✅ Show table when results exist
	
	        if (!$.fn.DataTable.isDataTable("#failuresTable")) {
	            $("#failuresTable").DataTable({
	                paging: false,
	                scrollY: "400px",
	                scrollCollapse: true,
	                order: [[3, "desc"]]
	            });
	        }
	    } else {
	        resultDiv.html(byImsiTemplates.successMessage("✅ No failures found for this IMSI."));
	        tableWrapper.hide(); // ❌ Hide table if no data
	        failuresCount.hide();
	    }
	}
	
	
	
	// Templates
	const byImsiTemplates = {
		failureEntry: function(failure) {
			return `<tr>
		                <td>${failure.eventId}</td>
		                <td>${failure.causeCode}</td>
		                <td>${failure.description}</td>
		                <td>${failure.dateTime}</td>
		            </tr>`;
		},
		
		failuresFoundMessage: function(failureCount) {
			return `📋 Total Failures Found: <strong>${failureCount}</strong>`;
		},
		
		successMessage: function (message) {
			return `<div class='alert alert-success'>
						${message}
					</div>`;
		},
		
		failureMessage: function (message) {
			return `<div class='alert alert-danger'>
						${message}
					</div>`;
		}
	}
});