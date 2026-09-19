// ============================================================
// FLEET TRIP TABLE — standalone
//
// No Jali modal. Plain <select> dropdowns. Auto-derives
// origin + destination from the picked product request,
// then asks OSRM for the route via <map-box>.
//
// Distance is displayed in Km (1 decimal) — stored as meters.
// Duration is displayed as H:MM h — stored as seconds.
// ============================================================

import "http://127.0.0.1:8080/mapBox";


class FleetTripTable extends HTMLElement {

    constructor() {
        super();
        this.attachShadow({ mode: "open" });

        // ---- Shared stylesheets ----
        [
            "http://127.0.0.1:8080/cssTableFormData",
            "http://127.0.0.1:8080/mapBoxCss",
            "http://127.0.0.1:8080/leafletCss"
        ].forEach(href => {
            const l = document.createElement("link");
            l.rel = "stylesheet";
            l.href = href;
            this.shadowRoot.appendChild(l);
        });

        // ---- Internal styles ----
        const style = document.createElement("style");
        style.textContent = `
            :host { display: block; }
            .ft-wrap { width: 100%; overflow-x: auto; }

            .ft-modal {
                position: fixed;
                inset: 0;
                z-index: 999999;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 2rem;
                background: rgba(15, 23, 42, 0.55);
                backdrop-filter: blur(3px);
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .ft-card {
                background: #ffffff;
                border-radius: 14px;
                width: min(900px, 96vw);
                max-height: 92vh;
                overflow-y: auto;
                box-shadow: 0 20px 60px rgba(15,23,42,0.35);
                display: flex;
                flex-direction: column;
            }

            .ft-header {
                padding: 18px 24px;
                border-bottom: 1px solid #e2e8f0;
                font-size: 1.15rem;
                font-weight: 700;
                color: #0f172a;
            }

            .ft-body {
                padding: 22px 24px;
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 16px 20px;
            }

            .ft-field {
                display: flex;
                flex-direction: column;
                gap: 6px;
            }

            .ft-field label {
                font-size: 12px;
                font-weight: 700;
                color: #475569;
                text-transform: uppercase;
                letter-spacing: 0.04em;
            }

            .ft-field select,
            .ft-field input {
                padding: 10px 12px;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
                background: #f8fafc;
                color: #0f172a;
                font-size: 14px;
                font-family: inherit;
                outline: none;
                transition: border-color 0.15s, background 0.15s;
            }

            .ft-field select:focus,
            .ft-field input:focus {
                border-color: #2563eb;
                background: #ffffff;
                box-shadow: 0 0 0 3px rgba(37,99,235,0.12);
            }

            .ft-field input[readonly] {
                background: #f1f5f9;
                color: #475569;
            }

            .ft-full { grid-column: 1 / -1; }

            .ft-derived {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 12px;
                padding: 14px;
                background: #f0f9ff;
                border: 1px solid #bae6fd;
                border-radius: 10px;
            }
            .ft-derived .ft-field label { color: #0369a1; }
            .ft-derived .ft-field input { background: #ffffff; }

            .ft-route-info {
                margin-top: 8px;
                padding: 10px 12px;
                background: #f8fafc;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                font-size: 13px;
                color: #475569;
                text-align: center;
            }
            .ft-route-info.loading { color: #2563eb; }
            .ft-route-info.error   { color: #dc2626; background: #fef2f2; border-color: #fecaca; }
            .ft-route-info.ok      { color: #16a34a; background: #f0fdf4; border-color: #bbf7d0; }

            .ft-footer {
                padding: 16px 24px;
                border-top: 1px solid #e2e8f0;
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                background: #f8fafc;
                border-radius: 0 0 14px 14px;
            }

            .ft-btn {
                padding: 10px 20px;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                border: 1px solid transparent;
                transition: all 0.15s;
                font-family: inherit;
            }
            .ft-btn-cancel { background: #ffffff; border-color: #cbd5e1; color: #475569; }
            .ft-btn-cancel:hover { background: #f1f5f9; }
            .ft-btn-save { background: #2563eb; color: white; border-color: #2563eb; }
            .ft-btn-save:hover { background: #1d4ed8; }
            .ft-btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

            .ft-spinner {
                display: inline-block;
                width: 12px;
                height: 12px;
                border: 2px solid #93c5fd;
                border-top-color: #2563eb;
                border-radius: 50%;
                animation: ft-spin 0.7s linear infinite;
                vertical-align: middle;
                margin-right: 6px;
            }
            @keyframes ft-spin { to { transform: rotate(360deg); } }
        `;
        this.shadowRoot.appendChild(style);

        this.container = document.createElement("div");
        this.shadowRoot.appendChild(this.container);

        this.trips = [];
        this.modal = null;
        this.routeGeometry = "";

        // Raw values from OSRM — what we actually save to the DB
        this._pendingDistance = null;
        this._pendingDuration = null;

        this.routeInfoCache = null;
    }

    get api()     { return this.getAttribute("api")      || ""; }
    get tableId() { return this.getAttribute("table-id") || "fleetTripTable"; }
    get caption() { return this.getAttribute("caption")  || "Fleet Trips"; }
    get classes() { return this.getAttribute("classes")  || ""; }

    connectedCallback() {
        this.renderTable();
        this.loadTrips();
    }

    // ============================================================
    // FORMATTERS
    // ============================================================

    _formatKm(meters) {
        if (meters == null || meters === "") return "";
        const km = Number(meters) / 1000;
        if (!Number.isFinite(km)) return "";
        return km.toFixed(1) + " Km";
    }

    _formatDuration(seconds) {
        if (seconds == null || seconds === "") return "";
        const total = Math.round(Number(seconds));
        if (!Number.isFinite(total)) return "";
        const h = Math.floor(total / 3600);
        const m = Math.floor((total % 3600) / 60);
        return `${h}:${String(m).padStart(2, "0")} h`;
    }

    // ============================================================
    // TABLE
    // ============================================================

    renderTable() {
        this.container.innerHTML = `
            <div class="ft-wrap">
                <table class="${this.classes} dataReadingTable" id="${this.tableId}">
                    <caption>
                        ${this.caption}
                        <button class="btn" id="ft-add">+</button>
                        <button class="btn" id="ft-edit">✎</button>
                        <button class="btn" id="ft-delete">🗑</button>
                    </caption>
                    <thead>
                        <tr>
                            <th></th>
                            <th>Team</th>
                            <th>Origin</th>
                            <th>Destination</th>
                            <th>Distance</th>
                            <th>Duration</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody id="ft-tbody"></tbody>
                </table>
            </div>
        `;

        this.shadowRoot.getElementById("ft-add")
            .addEventListener("click", () => this.openModal(null));

        this.shadowRoot.getElementById("ft-edit")
            .addEventListener("click", () => {
                const id = this.getSelectedId();
                if (id) this.openModal(id);
            });

        this.shadowRoot.getElementById("ft-delete")
            .addEventListener("click", async () => {
                const id = this.getSelectedId();
                if (!id) return;
                if (!confirm("Delete this trip?")) return;
                await fetch(`${this.api}?id=${id}`, { method: "DELETE" });
                this.loadTrips();
            });
    }

    async loadTrips() {
        const url = `${this.api}?pageRowCount=200&whichPage=1` +
                    `&dataSent0&dataSent1&dataSent2&dataSent3&dataSent4&dataSent5`;
        try {
            const r = await fetch(url);
            if (!r.ok) throw new Error(`HTTP ${r.status}`);
            this.trips = await r.json();
        } catch (e) {
            console.error("FleetTrip load error:", e);
            this.trips = [];
        }
        this.renderRows();
    }

    renderRows() {
        const tbody = this.shadowRoot.getElementById("ft-tbody");
        tbody.innerHTML = "";

        if (!this.trips.length) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:30px;color:#94a3b8">No trips yet. Click + to create one.</td></tr>`;
            return;
        }

        this.trips.forEach((trip, i) => {
            const tr = document.createElement("tr");
            tr.id = String(trip.fleet_trip_id);
            tr.innerHTML = `
                <th>
                    <input type="radio" name="${this.tableId}radio">
                    <text>${i + 1}</text>
                </th>
                <td>${trip.team_name ?? ""}</td>
                <td>${trip.origin_title ?? ""}</td>
                <td>${trip.destination_title ?? ""}</td>
                <td>${this._formatKm(trip.distance_meters)}</td>
                <td>${this._formatDuration(trip.duration_seconds)}</td>
                <td>${trip.status_title ?? ""}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    getSelectedId() {
        const checked = this.shadowRoot.querySelector(`#${this.tableId} input[type="radio"]:checked`);
        return checked ? checked.parentElement.parentElement.id : null;
    }

    // ============================================================
    // MODAL
    // ============================================================

    async openModal(editId) {

        this.routeGeometry = "";
        this.routeInfoCache = null;
        this._pendingDistance = null;
        this._pendingDuration = null;

        const trip = editId
            ? this.trips.find(t => String(t.fleet_trip_id) === String(editId))
            : null;

        this.modal = document.createElement("div");
        this.modal.className = "ft-modal";
        this.modal.innerHTML = `
            <div class="ft-card">
                <div class="ft-header">${trip ? "Update" : "Create"} Fleet Trip</div>
                <div class="ft-body">

                    <div class="ft-field">
                        <label>Fleet Team</label>
                        <select id="ft-team"><option value="">Loading…</option></select>
                    </div>

                    <div class="ft-field">
                        <label>Status</label>
                        <select id="ft-status"><option value="">Loading…</option></select>
                    </div>

                    <div class="ft-field ft-full">
                        <label>Product Request</label>
                        <select id="ft-request"><option value="">Loading…</option></select>
                    </div>

                    <div class="ft-derived ft-full" id="ft-derived" style="display:none">
                        <div class="ft-field">
                            <label>Origin (auto)</label>
                            <input id="ft-origin-display" readonly>
                        </div>
                        <div class="ft-field">
                            <label>Destination (auto)</label>
                            <input id="ft-dest-display" readonly>
                        </div>
                    </div>

                    <div class="ft-field ft-full">
                        <label>Route</label>
                        <map-box id="ft-map" mode="read" height="280px"></map-box>
                        <div class="ft-route-info" id="ft-route-info">
                            Select a product request to compute the route.
                        </div>
                    </div>

                    <div class="ft-field">
                        <label>Distance</label>
                        <input id="ft-distance" readonly placeholder="—">
                    </div>

                    <div class="ft-field">
                        <label>Duration</label>
                        <input id="ft-duration" readonly placeholder="—">
                    </div>

                </div>
                <div class="ft-footer">
                    <button class="ft-btn ft-btn-cancel" id="ft-cancel">Cancel</button>
                    <button class="ft-btn ft-btn-save" id="ft-save">Save</button>
                </div>
            </div>
        `;

        this.shadowRoot.appendChild(this.modal);

        this.shadowRoot.getElementById("ft-cancel")
            .addEventListener("click", () => this.closeModal());

        await this.populateDropdowns();

        this.shadowRoot.getElementById("ft-request")
            .addEventListener("change", (e) => this.onRequestChange(e.target.value));

        this.shadowRoot.getElementById("ft-map")
            .addEventListener("route-ready", (e) => {
                const d = e.detail;

                // Raw values — what we save
                this._pendingDistance = d.distance;
                this._pendingDuration = d.duration;
                this.routeGeometry    = d.polyline;

                // Formatted values — what we show
                this.shadowRoot.getElementById("ft-distance").value =
                    this._formatKm(d.distance);
                this.shadowRoot.getElementById("ft-duration").value =
                    this._formatDuration(d.duration);

                const info = this.shadowRoot.getElementById("ft-route-info");
                info.className = "ft-route-info ok";
                info.textContent =
                    `Route: ${this._formatKm(d.distance)} · ` +
                    `${this._formatDuration(d.duration)}`;
            });

        this.shadowRoot.getElementById("ft-save")
            .addEventListener("click", () => this.save(editId));
    }

    closeModal() {
        if (this.modal) {
            this.modal.remove();
            this.modal = null;
        }
    }

    // ============================================================
    // DROPDOWN POPULATION
    // ============================================================

    async populateDropdowns() {

        const [teams, requests, statuses] = await Promise.all([
            this.fetchLookup("http://127.0.0.1:8080/fleetTeamLookupApi"),
            this.fetchLookup("http://127.0.0.1:8080/productRequestHeaderPickerApi?pageRowCount=500&whichPage=1&dataSent0&dataSent1&dataSent2"),
            this.fetchLookup("http://127.0.0.1:8080/fleetTripStatusLookupApi")
        ]);

        this.fillSelect("ft-team",    teams,    "fleet_team_code",        "fleet_team_name");
        this.fillSelect("ft-status",  statuses, "fleet_trip_status_code", "fleet_trip_status_title");

        const req = this.shadowRoot.getElementById("ft-request");
        req.innerHTML = '<option value="">— select a request —</option>';
        requests.forEach(r => {
            const opt = document.createElement("option");
            opt.value = r.inventory_product_request_header_id;
            opt.textContent =
                `${r.request_type_title} · ${r.request_status_title} · ${r.request_date}`;
            req.appendChild(opt);
        });
    }

    async fetchLookup(url) {
        try {
            const r = await fetch(url);
            if (!r.ok) throw new Error(`HTTP ${r.status}`);
            return await r.json();
        } catch (e) {
            console.error("Lookup failed:", url, e);
            return [];
        }
    }

    fillSelect(id, rows, valueKey, labelKey) {
        const sel = this.shadowRoot.getElementById(id);
        if (!sel) return;
        sel.innerHTML = '<option value="">— select —</option>';
        rows.forEach(r => {
            const opt = document.createElement("option");
            opt.value = r[valueKey];
            opt.textContent = r[labelKey];
            sel.appendChild(opt);
        });
    }

    // ============================================================
    // REQUEST CHANGE — auto-route
    // ============================================================

    async onRequestChange(requestId) {

        const info    = this.shadowRoot.getElementById("ft-route-info");
        const derived = this.shadowRoot.getElementById("ft-derived");
        const mapBox  = this.shadowRoot.getElementById("ft-map");

        if (!requestId) {
            derived.style.display = "none";
            info.className = "ft-route-info";
            info.textContent = "Select a product request to compute the route.";
            mapBox.setPoints("");
            this.routeGeometry = "";
            this.routeInfoCache = null;
            this._pendingDistance = null;
            this._pendingDuration = null;
            this.shadowRoot.getElementById("ft-distance").value = "";
            this.shadowRoot.getElementById("ft-duration").value = "";
            return;
        }

        info.className = "ft-route-info loading";
        info.innerHTML = `<span class="ft-spinner"></span>Fetching request route info…`;

        try {
            const r = await fetch(
                `http://127.0.0.1:8080/productRequestRouteInfoApi?id=${encodeURIComponent(requestId)}`
            );
            if (!r.ok) throw new Error(`HTTP ${r.status}`);

            const data = await r.json();
            if (!data.length) throw new Error("No route info for this request.");

            const row = data[0];

            if (row.origin_lat == null || row.destination_lat == null) {
                throw new Error("Request's inventories are missing warehouse coordinates.");
            }

            // Cache for save
            this.routeInfoCache = {
                origin_warehouse_id: row.origin_warehouse_id,
                destination_warehouse_id: row.destination_warehouse_id
            };

            // Update display
            derived.style.display = "grid";
            this.shadowRoot.getElementById("ft-origin-display").value =
                row.origin_warehouse_title || "(unknown)";
            this.shadowRoot.getElementById("ft-dest-display").value =
                row.destination_warehouse_title || "(unknown)";

            // Kick off OSRM
            info.className = "ft-route-info loading";
            info.innerHTML = `<span class="ft-spinner"></span>Computing route via OSRM…`;

            mapBox.setPoints(
                `${row.origin_lat},${row.origin_lng};` +
                `${row.destination_lat},${row.destination_lng}`
            );

        } catch (err) {
            console.error("Route info error:", err);
            info.className = "ft-route-info error";
            info.textContent = "Failed: " + err.message;
            this.routeGeometry = "";
            this.routeInfoCache = null;
            this._pendingDistance = null;
            this._pendingDuration = null;
        }
    }

    // ============================================================
    // SAVE
    // ============================================================

    async save(editId) {

        const team    = this.shadowRoot.getElementById("ft-team").value;
        const request = this.shadowRoot.getElementById("ft-request").value;
        const status  = this.shadowRoot.getElementById("ft-status").value;

        // Use the RAW values from OSRM — not the formatted display strings
        const dist = this._pendingDistance ?? "";
        const dur  = this._pendingDuration ?? "";

        if (!team || !request || !status) {
            alert("Please select Team, Product Request, and Status.");
            return;
        }

        if (!this.routeInfoCache) {
            alert("Wait for the route to compute (or check the error above).");
            return;
        }

        const payload = {
            input0: team,
            input1: request,
            input2: this.routeInfoCache.origin_warehouse_id,
            input3: this.routeInfoCache.destination_warehouse_id,
            input4: dist,
            input5: dur,
            input6: this.routeGeometry || "",
            input7: status
        };

        const isEdit = editId != null;
        const url    = isEdit ? `${this.api}?id=${editId}` : this.api;
        const method = isEdit ? "PUT" : "POST";

        try {
            const response = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) throw new Error(`HTTP ${response.status}`);

            this.closeModal();
            await this.loadTrips();

        } catch (err) {
            console.error("Save failed:", err);
            alert("Save failed: " + err.message);
        }
    }
}


if (!customElements.get("fleet-trip-table")) {
    customElements.define("fleet-trip-table", FleetTripTable);
}