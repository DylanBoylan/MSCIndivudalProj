$(document).ready(function () {
    console.log("✅ getPlayers.js script loaded!");

    // Ensure DataTables is available before using
    if (!$.fn.DataTable) {
        console.error("❌ DataTables is NOT loaded. Check script order.");
        return;
    }

    console.log("✅ Initializing DataTable...");
    let table = $("#playersTable").DataTable();

    // Load team names into dropdown
    function loadTeams() {
        console.log("📡 Fetching team names...");
        $.ajax({
            url: "/api/teams/names",
            type: "GET",
            headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
            success: function (response) {
                console.log("✅ Teams received:", response);
                let teamSelect = $("#teamSelect");

                // Clear previous options & add default option
                teamSelect.empty().append(`<option value="">-- Select Team --</option>`);

                // Append teams from API response
                response.teams.forEach(team => {
                    teamSelect.append(`<option value="${team.teamName}">${team.teamName}</option>`);
                });
            },
            error: function (xhr) {
                console.error("❌ Error fetching team names:", xhr);
                alert("❌ Failed to load teams. Please try again.");
            }
        });
    }

    // Call function to load teams
    loadTeams();

    // Event listener for "Fetch Players" button
    $("#fetchPlayers").off("click").on("click", function () {
        console.log("🔄 Fetch Players button clicked!");
        fetchPlayers();
    });

    function fetchPlayers() {
        let teamName = $("#teamSelect").val();
        if (!teamName) {
            alert("Please select a team!");
            return;
        }

        console.log(`📡 Sending API request: /api/players/team?teamName=${teamName}`);

        $.ajax({
            url: `/api/players/team?teamName=${encodeURIComponent(teamName)}`,
            type: "GET",
            headers: {
                "Authorization": `Bearer ${AuthAPI.getToken()}`, 
                "Content-Type": "application/json"
            },
            success: function (response) {
                console.log("✅ Data received:", response);

                if (!response.players || response.players.length === 0) {
                    alert("No players found.");
                    $("#tableWrapper").hide();
                    return;
                }

                $("#tableWrapper").show();

                // ✅ Destroy existing DataTable instance properly
                if ($.fn.DataTable.isDataTable("#playersTable")) {
                    table.clear().destroy(); // Clears and destroys previous instance
                }

                // ✅ Initialize DataTable again with new data
                table = $("#playersTable").DataTable({
                    destroy: true,
                    paging: true,
                    pageLength: 10,
                    lengthMenu: [10, 25, 50, 100],
                    processing: true,
                    deferRender: true,
                    scrollY: "400px",
                    scrollCollapse: true,
                    order: [[1, "asc"]],
                    data: response.players,
                    columns: [
                        { data: "playerId" },
                        { data: "playerName" },
                        { data: "goals" },
                        { data: "shotsOnTarget" },
                        { data: "passesEfficiency" },
                        { data: "tackleEfficiency" },
                        { data: "dribbleEfficiency" }
                    ]
                });

                console.log("✅ Table updated successfully!");
            },
            error: function (xhr) {
                console.error("❌ Error fetching players:", xhr);
                alert("❌ Failed to fetch player stats. Please try again.");
            }
        });
    }
});
