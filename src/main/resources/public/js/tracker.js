
(function(tileserver){

  function setupTracker(url, id){
      var _paq = _paq || [];
      _paq.push(['trackPageView']);
      _paq.push(['enableLinkTracking']);
      _paq.push(['setTrackerUrl', url]);
      _paq.push(['setSiteId', id]);
      var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
      g.type='text/javascript'; g.async=true; g.defer=true; g.src=url; s.parentNode.insertBefore(g,s);

      window._paq = _paq;
  }

  m.request({ url: "config" })
  .then(function(cfg){
    var id = cfg["matomo.id"];
    var url = cfg["matomo.url"];
    if (cfg && id && url){
        setupTracker(url, id);
    }
  });



})();