$(document).ready(function () {
    // Event Listener for search button
    $("#fetchImsis").off("click").on("click", function () {
        let startDate = $("#startDate").val().trim();
        let endDate = $("#endDate").val().trim();
        let resultDiv = $("#imsisResult");

        resultDiv.empty(); // Clear previous results
        $("#errorMessage").hide();
        $("#tableWrapper").hide(); // Hide the table container immediately on a new search

        if (!startDate || !endDate) {
            $("#errorMessage").html("⚠️ Please enter both start and end dates.").show();
            return;
        }

        console.log(`📡 Sending API request: /api/call-failures/top10-imsis?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`);

        $.ajax({
            url: `/api/call-failures/top10-imsis?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`,
            method: "GET",
            headers: {
                "Authorization": `Bearer ${AuthAPI.getToken()}`,
                "Content-Type": "application/json"
            },
            success: function (data) {
				
                console.log("📊 IMSI failure summary received:", data);
				console.log("🔍 Raw response data:", JSON.stringify(data, null, 2)); // Pretty print response

                let imsis = data || [];
                let resultDiv = $("#imsisResult");
                let tableWrapper = $("#tableWrapper");

                $("#errorMessage").hide();
                resultDiv.empty();

                if (imsis.length > 0) {
                    let tableHtml = imsis.map(imsi => `
                        <tr>
                            <td>${imsi.imsi}</td>
                            <td>${imsi.failureCount}</td>
                        </tr>
                    `).join("");
                    $("#imsisTable tbody").html(tableHtml);
                    tableWrapper.show(); // ✅ Show table when results exist

                    if (!$.fn.DataTable.isDataTable("#imsisTable")) {
                        $("#imsisTable").DataTable({
                            paging: false,
                            scrollY: "400px",
                            scrollCollapse: true,
                            order: [[1, "desc"]]
                        });
                    }
                } else {
                    $("#errorMessage").html("✅ No failures found for the given time period.").show();
                    tableWrapper.hide(); // ❌ Hide table if no data
                }
            },
			error: function (xhr, status, error) {
			    console.error("❌ Error fetching IMSI failures:", xhr);

			    let errorMessage = "❌ Failed to fetch IMSI failures.";

			    if (xhr.status === 403) {
			        errorMessage = "⚠️ Query Error: Ensure dates are correctly formatted";
			    } else if (xhr.status === 401) {
			        errorMessage = "🔒 Unauthorized: Please log in.";
			    } else if (xhr.status === 500) {
			        errorMessage = "⚠️ Server Error: Please try again later.";
			    } else if (xhr.status === 0) {
			        errorMessage = "🌐 Network Error: Check your internet connection.";
			    } else {
			        errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
			    }

			    $("#errorMessage").html(errorMessage).show(); // Display error on screen
			}
        });
    });
});
