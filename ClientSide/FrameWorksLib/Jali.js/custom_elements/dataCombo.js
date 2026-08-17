

// ============================================================
// JALIFRAME DATA COMBO
// ============================================================

class DataCombo extends HTMLElement {

    constructor() {

        super();


        const dataCombo = document.createElement("link");
                dataCombo.rel = "stylesheet";
                dataCombo.href = "http://127.0.0.1:8080/dataComboCss";

        document.body.appendChild(dataCombo);
        // ========================================================
        // PUBLIC STATE
        // ========================================================

        this.value = "";

        this.selectedName = "";

        this.selectedRow = null;


        // ========================================================
        // INTERNAL STATE
        // ========================================================

        this.dataClosed = true;

        this.dataLoaded = false;

        this.loading = false;


        this.data = [];


        // ========================================================
        // ROOT
        // ========================================================

        this.root =
            document.createElement("div");

        this.root.className =
            "jali-data-combo";


        // ========================================================
        // DISPLAY
        // ========================================================

        this.display =
            document.createElement("div");

        this.display.className =
            "jali-data-combo-display";

        this.display.textContent =
            "▼";


        this.root.appendChild(
            this.display
        );


        // ========================================================
        // DROPDOWN
        // ========================================================

        this.dropdown =
            document.createElement("div");

        this.dropdown.className =
            "jali-data-combo-dropdown";

        this.dropdown.style.display =
            "none";


        this.root.appendChild(
            this.dropdown
        );


        // ========================================================
        // COMPONENT
        // ========================================================

        this.appendChild(
            this.root
        );


        // ========================================================
        // CLICK
        // ========================================================

        this.display.addEventListener(
            "click",
            event => {

                event.stopPropagation();

                this.toggle();
            }
        );


        // ========================================================
        // OUTSIDE CLICK
        // ========================================================

        this._outsideClick =
            event => {

                if (
                    !this.contains(event.target)
                ) {

                    this.close();
                }
            };


        document.addEventListener(
            "click",
            this._outsideClick
        );
    }


    // ============================================================
    // CONNECTED
    // ============================================================

    connectedCallback() {

        if (
            this.dataset.initialized === "true"
        ) {
            return;
        }


        this.dataset.initialized =
            "true";


        // ========================================================
        // ATTRIBUTES
        // ========================================================

        this.comboTitle =
            this.getAttribute("title") || "";


        this.apiEndpoint =
            this.getAttribute("name") || "";


        // ========================================================
        // COMPONENT ID
        // ========================================================

        if (
            !this.id &&
            this.comboTitle
        ) {

            this.id =
                this.comboTitle;
        }


        // ========================================================
        // ACCESSIBILITY
        // ========================================================

        this.setAttribute(
            "role",
            "combobox"
        );

        this.setAttribute(
            "tabindex",
            "0"
        );


        this.setAttribute(
            "aria-expanded",
            "false"
        );


        // ========================================================
        // KEYBOARD
        // ========================================================

        this.addEventListener(
            "keydown",
            event => {

                if (
                    event.key === "Enter" ||
                    event.key === " "
                ) {

                    event.preventDefault();

                    this.toggle();

                    return;
                }


                if (
                    event.key === "Escape"
                ) {

                    this.close();
                }
            }
        );


        
    }


    // ============================================================
    // DISCONNECTED
    // ============================================================

    disconnectedCallback() {

        document.removeEventListener(
            "click",
            this._outsideClick
        );
    }



    // ============================================================
    // TOGGLE
    // ============================================================

    async toggle() {

        if (
            this.dataClosed
        ) {

            await this.open();

        }
        else {

            this.close();
        }
    }


    // ============================================================
    // OPEN
    // ============================================================

    async open() {

        this.dataClosed =
            false;


        this.dropdown.style.display =
            "block";


        this.setAttribute(
            "aria-expanded",
            "true"
        );


        // --------------------------------------------------------
        // Load only once.
        // --------------------------------------------------------

        if (
            !this.dataLoaded
        ) {

            await this.loadData();
        }
    }


    // ============================================================
    // CLOSE
    // ============================================================

    close() {

        this.dataClosed =
            true;


        this.dropdown.style.display =
            "none";


        this.setAttribute(
            "aria-expanded",
            "false"
        );
    }


    // ============================================================
    // LOAD DATA
    // ============================================================

    async loadData() {

        if (
            this.loading
        ) {

            return;
        }


        this.loading =
            true;


        this.dropdown.innerHTML =
            "";


        const loading =
            document.createElement("div");


        loading.className =
            "jali-data-combo-loading";

        loading.textContent =
            "Loading...";


        this.dropdown.appendChild(
            loading
        );


        try {

            // ----------------------------------------------------
            // VALIDATE ENDPOINT
            // ----------------------------------------------------

            if (
                !this.apiEndpoint
            ) {

                throw new Error(
                    "DataCombo has no endpoint in the 'name' attribute."
                );
            }


            // ----------------------------------------------------
            // FETCH
            // ----------------------------------------------------

            const response =
                await fetch(
                    this.apiEndpoint
                );


            if (
                !response.ok
            ) {

                throw new Error(
                    `HTTP error: ${response.status}`
                );
            }


            const data =
                await response.json();


            if (
                !Array.isArray(data)
            ) {

                throw new Error(
                    "DataCombo endpoint must return a JSON array."
                );
            }


            this.data =
                data;


            this.dataLoaded =
                true;


            this.renderData();


        }
        catch(error) {

            console.error(
                "DataCombo fetch error:",
                error
            );


            this.dropdown.innerHTML =
                "";


            const errorElement =
                document.createElement("div");


            errorElement.className =
                "jali-data-combo-error";


            errorElement.textContent =
                "Failed to load data.";


            this.dropdown.appendChild(
                errorElement
            );

        }
        finally {

            this.loading =
                false;
        }
    }


    // ============================================================
    // RENDER DATA
    // ============================================================

    renderData() {

        this.dropdown.innerHTML =
            "";


        if (
            this.data.length === 0
        ) {

            const empty =
                document.createElement("div");


            empty.className =
                "jali-data-combo-empty";


            empty.textContent =
                "No data found.";


            this.dropdown.appendChild(
                empty
            );


            return;
        }


        this.data.forEach(
            object => {

                const keys =
                    Object.keys(
                        object
                    );


                // ------------------------------------------------
                // First property = ID
                // Second property = Display value
                // ------------------------------------------------

                const id =
                    object[keys[0]];


                const text =
                    object[keys[1]];


                const row =
                    document.createElement("div");


                row.className =
                    "jali-data-combo-row";


                row.setAttribute(
                    "dc_row_id",
                    String(id)
                );


                row.setAttribute(
                    "dc_row_name",
                    String(text ?? "")
                );


                row.textContent =
                    text ?? "";


                row.addEventListener(
                    "click",
                    event => {

                        event.stopPropagation();

                        this.select(
                            id,
                            text,
                            object
                        );
                    }
                );


                this.dropdown.appendChild(
                    row
                );
            }
        );
    }


    // ============================================================
    // SELECT
    // ============================================================

    select(
        id,
        text,
        object
    ) {

        // --------------------------------------------------------
        // Public value
        // --------------------------------------------------------

        this.value =
            id;


        // --------------------------------------------------------
        // Public selected name
        // --------------------------------------------------------

        this.selectedName =
            text;


        // --------------------------------------------------------
        // Public complete row
        // --------------------------------------------------------

        this.selectedRow =
            object;


        // --------------------------------------------------------
        // Keep useful attributes
        // --------------------------------------------------------

        this.dataset.selectedId =
            String(id);


        this.dataset.selectedName =
            String(text ?? "");


        // --------------------------------------------------------
        // Display
        // --------------------------------------------------------

        this.display.textContent =
            `▼  ${text ?? ""}`;


        // --------------------------------------------------------
        // Close
        // --------------------------------------------------------

        this.close();


        // --------------------------------------------------------
        // Notify application
        // --------------------------------------------------------

        this.dispatchEvent(
            new Event(
                "change",
                {
                    bubbles: true
                }
            )
        );
    }


    // ============================================================
    // REFRESH
    // ============================================================

    async refresh() {

        this.dataLoaded =
            false;

        this.data =
            [];


        await this.loadData();
    }
}


// ============================================================
// REGISTER
// ============================================================

if (
    !customElements.get(
        "data-combo"
    )
) {

    customElements.define(
        "data-combo",
        DataCombo
    );
}