
tileserver.start = function(cfg, layerConfig){

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

    function getTileSource(layerId, x,y,zoom,cacheBust){
        return "tiles/" + layerId + "/" + zoom + "/" + x + "/" + y + (cacheBust ? "?_=" + Date.now() : "");
    }

    function getImageId(layerId, x, y, zoom){
        return "tile-" + layerId + "/" + x + "/" + y + "/" + zoom;
    }

    function createTileLayer(layerId){
        return L.TileLayer.extend({
          createTile: function(coords){
            var tile = document.createElement('img');
            tile.src = getTileSource(layerId, coords.x, coords.y, coords.z);
            tile.id = getImageId(layerId, coords.x, coords.y, coords.z);
            return tile;
          }
        });
    }

    function updateTile(data){
        var id = getImageId(data.layerId, data.x, data.y, data.zoom);
        var el = document.getElementById(id);

        if (el){
            //Update src attribute if img found
            el.src = getTileSource(data.layerId, data.x, data.y, data.zoom, true);
        }
    }


    tileserver.websocketCallbacks.push(function(event){
        if (event.type === "tile-update"){
            updateTile(event.data);
        }
    });

    function filterHeight(fromY, toY){
        tileserver.currentHeight.from = fromY;
        tileserver.currentHeight.to = toY;

        tileserver.heightChangedCallbacks.forEach(function(cb){
            cb(fromY, toY)
        });
    }

    var layers = {};
    var defaultLayer = true;

    layerConfig.layers.forEach(function(layerDef){
        var Layer = createTileLayer(layerDef.id);
        var tileLayer = new Layer();
        layers[layerDef.name] = tileLayer;

        if (defaultLayer){
            tileLayer.addTo(map);
            defaultLayer = false;
            filterHeight(layerDef.from, layerDef.to);
        }
    });

    L.control.layers(layers, tileserver.overlays).addTo(map);

    map.on("baselayerchange", function(e){
        var layer;
        layerConfig.layers.forEach(function(l){ if (l.name == e.name) layer = l; })

        filterHeight(layer.from, layer.to);
    });

    tileserver.defaultOverlays.forEach(function(overlay){
        map.addLayer(overlay);
    });

    tileserver.mapElements.forEach(function(el){
	el.addTo(map);
    });

    //Export
    tileserver.map = map;

}