$(document).ready(function () {
    // Get user info from AuthAPI
    let userName = AuthAPI.getUsername(); 
    let userRole = AuthAPI.getUserRole();
    
    // Update UI with user info
    $("#userName").text(userName);
    $("#userRole").text(userRole);

	// Show current time
	function updateTime() {
	    const currentTimeElement = document.getElementById('currentTime');
	    if (!currentTimeElement) {
	        return;
	    }
	    const now = new Date();
	    const hours = now.getHours().toString().padStart(2, '0');
	    const minutes = now.getMinutes().toString().padStart(2, '0');
	    console.log(`Current time: ${hours}:${minutes}`); 
	    currentTimeElement.textContent = `${hours}:${minutes}`;
	}
    
    // Run updateTime immediately and then every second
    updateTime(); 
    setInterval(updateTime, 60000);
});
