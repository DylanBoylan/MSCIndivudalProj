$(document).ready(function () {
    console.log("✅ failures.js script loaded!");

    $("#fetchFailures").off("click").on("click", function () {
        console.log("🔄 Fetch Failures button clicked!");
        fetchFailures();
    });

	function fetchFailures() {
	    let phoneModel = $("#phoneModel").val();
	    let startDate = $("#startDate").val();
	    let endDate = $("#endDate").val();
	    let resultDiv = $("#failuresResult");
	
	    resultDiv.html(""); // Clear previous results
	
	    if (!phoneModel || !startDate || !endDate) {
	        resultDiv.html("<div class='alert alert-danger'>❌ Please enter a phone model and both dates.</div>");
	        return;
	    }
	
	    let formattedStart = formatDateTime(startDate);
	    let formattedEnd = formatDateTime(endDate);
	
	    console.log(`📡 Sending API request: /api/call-failures/count?phoneModel=${phoneModel}&startDate=${formattedStart}&endDate=${formattedEnd}`);
	
	    $.ajax({
	        url: `/api/call-failures/count?phoneModel=${encodeURIComponent(phoneModel)}&startDate=${encodeURIComponent(formattedStart)}&endDate=${encodeURIComponent(formattedEnd)}`,
	        method: "GET",
	        headers: {
	            "Content-Type": "application/json"
	        },
	        success: function (data) {
	            console.log("📊 Call Failure Count:", data.failures);
	            //let failureCount = data.failureCount || 0;
				let failures = data.failures || [];
				let resultDiv = $("#failuresResult");
			    let tableWrapper = $("#tableWrapper");
			    let failuresCount = $("#failuresCount");
				alert("Failure count:"+failuresCount)
	            if (failures.length > 0) {
	                /*resultDiv.html(`
	                    <div class="alert alert-info">📊 Total Call Failures: <strong>${failureCount}</strong></div>
	                `);
	            } else {
	                resultDiv.html("<div class='alert alert-success'>✅ No failures found for the given period.</div>");
	            }
				}*/	
				let tableHtml = failures.map(failure => `
		            <tr>
		                <td>${failure.eventId}</td>
		                <td>${failure.causeCode}</td>
		                <td>${failure.imsi}</td>
		                <td>${failure.dateTime}</td>
		            </tr>
		        `).join("");
				alert("tableHTML:"+tableHtml);
		        $("#failuresTable tbody").html(tableHtml);
		        failuresCount.html(`📋 Total Failures Found: <strong>${failures.length}</strong>`).show();
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
		        resultDiv.html("<div class='alert alert-success'>✅ No failures found for this IMSI.</div>");
		        tableWrapper.hide(); // ❌ Hide table if no data
		        failuresCount.hide();
		    }
		},
	        error: function (xhr) {
	            console.error("❌ Error fetching failures:", xhr);
	            resultDiv.html("<div class='alert alert-danger'>❌ Failed to fetch call failures.</div>");
	        }
	    });
	}
	
	// ✅ Format `YYYY-MM-DDTHH:MM` to `YYYY-MM-DD HH:MM:SS`
	function formatDateTime(datetimeLocal) {
	    return datetimeLocal.replace("T", " ") + ":00"; // Match API expected format
	}
});
