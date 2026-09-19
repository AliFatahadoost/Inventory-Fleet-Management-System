// ============================================================
// JALI MAP BOX
//
// Modes:
//   write — user clicks to pick lat/long
//   read  — shows a route between two or more points via OSRM
// ============================================================

class MapBox extends HTMLElement {

    constructor() {

        super();

        this.attachShadow({ mode: "open" });

        // ----------------------------------------------------
        // LEAFLET CSS — MUST live in the shadow root, otherwise
        // its positioning rules never reach the map elements.
        // ----------------------------------------------------

        const leafletCss = document.createElement("link");
        leafletCss.rel = "stylesheet";
        leafletCss.href = "http://127.0.0.1:8080/leafletCss";
        this.shadowRoot.appendChild(leafletCss);

        // ----------------------------------------------------
        // COMPONENT CSS
        // ----------------------------------------------------

        const boxCss = document.createElement("link");
        boxCss.rel = "stylesheet";
        boxCss.href = "http://127.0.0.1:8080/mapBoxCss";
        this.shadowRoot.appendChild(boxCss);

        // ----------------------------------------------------
        // CONTAINER
        // ----------------------------------------------------

        this.container = document.createElement("div");
        this.container.className = "jali-map-container";
        this.shadowRoot.appendChild(this.container);

        this._map = null;
        this._marker = null;
        this._routeLine = null;
        this._routeGroup = null;
        this._initialized = false;
        this._ro = null;
    }

    // ========================================================
    // ATTRIBUTES
    // ========================================================

    get mode() {
        return (this.getAttribute("mode") || "write").toLowerCase();
    }

    get value() {
        return this.getAttribute("value") || "";
    }

    get points() {
        return this.getAttribute("points") || "";
    }

    get zoom() {
        const z = parseInt(this.getAttribute("zoom"), 10);
        return Number.isFinite(z) ? z : 13;
    }

    get height() {
        return this.getAttribute("height") || "320px";
    }

    get osrmUrl() {
        return this.getAttribute("osrm-url") || "https://router.project-osrm.org";
    }

    get placeholder() {
        return this.getAttribute("placeholder") || "Click on the map to pick a location";
    }

    // ========================================================
    // CONNECTED
    // ========================================================

    async connectedCallback() {

        if (this._initialized) return;
        this._initialized = true;

        this.container.style.height = this.height;

        await this._loadLeaflet();

        this._configureLeafletDefaults();

        // Let the modal finish laying out before we measure anything
        await new Promise(r => setTimeout(r, 80));

        this._initMap();

        // Re-measure once more after a longer delay, in case the
        // modal animation is still running
        setTimeout(() => {
            if (this._map) this._map.invalidateSize();
        }, 250);

        // Keep it responsive
        if (window.ResizeObserver) {
            this._ro = new ResizeObserver(() => {
                if (this._map) this._map.invalidateSize();
            });
            this._ro.observe(this.container);
        }

        if (this.mode === "read") {
            await this._renderRoute();
        } else {
            this._renderWriteMode();
        }
    }

    // ========================================================
    // LEAFLET LOADER (global script, once per page)
    // ========================================================

    _loadLeaflet() {

        if (window.L) return Promise.resolve();

        if (this._leafletPromise) return this._leafletPromise;

        this._leafletPromise = new Promise((resolve, reject) => {

            const script = document.createElement("script");
            script.src = "http://127.0.0.1:8080/leafletJs";
            script.onload = () => resolve();
            script.onerror = () => reject(new Error("Failed to load Leaflet"));
            document.head.appendChild(script);
        });

        return this._leafletPromise;
    }

    // ========================================================
    // LEAFLET DEFAULTS — marker icons
    //
    // Leaflet auto-detects icon URLs by reading a magic CSS
    // class from the *document* — which doesn't work when our
    // Leaflet CSS is scoped to a shadow root. So we pin the
    // URLs directly.
    // ========================================================

    _configureLeafletDefaults() {

        if (!window.L || window.__jaliLeafletDefaults) return;
        window.__jaliLeafletDefaults = true;

        L.Icon.Default.mergeOptions({
            iconUrl:      "http://127.0.0.1:8080/leafletMarkerIcon",
            iconRetinaUrl:"http://127.0.0.1:8080/leafletMarkerIcon2x",
            shadowUrl:    "http://127.0.0.1:8080/leafletMarkerShadow",
        });
    }

    // ========================================================
    // MAP INIT
    // ========================================================

    _initMap() {

        const initial =
            this.mode === "read"
                ? this._parsePoints(this.points)[0] || [35.6892, 51.3890]
                : this._parseValue(this.value) || [35.6892, 51.3890];

        this._map = L.map(this.container, {
            center: initial,
            zoom: this.zoom,
            zoomControl: true,
            attributionControl: true
        });

        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
            maxZoom: 19,
            attribution: "&copy; OpenStreetMap contributors"
        }).addTo(this._map);

        // One more resize after tile layer attaches
        setTimeout(() => this._map && this._map.invalidateSize(), 100);
    }

    // ========================================================
    // WRITE MODE
    // ========================================================

    _renderWriteMode() {

        this._map.on("click", (e) => {

            const lat = e.latlng.lat;
            const lng = e.latlng.lng;

            this._setMarker(lat, lng);

            const value = `${lat.toFixed(6)},${lng.toFixed(6)}`;

            this.dataset.selectedValue = value;
            this.setAttribute("value", value);

            this.dispatchEvent(new Event("change", { bubbles: true }));
        });

        const initial = this._parseValue(this.value);

        if (initial) {
            this._setMarker(initial[0], initial[1]);
            this.dataset.selectedValue =
                `${initial[0].toFixed(6)},${initial[1].toFixed(6)}`;
        }

        this._map.getContainer().title = this.placeholder;
    }

    _setMarker(lat, lng) {

        if (this._marker) {
            this._marker.setLatLng([lat, lng]);
        } else {
            this._marker = L.marker([lat, lng]).addTo(this._map);
        }
    }

    // ========================================================
    // READ MODE
    // ========================================================

        async _renderRoute() {

        if (!this._map) return;

        // Clear previous route + markers
        if (this._routeGroup) {
            this._map.removeLayer(this._routeGroup);
            this._routeGroup = null;
        }

        const pts = this._parsePoints(this.points);

        if (pts.length < 2) {

            if (pts.length === 1) {
                this._routeGroup = L.layerGroup().addTo(this._map);
                L.marker(pts[0]).addTo(this._routeGroup);
                this._map.setView(pts[0], this.zoom);
            }
            return;
        }

        this._routeGroup = L.layerGroup().addTo(this._map);

        L.marker(pts[0])
            .addTo(this._routeGroup)
            .bindTooltip("Start", { permanent: true, direction: "top" });

        L.marker(pts[pts.length - 1])
            .addTo(this._routeGroup)
            .bindTooltip("End", { permanent: true, direction: "top" });

        const coordString = pts
            .map(p => `${p[1]},${p[0]}`)
            .join(";");

        const url =
            `${this.osrmUrl}/route/v1/driving/${coordString}` +
            `?overview=full&geometries=polyline`;

        try {

            const response = await fetch(url);
            if (!response.ok) throw new Error(`OSRM HTTP ${response.status}`);

            const data = await response.json();
            if (!data.routes || data.routes.length === 0) {
                throw new Error("OSRM returned no routes");
            }

            const route = data.routes[0];
            const coords = this._decodePolyline(route.geometry);

            const line = L.polyline(coords, {
                color: "#2563eb",
                weight: 5,
                opacity: 0.85
            }).addTo(this._routeGroup);

            this._map.fitBounds(line.getBounds(), { padding: [30, 30] });

            this.dataset.distanceMeters = String(route.distance);
            this.dataset.durationSeconds = String(route.duration);
            this.dataset.polyline = route.geometry;

            this.dispatchEvent(new CustomEvent("route-ready", {
                bubbles: true,
                detail: {
                    distance: route.distance,
                    duration: route.duration,
                    polyline: route.geometry
                }
            }));

        } catch (err) {

            console.error("MapBox OSRM error:", err);

            const fallback = L.polyline(pts, {
                color: "#dc2626",
                weight: 4,
                dashArray: "8,6"
            }).addTo(this._routeGroup);

            this._map.fitBounds(fallback.getBounds(), { padding: [30, 30] });
        }
    }

    // ========================================================
    // PUBLIC API — live re-render
    // ========================================================

    setPoints(pointsString) {

        this.setAttribute("points", pointsString || "");

        if (this._map) {
            this._renderRoute();
        }
    }

    _addRouteMarker(point, label) {

        const marker = L.marker(point).addTo(this._map);
        marker.bindTooltip(label, { permanent: true, direction: "top" });
        return marker;
    }

    // ========================================================
    // POLYLINE DECODER
    // ========================================================

    _decodePolyline(encoded) {

        const points = [];
        let index = 0;
        let lat = 0;
        let lng = 0;

        while (index < encoded.length) {

            let shift = 0;
            let result = 0;
            let byte;

            do {
                byte = encoded.charCodeAt(index++) - 63;
                result |= (byte & 0x1f) << shift;
                shift += 5;
            } while (byte >= 0x20);

            const dlat = (result & 1) ? ~(result >> 1) : (result >> 1);
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                byte = encoded.charCodeAt(index++) - 63;
                result |= (byte & 0x1f) << shift;
                shift += 5;
            } while (byte >= 0x20);

            const dlng = (result & 1) ? ~(result >> 1) : (result >> 1);
            lng += dlng;

            points.push([lat / 1e5, lng / 1e5]);
        }

        return points;
    }

    // ========================================================
    // PARSERS
    // ========================================================

    _parseValue(str) {

        if (!str) return null;

        const parts = str.split(",");
        if (parts.length !== 2) return null;

        const lat = parseFloat(parts[0]);
        const lng = parseFloat(parts[1]);

        if (!Number.isFinite(lat) || !Number.isFinite(lng)) return null;

        return [lat, lng];
    }

    _parsePoints(str) {

        if (!str) return [];

        return str
            .split(";")
            .map(s => this._parseValue(s.trim()))
            .filter(v => v !== null);
    }

    // ========================================================
    // DISCONNECTED
    // ========================================================

    disconnectedCallback() {

        if (this._ro) {
            this._ro.disconnect();
            this._ro = null;
        }

        if (this._map) {
            this._map.remove();
            this._map = null;
        }
    }
}

if (!customElements.get("map-box")) {
    customElements.define("map-box", MapBox);
}