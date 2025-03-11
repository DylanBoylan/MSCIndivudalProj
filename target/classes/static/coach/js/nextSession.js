$(document).ready(function () {
    console.log("✅ Training Plan Page Loaded!");

    // Fetch teams on page load
    fetchTeams();

    $("#generatePlan").on("click", function () {
        generateTrainingPlan();
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
                teamSelect.append(`<option value="${team.teamName}">${team.teamName}</option>`); // ✅ FIXED
            });
        },
        error: function (xhr) {
            console.error("❌ Error fetching teams:", xhr);
            alert("❌ Error fetching teams. Please try again.");
        }
    });
}

function generateTrainingPlan() {
    let teamName = $("#teamSelect").val();
    let trainingType = $("#trainingTypeSelect").val();
    let resultDiv = $("#trainingPlanResult");
    let progressBar = $("#progressBar"); // Get the progress bar element

    if (!teamName || !trainingType) {
        alert("❌ Please select both a team and a training type.");
        return;
    }

    console.log(`📡 Requesting training plan for Team: ${teamName}, Type: ${trainingType}`);

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
        url: `/api/training/plan?teamName=${encodeURIComponent(teamName)}&type=${trainingType}`,
        method: "GET",
        headers: { "Authorization": `Bearer ${AuthAPI.getToken()}` },
        success: function (data) {
            console.log("✅ Training plan received:", data);
            resultDiv.html(`<strong>${data.trainingPlan.replace(/\n/g, '<br>')}</strong>`).fadeIn();
            progressBar.css("width", "100%"); // Complete the progress bar
            setTimeout(() => progressBar.parent().fadeOut(), 500); // Hide after a short delay
        },
        error: function (xhr) {
            console.error("❌ Error generating training plan:", xhr);
            resultDiv.html("<div class='alert alert-danger'>❌ Failed to generate training plan.</div>").fadeIn();
            progressBar.parent().fadeOut(); // Hide progress bar on error
        }
    });
}
