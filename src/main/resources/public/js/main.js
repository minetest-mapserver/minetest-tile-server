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

var playerMarkers = {}; // name -> L.Marker

function playerMapper(info) {
    var player = info.player;
    var marker = playerMarkers[player.name];
    var zOffset = 15;
    var latLng = [(player.posz/10)-zOffset, player.posx/10];

    var age = Date.now() - player.modificationDate;

    var popup = "<h4>" + player.name + "</h4><hr>" +
      "<b>X: </b> " + player.posx/10 + "<br>" +
      "<b>Y: </b> " + player.posy/10 + "<br>" +
      "<b>Z: </b> " + player.posz/10 + "<br>" +
      "<b>XP: </b> " + info.metadata.xp + "<br>" +
      "<b>Deathcount: </b> " + info.metadata.died + "<br>" +
      "<b>Playtime: </b> " + moment.duration((info.metadata.played_time || 1) * 1000).humanize() + "<br>" +
      "<b>Digcount: </b> " + info.metadata.digged_nodes + "<br>" +
      "<b>Craftcount: </b> " + info.metadata.crafted + "<br>" +
      "<b>Placecount: </b> " + info.metadata.placed_nodes + "<br>" +
      "<b>Health: </b> " + player.hp + "<br>" +
      "<b>Breath: </b> " + player.breath + "<br>" +
      "<b>Last login: </b> " + moment.duration(age).humanize();



    if (!marker){
      //Create marker
      marker = L.marker(latLng);
      marker.bindPopup(popup).addTo(map);
      playerMarkers[player.name] = marker;

    } else {
      //Update marker
      marker.setLatLng(latLng);
      marker.bindPopup(popup);

    }

}

function updatePlayers(){
  fetch("player")
  .then(function(res) { return res.json(); })
  .then(function(list) { list.forEach(playerMapper); });
}

updatePlayers();

var wsUrl = location.protocol.replace("http", "ws") + "//" + location.host + location.pathname.substring(0, location.pathname.lastIndexOf("/")) + "/ws";
var ws = new WebSocket(wsUrl);
ws.onmessage = function(e){
    var event = JSON.parse(e.data);
    if (event.type === "player-move"){
        playerMapper(event.data.info);
    }

    if (event.type === "tile-update"){
        var id = getImageId(event.data.x, event.data.y, event.data.zoom);
        var el = document.getElementById(id);

        if (el){
            el.src = getTileSource(event.data.x, event.data.y, event.data.zoom, true);
        }

    }
}

