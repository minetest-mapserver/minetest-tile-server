
(function(tileserver){


    var playerMarkers = {}; // name -> L.Marker

    function updatePoi(poi) {
        if (!poi.active)
            return;

        var marker = L.marker([poi.z - 15, poi.x]);

        var popup = "<h4>" + poi.name + "</h4><hr>" +
          "<b>X: </b> " + poi.x + "<br>" +
          "<b>Y: </b> " + poi.y + "<br>" +
          "<b>Z: </b> " + poi.z + "<br>" +
          "<b>Category: </b> " + poi.category + "<br>" +
          "<b>Owner: </b> " + poi.owner + "<br>";

        marker.bindPopup(popup).addTo(tileserver.poiLayer);

    }

    function update(){
      m.request({ url: "poi" })
      .then(function(list){
        list.forEach(updatePoi);
      });
    }

    //initial update
    update();

})(window.tileserver);