$(document).ready(function () {
    console.log("✅ count-by-imsi.js script loaded!");

    $("#getFailuresBtn").off("click").on("click", function () {
        console.log("🔄 Fetch Failures button clicked!");
        fetchFailures();
    });
	
	// -- FUNCTIONS --
	function fetchFailures() {
		// Values
		let startDate = $("#startTime").val().trim();
		let endDate = $("#endTime").val().trim();
	
		if (!startDate || !endDate) {
	    	$("#failuresResult").html(countByImsiTemplates.failureMessage("❌ Please enter both start and end times."));
	    	return;
		}
	
		let formattedStart = formatDateTime(startDate);
		let formattedEnd = formatDateTime(endDate);
		$.ajax({
	    	url: `/api/call-failures/count-by-imsi?startDate=${encodeURIComponent(formattedStart)}&endDate=${encodeURIComponent(formattedEnd)}`,
	    	method: "GET",
	     	headers: {
				"Authorization": `Bearer ${AuthAPI.getToken()}`,
	        	"Content-Type": "application/json"
			},
			dataType: "json",
			success: displayFailures,
			error: function (xhr) {
	        	console.error("❌ Error fetching failure stats:", xhr.status, xhr.responseText);
	        	let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
	       		resultDiv.html(countByImsiTemplates.failureMessage(errorMessage));
			}
		});
	}
	
	function displayFailures(data) {
		let failureStats = data.failureStats || [];
		// Elements
		let resultDiv = $("#failuresResult");
		let failuresCount = $("#failuresCount");
		let tableWrapper = $("#tableWrapper");
		let tableBody = $("#failuresTable tbody");
	
		// Clear previous results
		resultDiv.html("");
		failuresCount.hide();
		tableWrapper.hide();
		tableBody.empty();
	    if (failureStats.length > 0) {
	    	populateTable(failureStats);
		} else {
	    	$("#failuresResult").html(countByImsiTemplates.successMessage("✅ No failures found in this time range."));
	    }
	}
	
	function populateTable(failureStats) {
		// Elements
		let resultDiv = $("#failuresResult");
		let failuresCount = $("#failuresCount");
		let tableWrapper = $("#tableWrapper");
		let tableBody = $("#failuresTable tbody");
		
		// Convert failureStats to HTML rows
		let tableHtml = failureStats.map(countByImsiTemplates.failureStat).join("/n"); 
		tableBody.html(tableHtml);
	
	    let totalFailures = failureStats.reduce((sum, stat) => sum + stat.failureCount, 0);
	    failuresCount.html(`📋 Total IMSIs: <strong>${failureStats.length}</strong> | Total Failures: <strong>${totalFailures}</strong>`).show();
	    tableWrapper.show();
	
	    // Destroy existing DataTable before reinitializing
	    if ($.fn.DataTable.isDataTable("#failuresTable")) {
	    	$("#failuresTable").DataTable().clear().destroy();
	    }
	
		// Reinitialize DataTable with pagination & scrolling
	    $("#failuresTable").DataTable({
	    	paging: true,        // ✅ Enable pagination (allows user to navigate)
	        pageLength: 10,      // ✅ Show 10 rows per page
	        lengthMenu: [10, 25, 50, 100], // ✅ Allow user to choose how many rows to see
	        processing: true,    // ✅ Show loading indicator for large data
	        deferRender: true,   // ✅ Render only visible rows for performance
	        scrollY: "400px",    // ✅ Scrollable table for better UX
	        scrollCollapse: true,
	        order: [[1, "desc"]] // ✅ Sort by failure count
		});
	}
	
	
	// Templates
	const countByImsiTemplates = {
		successMessage: function (message) {
			return `<div class='alert alert-success'>
						${message}
					</div>`;
		},
		
		failureStat: function (stat) {
			return `<tr>
			           <td>${stat.imsi}</td>
			           <td>${stat.failureCount}</td>
			           <td>${stat.totalDuration}</td>
			       </tr>`;
		},
		
		failureMessage: function (errorMessage) {
			return `<div class='alert alert-danger'>
						${errorMessage}
					</div>`;
		}
	}
	
	// Helper functions
	function formatDateTime(datetimeLocal) {
	    return datetimeLocal.replace("T", " ") + ":00"; // Formats correctly for API
	}
});