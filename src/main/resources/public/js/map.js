
(function(tileserver){


    var crs = L.Util.extend({}, L.CRS.Simple, {
        //transformation: L.transformation(0.001, 0, -0.001, 0),
        scale: function (zoom) {
            return Math.pow(2, zoom-9);
        }
    });

    var initialZoom = 11;
    var initialCenter = [0, 0];

    var hashParts = location.hash.substring(1).split("/");
    if (hashParts.length == 3){
      initialCenter[0] = +hashParts[1];
      initialCenter[1] = +hashParts[0];
      initialZoom = +hashParts[2]
    }

    var map = L.map('image-map', {
      minZoom: 1,
      maxZoom: 13,
      center: initialCenter,
      zoom: initialZoom,
      crs: crs
    });

    //works: map.on('mousemove', ev => console.log(ev.latlng));

    function updateHash(){
      var center = map.getCenter();
      location.hash = center.lng + "/" + center.lat + "/" + map.getZoom();
    }

    map.on('zoomend', updateHash)
    map.on('moveend', updateHash)
    updateHash();

    function getTileSource(x,y,zoom,cacheBust){
        return "tiles/" + zoom + "/" + x + "/" + y + (cacheBust ? "?_=" + Date.now() : "");
    }

    function getImageId(x,y,zoom){
        return "tile-" + x + "/" + y + "/" + zoom;
    }

    var RealtimeTileLayer = L.TileLayer.extend({
      createTile: function(coords){
        var tile = document.createElement('img');
        tile.src = getTileSource(coords.x, coords.y, coords.z);
        tile.id = getImageId(coords.x, coords.y, coords.z);
        return tile;
      }
    });

    var tileLayer = new RealtimeTileLayer();
    tileLayer.addTo(map);

    //L.tileLayer('tiles/{z}/{x}/{y}').addTo(map);
    //L.marker([-207, 7]).bindPopup("Spawn").addTo(map);


    function updateTile(data){
        var id = getImageId(data.x, data.y, data.zoom);
        var el = document.getElementById(id);

        if (el){
            //Update src attribute if img found
            el.src = getTileSource(data.x, data.y, data.zoom, true);
        }
    }

    //Export
    tileserver.map = map;
    tileserver.updateTile = updateTile;

})(window.tileserver);