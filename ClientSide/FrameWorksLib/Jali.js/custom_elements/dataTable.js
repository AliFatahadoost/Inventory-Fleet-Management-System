import "http://127.0.0.1:8080/dataCombo"
import "http://127.0.0.1:8080/mapBox"


class FetchDataTable extends HTMLElement {

    constructor() {
        super();

        this.attachShadow({ mode: "open" });

        const tableStyle = document.createElement("link");
        tableStyle.rel = "stylesheet";
        tableStyle.href = "http://127.0.0.1:8080/cssTableFormData";

        const dateBoxStyle = document.createElement("link");
        dateBoxStyle.rel = "stylesheet";
        dateBoxStyle.href = "http://127.0.0.1:8080/dateBoxCss";

        const dataCombo = document.createElement("link");
        dataCombo.rel = "stylesheet";
        dataCombo.href = "http://127.0.0.1:8080/dataComboCss";

        const dataFormStyle = document.createElement("link");
        dataFormStyle.rel = "stylesheet";
        dataFormStyle.href = "http://127.0.0.1:8080/cssDataForm";

        const mapBoxStyle = document.createElement("link");
        mapBoxStyle.rel = "stylesheet";
        mapBoxStyle.href = "http://127.0.0.1:8080/mapBoxCss";

        this.shadowRoot.appendChild(tableStyle);
        this.shadowRoot.appendChild(dateBoxStyle);
        this.shadowRoot.appendChild(dataFormStyle);
        this.shadowRoot.appendChild(dataCombo);
        this.shadowRoot.appendChild(mapBoxStyle);

        this.container = document.createElement("div");
        this.shadowRoot.appendChild(this.container);
    }


    // ============================================================
    // CONNECTED
    // ============================================================

    connectedCallback() {

        this.isReadonly = this.hasAttribute("readonly");

        const API = this.getAttribute("api");
        const tableId = this.getAttribute("table-id");
        const tableCaption = this.getAttribute("caption");
        const classTable = this.getAttribute("classes");
        const inputList = this.getAttribute("inputs");

        const columns =
            (this.getAttribute("columns") || "").split(",");

        this.generateTable(
            API,
            tableId,
            tableCaption,
            classTable,
            inputList,
            ...columns
        );
    }


    // ============================================================
    // GENERATE MODAL (CREATE / EDIT)
    // ============================================================

    generateDataManupulatingModal
    (
        API,
        rowId,
        action,
        formId,
        title,
        classes,
        inputs,
        columns
    )
    {

        let startHtml = `
        <div id="form${formId}" class="containerInputForm ${classes}">
            <div class="backGround"></div>
            <div class="dataCard">
                <div class="titleHolder"> 
                    <h1 class="Title"> ${title} </h1> 
                </div>
                <div class="inputSection">`;

        let endHtml =`
                    </div>
                    <div class="buttonsPlace">
                        <button id="cancleBtnform${formId}" class="btn btnDelete">cancle</button>
                        <button id="saveBtnform${formId}" class="btn btnSave">Save</button>
                    </div>
                </div>
            </div>
            `;

        let inputList = "";

        let tempText = inputs;

        for (let i = 0; i < 1000; i++)
        {
            const blockEnd = tempText.indexOf(";;");
            if (blockEnd === -1) break;

            const block = tempText.substring(0, blockEnd);

            const extractField = (blk, marker) => {
                const markers = ["|name::", "|title::", "|type::", "|value::", "|api::", "|columns::"];
                const idx = blk.indexOf(marker);
                if (idx === -1) return "";

                const start = idx + marker.length;
                let end = blk.length;
                for (const m of markers) {
                    if (m === marker) continue;
                    const p = blk.indexOf(m, start);
                    if (p !== -1 && p < end) end = p;
                }
                return blk.substring(start, end).trim();
            };

            const tempName = extractField(block, "|name::");
            const tempTitle = extractField(block, "|title::");
            const tempType = extractField(block, "|type::");
            const tempDefaultValue = extractField(block, "|value::");
            const tempApi = extractField(block, "|api::");
            const tempColumns = extractField(block, "|columns::");

            // ------------------------------------------------
            // CHECKBOX
            // ------------------------------------------------
            if (tempType == "checkbox")
            {
                inputList += `<div><label for="input${tempName}">${tempTitle} </label><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" ${tempDefaultValue == "1"? "checked" : ""} placeholder="${tempTitle}"></div>`;
            }

            // ------------------------------------------------
            // RADIO
            // ------------------------------------------------
            else if (tempType == "radio")
            {
                inputList += `<div><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" value="${tempDefaultValue}" ${tempDefaultValue != ""? "checked" : ""} placeholder="${tempTitle}"><label for="input${tempName}">${tempTitle} </label></div>`;
            }

            // ------------------------------------------------
            // DATA COMBO
            // ------------------------------------------------
            else if (tempType == "data-combo")
            {
                inputList +=
                    `<div>
                        <label for="${tempName}">${tempTitle}</label>

                        <data-combo
                            class="formInputform${formId}"
                            type="data-combo"
                            id="${tempName}"
                            name="${tempName}"
                            title="${tempTitle}"
                            api="${tempApi}"
                            value="${tempDefaultValue || ""}">
                        </data-combo>
                    </div>`;
            }

            // ------------------------------------------------
            // DATE BOX
            // ------------------------------------------------
            else if (tempType == "date-box")
            {
                inputList +=
                    `<div>
                        <label for="${tempName}">${tempTitle}</label>

                        <date-box
                            class="formInputform${formId}"
                            type="date-box"
                            id="${tempName}"
                            name="${tempName}"
                            title="${tempTitle}"
                            value="${tempDefaultValue || ""}">
                        </date-box>
                    </div>`;
            }

            // ------------------------------------------------
            // FIND OBJECT BOX
            // ------------------------------------------------
            else if (tempType == "find-object-box")
            {
                inputList +=
                    `<div>
                        <label for="${tempName}">${tempTitle}</label>

                        <find-object-box
                            class="formInputform${formId}"
                            type="find-object-box"
                            id="${tempName}"
                            name="${tempName}"
                            title="${tempTitle}"
                            api="${tempApi}"
                            columns="${tempColumns}"
                            value="${tempDefaultValue || ""}">
                        </find-object-box>
                    </div>`;
            }

            // ------------------------------------------------
            // MAP BOX
            //
            // `name` is a comma-joined list of destination
            // field names: e.g. "warehouse_lat,warehouse_long".
            // We emit 2 positional values at save time.
            // ------------------------------------------------
            else if (tempType == "map-box")
            {
                const safeId = tempName.replace(/,/g, "_");

                inputList +=
                    `<div>
                        <label for="${safeId}">${tempTitle}</label>

                        <map-box
                            class="formInputform${formId}"
                            type="map-box"
                            id="${safeId}"
                            name="${tempName}"
                            title="${tempTitle}"
                            mode="write"
                            zoom="13"
                            height="300px"
                            value="${tempDefaultValue || ""}">
                        </map-box>
                    </div>`;
            }

            // ------------------------------------------------
            // CURRENT USER (hidden, auto-filled)
            //
            // Fetches /currentUserApi and fills the input
            // with the logged-in user's SYS_USER_CODE.
            // ------------------------------------------------
            else if (tempType == "current-user")
            {
                inputList +=
                    `<div style="display:none">
                        <input
                            class="formInputform${formId}"
                            name="input${tempName}"
                            type="hidden"
                            data-current-user="1"
                            value="${tempDefaultValue || ""}">
                    </div>`;
            }

            // ------------------------------------------------
            // NORMAL INPUT (text / number / password / ...)
            // ------------------------------------------------
            else
            {
                inputList += `<div><label for="input${tempName}">${tempTitle} </label><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" value="${tempDefaultValue}" placeholder="${tempTitle}"></div>`;
            }

            tempText = tempText.substring(blockEnd + 2);
        }

        let HTML = startHtml + inputList + endHtml;

        const template = document.createElement("template");
        template.innerHTML = HTML;

        this.container.appendChild(template.content.cloneNode(true));

        // ------------------------------------------------
        // PRE-FILL current-user fields
        // ------------------------------------------------
        this.shadowRoot
            .querySelectorAll(`[data-current-user]`)
            .forEach(async (el) => {
                if (el.value) return;
                try {
                    const r = await fetch("http://127.0.0.1:8080/currentUserApi");
                    const data = await r.json();
                    if (Array.isArray(data) && data.length) {
                        el.value = data[0].user_code;
                    }
                } catch (e) {
                    console.error("current-user fetch failed", e);
                }
            });

        this.shadowRoot.getElementById(`saveBtnform${formId}`).addEventListener("click", ()=>{
            this.saveFormData(API,rowId, "form"+formId, action);
            this.shadowRoot.getElementById("form"+formId).remove();
            this.refreshDataTable(API,formId, columns);
        });

        this.shadowRoot.getElementById(`cancleBtnform${formId}`).addEventListener("click", ()=>{
            this.shadowRoot.getElementById("form"+formId).remove();
        });
    }


    // ============================================================
    // FILL INPUT LIST WITH ROW DATA (for edit mode)
    // ============================================================

    fillInputListWithRowData(inputList, row) {

        if (!row) return inputList;

        return inputList.replace(
            /\|name::([^|]*)\|title::([^|]*)\|type::([^|]*)\|value::([^;]*);;/g,
            (match, name, title, type, oldValue) => {

                // ------------------------------------------------
                // MAP BOX — combine two columns into one value
                // ------------------------------------------------
                if (type === "map-box") {

                    const [firstField, secondField] =
                        name.split(",").map(s => s.trim());

                    const lat =
                        row[firstField] !== undefined ? row[firstField] : "";
                    const lng =
                        row[secondField] !== undefined ? row[secondField] : "";

                    const newValue =
                        (lat !== "" && lng !== "")
                            ? `${lat},${lng}`
                            : oldValue;

                    return `|name::${name}|title::${title}|type::${type}|value::${newValue};;`;
                }

                // ------------------------------------------------
                // EVERYTHING ELSE
                // ------------------------------------------------
                const columnName = name.startsWith("input")
                    ? name.substring(5)
                    : name;

                const newValue =
                    row[columnName] !== undefined
                        ? row[columnName]
                        : oldValue;

                return `|name::${name}|title::${title}|type::${type}|value::${newValue};;`;
            }
        );
    }


    // ============================================================
    // SAVE (CREATE / UPDATE)
    //
    // Uses a positional `nextIndex` counter so map-box can emit
    // 2 values while everything else emits 1.
    // ============================================================

    async saveFormData(API, rowId, formId, action)
    {
        let sendingJSON = {};
        let nextIndex = 0;

        this.shadowRoot
            .getElementById(formId)
            .querySelectorAll(`.formInput${formId}`)
            .forEach((element) => {

                const tag = element.tagName.toLowerCase();
                const dataType = element.getAttribute("type") || element.type;

                // ------------------------------------------------
                // MAP BOX — emits lat then long
                // ------------------------------------------------
                if (tag === "map-box" || dataType === "map-box") {
                    const parts = (element.dataset.selectedValue || "").split(",");
                    sendingJSON[`input${nextIndex++}`] = (parts[0] || "").trim();
                    sendingJSON[`input${nextIndex++}`] = (parts[1] || "").trim();
                    return;
                }

                // ------------------------------------------------
                // DATA COMBO
                // ------------------------------------------------
                if (tag === "data-combo" || dataType === "data-combo") {
                    sendingJSON[`input${nextIndex++}`] = element.dataset.selectedId || "";
                    return;
                }

                // ------------------------------------------------
                // FIND OBJECT BOX
                // ------------------------------------------------
                if (tag === "find-object-box" || dataType === "find-object-box") {
                    sendingJSON[`input${nextIndex++}`] = element.dataset.selectedId || "";
                    return;
                }

                // ------------------------------------------------
                // DATE BOX
                // ------------------------------------------------
                if (tag === "date-box" || dataType === "date-box") {
                    sendingJSON[`input${nextIndex++}`] = element.dataset.selectedDate || "";
                    return;
                }

                // ------------------------------------------------
                // CHECKBOX
                // ------------------------------------------------
                if (element.type === "checkbox") {
                    sendingJSON[`input${nextIndex++}`] = element.checked ? 1 : 0;
                    return;
                }

                // ------------------------------------------------
                // RADIO
                // ------------------------------------------------
                if (element.type === "radio") {
                    if (element.checked) {
                        sendingJSON[`input${nextIndex++}`] = element.value;
                    }
                    return;
                }

                // ------------------------------------------------
                // DEFAULT (text, number, password, hidden, ...)
                // ------------------------------------------------
                sendingJSON[`input${nextIndex++}`] = element.value;
            });

        if (action.toLowerCase() == 'create') {
            try {
                const response = await fetch(API, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(sendingJSON),
                });
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                return response;
            } catch (error) {
                console.error("Error posting data:", error);
                throw error;
            }
        }
        else if (action.toLowerCase() == 'update') {
            try {
                const response = await fetch(API + `?id=${rowId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(sendingJSON),
                });
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                return response;
            } catch (error) {
                console.error("Error posting data:", error);
                throw error;
            }
        }
    }


    // ============================================================
    // GENERATE TABLE
    // ============================================================

    async generateTable(API, tableId, tableCaption, classTable, inputList, ...columnsShownInOrder)
    {
        const actionButtons = this.isReadonly
            ? ""
            : `<button class="btn" id="addBtn_${tableId}">+</button>
               <button class="btn" id="editBtn_${tableId}">✎</button>
               <button class="btn" id="deleteBtn_${tableId}">🗑</button>`;

        // --------------------------------------------------------
        // EXTRA BUTTONS
        //
        // Format in HTML:
        //   extra-buttons="Label1:/endpoint1, Label2:/endpoint2"
        //
        // Each button, when clicked with a row selected:
        //   1. Fetches /currentUserApi to get the logged-in code
        //   2. Sends PUT {input0: userCode} to the endpoint with ?id=rowId
        // --------------------------------------------------------
        const extraButtonsSpec = (this.getAttribute("extra-buttons") || "")
            .split(",")
            .map(s => s.trim())
            .filter(Boolean);

        let extraButtonsHtml = "";
        extraButtonsSpec.forEach((spec, i) => {
            const parts = spec.split(":");
            const label = (parts[0] || "").trim();
            const endpoint = (parts[1] || "").trim();
            if (!label || !endpoint) return;
            extraButtonsHtml +=
                `<button class="btn extraBtn" id="extraBtn_${tableId}_${i}" data-endpoint="${endpoint}">${label}</button>`;
        });

        let tableStart =
        `
        <div>
            <table class="${classTable} dataReadingTable" id="${tableId}">
            <caption> ${tableCaption} ${actionButtons} ${extraButtonsHtml} </caption>
            <thead>
            <tr>
            <th><button id="searchBtn_${tableId}">🔍</button></th>
        `;

        for (let i = 0; i < columnsShownInOrder.length; i++)
        {
            tableStart += `
                <th scope="col"><input class="columnSortingInput${tableId}" type=text placeholder="${columnsShownInOrder[i]}"></th>
            `;
        }

        tableStart += `</tr>
        </thead><tbody id="tbody_${tableId}">`;

        tableStart += `
        </tbody>
        </table>

        <input
            type="number"
            value="50"
            placeholder="RowCount in Each page :"
            id="paginationCount${tableId}"
            class="paginationInput paginationCount"
        >

        <input
            value="1"
            placeholder="which page :"
            id="paginationWhichPage${tableId}"
            type="number"
            class="paginationInput paginationPage"
        >

        </div>
        `;

        this.container.innerHTML = tableStart;

        let data = await this.dataFetchFromAPI(API, tableId);

        this.refreshDataTable(API, tableId, columnsShownInOrder);

        this.shadowRoot.getElementById(`searchBtn_${tableId}`).addEventListener("click", ()=>{
            this.refreshDataTable(API, tableId, columnsShownInOrder);
        });

        this.shadowRoot.getElementById(`paginationWhichPage${tableId}`).addEventListener("change", ()=>{
            this.refreshDataTable(API, tableId, columnsShownInOrder);
        });

        this.shadowRoot.getElementById(`paginationCount${tableId}`).addEventListener("change", ()=>{
            this.refreshDataTable(API, tableId, columnsShownInOrder);
        });

        this.shadowRoot.getElementById(`${tableId}`).querySelectorAll("input").forEach(e=> {
            e.addEventListener("change", ()=>{
                this.refreshDataTable(API, tableId, columnsShownInOrder);
            });
        });

        if (!this.isReadonly) {

            this.shadowRoot.getElementById(`deleteBtn_${tableId}`).addEventListener("click", ()=>{
                let ID = this.getSelectedRowId(tableId);
                this.deleteDataWithAPI(API, ID);
                this.refreshDataTable(API, tableId, columnsShownInOrder);
            });

            this.shadowRoot.getElementById(`addBtn_${tableId}`).addEventListener("click", ()=>{
                this.generateDataManupulatingModal(
                    API,
                    0,
                    "create",
                    tableId,
                    "inserting Data into " + tableId,
                    classTable,
                    `${inputList}`,
                    columnsShownInOrder
                );
            });
        }

        this.shadowRoot.getElementById(`editBtn_${tableId}`).addEventListener("click", () => {

            if (this.isReadonly) return;

            const ID = this.getSelectedRowId(tableId);
            if (!ID) return;

            const selectedRow = data.find(row => {
                const firstKey = Object.keys(row)[0];
                return String(row[firstKey]) === String(ID);
            });

            const filledInputList =
                this.fillInputListWithRowData(inputList, selectedRow);

            this.generateDataManupulatingModal(
                API,
                ID,
                "update",
                tableId,
                "Updating Data from " + tableId,
                classTable,
                filledInputList,
                columnsShownInOrder
            );
        });

        // --------------------------------------------------------
        // WIRE EXTRA BUTTONS
        // --------------------------------------------------------
        extraButtonsSpec.forEach((spec, i) => {
            const parts = spec.split(":");
            const label = (parts[0] || "").trim();
            const endpoint = (parts[1] || "").trim();
            if (!label || !endpoint) return;

            const btn = this.shadowRoot.getElementById(`extraBtn_${tableId}_${i}`);
            if (!btn) return;

            btn.addEventListener("click", async () => {

                const id = this.getSelectedRowId(tableId);
                if (!id) {
                    alert("Select a row first.");
                    return;
                }

                // ------------------------------------------------
                // Get current user code
                // ------------------------------------------------
                let userCode = "";
                try {
                    const r = await fetch("http://127.0.0.1:8080/currentUserApi");
                    const j = await r.json();
                    if (Array.isArray(j) && j.length) {
                        userCode = j[0].user_code;
                    }
                } catch (e) {
                    console.error("current-user fetch failed", e);
                }

                if (!userCode) {
                    alert("Could not determine current user.");
                    return;
                }

                // ------------------------------------------------
                // Send the action
                // ------------------------------------------------
                try {
                    const response = await fetch(`${endpoint}?id=${id}`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ input0: userCode })
                    });

                    if (!response.ok) throw new Error(`HTTP ${response.status}`);

                    this.refreshDataTable(API, tableId, columnsShownInOrder);

                } catch (e) {
                    console.error("Extra button action failed:", e);
                    alert("Action failed: " + e.message);
                }
            });
        });
    }


    // ============================================================
    // REFRESH TABLE BODY
    // ============================================================

    async refreshDataTable(API, tableId, columnsShownInOrder) {

        const newData = await this.dataFetchFromAPI(API, tableId);

        const tbody = this.shadowRoot.getElementById(`tbody_${tableId}`);

        tbody.innerHTML = this.generateTableRows(
            newData,
            columnsShownInOrder
        );
    }


    // ============================================================
    // GENERATE ROWS
    // ============================================================

    generateTableRows(data, columnsShownInOrder, tableId) {

        let rows = "";

        for (let i = 0; i < data.length; i++) {
            const columnNames = Object.keys(data[i]);

            const selector = this.isReadonly
                ? `<text>${i + 1}</text>`
                : `<input type="radio" name="${tableId}radiobutton">
                   <text>${i + 1}</text>`;

            rows += `
                <tr id="${data[i][columnNames[0]]}">
                    <th>
                        ${selector}
                    </th>
            `;

            for (let j = 1; j < columnsShownInOrder.length + 1; j++) {
                rows += `<td>${data[i][columnNames[j]]}</td>`;
            }

            rows += `</tr>`;
        }

        return rows;
    }


    // ============================================================
    // FETCH
    // ============================================================

    async dataFetchFromAPI(URL, tableId)
    {
        try
        {
            const response = await fetch(this.getURLMaker(URL, tableId), { method: 'GET' });
            if (!response.ok)
                throw new Error(`something went Wrong during Fetching Data ${response.status}`);

            return await response.json();
        }
        catch(error)
        {
            console.log("generate Table has hit the Error during fetching Data : " + error);
            throw error;
        }
    }


    getURLMaker(URL, tableId)
    {
        let searchData =
            `?pageRowCount=` + this.shadowRoot.getElementById(`paginationCount${tableId}`).value +
            `&whichPage=` + this.shadowRoot.getElementById(`paginationWhichPage${tableId}`).value + `&`;

        this.shadowRoot.querySelectorAll(`.columnSortingInput${tableId}`).forEach((e, i) => {
            if (e.value != '')
                searchData += `dataSent${i}=` + e.value + "&"
            else
                searchData += `dataSent${i}&`
        });

        searchData = URL + searchData.slice(0, -1);
        console.log(searchData);
        return searchData;
    }


    async deleteDataWithAPI(URL, ID)
    {
        try
        {
            const response = await fetch(URL + `?id=${ID}`, { method: 'DELETE' });
            if (!response.ok)
                throw new Error(`something went Wrong during Fetching Data ${response.status}`);

            return await response.json();
        }
        catch(error)
        {
            console.log("generate Table has hit the Error during fetching Data : " + error);
        }
    }


    // ============================================================
    // ROW SELECTION
    // ============================================================

    getSelectedRowId(tableId)
    {
        let table = this.shadowRoot.getElementById(tableId);
        let selectedRadioButton;

        table.querySelectorAll("input").forEach(e => {
            if (e.type != "radio") return;
            if (!e.checked) return;
            selectedRadioButton = e;
        });

        if (!selectedRadioButton) return null;

        return selectedRadioButton.parentElement.parentElement.id;
    }
}


customElements.define('fetch-data-table', FetchDataTable);