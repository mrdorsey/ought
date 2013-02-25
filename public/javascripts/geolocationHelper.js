var GeolocationHelper = {
	latitude: null,
	longitude: null,
	error: "",
		
	getUserLocation: function() {
		if(navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(this.saveCoordinates, this.handleErrors);
		}
		else {
			this.error = "Geolocation is not supported by this browser.";
		}
	},
	
	handleErrors: function(error) {
		switch(error.code) {
		    case error.PERMISSION_DENIED:
		    	this.error = "User denied the request for Geolocation."
		    	break;
		    case error.POSITION_UNAVAILABLE:
		    	this.error = "Location information is unavailable."
		    	break;
		    case error.TIMEOUT:
		    	this.error = "The request to get user location timed out."
		    	break;
		    case error.UNKNOWN_ERROR:
		    	this.error = "An unknown error occurred."
		    	break;
	    }
	},
		
	saveCoordinates: function(position) {
		this.latitude = position.coords.latitude;
		this.longitude = position.coords.longitude;
	}
}