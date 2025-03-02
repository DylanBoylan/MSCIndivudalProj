$(document).ready(function () {
	loadExcelFiles(); // Load available Excel files on page load

	// Event listener for upload file button
	$("#uploadButton").off("click").on("click", function () {
		$("#downloadSkippedLog").hide(); // Hide download log file
		uploadSelectedFile();
	});
 
	// Event listener for download log file button
	$("#downloadSkippedLog").off("click").on("click", function () {
		downloadLogFile();
	});
	
	// -- FUNCTIONS --
	// Load excel files
	function loadExcelFiles() {
		$.ajax({
	        url: "/excel-files",
	        method: "GET",
	        headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
	        success: populateFileDropdown,
	        error: function (xhr) {
	            console.error("❌ Error loading files:", xhr);
	            errorCallback(xhr.responseText || "Failed to retrieve files.");
	        }
	    });
	}
	
	function populateFileDropdown(files) {
		let dropdown = $("#fileDropdown");
	    dropdown.empty();
	    files.forEach(file => {
	        dropdown.append(`<option value="${file}">${file}</option>`);
	    });
	}
	
	// Upload file
	function uploadSelectedFile() {
	    let selectedFile = $("#fileDropdown").val();
	    if (!selectedFile) {
	        $("#uploadStatus").html(`<div class="alert alert-danger">❌ Please select a file.</div>`);
	        return;
	    }
		
		$("#uploadStatus").html(fileTemplates.spinner());
		
		$.ajax({
			url: `/import/${selectedFile}`,
			type: "POST",
	 		headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`},
			success: function (response) {
				$("#uploadStatus").html(fileTemplates.uploadSuccess(response.rowsProcessed, response.rowsSkipped));
				$("#downloadSkippedLog").show();
			},
	      	error: function (xhr) {
	           console.error("❌ File upload failed:", xhr);
	           errorCallback(xhr.responseText || "Upload failed.");
	       }
	   });
	}
	
	// Download file
	function downloadLogFile() {
		fetch('/logs/skipped_rows.log', {
            method: 'GET',
			headers: {"Authorization": `Bearer ${AuthAPI.getToken()}`}
        })
        .then(response => response.blob()) // Convert response to blob
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "skipped_rows.log";
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url); // Clean up
        })
        .catch(error => console.error("Error downloading file:", error));
	}
	
	
	// Templates
	const fileTemplates = {
		spinner: function () {
			return `<div id="spinner-border " class="spinner-border text-light" role="status">
				      <span class="visually-hidden">Loading...</span>
				    </div>
				    <p>Uploading...</p>`;
		},
		uploadSuccess: function (processed, skipped) {
			let successRate = (processed + skipped > 0)
			            ? ((processed / (processed + skipped)) * 100).toFixed(2)
			            : 100;;
			return `<div class="alert alert-success">
			        	✅ Upload Complete: ${processed} rows processed, ${skipped} skipped. <br>
			        	📊 Success Rate: <strong>${successRate}%</strong>
			        </div>`;
		}
	}
});