$(document).ready(function () {
    console.log("✅ Graph Page Loaded!");

    // ⏳ Wait for Chart.js to load before running any checks
    let checkChartJs = setInterval(function () {
        if (typeof Chart !== "undefined") {
            clearInterval(checkChartJs); // ✅ Stop checking once loaded
            console.log("✅ Chart.js is available.");
        } else {
            console.warn("⏳ Waiting for Chart.js to load...");
        }
    }, 500); // Check every 500ms

    // ✅ Set up click event for fetching data
    $("#fetchGraphData").off("click").on("click", function () {
        console.log("🔄 Fetching Graph Data...");
        fetchGraphData();
    });
});


function fetchGraphData() {
    if (!AuthAPI.isLoggedIn()) {
        alert("❌ Unauthorized: Please log in.");
        return;
    }

    // ✅ Elements
    let resultDiv = $("#graphResult");
    let graphCanvas = $("#teamPointsChart");
    let graphCount = $("#graphCount");

    // ✅ Clear previous results
    resultDiv.empty();
    graphCanvas.hide();
    graphCount.hide();

    $.ajax({
        url: "/api/teams/points",  // ✅ Ensure this matches your backend endpoint
        method: "GET",
        headers: {
            "Authorization": `Bearer ${AuthAPI.getToken()}`,
            "Content-Type": "application/json"
        },
        success: function (data) {
            let teams = data.teams || [];
            let totalTeams = data.totalTeams || 0;

            // ✅ Handle empty dataset
            if (teams.length === 0) {
                resultDiv.html("<div class='alert alert-warning'>⚠️ No team points data available</div>");
                graphCanvas.hide();
                graphCount.hide();
                return;
            }

            // ✅ Extract labels (team names) & data (points)
            let teamNames = teams.map(team => team.teamName);
            let teamPoints = teams.map(team => team.totalPoints);

            // ✅ Render the graph
            renderGraph(teamNames, teamPoints);

            // ✅ Show total teams count
            graphCount.html(`📋 Total Teams Found: <strong>${totalTeams}</strong>`).show();
        },
        error: function (xhr) {
            console.error("❌ Error fetching graph data:", xhr);
            resultDiv.html("<div class='alert alert-danger'>❌ Failed to fetch team points.</div>");
        }
    });
}

function renderGraph(labels, data) {
    let ctx = document.getElementById('teamPointsChart').getContext('2d');

    // ✅ Destroy previous chart instance if it exists
    if (window.teamPointsChartInstance && typeof window.teamPointsChartInstance.destroy === 'function') {
        window.teamPointsChartInstance.destroy();
    }

    // ✅ Create new bar chart
    window.teamPointsChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total Points',
                data: data,
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1 // ✅ Ensure only whole numbers are displayed
                    }
                }
            }
        }
    });

    // ✅ Show the chart
    $("#teamPointsChart").show();
}
