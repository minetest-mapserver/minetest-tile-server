
(function(tileserver){

	var travelnetLayer = L.layerGroup();

	var TravelnetIcon = L.icon({
		iconUrl: 'pics/travelnet_inv.png',

		iconSize:     [64, 64],
		iconAnchor:   [32, 32],
		popupAnchor:  [0, -32]
	});

	function updateTravelnet(travelnet) {
		if (travelnet.y < tileserver.currentHeight.from || travelnet.y > tileserver.currentHeight.to)
		    //ignore block from different height
		    return;

		var marker = L.marker([travelnet.z, travelnet.x], {icon: TravelnetIcon});

		var popup = "<h4>" + travelnet.name + "</h4><hr>" +
		  "<b>X: </b> " + travelnet.x + "<br>" +
		  "<b>Y: </b> " + travelnet.y + "<br>" +
		  "<b>Z: </b> " + travelnet.z + "<br>" +
		  "<b>Network: </b> " + travelnet.network + "<br>" +
		  "<b>Owner: </b> " + travelnet.owner + "<br>";

		marker.bindPopup(popup).addTo(travelnetLayer);
	}

	var list = [];

	function redraw(map){
		travelnetLayer.clearLayers();

		if (map && map.getZoom && map.getZoom() < 10)
			//too much clutter
			return;

		list.forEach(updateTravelnet);
	}

	function update(){
		m.request({ url: "travelnet" })
		.then(function(_list){
			list = _list;
			redraw();
		});
	}

	tileserver.mapLoadedCallbacks.push(function(map){
		redraw(map);
		map.on("zoomend", function(ev){ redraw(map); });
	});


	//redraw on height change
	tileserver.heightChangedCallbacks.push(redraw);


	tileserver.overlays["Travelnet"] = travelnetLayer;
	tileserver.defaultOverlays.push(travelnetLayer);


	//initial update
	update();

})(window.tileserver);