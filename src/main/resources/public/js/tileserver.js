

window.tileserver = {
    //start()

    websocketCallbacks: [], //[fn(event),...]

    heightChangedCallbacks: [], //[fn(fromY, toY), ... ]
    currentHeight: {
        from: 0,
        to: 100
    },

    mapElements: [], //custom elements added with el.addTo(map)

    overlays: {}, //name: Layer
    defaultOverlays: [], //Layer

    layers: {}, //name: Layer
    defaultLayers: [] //Layer
};
