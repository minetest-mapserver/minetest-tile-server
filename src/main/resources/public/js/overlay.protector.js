
(function(tileserver){
	
	var layerGroup = L.layerGroup();
	tileserver.overlays["Protections"] = layerGroup;

	var layerId = 0;

	function updateProtectors(map){
		var latlng = map.getCenter();
		var zoom = map.getZoom();

        if (zoom < 12){
    		layerGroup.clearLayers();
            return; //too much info
        }

		m.request("protector/" + layerId + "/" + parseInt(latlng.lng) + "/" + parseInt(latlng.lat))
		.then(function(list){
    		layerGroup.clearLayers();

			var geoJsonLayer = L.geoJSON([], {
			    onEachFeature: function(feature, layer){
			        if (feature.properties && feature.properties.popupContent) {
                        layer.bindPopup(feature.properties.popupContent);
                    }
			    }
			});

			list.forEach(function(protector){
				var feature = {
					"type":"Feature",
					"geometry": {
						"type":"Polygon",
						"coordinates":[[
						    [protector.x-5,protector.z-5],
						    [protector.x-5,protector.z+6],
						    [protector.x+6,protector.z+6],
						    [protector.x+6,protector.z-5],
						    [protector.x-5,protector.z-5]
						]]
					},
					"properties":{
					    "name": protector.owner,
					    "popupContent": protector.owner
					}
				}
				geoJsonLayer.addData(feature);
			});

			geoJsonLayer.addTo(layerGroup);
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

			updateProtectors(map);
		});

		updateProtectors(map);
	});



})(window.tileserver);