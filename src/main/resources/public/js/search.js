(function(){


	var state = {
		keyword: ""
	};

	var shops = [];

	function onSearchKeyword(e){
		state.keyword = e.target.value;
	}

	var SearchField = m("input", {type: "text", class: "form-control", oninput: onSearchKeyword, placeholder: "Search POI or shop items"});

	var ResultRows = {
		view: function(){
			var rows = [];

			shops.forEach(function(shop){
				var match = false;

				if (shop.owner.indexOf(state.keyword) >= 0)
					match = true;

				if (shop.inItem.indexOf(state.keyword) >= 0)
					match = true;

				if (shop.outItem.indexOf(state.keyword) >= 0)
					match = true;

				if (!match)
					return;

				var icon = "pics/shop.png";

				if (shop.outStock == 0)
					icon = "pics/shop_empty.png";

				rows.push(m("tr", [
					m("td", m("span", [
							m("img", {src:icon}),
							shop.owner + " ",
							m("span", {class:"badge badge-secondary"}, shop.type)
						])
					),
					m("td", [m("span", {class:"badge badge-primary"}, shop.inCount + "x"), " " + shop.inItem]),
					m("td", [m("span", {class:"badge badge-primary"}, shop.outCount + "x"), " " + shop.outItem]),
					m("td", shop.outStock),
					m("td", shop.x + "/" + shop.y + "/" + shop.z)
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
						m("th", "Owner"),
						m("th", "In-item"),
						m("th", "Out-item"),
						m("th", "Stock"),
						m("th", "Location/Distance"),
					])
				]),
				m(ResultRows)
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

	//TODO: POI, travelnet
	m.request("shop")
	.then(function(_shops){ shops = _shops; });

	m.mount(document.getElementById("app"), App);
})();