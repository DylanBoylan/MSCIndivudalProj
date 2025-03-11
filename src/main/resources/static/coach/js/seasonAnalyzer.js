$(document).ready(function () {
    console.log("✅ Season Analyzer Page Loaded!");

    // Fetch teams on page load
    fetchTeams();

    $("#analyzeSeason").on("click", function () {
        analyzeSeason();
    });
});

function fetchTeams() {
    console.log("📡 Fetching teams...");

    $.ajax({
        url: "/api/teams/names",
        method: "GET",
        headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
        success: function (data) {
            console.log("✅ Teams received:", data);

            let teamSelect = $("#teamSelect");
            teamSelect.empty().append('<option value="" disabled selected>Select a Team</option>');

            if (!data.teams || data.teams.length === 0) {
                teamSelect.append('<option value="" disabled>No teams available</option>');
                return;
            }

            data.teams.forEach(team => {
                teamSelect.append(`<option value="${team.teamName}">${team.teamName}</option>`); // ✅ Uses team name
            });
        },
        error: function (xhr) {
            console.error("❌ Error fetching teams:", xhr);
            alert("❌ Error fetching teams. Please try again.");
        }
    });
}

function analyzeSeason() {
    let teamName = $("#teamSelect").val();
    let analysisType = $("#analysisTypeSelect").val();
    let resultDiv = $("#seasonResult");
    let progressBar = $("#progressBar"); // Get the progress bar element

    if (!teamName || !analysisType) {
        alert("❌ Please select both a team and an analysis type.");
        return;
    }

    console.log(`📡 Requesting season analysis for Team: ${teamName}, Type: ${analysisType}`);

    // Reset and show progress bar
    progressBar.css("width", "0%").parent().show();

    let progress = 0;
    let interval = setInterval(() => {
        progress += 10;
        progressBar.css("width", progress + "%");

        if (progress >= 90) {
            clearInterval(interval); // Stop at 90% until response arrives
        }
    }, 500);

    $.ajax({
        url: `/api/season/analyze?teamName=${encodeURIComponent(teamName)}&analysisType=${encodeURIComponent(analysisType)}`,
        method: "GET",
        headers: { 
            "Authorization": `Bearer ${AuthAPI.getToken()}`,
            "Content-Type": "application/json"
        },
        success: function (data) {
            console.log("✅ Season analysis received:", data);

            let aiReport = data.aiReport ? data.aiReport.replace(/\n/g, '<br>') : "No AI report available.";
            
            resultDiv.html(`<strong>${aiReport}</strong>`).fadeIn();
            progressBar.css("width", "100%"); // Complete the progress bar
            setTimeout(() => progressBar.parent().fadeOut(), 500); // Hide after a short delay
        },
        error: function (xhr) {
            console.error("❌ Error fetching season analysis:", xhr);
            resultDiv.html("<div class='alert alert-danger'>❌ Failed to generate season analysis.</div>").fadeIn();
            progressBar.parent().fadeOut(); // Hide progress bar on error
        }
    });
}
