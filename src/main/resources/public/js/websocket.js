
(function(tileserver){

	var wsUrl = location.protocol.replace("http", "ws") + "//" + location.host + location.pathname.substring(0, location.pathname.lastIndexOf("/")) + "/ws";

	function connect(){
		var ws = new WebSocket(wsUrl);
		ws.onmessage = function(e){
			var event = JSON.parse(e.data);
			tileserver.websocketCallbacks.forEach(function(cb){
				cb(event);
			});
		}

		ws.onerror = function(e){
			setTimeout(connect, 2500);
		}

	}

	//Initial connect
	connect();

})(window.tileserver);