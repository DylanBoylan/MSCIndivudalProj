$(window).on("load", function () {
    console.log("✅ showMatches.js script loaded!");

    // Initialize DataTable globally
    let table = $("#matchesTable").DataTable();

    // Event listener for "Fetch Matches" button
    $("#fetchMatches").off("click").on("click", function () {
        console.log("🔄 Fetch Matches button clicked!");
        fetchMatches();
    });

    function fetchMatches() {
        console.log("📡 Sending API request: /api/matches/results");

        // Elements
        let resultDiv = $("#matchesResult");
        let tableWrapper = $("#tableWrapper");
        let matchesCount = $("#matchesCount");
        let tableBody = $("#matchesTable tbody");

        // ✅ Clear previous results
        resultDiv.html("");
        matchesCount.hide();
        tableWrapper.hide();
        tableBody.empty();

        $.ajax({
            url: "/api/matches/results", // ✅ Ensure this matches your backend endpoint
            method: "GET",
            headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
            success: function (data) {
                console.log("✅ Data received:", data);

                let matches = data.matches || [];
                let numMatches = matches.length;

                if (numMatches > 0) {
                    // Show matches
                    let tableHtml = matches.map(matchesTemplates.matchEntry).join("\n");
                    tableBody.html(tableHtml);

                    // Show matches count
                    matchesCount.html(matchesTemplates.matchesFoundMessage(numMatches)).show();
                    tableWrapper.show();

                    // ✅ Destroy existing DataTable before reinitializing
                    if ($.fn.DataTable.isDataTable("#matchesTable")) {
                        table.clear().destroy();
                    }

                    // ✅ Reinitialize DataTable with new data
                    table = $("#matchesTable").DataTable({
                        destroy: true,
                        paging: true,
                        pageLength: 10,
                        lengthMenu: [10, 25, 50, 100],
                        processing: true,
                        deferRender: true,
                        scrollY: "400px",
                        scrollCollapse: true,
                        order: [[0, "desc"]],
                        data: matches,
                        columns: [
                            { data: "matchId" },
                            { data: "homeTeam" },
                            { data: "awayTeam" },
                            { data: "homeResult" }
                        ]
                    });

                    console.log("✅ Table updated! Rows:", $("#matchesTable tbody tr").length);
                } else {
                    resultDiv.html(matchesTemplates.noMatchesMessage());
                    tableWrapper.hide(); // ❌ Hide table if no data
                    matchesCount.hide();
                }
            },
            error: function (xhr) {
                console.error("❌ Error fetching matches:", xhr);
                let errorMessage = `❌ Error ${xhr.status}: ${xhr.responseText}`;
                $("#matchesResult").html(matchesTemplates.failureMessage(errorMessage));
            }
        });
    }

    // Templates
    const matchesTemplates = {
        matchEntry: function (match) {
            return `<tr>
                        <td>${match.matchId}</td>
                        <td>${match.homeTeam}</td>
                        <td>${match.awayTeam}</td>
                        <td>${match.homeResult}</td>
                    </tr>`;
        },

        noMatchesMessage: function () {
            return this.failureMessage("No matches found.");
        },

        matchesFoundMessage: function (numMatches) {
            return `📋 Total Matches Found: <strong>${numMatches}</strong>`;
        },

        failureMessage: function (message) {
            return `<div class='alert alert-danger'>
                        ${message}
                    </div>`;
        }
    };
});
