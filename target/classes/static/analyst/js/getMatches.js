$(document).ready(function () {
    console.log("✅ by-imsi.js script loaded!");

    // Initialize DataTable
    var table = $("#matchesTable").DataTable({
        paging: true,
        scrollY: "400px",
        scrollCollapse: true,
        order: [[0, "desc"]]
    });

    $("#fetchMatches").off("click").on("click", function () {
        console.log("🔄 Fetch Matches button clicked!");
        fetchMatches();
    });

    // -- FUNCTIONS --
	function fetchMatches() {
	    let token = AuthAPI.getToken();
	    console.log("🔑 Token Retrieved:", JSON.stringify(token)); // Debugging

	    if (!token) {
	        alert("❌ User is not authenticated. Please log in.");
	        return;
	    }

	    $.ajax({
	        url: "/api/matches",
	        method: "GET",
	        headers: {
	            "Authorization": `Bearer ${token.trim()}`,  // ✅ Fixed interpolation
	            "Content-Type": "application/json"
	        },
	        beforeSend: function (xhr) {
	            console.log("📡 Adding Authorization Header:", `Bearer ${token.trim()}`);
	            xhr.setRequestHeader("Authorization", `Bearer ${token.trim()}`);  // ✅ Explicitly setting header
	        },
	        success: function (response) {
	            console.log("✅ API Response:", response);
	            displayMatches(response);
	        },
	        error: function (xhr) {
	            console.error("❌ Error fetching matches:", xhr);
	            alert("❌ Failed to fetch match data. Check authentication.");
	        }
	    });
	}


    function displayMatches(data) {
        let matches = data.matches || [];

        if (matches.length > 0) {
            table.clear().draw(); // Clear previous results

            matches.forEach(match => {
                table.row.add([
                    match.matchId,
                    match.teamHomeID, // Replace with actual team names if needed
                    match.teamAwayID,
                    match.teamHomeFormation,
                    match.teamAwayFormation,
                    match.resultOfTeamHome,
                    match.date
                ]).draw(false);
            });

            $("#tableWrapper").show(); // ✅ Show table when data exists
        } else {
            alert("✅ No match data found.");
            $("#tableWrapper").hide(); // ❌ Hide table if no data
        }
    }
});
