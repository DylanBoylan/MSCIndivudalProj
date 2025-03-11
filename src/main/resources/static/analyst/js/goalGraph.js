$(document).ready(function () {
    console.log("✅ Goals Graph Page Loaded!");

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
    let graphCanvas = $("#teamGoalsChart");
    let graphCount = $("#graphCount");

    // ✅ Clear previous results
    resultDiv.empty();
    graphCanvas.hide();
    graphCount.hide();

    $.ajax({
        url: "/api/teams/goals",  // ✅ Ensure this matches your backend endpoint
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
                resultDiv.html("<div class='alert alert-warning'>⚠️ No team goals data available</div>");
                graphCanvas.hide();
                graphCount.hide();
                return;
            }

            // ✅ Extract labels (team names) & data (goals)
            let teamNames = teams.map(team => team.teamName);
            let teamGoals = teams.map(team => team.totalGoals);

            // ✅ Render the pie chart
            renderPieChart(teamNames, teamGoals);

            // ✅ Show total teams count
            graphCount.html(`📋 Total Teams Found: <strong>${totalTeams}</strong>`).show();
        },
        error: function (xhr) {
            console.error("❌ Error fetching graph data:", xhr);
            resultDiv.html("<div class='alert alert-danger'>❌ Failed to fetch team goals.</div>");
        }
    });
}

function renderPieChart(labels, data) {
    let ctx = document.getElementById('teamGoalsChart').getContext('2d');

    // ✅ Destroy previous chart instance if it exists
    if (window.teamGoalsChartInstance && typeof window.teamGoalsChartInstance.destroy === 'function') {
        window.teamGoalsChartInstance.destroy();
    }

    // ✅ Create new pie chart
    window.teamGoalsChartInstance = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total Goals',
                data: data,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)',
                    'rgba(255, 159, 64, 0.6)',
                    'rgba(199, 199, 199, 0.6)',
                    'rgba(83, 102, 255, 0.6)',
                    'rgba(255, 69, 0, 0.6)',
                    'rgba(34, 177, 76, 0.6)'
                ],
                borderColor: 'rgba(255, 255, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true
        }
    });

    // ✅ Show the chart
    $("#teamGoalsChart").show();
}
