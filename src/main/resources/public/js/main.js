
(function(){

    var config = m.request("config");
    var layers = m.request("layers");

    Promise.all([config, layers])
    .then(function(params){
        var cfg = params[0];
        var layers = params[1];

        tileserver.start(cfg, layers);
    });

})();

