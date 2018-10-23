
var httpProxy = require('http-proxy'),
    http = require("http"),
    serveStatic = require('serve-static'),
    connect   = require('connect');


var proxy = httpProxy.createProxy({
	target: "https://pandorabox.io/map",
	secure: false
});

var app = connect()
.use("/", function(req, res, next){
	if (req.url.substring(0,3) == "/js" || req.url == "/" || req.url.substring(0,5) == "/pics"){
		console.log("Local: " + req.url);
		next();
		return;
	}

	console.log("Remote: " + req.url);
	proxy.web(req, res);
})
.use(serveStatic("src/main/resources/public"));

var server = http.createServer(app);

server.listen(8080);

console.log("Listening on http://127.0.0.1:8080");
