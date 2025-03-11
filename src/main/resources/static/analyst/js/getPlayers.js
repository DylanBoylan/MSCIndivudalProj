$(document).ready(function () {
    console.log("✅ players.js script loaded!");

    // Event listener for "Fetch Players" button
    $("#fetchPlayers").off("click").on("click", function () {
        console.log("🔄 Fetch Players button clicked!");
        fetchPlayers();
    });

    function fetchPlayers() {
        console.log("📡 Sending API request: /api/players");

        // Elements
        let resultDiv = $("#playersResult");
        let tableWrapper = $("#tableWrapper");
        let playersCount = $("#playersCount");
        let tableBody = $("#playersTable tbody");

        // ✅ Clear previous results
        resultDiv.html("");
        playersCount.hide();
        tableWrapper.hide();
        tableBody.empty();

        $.ajax({
            url: "/api/players", // ✅ Ensure this matches your backend endpoint
            method: "GET",
            headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
            success: displayPlayers,
            error: function (xhr) {
                console.error("❌ Error fetching players:", xhr);
                let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
                $("#playersResult").html(playersTemplates.failureMessage(errorMessage));
            }
        });
    }

    function displayPlayers(data) {
        let players = data.players || [];
        let numPlayers = players.length;

        // Elements
        let resultDiv = $("#playersResult");
        let tableWrapper = $("#tableWrapper");
        let playersCount = $("#playersCount");
        let tableBody = $("#playersTable tbody");

        if (numPlayers > 0) {
            // Show players
            let tableHtml = players.map(playersTemplates.playerEntry).join("\n");
            tableBody.html(tableHtml);

            // Show player count
            playersCount.html(playersTemplates.playersFoundMessage(numPlayers)).show();
            tableWrapper.show();

            // ✅ Destroy existing DataTable before reinitializing
            if ($.fn.DataTable.isDataTable("#playersTable")) {
                $("#playersTable").DataTable().clear().destroy();
            }

            // ✅ Reinitialize DataTable with pagination & scrolling
            $("#playersTable").DataTable({
                paging: true,         // ✅ Enable pagination
                pageLength: 10,       // ✅ Show 10 rows per page
                lengthMenu: [10, 25, 50, 100], // ✅ Allow user to choose row count
                processing: true,     // ✅ Show loading animation
                deferRender: true,    // ✅ Load only visible rows for better performance
                scrollY: "400px",     // ✅ Keep table scrollable
                scrollCollapse: true,
                order: [[0, "asc"]]  // ✅ Sort by Player ID (ascending)
            });

            console.log("✅ Table updated! Rows:", $("#playersTable tbody tr").length);
        } else {
            resultDiv.html(playersTemplates.noPlayersMessage());
            tableWrapper.hide(); // ❌ Hide table if no data
            playersCount.hide();
        }
    }

    // Templates
    const playersTemplates = {
        playerEntry: function (player) {
            return `<tr>
                        <td>${player.playerId}</td>
                        <td>${player.name}</td>
                    </tr>`;
        },

        noPlayersMessage: function () {
            return this.failureMessage("No players found.");
        },

        playersFoundMessage: function (numPlayers) {
            return `📋 Total Players Found: <strong>${numPlayers}</strong>`;
        },

        failureMessage: function (message) {
            return `<div class='alert alert-danger'>
                        ${message}
                    </div>`;
        }
    };
});
