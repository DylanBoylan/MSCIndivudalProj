$(document).ready(function () {
    console.log("✅ imsis.js script loaded!");
	
	$("#fetchImsis").off("click").on("click", function () {
        let startTime = $("#startTime").val().trim();
        let endTime = $("#endTime").val().trim();
        let resultDiv = $("#imsisResult");

        // ✅ Clear previous results
        resultDiv.html("");

        if (!startTime || !endTime) {
            resultDiv.html("<div class='alert alert-danger'>❌ Please enter both start and end times.</div>");
            return;
        }

        let formattedStart = formatDateTime(startTime);
        let formattedEnd = formatDateTime(endTime);

        console.log(`📡 Sending API request: /api/call-failures/imsis?startTime=${formattedStart}&endTime=${formattedEnd}`);

        let startTimeMeasure = performance.now(); // ✅ Start measuring response time

        // ✅ Show a loading animation while waiting for data
        resultDiv.html(`<div class='alert alert-warning'>⏳ Fetching IMSIs... Please wait.</div>`);

        $.ajax({
            url: `/api/call-failures/imsis?startTime=${encodeURIComponent(formattedStart)}&endTime=${encodeURIComponent(formattedEnd)}`,
            method: "GET",
            headers: {
                "Authorization": `Bearer ${AuthAPI.getToken()}`,
                "Content-Type": "application/json"
            },
            success: function (data) {
                let endTimeMeasure = performance.now();
                console.log(`⏳ API Response Time: ${(endTimeMeasure - startTimeMeasure).toFixed(2)} ms`);

                let imsis = data.imsis || [];

                if (imsis.length > 0) {
                    console.log("📊 IMSIs received:", imsis.length);

                    // ✅ Use virtual scrolling for large lists
                    let listItems = imsis.slice(0, 50).map(imsi => `<li class="list-group-item">${imsi}</li>`).join("");

                    resultDiv.html(`
                        <div class="alert alert-info">📊 Total IMSIs Found: <strong>${imsis.length}</strong></div>
                        <div class="scrollable-container">
                            <ul class="list-group">${listItems}</ul>
                        </div>
                        <button id="loadMoreImsis" class="btn btn-primary mt-2">Load More</button>
                    `);

                    // ✅ Load more IMSIs dynamically when clicking "Load More"
                    let currentIndex = 50;
                    $("#loadMoreImsis").on("click", function () {
                        let moreItems = imsis.slice(currentIndex, currentIndex + 50)
                            .map(imsi => `<li class="list-group-item">${imsi}</li>`).join("");

                        if (moreItems) {
                            $(".list-group").append(moreItems);
                            currentIndex += 50;
                        }

                        // ✅ Hide button when all data is loaded
                        if (currentIndex >= imsis.length) {
                            $("#loadMoreImsis").hide();
                        }
                    });

                } else {
                    resultDiv.html("<div class='alert alert-success'>✅ No IMSIs found for the given time range.</div>");
                }
            },
            error: function (xhr) {
                console.error("❌ Error fetching IMSIs:", xhr);
                resultDiv.html("<div class='alert alert-danger'>❌ Failed to fetch IMSIs.</div>");
            }
        });
    });
	

	function formatDateTime(datetimeLocal) {
	    return datetimeLocal.replace("T", " ") + ":00"; // Formats correctly for API
	}
});
