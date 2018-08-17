
(function(tileserver){

    var travelnetLayer = L.layerGroup();

    var TravelnetIcon = L.icon({
        iconUrl: 'pics/travelnet_inv.png',

        iconSize:     [64, 64],
        iconAnchor:   [32, 32],
        popupAnchor:  [0, -32]
    });

    function updateTravelnet(travelnet) {
        var marker = L.marker([travelnet.z - 16, travelnet.x], {icon: TravelnetIcon});

        var popup = "<h4>" + travelnet.name + "</h4><hr>" +
          "<b>X: </b> " + travelnet.x + "<br>" +
          "<b>Y: </b> " + travelnet.y + "<br>" +
          "<b>Z: </b> " + travelnet.z + "<br>" +
          "<b>Network: </b> " + travelnet.network + "<br>" +
          "<b>Owner: </b> " + travelnet.owner + "<br>";

        marker.bindPopup(popup).addTo(travelnetLayer);

    }

    function update(){
      m.request({ url: "travelnet" })
      .then(function(list){
        list.forEach(updateTravelnet);
      });
    }

    tileserver.overlays["Travelnet"] = travelnetLayer;
    tileserver.defaultOverlays.push(travelnetLayer);


    //initial update
    update();

})(window.tileserver);