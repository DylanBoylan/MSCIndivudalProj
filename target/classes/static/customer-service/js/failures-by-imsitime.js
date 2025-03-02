$(document).ready(function () {
    console.log("✅ failures-by-imsitime.js script loaded!");

	// Event listener for "Get failures" button
    $("#fetchFailures").off("click").on("click", function () {
        console.log("🔄 Fetch Failures button clicked!");
        fetchFailures();
    });
	
	
	function fetchFailures() {
	    console.log("🔄 Fetch Failures button clicked!");
	
		// Values
	    let imsi = $("#imsi").val().trim();
	    let startDate = formatDateTime($("#startDate").val().trim());
	    let endDate = formatDateTime($("#endDate").val().trim());
	
	    if (!imsi || !startDate || !endDate) {
	        $("#failuresResult").html(failuresByImsiTimeTemplates.failureMessage("❌ Please enter an IMSI and both dates.</div>"));
	        return;
	    }
		
		// Elements
	    let resultDiv = $("#failuresResult");
	    let tableWrapper = $("#tableWrapper");
	    let failuresCount = $("#failuresCount");
	    let tableBody = $("#failuresTable tbody");
	
	    // ✅ Clear previous results
	    resultDiv.html("");
	    failuresCount.hide();
	    tableWrapper.hide();
	    tableBody.empty();
				
		// WORKING ON THIS FILE
		let url = `/api/call-failures/by-imsi-time?imsi=${imsi}&startDate=${startDate}&endDate=${endDate}`;
		$.ajax({
	        url: url,
	        method: "GET",
	        headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        success: displayFailures,
	        error: function (xhr) {
	            console.error("❌ Error fetching failures:", xhr);
	            let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
	            $("#failuresResult").html(failuresByImsiTimeTemplates.failureMessage(errorMessage));
	        }
	    });
	}
	
	function displayFailures(data) {
		// Values
		let imsi = data.imsi;
		let failures = data.failures;
		let numFailures = failures.length;
	
		// Elements
	    let resultDiv = $("#failuresResult");
	    let tableWrapper = $("#tableWrapper");
	    let failuresCount = $("#failuresCount");
	    let tableBody = $("#failuresTable tbody");
		
	    if (numFailures > 0) {
	        // Show failures
	        let tableHtml = failures.map(failuresByImsiTimeTemplates.failure).join("\n");
	        tableBody.html(tableHtml);
	
			// Show failure count
	        failuresCount.html(failuresByImsiTimeTemplates.failureCount(numFailures)).show();
	        tableWrapper.show();
	
	        // ✅ Destroy existing DataTable before reinitializing
	        if ($.fn.DataTable.isDataTable("#failuresTable")) {
	            $("#failuresTable").DataTable().clear().destroy();
	        }
	
	        // ✅ Reinitialize DataTable with pagination & scrolling
	        $("#failuresTable").DataTable({
	            paging: true,         // ✅ Enable pagination (prevents UI freezing)
	            pageLength: 10,       // ✅ Show 10 rows per page
	            lengthMenu: [10, 25, 50, 100], // ✅ Allow user to choose row count
	            processing: true,     // ✅ Show loading animation
	            deferRender: true,    // ✅ Loads only visible rows for better performance
	            scrollY: "400px",     // ✅ Keeps table scrollable
	            scrollCollapse: true,
	            order: [[2, "desc"]]  // ✅ Sort by date (latest first)
	        });
	
	        console.log("✅ Table updated! Rows:", $("#failuresTable tbody tr").length);
	    } else {
	        resultDiv.html(failuresByImsiTimeTemplates.noImsiMessage(imsi));
	        tableWrapper.hide(); // ❌ Hide table if no data
	        failuresCount.hide();
	    }
	}
	
	
	// Templates
	const failuresByImsiTimeTemplates = {
		failure: function (failure) {
			return `<tr>
						<td>${failure.eventId}</td>
			            <td>${failure.causeCode}</td>
			            <td>${failure.dateTime}</td>
			        </tr>`;
		},
		
		noImsiMessage: function (imsi) {
			return this.failureMessage(`No failures found for this IMSI: <strong>${imsi}</strong>.`);
		},
		
		failureCount: function (numFailures) {
			return `📋 Total Failures Found: <strong>${numFailures}</strong>`;
		},
		
		failureMessage: function (message) {
			return `<div class='alert alert-danger'>
						${message}
					</div>`;
		}
	}
	
	
	// Helper functions
	// ✅ Format `YYYY-MM-DDTHH:MM` to `YYYY-MM-DD HH:MM:SS`
	function formatDateTime(datetimeLocal) {
	    return datetimeLocal.replace("T", " ") + ":00"; // Match API expected format
	}
});