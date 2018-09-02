(function(){

	L.Control.Search = L.Control.extend({
	    onAdd: function(map) {
		var div = L.DomUtil.create('div', 'leaflet-bar leaflet-custom-display');

		div.innerHTML = "<input type='text' placeholder='Search'/>";

		return div;
	    },

	    onRemove: function(map) {
	    }
	});

	L.control.search = function(opts) {
	    return new L.Control.Search(opts);
	}

	var el = L.control.search({ position: 'bottomright' });

	tileserver.mapElements.push(el);


})();