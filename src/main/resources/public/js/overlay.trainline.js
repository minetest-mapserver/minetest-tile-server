
(function(tileserver){

	var layerGroup = L.layerGroup();

	var geojsonMarkerOptions = {
		radius: 8,
		fillColor: "#ff7800",
		color: "#000",
		weight: 1,
		opacity: 1,
		fillOpacity: 0.8
	};

	function update(){
		m.request({ url: "trainline" })
		.then(function(list){
			layerGroup.clearLayers();
			var geoJsonLayer = L.geoJSON([], {
				onEachFeature: function(feature, layer){
					if (feature.properties && feature.properties.popupContent) {
						layer.bindPopup(feature.properties.popupContent);
					}
				},
				pointToLayer: function (feature, latlng) {
					return L.circleMarker(latlng, geojsonMarkerOptions);
				}
			});
			geoJsonLayer.addTo(layerGroup);



			var lines = {}; // { "A1":[] }

			//Sort by line
			list.forEach(function(entry){
				var line = lines[entry.line];
				if (!line){
					line = [];
					lines[entry.line] = line;
				}

				line.push(entry);
			});

			//Order by index and display
			Object.keys(lines).forEach(function(linename){
				lines[linename].sort(function(a,b){
					return a.index - b.index;
				});


				var coords = [];
				var stations = [];

				lines[linename].forEach(function(entry){
					coords.push([entry.x, entry.z]);


					if (entry.station) {
						stations.push({
							"type": "Feature",
							"properties": {
								"name": entry.station,
								"popupContent": "<b>Train-station (Line " + entry.line + ")</b><hr>" + 
									entry.station
							},
							"geometry": {
								"type": "Point",
								"coordinates": [entry.x, entry.z]
							}
						});

					}
				});

				var feature = {
					"type":"Feature",
					"geometry": {
						"type":"LineString",
						"coordinates":coords
					},
					"properties":{
					    "name": linename,
					    "popupContent": "<b>Train-line (" + linename + ")</b>"
					}
				}
				//line-points
				geoJsonLayer.addData(feature);

				//stations
				stations.forEach(function(stationfeature){
					geoJsonLayer.addData(stationfeature);
				});

			});

		});
	}

	//initial update
	update();

	//update on height change
	tileserver.heightChangedCallbacks.push(update);

	tileserver.overlays["Trainlines"] = layerGroup;
	tileserver.defaultOverlays.push(layerGroup);

})(window.tileserver);