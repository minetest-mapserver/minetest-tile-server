(function(){

	function getDistance(p1, p2){
		var dx = p1.x - p2.x;
		var dy = p1.y - p2.y;
		var dz = p1.z - p2.z;

		var dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));

		return Math.floor(dist);
	}

	var hasLocation = false;
	var location = {x:0, y:0, z:0};

	var hashParts = window.location.hash.substring(1).split("/");
	if (hashParts.length == 3){
		hasLocation = true;
		location.x = +hashParts[0];
		location.y = +hashParts[1];
		location.z = +hashParts[2];
	}

	var state = {
		keyword: ""
	};

	var shops = [];
	var pois = [];

	function onSearchKeyword(e){
		state.keyword = e.target.value;
	}

	var SearchField = m("input", {type: "text", class: "form-control", oninput: onSearchKeyword, placeholder: "Search POI or shop items"});

	var PoiResults = {
		view: function(){
			var rows = [];

			var matchingPois = [];
			pois.forEach(function(poi){
				var match = false;

				if (poi.owner.toLowerCase().indexOf(state.keyword.toLowerCase()) >= 0)
					match = true;

				if (poi.name && poi.name.toLowerCase().indexOf(state.keyword.toLowerCase()) >= 0)
					match = true;

				if (!match)
					return;

				matchingPois.push(poi);
			});

			if (hasLocation){
				matchingPois.sort(function(a,b){
					var dist_a = getDistance(location, a);
					var dist_b = getDistance(location, b);

					return dist_a - dist_b;
				});
			}



			matchingPois.forEach(function(poi){

				var Range = null;

				if (hasLocation){
					var distance = getDistance(location, poi);
					Range = m("span", {class:"badge badge-primary"}, distance + " m");
				}

				var mapLink = "./#" + poi.x + "/" + poi.z + "/13";

				rows.push(m("tr", [
					m("td", m("span", [
							m("img", {src:"css/images/marker-icon.png"}),
							" Point of Interest"
						])
					),
					m("td", poi.owner),
					m("td", poi.name),
					m("td", [m("a", {href: mapLink}, poi.x + "/" + poi.y + "/" + poi.z), " ", Range])
				]));
			});

			return rows;
		}
	};

	var ShopResults = {
		view: function(){
			var rows = [];

			var matchingShops = [];
			shops.forEach(function(shop){
				var match = false;

				if (shop.owner.toLowerCase().indexOf(state.keyword.toLowerCase()) >= 0)
					match = true;

				if (shop.inItem && shop.inItem.toLowerCase().indexOf(state.keyword.toLowerCase()) >= 0)
					match = true;

				if (shop.outItem && shop.outItem.toLowerCase().indexOf(state.keyword.toLowerCase()) >= 0)
					match = true;

				if (!match)
					return;

				matchingShops.push(shop);
			});

			if (hasLocation){
				matchingShops.sort(function(a,b){
					var dist_a = getDistance(location, a);
					var dist_b = getDistance(location, b);

					return dist_a - dist_b;
				});
			}

			matchingShops.forEach(function(shop){

				var icon = "pics/shop.png";

				if (shop.outStock == 0)
					icon = "pics/shop_empty.png";

				var mapLink = "./#" + shop.x + "/" + shop.z + "/13";

				var Range = null;

				if (hasLocation){
					var distance = getDistance(location, shop);
					Range = m("span", {class:"badge badge-primary"}, distance + " m");
				}

				rows.push(m("tr", [
					m("td", m("span", [
							m("img", {src:icon}),
							"Shop ",
							m("span", {class:"badge badge-secondary"}, shop.type)
						])
					),
					m("td", shop.owner),
					m("td", [
						"Trading ",
						m("span", {class:"badge badge-primary"}, shop.inCount + "x"),
						m("span", {class:"badge badge-secondary"}, shop.inItem),
						" for ",
						m("span", {class:"badge badge-primary"}, shop.outCount + "x"),
						m("span", {class:"badge badge-secondary"}, shop.outItem),
						" (" + shop.outStock + " in stock)"
					]),
					m("td", [m("a", {href: mapLink}, shop.x + "/" + shop.y + "/" + shop.z), " ", Range])
				]));

			});

			return rows;
		}
	};

	var ResultTable = {
		view: function(){
			return m("table", {class:"table table-condensed table-striped"}, [
				m("thead", [
					m("tr", [
						m("th", "Type"),
						m("th", "Owner"),
						m("th", "Description"),
						m("th", "Location/Distance"),
					])
				]),
				m(PoiResults),
				m(ShopResults)
			]);
		}
	};

	var App = {
		view: function(){
			return m("div", {class: "row"}, [
				SearchField, m(ResultTable)
			]);
		}
	};

	//TODO: travelnet
	m.request("shop")
	.then(function(_shops){ shops = _shops; });

	m.request("poi")
	.then(function(_pois){ pois = _pois; });

	m.mount(document.getElementById("app"), App);

})();