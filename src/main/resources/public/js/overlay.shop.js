
(function(tileserver){

    var shopLayer = L.layerGroup();

    var ShopIcon = L.icon({
        iconUrl: 'pics/shop.png',

        iconSize:     [32, 32],
        iconAnchor:   [16, 16],
        popupAnchor:  [0, -16]
    });

    function updateShop(shop) {

        if (shop.y < tileserver.currentHeight.from || shop.y > tileserver.currentHeight.to)
            //ignore block from different height
            return;

        var marker = L.marker([shop.z, shop.x], {icon: ShopIcon});

        var popup = "<h4>" + shop.type + "</h4><hr>" +
          "<b>Owner: </b> " + shop.owner + "<br>" +
          "<b>In item: </b> " + shop.inItem + "<br>" +
          "<b>Out item: </b> " + shop.outItem + "<br>" +
          "<b>Stock: </b> " + shop.outStock + "<br>" +
          "<b>X: </b> " + shop.x + "<br>" +
          "<b>Y: </b> " + shop.y + "<br>" +
          "<b>Z: </b> " + shop.z + "<br>";

        marker.bindPopup(popup).addTo(shopLayer);

    }

    function update(){
      m.request({ url: "shop" })
      .then(function(list){
        shopLayer.clearLayers();
        list.forEach(updateShop);
      });
    }

    //update on height change
    tileserver.heightChangedCallbacks.push(update);


    tileserver.overlays["Shops"] = shopLayer;
    tileserver.defaultOverlays.push(shopLayer);

    //initial update
    update();

})(window.tileserver);