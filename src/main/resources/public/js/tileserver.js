

window.tileserver = {
    //start()

    websocketCallbacks: [], //[fn(event),...]

    filterHeightCallbacks: [], //[fn(fromY, toY), ... ]

    overlays: {}, //name: Layer
    defaultOverlays: [], //Layer

    layers: {}, //name: Layer
    defaultLayers: [] //Layer
};
