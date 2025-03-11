$(document).ready(function () {
    console.log("✅ actions.js script loaded!");

    // Event listener for "Fetch Actions" button
    $("#fetchActions").off("click").on("click", function () {
        console.log("🔄 Fetch Actions button clicked!");
        fetchActions();
    });

    function fetchActions() {
        console.log("📡 Sending API request: /api/actions");

        // Elements
        let resultDiv = $("#actionsResult");
        let tableWrapper = $("#tableWrapper");
        let actionsCount = $("#actionsCount");
        let tableBody = $("#actionsTable tbody");

        // ✅ Clear previous results
        resultDiv.html("");
        actionsCount.hide();
        tableWrapper.hide();
        tableBody.empty();

        $.ajax({
            url: "/api/actions", // ✅ Ensure this matches your backend endpoint
            method: "GET",
            headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
            success: displayActions,
            error: function (xhr) {
                console.error("❌ Error fetching actions:", xhr);
                let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
                $("#actionsResult").html(actionsTemplates.failureMessage(errorMessage));
            }
        });
    }

    function displayActions(data) {
        let actions = data.actions || [];
        let numActions = actions.length;

        // Elements
        let resultDiv = $("#actionsResult");
        let tableWrapper = $("#tableWrapper");
        let actionsCount = $("#actionsCount");
        let tableBody = $("#actionsTable tbody");

        if (numActions > 0) {
            // Show actions
            let tableHtml = actions.map(actionsTemplates.actionEntry).join("\n");
            tableBody.html(tableHtml);

            // Show actions count
            actionsCount.html(actionsTemplates.actionsFoundMessage(numActions)).show();
            tableWrapper.show();

            // ✅ Destroy existing DataTable before reinitializing
            if ($.fn.DataTable.isDataTable("#actionsTable")) {
                $("#actionsTable").DataTable().clear().destroy();
            }

            // ✅ Reinitialize DataTable with pagination & scrolling
            $("#actionsTable").DataTable({
                paging: true,         // ✅ Enable pagination
                pageLength: 10,       // ✅ Show 10 rows per page
                lengthMenu: [10, 25, 50, 100], // ✅ Allow user to choose row count
                processing: true,     // ✅ Show loading animation
                deferRender: true,    // ✅ Load only visible rows for better performance
                scrollY: "400px",     // ✅ Keep table scrollable
                scrollCollapse: true,
                order: [[1, "desc"]]  // ✅ Sort by Match ID (latest first)
            });

            console.log("✅ Table updated! Rows:", $("#actionsTable tbody tr").length);
        } else {
            resultDiv.html(actionsTemplates.noActionsMessage());
            tableWrapper.hide(); // ❌ Hide table if no data
            actionsCount.hide();
        }
    }

    // Templates
    const actionsTemplates = {
        actionEntry: function (action) {
            return `<tr>
                        <td>${action.playerId}</td>
                        <td>${action.matchId}</td>
                        <td>${action.teamId}</td>
                        <td>${action.goals}</td>
                        <td>${action.shotsOnTarget}</td>
                        <td>${action.passesEfficiency}</td>
                        <td>${action.tackleEfficiency}</td>
                        <td>${action.dribbleEfficiency}</td>
                    </tr>`;
        },

        noActionsMessage: function () {
            return this.failureMessage("No actions found.");
        },

        actionsFoundMessage: function (numActions) {
            return `📋 Total Actions Found: <strong>${numActions}</strong>`;
        },

        failureMessage: function (message) {
            return `<div class='alert alert-danger'>
                        ${message}
                    </div>`;
        }
    };
});
