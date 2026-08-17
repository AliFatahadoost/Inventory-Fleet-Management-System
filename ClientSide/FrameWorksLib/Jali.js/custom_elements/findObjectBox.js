

// ============================================================
// FIND OBJECT BOX
// ============================================================
class FindObjectBox extends HTMLElement {
    constructor() {
        super();
        // --------------------------------------------------------
        // COMPONENT STATE
        // --------------------------------------------------------
        this.fobColumns = [];
        this.value = "";
        this.selectedName = "";
        this.selectedRow = null;
        this.modal = null;
        this.fobState = {
            pageRowCount: 50,
            whichPage: 1,
            idKey: null,
            selectedId: null,
            selectedRow: null,
            selectedName: null,
            loading: false
        };
        // --------------------------------------------------------
        // ROOT
        // --------------------------------------------------------
        this.root =
            document.createElement("div");
        this.root.className =
            "jali-fob";
        // --------------------------------------------------------
        // DISPLAY
        // --------------------------------------------------------

        const FOB = document.createElement("link");
        FOB.rel = "stylesheet";
        FOB.href = "http://127.0.0.1:8080/FindObjectBoxCss";

        document.body.appendChild(FOB); 

        this.display =
            document.createElement("div");
        this.display.className =
            "jali-fob-display";
        this.display.textContent =
            "🔍 |";
        this.root.appendChild(
            this.display
        );
        // --------------------------------------------------------
        // COMPONENT CLICK
        // --------------------------------------------------------
        this.root.addEventListener(
            "click",
            event => {
                if (
                    event.target.closest(".jali-fob-modal")
                ) {
                    return;
                }
                this.open();
            }
        );
        this.appendChild(
            this.root
        );
    }
    // ============================================================
    // CONNECTED
    // ============================================================
    connectedCallback() {
        if (
            this.dataset.fobInitialized === "true"
        ) {
            return;
        }
        this.dataset.fobInitialized =
            "true";
        // ========================================================
        // READ ATTRIBUTES
        // ========================================================
        const title =
            this.getAttribute("title") || "";
        const api =
            this.getAttribute("name") || "";
        const columns =
            (
                this.getAttribute("value") ||
                ""
            )
            .split(",")
            .map(
                column => column.trim()
            )
            .filter(
                column => column !== ""
            );
        // ========================================================
        // STORE COMPONENT DATA
        // ========================================================
        this.fobColumns =
            columns;
        this.fobTitle =
            title;
        this.fobApi =
            api;
        // ========================================================
        // INITIAL DISPLAY
        // ========================================================
        this.display.textContent =
            "🔍 |";
        // ========================================================
        // OPTIONAL HTML ID
        // ========================================================
        if (!this.id && title) {
            this.id = title;
        }
        // ========================================================
        // ACCESSIBILITY
        // ========================================================
        this.setAttribute(
            "role",
            "button"
        );
        this.setAttribute(
            "tabindex",
            "0"
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
                    this.open();
                }
            }
        );
    }
    // ============================================================
    // OPEN
    // ============================================================
    async open() {
        if (this.modal) {
            return;
        }
        this.fobState.whichPage =
            1;
        this.fobState.selectedId =
            null;
        this.fobState.selectedRow =
            null;
        this.fobState.selectedName =
            null;
        this.modal =
            this.createModal();
        document.body.appendChild(
            this.modal
        );
        await this.loadData();
    }
    // ============================================================
    // CLOSE
    // ============================================================
    close() {
        if (!this.modal) {
            return;
        }
        this.modal.remove();
        this.modal =
            null;
    }
    // ============================================================
    // CREATE MODAL
    // ============================================================
    createModal() {
        const modal =
            document.createElement("div");
        modal.className =
            "jali-fob-modal";
        // ========================================================
        // CARD
        // ========================================================
        const card =
            document.createElement("div");
        card.className =
            "jali-fob-card";
        // ========================================================
        // TITLE
        // ========================================================
        const title =
            document.createElement("h2");
        title.className =
            "jali-fob-title";
        title.textContent =
            this.fobTitle;
        card.appendChild(
            title
        );
        // ========================================================
        // SEARCH AREA
        // ========================================================
        const searchArea =
            document.createElement("div");
        searchArea.className =
            "jali-fob-search";
        this.fobColumns.forEach(
            (column, index) => {
                const field =
                    document.createElement("div");
                field.className =
                    "jali-fob-search-field";
                const label =
                    document.createElement("label");
                label.textContent =
                    column;
                const input =
                    document.createElement("input");
                input.type =
                    "text";
                input.className =
                    "jali-fob-search-input";
                input.dataset.index =
                    String(index);
                field.appendChild(
                    label
                );
                field.appendChild(
                    input
                );
                searchArea.appendChild(
                    field
                );
                // ------------------------------------------------
                // ENTER
                // ------------------------------------------------
                input.addEventListener(
                    "keydown",
                    async event => {
                        if (
                            event.key !== "Enter"
                        ) {
                            return;
                        }
                        this.fobState.whichPage =
                            1;
                        await this.loadData();
                    }
                );
            }
        );
        // ========================================================
        // SEARCH BUTTON
        // ========================================================
        const searchButton =
            document.createElement("button");
        searchButton.type =
            "button";
        searchButton.className =
            "jali-fob-search-button";
        searchButton.textContent =
            "Search";
        searchButton.addEventListener(
            "click",
            async () => {
                this.fobState.whichPage =
                    1;
                await this.loadData();
            }
        );
        searchArea.appendChild(
            searchButton
        );
        card.appendChild(
            searchArea
        );
        // ========================================================
        // TABLE CONTAINER
        // ========================================================
        const tableContainer =
            document.createElement("div");
        tableContainer.className =
            "jali-fob-table-container";
        // ========================================================
        // TABLE
        // ========================================================
        const table =
            document.createElement("table");
        table.className =
            "jali-fob-table";
        const thead =
            document.createElement("thead");
        const tbody =
            document.createElement("tbody");
        table.appendChild(
            thead
        );
        table.appendChild(
            tbody
        );
        tableContainer.appendChild(
            table
        );
        card.appendChild(
            tableContainer
        );
        // ========================================================
        // PAGINATION
        // ========================================================
        const pagination =
            document.createElement("div");
        pagination.className =
            "jali-fob-pagination";
        const previous =
            document.createElement("button");
        previous.type =
            "button";
        previous.textContent =
            "Previous";
        const pageInfo =
            document.createElement("span");
        pageInfo.className =
            "jali-fob-page-info";
        const next =
            document.createElement("button");
        next.type =
            "button";
        next.textContent =
            "Next";
        const pageCount =
            document.createElement("input");
        pageCount.type =
            "number";
        pageCount.className =
            "jali-fob-page-count";
        pageCount.min =
            "1";
        pageCount.value =
            String(
                this.fobState.pageRowCount
            );
        pagination.appendChild(
            previous
        );
        pagination.appendChild(
            pageInfo
        );
        pagination.appendChild(
            next
        );
        pagination.appendChild(
            pageCount
        );
        card.appendChild(
            pagination
        );
        // ========================================================
        // FOOTER
        // ========================================================
        const footer =
            document.createElement("div");
        footer.className =
            "jali-fob-footer";
        const exit =
            document.createElement("button");
        exit.type =
            "button";
        exit.textContent =
            "Exit";
        const select =
            document.createElement("button");
        select.type =
            "button";
        select.textContent =
            "Select";
        footer.appendChild(
            exit
        );
        footer.appendChild(
            select
        );
        card.appendChild(
            footer
        );
        modal.appendChild(
            card
        );
        // ========================================================
        // PREVIOUS
        // ========================================================
        previous.addEventListener(
            "click",
            async () => {
                if (
                    this.fobState.whichPage <= 1
                ) {
                    return;
                }
                this.fobState.whichPage--;
                await this.loadData();
            }
        );
        // ========================================================
        // NEXT
        // ========================================================
        next.addEventListener(
            "click",
            async () => {
                this.fobState.whichPage++;
                await this.loadData();
            }
        );
        // ========================================================
        // PAGE COUNT
        // ========================================================
        pageCount.addEventListener(
            "change",
            async () => {
                let count =
                    parseInt(
                        pageCount.value,
                        10
                    );
                if (
                    !Number.isFinite(count) ||
                    count <= 0
                ) {
                    count = 50;
                    pageCount.value =
                        "50";
                }
                this.fobState.pageRowCount =
                    count;
                this.fobState.whichPage =
                    1;
                await this.loadData();
            }
        );
        // ========================================================
        // EXIT
        // ========================================================
        exit.addEventListener(
            "click",
            () => {
                this.close();
            }
        );
        // ========================================================
        // SELECT
        // ========================================================
        select.addEventListener(
            "click",
            () => {
                this.commitSelection();
            }
        );
        // ========================================================
        // CLICK OUTSIDE
        // ========================================================
        modal.addEventListener(
            "click",
            event => {
                if (
                    event.target === modal
                ) {
                    this.close();
                }
            }
        );
        return modal;
    }
    // ============================================================
    // GET SEARCH VALUES
    // ============================================================
    getSearchValues() {
        if (!this.modal) {
            return [];
        }
        const values =
            this.fobColumns.map(
                () => ""
            );
        const inputs =
            this.modal.querySelectorAll(
                ".jali-fob-search-input"
            );
        inputs.forEach(
            input => {
                const index =
                    Number(
                        input.dataset.index
                    );
                if (
                    Number.isInteger(index) &&
                    index >= 0 &&
                    index < values.length
                ) {
                    values[index] =
                        input.value;
                }
            }
        );
        return values;
    }
    // ============================================================
    // BUILD URL
    // ============================================================
    buildURL() {
        const values =
            this.getSearchValues();
        const params =
            new URLSearchParams();
        params.set(
            "pageRowCount",
            String(
                this.fobState.pageRowCount
            )
        );
        params.set(
            "whichPage",
            String(
                this.fobState.whichPage
            )
        );
        // ========================================================
        // POSITIONAL PARAMETERS
        // ========================================================
        for (
            let i = 0;
            i < this.fobColumns.length;
            i++
        ) {
            params.set(
                `dataSent${i}`,
                values[i] ?? ""
            );
        }
        return (
            `${this.fobApi}?${params.toString()}`
        );
    }
    // ============================================================
    // LOAD DATA
    // ============================================================
    async loadData() {
        if (
            this.fobState.loading
        ) {
            return;
        }
        this.fobState.loading =
            true;
        try {
            const url =
                this.buildURL();
            console.log(
                "FOB GET:",
                url
            );
            const response =
                await fetch(
                    url,
                    {
                        method: "GET"
                    }
                );
            if (!response.ok) {
                throw new Error(
                    `HTTP error: ${response.status}`
                );
            }
            const data =
                await response.json();
            this.renderTable(
                data
            );
            const pageInfo =
                this.modal.querySelector(
                    ".jali-fob-page-info"
                );
            if (pageInfo) {
                pageInfo.textContent =
                    `Page ${this.fobState.whichPage}`;
            }
        }
        catch(error) {
            console.error(
                "FOB fetch error:",
                error
            );
        }
        finally {
            this.fobState.loading =
                false;
        }
    }
    // ============================================================
    // RENDER TABLE
    // ============================================================
    renderTable(data) {
        const thead =
            this.modal.querySelector(
                "thead"
            );
        const tbody =
            this.modal.querySelector(
                "tbody"
            );
        thead.innerHTML =
            "";
        tbody.innerHTML =
            "";
        // ========================================================
        // EMPTY
        // ========================================================
        if (
            !Array.isArray(data) ||
            data.length === 0
        ) {
            const row =
                document.createElement("tr");
            const cell =
                document.createElement("td");
            cell.colSpan =
                Math.max(
                    1,
                    this.fobColumns.length + 1
                );
            cell.textContent =
                "No data found.";
            row.appendChild(
                cell
            );
            tbody.appendChild(
                row
            );
            return;
        }
        // ========================================================
        // DETERMINE ID
        // ========================================================
        const returnedKeys =
            Object.keys(
                data[0]
            );
        const idKey =
            returnedKeys[0];
        this.fobState.idKey =
            idKey;
        // ========================================================
        // HEADER
        // ========================================================
        const headerRow =
            document.createElement("tr");
        const selectHeader =
            document.createElement("th");
        selectHeader.textContent =
            "Select";
        headerRow.appendChild(
            selectHeader
        );
        for (
            let i = 1;
            i < returnedKeys.length &&
            i <= this.fobColumns.length;
            i++
        ) {
            const th =
                document.createElement("th");
            th.textContent =
                returnedKeys[i];
            headerRow.appendChild(
                th
            );
        }
        thead.appendChild(
            headerRow
        );
        // ========================================================
        // ROWS
        // ========================================================
        data.forEach(
            object => {
                const row =
                    document.createElement("tr");
                const id =
                    object[idKey];
                row.dataset.fobId =
                    String(id);
                // ------------------------------------------------
                // RADIO
                // ------------------------------------------------
                const radioCell =
                    document.createElement("td");
                const radio =
                    document.createElement("input");
                radio.type =
                    "radio";
                radio.name =
                    `jaliFobRadio_${this.id || this.fobTitle}`;
                radioCell.appendChild(
                    radio
                );
                row.appendChild(
                    radioCell
                );
                // ------------------------------------------------
                // NON-ID COLUMNS
                // ------------------------------------------------
                for (
                    let i = 1;
                    i < returnedKeys.length &&
                    i <= this.fobColumns.length;
                    i++
                ) {
                    const key =
                        returnedKeys[i];
                    const cell =
                        document.createElement("td");
                    cell.textContent =
                        object[key] ?? "";
                    row.appendChild(
                        cell
                    );
                }
                // ------------------------------------------------
                // SELECT
                // ------------------------------------------------
                const selectRow =
                    () => {
                        // Remove previous highlight
                        tbody
                            .querySelectorAll(
                                ".jali-fob-selected"
                            )
                            .forEach(
                                oldRow => {
                                    oldRow.classList.remove(
                                        "jali-fob-selected"
                                    );
                                }
                            );
                        row.classList.add(
                            "jali-fob-selected"
                        );
                        radio.checked =
                            true;
                        this.fobState.selectedId =
                            id;
                        this.fobState.selectedRow =
                            object;
                        if (
                            returnedKeys.length > 1
                        ) {
                            this.fobState.selectedName =
                                object[
                                    returnedKeys[1]
                                ];
                        }
                        else {
                            this.fobState.selectedName =
                                id;
                        }
                    };
                row.addEventListener(
                    "click",
                    selectRow
                );
                radio.addEventListener(
                    "click",
                    event => {
                        event.stopPropagation();
                    }
                );
                radio.addEventListener(
                    "change",
                    selectRow
                );
                tbody.appendChild(
                    row
                );
            }
        );
    }
    // ============================================================
    // COMMIT
    // ============================================================
    commitSelection() {
        const state =
            this.fobState;
        if (
            state.selectedId === null
        ) {
            return;
        }
        // ========================================================
        // PUBLIC VALUE
        // ========================================================
        this.value =
            state.selectedId;
        // ========================================================
        // PUBLIC SELECTED DATA
        // ========================================================
        this.selectedName =
            state.selectedName;
        this.selectedRow =
            state.selectedRow;
        // ========================================================
        // DISPLAY
        // ========================================================
        this.display.textContent =
            `🔍 | ${state.selectedName}`;
        // ========================================================
        // DATA ATTRIBUTE
        // ========================================================
        this.dataset.selectedId =
            String(
                state.selectedId
            );
        // ========================================================
        // CHANGE EVENT
        // ========================================================
        this.dispatchEvent(
            new Event(
                "change",
                {
                    bubbles: true
                }
            )
        );
        // ========================================================
        // CLOSE
        // ========================================================
        this.close();
    }
}
// ============================================================
// REGISTER ELEMENT
// ============================================================
if (
    !customElements.get(
        "find-object-box"
    )
) {
    customElements.define(
        "find-object-box",
        FindObjectBox
    );
}