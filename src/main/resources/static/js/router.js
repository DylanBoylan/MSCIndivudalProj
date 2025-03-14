// js/router.js
const Router = {
	paths: {
		"login": "/components/login.html",
		"home": "/components/home.html", // Homepage
		"file-upload": "/admin/components/file-upload.html",
		"register": "/admin/components/register.html",
		"accounts": "/admin/components/accounts.html",
		"getMatches": "/analyst/components/getMatches.html",
		"getPlayers": "/analyst/components/getPlayers.html", 
		"getTeams": "/analyst/components/getTeams.html", 
		"getActions": "/analyst/components/getActions.html",
		"goalsGraph": "/analyst/components/goalsGraph.html",
		"pointsGraph": "/analyst/components/pointsGraph.html",
		"nextSession": "/coach/components/nextSession.html",
		"showMatches": "/analyst/components/showMatches.html",
		"showPlayerStats": "/analyst/components/showPlayerStats.html",
		"seasonAnalyzer": "/coach/components/seasonAnalyzer.html"
	},
	
	init: function () {
		$(document).off("click", "[data-page]").on("click", "[data-page]", function (event) {
	    	event.preventDefault();
	    	const page = $(this).attr("href").substring(1); // Remove hash symbol
			console.log(`Page is: ${page}`);
	        Router.navigate(page);
	    });
	    
		// Listen for URL hash changes (back/forward navigation)
	    $(window).off("hashchange").on("hashchange", function () {
	    	let page = location.hash.substring(1) || this.loginPageRef;
			console.log(`Page is: ${page}`);
	    	Router.loadPage(page);
	    });
		
	    // Perform the initial page load based on the current hash (default to "login")
	    let initialPage = location.hash.substring(1) || "login";
	    Router.loadPage(initialPage);
	},

	navigate: function (page) {
    	// Update the URL hash and load the corresponding page
    	history.pushState(null, "", `#${page}`);
    	Router.loadPage(page);
	},
	
	loadPage: function (page) {
		// Enforce authentication: if not authenticated and page isn’t "login", force login.
		if (!AuthAPI.isLoggedIn()) {
      		page = "login"; // Redirect to login
      		history.pushState(null, "", "#login");
   		} else if (page === "login") {
			page = "home"; // Redirect to homepage when you try to go to login page when already logged in
		}
		
		// PAGES --
		// Show or hide the navbar and sidebar based on the page.
	    if (page === "login") {
	    	$("#navbar").hide();
	    	$("#sidebar").hide();
	    } else {
	    	$("#navbar").show();
	    	$("#sidebar").show();
			// Load navbar
			Router.loadContentInto("/components/navbar.html", "navbar");
			// Load Sidebar
			Router.loadContentInto("/components/sidebar.html", "sidebar");
	    }	
		
	   	// Load page content into content space
		Router.loadContentInto(`${this.paths[page]}`, "content");
		
		// Load footer
		Router.loadContentInto("/components/footer.html", "footer");
	},
	
	loadContentInto: function (src, idOfDest) {
		$.get(src, function(responseText) {
			const parser = new DOMParser();
			const contentDoc = parser.parseFromString(responseText, 'text/html');		
	        const bodyContent = $(contentDoc).find('body').html();
			
			// Set content to the content above
	        $(`#${idOfDest}`).html(bodyContent);

	        // If there are scripts in the loaded content, execute them
	        $(responseText).find('script').each(function() {
	            $.getScript($(this).attr('src'));
	        });

	        console.log(`Content Loaded & Scripts Executed from ${src}`);
	    });
	}
};

$(document).ready(function () {
    Router.init();
});