$(document).ready(function () {
    console.log("✅ top10-operators.js script loaded!");

    $("#fetchFailures").off("click").on("click", function () {
        console.log("🔄 Fetch Failures button clicked!");
        fetchFailures();
    });
});

function fetchFailures() {
    if (!AuthAPI.isLoggedIn()) {
        alert("❌ Unauthorized: Please log in.");
        return;
    }

    let startDate = $("#startDate").val();
    let endDate = $("#endDate").val();
    let resultDiv = $("#failuresResult");
	
	// Clear previous messsages
	$("#failuresCount").hide()
	
	// Clear previous results
    resultDiv.html("");

    if (!startDate || !endDate) {
        resultDiv.html("<div class='alert alert-danger'>❌ Please enter both dates.</div>");
        return;
    }

    let formattedStart = formatDateTime(startDate);
    let formattedEnd = formatDateTime(endDate);
	
    $.ajax({
        url: `/api/call-failures/top10?startDate=${encodeURIComponent(formattedStart)}&endDate=${encodeURIComponent(formattedEnd)}`,
        method: "GET",
        headers: {
            "Authorization": `Bearer ${AuthAPI.getToken()}`,
            "Content-Type": "application/json"
        },
		success: function (data) {
		    let failures = data.failures || [];
		    let resultDiv = $("#failuresResult");
		    let tableWrapper = $("#tableWrapper");
		    let failuresCount = $("#failuresCount");

		    if (failures.length > 0) {
		        let tableHtml = failures.map(failure => `
		            <tr>
		                <td>${failure.eventId}</td>
		                <td>${failure.causeCode}</td>
		                <td>${failure.market}</td>
		                <td>${failure.operator}</td>
		                <td>${failure.cellId}</td>
		                <td>${failure.failureCount}</td>
		            </tr>
		        `).join("");
		        $("#failuresTable tbody").html(tableHtml);
		        failuresCount.html(`📋 Total Failures Found: <strong>${failures.length}</strong>`).show();
		        tableWrapper.show(); // ✅ Show table when results exist

		        // ✅ Fix DataTables reinitialization issue
		        if ($.fn.DataTable.isDataTable("#failuresTable")) {
		            $("#failuresTable").DataTable().destroy();
		        }
		        $("#failuresTable tbody").empty(); // Clear old data before adding new one
		        $("#failuresTable").DataTable({
		            paging: false,
		            scrollY: "400px",
		            scrollCollapse: true,
		            order: [[3, "desc"]]
		        });

		    } else {
		        resultDiv.html("<div class='alert alert-success'>✅ No failures found for the given dates.</div>");
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
