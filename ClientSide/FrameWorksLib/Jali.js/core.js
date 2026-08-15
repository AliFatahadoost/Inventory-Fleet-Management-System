import "http://127.0.0.1:8080/tableFormElement"
import "http://127.0.0.1:8080/dataCombo"

const FOB = document.createElement("link");
        FOB.rel = "stylesheet";
        FOB.href = "http://127.0.0.1:8080/FindObjectBoxCss";

document.body.appendChild(FOB);

renderFob();
export function renderFob()
{
    document
        .querySelectorAll('input[type="find-object-box"]')
        .forEach(input => {

            // Prevent duplicate initialization
            if (input.dataset.fobRendered === "true")
                return;

            input.dataset.fobRendered = "true";


            // ============================================================
            // INITIALIZATION
            // ============================================================

            /*
             * Example:
             *
             * value="username,country,password"
             *
             * This is read ONLY here.
             *
             * It tells FOB:
             *
             *     3 non-ID columns exist.
             *
             * The names themselves are NOT used for matching.
             */

            input.id = input.title;

            const columns =
                (input.getAttribute("value") || "")
                    .split(",")
                    .map(x => x.trim())
                    .filter(x => x !== "");


            /*
             * Store the schema permanently.
             */
            input.fobColumns = columns;


            // ============================================================
            // HIDDEN DATA BUFFER
            // ============================================================

            /*
             * This buffer stores the current values positionally.
             *
             * Example:
             *
             * columns:
             *
             *   username
             *   country
             *   password
             *
             * buffer:
             *
             *   ["ali", "iran", ""]
             *
             * This means:
             *
             *   dataSent0 = ali
             *   dataSent1 = iran
             *   dataSent2 =
             */

            const dataBuffer =
                document.createElement("div");

            dataBuffer.id =
                `${input.id}_hiddenDatabuffer`;

            dataBuffer.style.display =
                "none";


            dataBuffer.dataset.values =
                JSON.stringify(
                    columns.map(() => "")
                );


            dataBuffer.dataset.columnCount =
                String(columns.length);


            input.parentElement.appendChild(
                dataBuffer
            );


            // ============================================================
            // INPUT SETUP
            // ============================================================

            /*
             * The original value was the column definition.
             *
             * From this point forward input.value is the REAL
             * selected ID.
             */

            input.value = "";

            input.readOnly = true;

            input.style.caretColor =
                "transparent";

            input.style.cursor =
                "default";

            input.dataClosed =
                true;


            // ============================================================
            // DISPLAY OVERLAY
            // ============================================================

            const display =
                document.createElement("div");

            display.id =
                `display_${input.id}`;

            display.setAttribute(
                "data-jali-fob-display",
                ""
            );

            display.textContent =
                "🔍 |";


            const parent =
                input.parentElement;

            if (!parent)
                return;


            if (
                getComputedStyle(parent).position === "static"
            )
            {
                parent.style.position =
                    "relative";
            }


            const inputStyle =
                getComputedStyle(input);


            display.style.position =
                "absolute";

            display.style.left =
                `${input.offsetLeft}px`;

            display.style.top =
                `${input.offsetTop}px`;

            display.style.width =
                `${input.offsetWidth}px`;

            display.style.height =
                `${input.offsetHeight}px`;

            display.style.backgroundColor =
                inputStyle.backgroundColor;

            display.style.font =
                inputStyle.font;

            display.style.padding =
                inputStyle.padding;

            display.style.border =
                inputStyle.border;

            display.style.boxSizing =
                "border-box";

            display.style.cursor =
                "default";

            display.style.zIndex =
                "1000";


            parent.appendChild(
                display
            );


            display.addEventListener(
                "click",
                () => input.click()
            );


            // ============================================================
            // OPEN / CLOSE
            // ============================================================

            input.addEventListener(
                "click",
                async () => {

                    // ----------------------------------------------------
                    // OPEN
                    // ----------------------------------------------------

                    if (input.dataClosed)
                    {
                        input.dataClosed =
                            false;


                        let modal =
                            document.getElementById(
                                `fobModal_${input.id}`
                            );


                        if (!modal)
                        {
                            modal =
                                createFobModal(
                                    input,
                                    display,
                                    dataBuffer
                                );

                            document.body.appendChild(
                                modal
                            );
                        }
                        else
                        {
                            modal.style.display =
                                "block";
                        }


                        await loadFobData(
                            modal
                        );

                        return;
                    }


                    // ----------------------------------------------------
                    // CLOSE
                    // ----------------------------------------------------

                    input.dataClosed =
                        true;


                    const modal =
                        document.getElementById(
                            `fobModal_${input.id}`
                        );


                    if (modal)
                    {
                        modal.remove();
                    }
                }
            );
        });
}



// ========================================================================
// CREATE FOB MODAL
// ========================================================================

function createFobModal(
    input,
    display,
    dataBuffer
)
{
    const modal =
        document.createElement("div");

    modal.id =
        `fobModal_${input.id}`;


    // ====================================================================
    // STATE
    // ====================================================================

    modal.fobState = {

        input: input,

        display: display,

        dataBuffer: dataBuffer,

        api: input.name,

        /*
         * This is the column schema captured during initialization.
         *
         * The names themselves are only metadata.
         * Their count determines how many dataSent parameters exist.
         */
        columns: input.fobColumns,

        pageRowCount: 50,

        whichPage: 1,

        /*
         * First property returned by backend.
         */
        idKey: null,

        /*
         * Temporary selection.
         */
        selectedId: null,

        selectedRow: null,

        selectedName: null,

        loading: false
    };


    // ====================================================================
    // CARD
    // ====================================================================

    const card =
        document.createElement("div");


    // ====================================================================
    // TITLE
    // ====================================================================

    const title =
        document.createElement("h2");

    title.textContent =
        input.title;


    card.appendChild(
        title
    );


    // ====================================================================
    // SEARCH AREA
    // ====================================================================

    const searchArea =
        document.createElement("div");

    searchArea.id =
        `fobSearchArea_${input.id}`;


    /*
     * IMPORTANT:
     *
     * The number of search slots comes from the ORIGINAL
     * value attribute.
     *
     * NOT from JSON property names.
     */

    input.fobColumns.forEach(
        (column, index) => {

            const wrapper =
                document.createElement("div");


            const label =
                document.createElement("label");


            /*
             * Label is only visual.
             *
             * It does NOT affect the backend request.
             */

            label.textContent =
                column;


            const searchInput =
                document.createElement("input");


            searchInput.type =
                "text";

            searchInput.className =
                "fob-search-input";


            /*
             * THIS is the important piece.
             *
             * 0 -> dataSent0
             * 1 -> dataSent1
             * 2 -> dataSent2
             */

            searchInput.dataset.index =
                String(index);


            wrapper.appendChild(
                label
            );

            wrapper.appendChild(
                searchInput
            );


            searchArea.appendChild(
                wrapper
            );


            /*
             * Whenever the slot changes, update the hidden buffer.
             */

            searchInput.addEventListener(
                "input",
                () => {

                    updateFobDataBuffer(
                        modal
                    );
                }
            );


            /*
             * Enter -> search
             */

            searchInput.addEventListener(
                "keydown",
                async event => {

                    if (
                        event.key !== "Enter"
                    )
                    {
                        return;
                    }


                    modal.fobState.whichPage =
                        1;


                    updateFobDataBuffer(
                        modal
                    );


                    await loadFobData(
                        modal
                    );
                }
            );
        }
    );


    // ====================================================================
    // SEARCH BUTTON
    // ====================================================================

    const searchButton =
        document.createElement("button");

    searchButton.type =
        "button";

    searchButton.textContent =
        "Search";


    searchButton.addEventListener(
        "click",
        async () => {

            modal.fobState.whichPage =
                1;


            updateFobDataBuffer(
                modal
            );


            await loadFobData(
                modal
            );
        }
    );


    searchArea.appendChild(
        searchButton
    );


    card.appendChild(
        searchArea
    );


    // ====================================================================
    // TABLE
    // ====================================================================

    const table =
        document.createElement("table");

    table.id =
        `fobTable_${input.id}`;


    const thead =
        document.createElement("thead");


    const tbody =
        document.createElement("tbody");


    tbody.id =
        `fobTbody_${input.id}`;


    table.appendChild(
        thead
    );

    table.appendChild(
        tbody
    );


    card.appendChild(
        table
    );


    // ====================================================================
    // PAGINATION
    // ====================================================================

    const pagination =
        document.createElement("div");


    const previousButton =
        document.createElement("button");

    previousButton.type =
        "button";

    previousButton.textContent =
        "Previous";


    const pageInfo =
        document.createElement("span");

    pageInfo.className =
        "fob-page-info";


    const nextButton =
        document.createElement("button");

    nextButton.type =
        "button";

    nextButton.textContent =
        "Next";


    const pageCountInput =
        document.createElement("input");

    pageCountInput.type =
        "number";

    pageCountInput.min =
        "1";

    pageCountInput.value =
        "50";


    pagination.appendChild(
        previousButton
    );

    pagination.appendChild(
        pageInfo
    );

    pagination.appendChild(
        nextButton
    );

    pagination.appendChild(
        pageCountInput
    );


    card.appendChild(
        pagination
    );


    // ====================================================================
    // FOOTER
    // ====================================================================

    const footer =
        document.createElement("div");


    const exitButton =
        document.createElement("button");

    exitButton.type =
        "button";

    exitButton.textContent =
        "Exit";


    const selectButton =
        document.createElement("button");

    selectButton.type =
        "button";

    selectButton.textContent =
        "Select";


    footer.appendChild(
        exitButton
    );

    footer.appendChild(
        selectButton
    );


    card.appendChild(
        footer
    );


    modal.appendChild(
        card
    );


    // ====================================================================
    // PREVIOUS
    // ====================================================================

    previousButton.addEventListener(
        "click",
        async () => {

            if (
                modal.fobState.whichPage <= 1
            )
            {
                return;
            }


            modal.fobState.whichPage--;


            await loadFobData(
                modal
            );
        }
    );


    // ====================================================================
    // NEXT
    // ====================================================================

    nextButton.addEventListener(
        "click",
        async () => {

            modal.fobState.whichPage++;


            await loadFobData(
                modal
            );
        }
    );


    // ====================================================================
    // PAGE COUNT
    // ====================================================================

    pageCountInput.addEventListener(
        "change",
        async () => {

            let count =
                parseInt(
                    pageCountInput.value,
                    10
                );


            if (
                !Number.isFinite(count) ||
                count <= 0
            )
            {
                count = 50;

                pageCountInput.value =
                    "50";
            }


            modal.fobState.pageRowCount =
                count;

            modal.fobState.whichPage =
                1;


            await loadFobData(
                modal
            );
        }
    );


    // ====================================================================
    // EXIT
    // ====================================================================

    exitButton.addEventListener(
        "click",
        () => {

            input.dataClosed =
                true;


            /*
             * DO NOT:
             *
             * input.value = ...
             *
             * display.textContent = ...
             *
             * The temporary selection is discarded.
             */

            modal.remove();
        }
    );


    // ====================================================================
    // SELECT
    // ====================================================================

    selectButton.addEventListener(
        "click",
        () => {

            const state =
                modal.fobState;


            if (
                state.selectedId === null
            )
            {
                return;
            }


            /*
             * Commit the ID.
             */

            state.input.value =
                state.selectedId;


            /*
             * Keep human-readable selected value.
             */

            state.input.dataSelectedName =
                state.selectedName;


            /*
             * Update the visible overlay.
             */

            state.display.textContent =
                `🔍 | ${state.selectedName}`;


            state.input.dataClosed =
                true;


            modal.remove();
        }
    );


    return modal;
}



// ========================================================================
// UPDATE HIDDEN BUFFER
// ========================================================================

function updateFobDataBuffer(
    modal
)
{
    const state =
        modal.fobState;


    /*
     * Read the slots POSITIONALLY.
     */

    const searchInputs =
        modal.querySelectorAll(
            ".fob-search-input"
        );


    /*
     * Start with exactly as many empty slots as
     * the original value attribute defined.
     */

    const values =
        state.columns.map(
            () => ""
        );


    /*
     * Put the current search values into their
     * corresponding positions.
     */

    searchInputs.forEach(
        searchInput => {

            const index =
                parseInt(
                    searchInput.dataset.index,
                    10
                );


            if (
                Number.isInteger(index) &&
                index >= 0 &&
                index < values.length
            )
            {
                values[index] =
                    searchInput.value;
            }
        }
    );


    /*
     * Save into hidden buffer.
     */

    state.dataBuffer.dataset.values =
        JSON.stringify(values);


    /*
     * Keep the original column count available.
     */

    state.dataBuffer.dataset.columnCount =
        String(state.columns.length);
}



// ========================================================================
// BUILD GET URL
// ========================================================================

function buildFobURL(
    modal
)
{
    const state =
        modal.fobState;


    /*
     * Read the positional values from buffer.
     */

    let bufferValues =
        [];


    try
    {
        bufferValues =
            JSON.parse(
                state.dataBuffer.dataset.values ||
                "[]"
            );
    }
    catch(error)
    {
        console.error(
            "Invalid FOB data buffer:",
            error
        );

        bufferValues =
            [];
    }


    /*
     * Build query.
     */

    const params =
        new URLSearchParams();


    /*
     * --------------------------------------------------------
     * SLOT 0
     * --------------------------------------------------------
     */

    params.set(
        "pageRowCount",
        String(state.pageRowCount)
    );


    /*
     * --------------------------------------------------------
     * SLOT 1
     * --------------------------------------------------------
     */

    params.set(
        "whichPage",
        String(state.whichPage)
    );


    /*
     * --------------------------------------------------------
     * SLOT 2+
     *
     * Every non-ID column gets one dataSent parameter.
     *
     * The COLUMN NAME IS NEVER SENT.
     *
     * Only the POSITION matters.
     * --------------------------------------------------------
     */

    for (
        let i = 0;
        i < state.columns.length;
        i++
    )
    {
        /*
         * Empty values are intentionally preserved.
         */

        const value =
            bufferValues[i] ?? "";


        params.set(
            `dataSent${i}`,
            value
        );
    }


    const URL =
        `${state.api}?${params.toString()}`;


    return URL;
}



// ========================================================================
// LOAD DATA
// ========================================================================

async function loadFobData(
    modal
)
{
    const state =
        modal.fobState;


    if (state.loading)
        return;


    state.loading =
        true;


    try
    {
        /*
         * Make sure the buffer is synchronized before
         * every request.
         */

        updateFobDataBuffer(
            modal
        );


        /*
         * Build GET URL.
         */

        const URL =
            buildFobURL(
                modal
            );


        console.log(
            "FOB GET:",
            URL
        );


        /*
         * ----------------------------------------------------
         * Example:
         *
         * value="username,country,password"
         *
         * Initial request:
         *
         * ?pageRowCount=50
         * &whichPage=1
         * &dataSent0=
         * &dataSent1=
         * &dataSent2=
         * ----------------------------------------------------
         */

        const response =
            await fetch(
                URL,
                {
                    method: "GET"
                }
            );


        if (!response.ok)
        {
            throw new Error(
                `HTTP error: ${response.status}`
            );
        }


        const data =
            await response.json();


        renderFobTable(
            modal,
            data
        );


        const pageInfo =
            modal.querySelector(
                ".fob-page-info"
            );


        pageInfo.textContent =
            `Page ${state.whichPage}`;

    }
    catch(error)
    {
        console.error(
            "FOB fetch error:",
            error
        );
    }
    finally
    {
        state.loading =
            false;
    }
}



// ========================================================================
// RENDER TABLE
// ========================================================================

function renderFobTable(
    modal,
    data
)
{
    const state =
        modal.fobState;


    const thead =
        modal.querySelector(
            "thead"
        );


    const tbody =
        modal.querySelector(
            "tbody"
        );


    thead.innerHTML =
        "";

    tbody.innerHTML =
        "";


    // ====================================================================
    // EMPTY
    // ====================================================================

    if (
        !Array.isArray(data) ||
        data.length === 0
    )
    {
        const row =
            document.createElement("tr");


        const cell =
            document.createElement("td");


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


    // ====================================================================
    // DETERMINE ID
    // ====================================================================

    /*
     * The first returned property is ALWAYS the ID.
     *
     * Example:
     *
     * {
     *     ID: 1001,
     *     username: "Ali",
     *     country: "Iran",
     *     password: "123"
     * }
     *
     * returnedKeys[0] = ID
     * returnedKeys[1] = username
     * returnedKeys[2] = country
     * returnedKeys[3] = password
     */

    const returnedKeys =
        Object.keys(
            data[0]
        );


    const idKey =
        returnedKeys[0];


    state.idKey =
        idKey;


    // ====================================================================
    // TABLE HEADER
    // ====================================================================

    const headerRow =
        document.createElement("tr");


    const selectHeader =
        document.createElement("th");


    selectHeader.textContent =
        "Select";


    headerRow.appendChild(
        selectHeader
    );


    /*
     * IMPORTANT:
     *
     * Headers come from the RETURNED DATA positions,
     * excluding ID.
     *
     * The input.value names are only used to establish
     * how many non-ID positions exist.
     */

    for (
        let i = 1;
        i < returnedKeys.length &&
        i <= state.columns.length;
        i++
    )
    {
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


    // ====================================================================
    // ROWS
    // ====================================================================

    data.forEach(
        object => {

            const row =
                document.createElement("tr");


            /*
             * -----------------------------------------------
             * ID
             * -----------------------------------------------
             */

            const id =
                object[idKey];


            row.dataset.fobId =
                String(id);


            /*
             * -----------------------------------------------
             * RADIO
             * -----------------------------------------------
             */

            const radioCell =
                document.createElement("td");


            const radio =
                document.createElement("input");


            radio.type =
                "radio";


            radio.name =
                `fobRadio_${state.input.id}`;


            radioCell.appendChild(
                radio
            );


            row.appendChild(
                radioCell
            );


            /*
             * -----------------------------------------------
             * NON-ID DATA
             *
             * POSITIONAL
             *
             * returnedKeys[1] -> first visible column
             * returnedKeys[2] -> second visible column
             * returnedKeys[3] -> third visible column
             * -----------------------------------------------
             */

            for (
                let i = 1;
                i < returnedKeys.length &&
                i <= state.columns.length;
                i++
            )
            {
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


            // ============================================================
            // TEMPORARY SELECTION
            // ============================================================

            const selectRow =
                () => {

                    radio.checked =
                        true;


                    /*
                     * Store ID temporarily.
                     */

                    state.selectedId =
                        id;


                    /*
                     * Keep complete row available.
                     */

                    state.selectedRow =
                        object;


                    /*
                     * First NON-ID returned column is
                     * the human-readable display value.
                     */

                    if (
                        returnedKeys.length > 1
                    )
                    {
                        state.selectedName =
                            object[
                                returnedKeys[1]
                            ];
                    }
                    else
                    {
                        state.selectedName =
                            id;
                    }
                };


            row.addEventListener(
                "click",
                selectRow
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