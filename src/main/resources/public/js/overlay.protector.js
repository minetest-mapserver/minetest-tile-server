
(function(tileserver){
	
	var layerGroup = L.layerGroup();
	tileserver.overlays["Protections"] = layerGroup;

	var layerId = 0;

	function updateProtectors(map){
		var latlng = map.getCenter();
		m.request("protector/" + layerId + "/" + parseInt(latlng.lng) + "/" + parseInt(latlng.lat))
		.then(function(list){
			layerGroup.clearLayers();

			geoJsonLayer = L.geoJSON();
			list.forEach(function(protector){
				var feature = {
					"type":"Feature",
					"geometry": {
						"type":"Point",
						"coordinates":[protector.x,protector.z]
					},
					"properties":{"name":protector.owner}
				}
				geoJsonLayer.addData(feature);
			});

			geoJsonLayer.addTo(layerGroup);
			console.log(list);
		});
	}


	tileserver.mapLoadedCallbacks.push(function(map){
		map.on("zoomend", function(ev){ updateProtectors(map); });
		map.on("moveend", function(ev){ updateProtectors(map); });

		map.on("baselayerchange", function(ev){
			tileserver.layers.forEach(function(layer){
				if (layer.name == ev.name)
					layerId = layer.id;
			});
		});

		updateProtectors(map);
	});



})(window.tileserver);