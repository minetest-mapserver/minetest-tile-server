(function(){

	L.Control.SearchButton = L.Control.extend({
	    onAdd: function(map) {
		var div = L.DomUtil.create('div', 'leaflet-bar leaflet-custom-display');
		function update(ev){
			var center = map.getCenter();
			div.innerHTML = "<a href='search.html#" +
				center.lng + "/" +
				tileserver.currentHeight.from +
				"/" + center.lat +
				"'><img src='pics/search.png'></a>";
		}

		map.on("moveend", update);
		tileserver.mapLoadedCallbacks.push(update);

		return div;
	    },

	    onRemove: function(map) {
	    }
	});

	L.control.searchButton = function(opts) {
	    return new L.Control.SearchButton(opts);
	}

	var el = L.control.searchButton({ position: 'topright' });

	tileserver.mapElements.push(el);


})();