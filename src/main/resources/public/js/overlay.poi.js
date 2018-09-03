
(function(tileserver){

    var poiLayer = L.layerGroup();

    var playerMarkers = {}; // name -> L.Marker

    function updatePoi(poi) {
        if (!poi.active)
            return;

       if (poi.y < tileserver.currentHeight.from || poi.y > tileserver.currentHeight.to)
            //ignore block from different height
            return;

        var marker = L.marker([poi.z, poi.x]);

        var popup = "<h4>" + poi.name + "</h4><hr>" +
          "<b>X: </b> " + poi.x + "<br>" +
          "<b>Y: </b> " + poi.y + "<br>" +
          "<b>Z: </b> " + poi.z + "<br>" +
          "<b>Category: </b> " + poi.category + "<br>" +
          "<b>Owner: </b> " + poi.owner + "<br>";

        marker.bindPopup(popup).addTo(poiLayer);
    }

    function update(){
      m.request({ url: "poi" })
      .then(function(list){
        poiLayer.clearLayers();
        list.forEach(updatePoi);
      });
    }

    //initial update
    update();

    //update on height change
    tileserver.heightChangedCallbacks.push(update);


    tileserver.overlays["POI"] = poiLayer;
    tileserver.defaultOverlays.push(poiLayer);

})(window.tileserver);