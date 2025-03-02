const AuthAPI = {
	saveToken: function (token) {
		localStorage.setItem("jwt", token);
	},

	removeToken: function () {
		localStorage.removeItem("jwt");
	},

	getToken: function () {
		return localStorage.getItem("jwt") || "";
	},

	getUsername: function () {
		const token = this.getToken();
		if (!token) return null;
		try {
			const payload = JSON.parse(atob(token.split('.')[1]));
			// Extract username from subject which is the user's email			
			return payload.sub.split("@")[0] || "Unknown User";
		} catch (e) {
			return null; // Invalid token
		}
	},

	getUserRole: function () {
		const token = this.getToken();
		if (!token) return "GUEST";
		try {
			const payload = JSON.parse(atob(token.split('.')[1]));
			return payload.role || "GUEST";
		} catch (e) {
			return "GUEST"; // Invalid token
		}
	},

	isLoggedIn: function () {
		return !!this.getToken(); // Checks if a token exists
	},
};
