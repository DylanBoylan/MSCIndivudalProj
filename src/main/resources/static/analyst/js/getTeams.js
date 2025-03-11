$(document).ready(function () {
    console.log("✅ teams.js script loaded!");

    // Event listener for "Fetch Teams" button
    $("#fetchTeams").off("click").on("click", function () {
        console.log("🔄 Fetch Teams button clicked!");
        fetchTeams();
    });

    function fetchTeams() {
        console.log("📡 Sending API request: /api/teams");

        // Elements
        let resultDiv = $("#teamsResult");
        let tableWrapper = $("#tableWrapper");
        let teamsCount = $("#teamsCount");
        let tableBody = $("#teamsTable tbody");

        // ✅ Clear previous results
        resultDiv.html("");
        teamsCount.hide();
        tableWrapper.hide();
        tableBody.empty();

        $.ajax({
            url: "/api/teams", // ✅ Ensure this matches your backend endpoint
            method: "GET",
            headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
            success: displayTeams,
            error: function (xhr) {
                console.error("❌ Error fetching teams:", xhr);
                let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
                $("#teamsResult").html(teamsTemplates.failureMessage(errorMessage));
            }
        });
    }

    function displayTeams(data) {
        let teams = data.teams || [];
        let numTeams = teams.length;

        // Elements
        let resultDiv = $("#teamsResult");
        let tableWrapper = $("#tableWrapper");
        let teamsCount = $("#teamsCount");
        let tableBody = $("#teamsTable tbody");

        if (numTeams > 0) {
            // Show teams
            let tableHtml = teams.map(teamsTemplates.teamEntry).join("\n");
            tableBody.html(tableHtml);

            // Show team count
            teamsCount.html(teamsTemplates.teamsFoundMessage(numTeams)).show();
            tableWrapper.show();

            // ✅ Destroy existing DataTable before reinitializing
            if ($.fn.DataTable.isDataTable("#teamsTable")) {
                $("#teamsTable").DataTable().clear().destroy();
            }

            // ✅ Reinitialize DataTable with pagination & scrolling
            $("#teamsTable").DataTable({
                paging: true,         // ✅ Enable pagination
                pageLength: 10,       // ✅ Show 10 rows per page
                lengthMenu: [10, 25, 50, 100], // ✅ Allow user to choose row count
                processing: true,     // ✅ Show loading animation
                deferRender: true,    // ✅ Load only visible rows for better performance
                scrollY: "400px",     // ✅ Keep table scrollable
                scrollCollapse: true,
                order: [[0, "asc"]]  // ✅ Sort by Team ID (ascending)
            });

            console.log("✅ Table updated! Rows:", $("#teamsTable tbody tr").length);
        } else {
            resultDiv.html(teamsTemplates.noTeamsMessage());
            tableWrapper.hide(); // ❌ Hide table if no data
            teamsCount.hide();
        }
    }

    // Templates
    const teamsTemplates = {
        teamEntry: function (team) {
            return `<tr>
                        <td>${team.teamId}</td>
                        <td>${team.name}</td>
                    </tr>`;
        },

        noTeamsMessage: function () {
            return this.failureMessage("No teams found.");
        },

        teamsFoundMessage: function (numTeams) {
            return `📋 Total Teams Found: <strong>${numTeams}</strong>`;
        },

        failureMessage: function (message) {
            return `<div class='alert alert-danger'>
                        ${message}
                    </div>`;
        }
    };
});
