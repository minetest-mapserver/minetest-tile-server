
(function(tileserver){

    var wsUrl = location.protocol.replace("http", "ws") + "//" + location.host + location.pathname.substring(0, location.pathname.lastIndexOf("/")) + "/ws";
    var ws = new WebSocket(wsUrl);
    ws.onmessage = function(e){
        var event = JSON.parse(e.data);
        tileserver.websocketCallbacks.forEach(function(cb){
            cb(event);
        });
    }

})(window.tileserver);