import "http://127.0.0.1:8080/dataCombo"


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

        this.shadowRoot.appendChild(tableStyle);
        this.shadowRoot.appendChild(dateBoxStyle);
        this.shadowRoot.appendChild(dataFormStyle);
        this.shadowRoot.appendChild(dataCombo);

        this.container = document.createElement("div");
        this.shadowRoot.appendChild(this.container);
    }


    connectedCallback() {

        const API = this.getAttribute("api");
        const tableId = this.getAttribute("table-id");
        const tableCaption = this.getAttribute("caption");
        const classTable = this.getAttribute("classes");
        const inputList = this.getAttribute("inputs");

        const columns =
            this.getAttribute("columns").split(",");

        this.generateTable(
            API,
            tableId,
            tableCaption,
            classTable,
            inputList,
            ...columns
        );
    }



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
        
        for(let i = 0; i<1000 && tempText.indexOf("|value::") != -1 ; i++)
        {
            let tempNameindx = tempText.indexOf("|name::");
            let tempTitleindx = tempText.indexOf("|title::");
            let tempTypeindx = tempText.indexOf("|type::");
            let tempValueindx = tempText.indexOf("|value::");
            let tempSemiColumn = tempText.indexOf(";;");

            let tempName = tempText.slice(tempNameindx + "|name::".length, tempTitleindx);
            let tempTitle = tempText.slice(tempTitleindx + "|title::".length, tempTypeindx);
            let tempType = tempText.slice(tempTypeindx + "|type::".length, tempValueindx);
            let tempDefaultValue = tempText.slice(tempValueindx + "|value::".length, tempSemiColumn);

            if(tempType == "checkbox")
            {
                inputList += `<div><label for="input${tempName}">${tempTitle} </label><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" ${tempDefaultValue == "1"? "checked" : ""} placeholder="${tempTitle}"></div>`;
                
            }
            else if(tempType == "radio")
            {
                inputList += `<div><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" value="${tempDefaultValue}" ${tempDefaultValue != ""? "checked" : ""} placeholder="${tempTitle}"><label for="input${tempName}">${tempTitle} </lable></div>`;
            }
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
                            value="${tempDefaultValue || ""}">
                        </data-combo>
                    </div>`;
            }
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
                            value="${tempDefaultValue || ""}">
                        </find-object-box>
                    </div>`;
            }
            else
            {
                inputList += `<div><label for="input${tempName}">${tempTitle} </lable><input class="formInputform${formId}" name="input${tempName}" type="${tempType}" value="${tempDefaultValue}" placeholder="${tempTitle}"></div>`;
            }
            tempText = tempText.slice(tempSemiColumn + 2);
        }

        

        let HTML = startHtml + inputList + endHtml;

        const template = document.createElement("template");
        template.innerHTML = HTML;

        this.container.appendChild(template.content.cloneNode(true));
        this.renderShadowDataCombos(this.shadowRoot);
        this.shadowRoot.getElementById(`saveBtnform${formId}`).addEventListener("click", ()=>{
            this.saveFormData(API,rowId, "form"+formId, action);
            this.shadowRoot.getElementById("form"+formId).remove();
            this.refreshDataTable(API,formId, columns);
        });
        this.shadowRoot.getElementById(`cancleBtnform${formId}`).addEventListener("click", ()=>{
            this.shadowRoot.getElementById("form"+formId).remove();
        });
    }

fillInputListWithRowData(inputList, row) {
    if (!row) return inputList;

    return inputList.replace(
        /\|name::([^|]*)\|title::([^|]*)\|type::([^|]*)\|value::([^;]*);;/g,
        (match, name, title, type, oldValue) => {

            // Your generated HTML uses "input" + column name
            const columnName = name.startsWith("input")
                ? name.substring(5)
                : name;

            // Find the corresponding column in the selected row
            const newValue =
                row[columnName] !== undefined
                    ? row[columnName]
                    : oldValue;

            return `|name::${name}|title::${title}|type::${type}|value::${newValue};;`;
        }
    );
}

async  saveFormData(API,rowId, formId, action)
{

    let sendingJSON = {};
    console.log(`.formInput${formId}`);
    this.shadowRoot.getElementById(formId).querySelectorAll(`.formInput${formId}`).forEach((elements, index) => {

        if(elements.type == "checkbox")
        {
            sendingJSON[`input${index}`] = elements.checked ? 1 : 0;
        }
        else if(elements.type == "radio")
        {
            if(elements.checked)
            sendingJSON[`input${index}`] = elements.value;
        }
        else if(elements.type == "data-combo")
        {
            sendingJSON[`input${index}`] = elements.dataset.selectedId;
        }
        else if(elements.type == "find-object-box")
        {
            sendingJSON[`input${index}`] = elements.dataset.selectedId;
        }
        else
        {
            sendingJSON[`input${index}`] = elements.value;
        }

    });

    if(action.toLowerCase() == 'create'){
        try {
            const response = await fetch(API, {
            method: "POST",
            body: JSON.stringify(sendingJSON),
            });

            if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response; // assuming the server returns JSON
            console.log("Success:", result);
            return result;
        } catch (error) {
            console.error("Error posting data:", error);
            throw error;
        }
    }
    else if(action.toLowerCase() == 'update')
    {
        console.log(rowId);
        try {
            const response = await fetch(API+`?id=${rowId}`, {
            method: "PUT",
            body: JSON.stringify(sendingJSON),
            });

            if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response; // assuming the server returns JSON
            console.log("Success:", result);
            return result;
        } catch (error) {
            console.error("Error posting data:", error);
            throw error;
        }    
    }
    
}

//this requires an ID as the first column in the Recived Data From API even if its not to Shown
//this  returns a String which is the HTML of the Table with their Data Inside of
async  generateTable(API, tableId, tableCaption, classTable, inputList, ...columnsShownInOrder) 
{
    //this is the Tables Header
    let tableStart = 
    `
    <div>
        <table class="${classTable} dataReadingTable" id="${tableId}">
        <caption> ${tableCaption} <button class="btn" id="addBtn_${tableId}">+</button> <button class="btn" id="editBtn_${tableId}">✎</button> <button class="btn" id="deleteBtn_${tableId}">🗑</button> </caption>
        <thead>
        <tr>
        <th><button id="searchBtn_${tableId}">🔍</button></th>
    `;
    //columnsShownInOrder = columnsShownInOrder.reverse();
    for(let i =0; i < columnsShownInOrder.length; i++)
    {
        tableStart += `
            <th scope="col"><input class="columnSortingInput${tableId}" type=text placeholder="${columnsShownInOrder[i]}"></th>
        `;
    }

    
    
    tableStart += `</tr>
    </thead><tbody id="tbody_${tableId}">`;
    let data;
    
    


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
    data = await this.dataFetchFromAPI(API, tableId);
    
    this.refreshDataTable(API,tableId, columnsShownInOrder);

    this.shadowRoot.getElementById(`searchBtn_${tableId}`).addEventListener("click", ()=>{
        
        this.refreshDataTable(API,tableId, columnsShownInOrder);

    });    
    
    this.shadowRoot.getElementById(`paginationWhichPage${tableId}`).addEventListener("change", ()=>{
        
        this.refreshDataTable(API,tableId, columnsShownInOrder); 
    });

    this.shadowRoot.getElementById(`paginationCount${tableId}`).addEventListener("change", ()=>{
        
        this.refreshDataTable(API,tableId, columnsShownInOrder); 
    });
        

    this.shadowRoot.getElementById(`${tableId}`).querySelectorAll("input").forEach(e=> {

        e.addEventListener("change", ()=>{
        
        this.refreshDataTable(API,tableId, columnsShownInOrder); 
        });
        

    }); 

    this.shadowRoot.getElementById(`deleteBtn_${tableId}`).addEventListener("click", ()=>{
        let ID = this.getSelectedRowId(tableId);
        this.deleteDataWithAPI(API, ID);
        this.refreshDataTable(API,tableId, columnsShownInOrder);
    });    

    this.shadowRoot.getElementById(`addBtn_${tableId}`).addEventListener("click", ()=>{
    this.generateDataManupulatingModal(API,0 , "create",tableId, "inserting Data into " + tableId, classTable,
        `
        ${inputList}
        `, columnsShownInOrder);
    });


    
    this.shadowRoot.getElementById(`editBtn_${tableId}`).addEventListener("click", () => {
    const ID = this.getSelectedRowId(tableId);
    if (!ID) {
        return;
    }

    // Find the selected row from the already fetched data
    const selectedRow = data.find(row => {
        const firstKey = Object.keys(row)[0];
        return String(row[firstKey]) === String(ID);
    });

    // Build a new inputList where every `value::` is filled with the row's value
    const filledInputList = this.fillInputListWithRowData(inputList, selectedRow);

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
}

async refreshDataTable(API, tableId, columnsShownInOrder){


    const newData = await this.dataFetchFromAPI(API, tableId);

    const tbody = this.shadowRoot
        .getElementById(`tbody_${tableId}`);

    tbody.innerHTML = this.generateTableRows(
        newData,
        columnsShownInOrder
    );

}

generateTableRows(data, columnsShownInOrder, tableId) {
    let rows = "";

    for (let i = 0; i < data.length; i++) {
        const columnNames = Object.keys(data[i]);

        rows += `
            <tr id="${data[i][columnNames[0]]}">
                <th>
                    <input type="radio" name="${tableId}radiobutton">
                    <text>${i + 1}</text>
                </th>
        `;

        for (let j = 1; j < columnsShownInOrder.length + 1; j++) {
            rows += `<td>${data[i][columnNames[j]]}</td>`;
        }

        rows += `</tr>`;
    }

    return rows;
}


async dataFetchFromAPI(URL, tableId)
{
    try
    {
        
        const response = await fetch(this.getURLMaker(URL, tableId), {method: 'GET'});
        if(!response.ok)
            throw new Error(`something went Wrong during Fetching Data ${response.status}`);

        const JSON = await response.json();
        return JSON;
        
    }
    catch(error)
    {
        console.log("generate Table has hit the Error during fetching Data : " + error);
        throw error;
        return;
    }
}


getURLMaker(URL, tableId)
{

    let searchData = `?pageRowCount=` + this.shadowRoot.getElementById(`paginationCount${tableId}`).value +
                     `&whichPage=`+this.shadowRoot.getElementById(`paginationWhichPage${tableId}`).value + `&`;

    this.shadowRoot.querySelectorAll(`.columnSortingInput${tableId}`).forEach((e ,i) => {
        if(e.value != '')
            searchData += `dataSent${i}=`+ e.value + "&"
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
        const response = await fetch(URL + `?id=${ID}`, {method: 'DELETE'});
        if(!response.ok)
            throw new Error(`something went Wrong during Fetching Data ${response.status}`);

        const JSON = await response.json();
        return JSON;
        
    }
    catch(error)
    {
        console.log("generate Table has hit the Error during fetching Data : " + error);
    }
}


 getSelectedRowId(tableId)
{

    let table = this.shadowRoot.getElementById(tableId);
    let selectedRadioButton;
    table.querySelectorAll("input").forEach(e => {
        
        if (e.type != "radio")
            return;

        if (!e.checked)
            return;

        selectedRadioButton = e;

    });

    let ID = selectedRadioButton.parentElement.parentElement.id;
    return ID;
}

renderShadowDataCombos(root) {

    root.querySelectorAll('input[type="data-combo"]').forEach(input => {

        input.readOnly = true;
        input.style.caretColor = "transparent";
        input.style.cursor = "default";

        if (!input.id) {
            input.id = `jaliCombo_${crypto.randomUUID()}`;
        }

        input.dataClosed = true;

        const display = document.createElement("div");

        display.setAttribute(
            "data-jali-shadow-combo-display",
            ""
        );

        display.textContent = "▼";

        // Put overlay inside the SAME parent as the input.
        // This makes positioning much more reliable.
        const parent = input.parentElement;

        parent.style.position =
            getComputedStyle(parent).position === "static"
                ? "relative"
                : getComputedStyle(parent).position;

        const inputStyle = getComputedStyle(input);

        display.style.position = "absolute";
        display.style.left = `${input.offsetLeft}px`;
        display.style.top = `${input.offsetTop}px`;
        display.style.width = `${input.offsetWidth}px`;
        display.style.height = `${input.offsetHeight}px`;

        display.style.backgroundColor =
            inputStyle.backgroundColor;

        display.style.color =
            inputStyle.color;

        display.style.font =
            inputStyle.font;

        display.style.fontSize =
            inputStyle.fontSize;

        display.style.fontFamily =
            inputStyle.fontFamily;

        display.style.fontWeight =
            inputStyle.fontWeight;

        display.style.lineHeight =
            inputStyle.lineHeight;

        display.style.padding =
            inputStyle.padding;

        display.style.border =
            inputStyle.border;

        display.style.borderRadius =
            inputStyle.borderRadius;

        display.style.boxSizing = "border-box";

        display.style.cursor = "pointer";

        display.style.display = "flex";
        display.style.alignItems = "center";
        display.style.justifyContent = "space-between";

        display.style.zIndex = "1000";

        parent.appendChild(display);

        display.addEventListener("click", () => {
            input.click();
        });

        input.addEventListener("click", async () => {

            if (input.dataClosed) {

                input.dataClosed = false;

                let container =
                    root.querySelector(
                        `[data-jali-shadow-combo="${CSS.escape(input.id)}"]`
                    );

                if (!container) {

                    container = document.createElement("div");

                    container.setAttribute(
                        "data-jali-shadow-combo",
                        input.id
                    );

                    const inputRect =
                        input.getBoundingClientRect();

                    const parentRect =
                        parent.getBoundingClientRect();

                    container.style.position = "absolute";

                    container.style.left =
                        `${input.offsetLeft}px`;

                    container.style.top =
                        `${input.offsetTop + input.offsetHeight}px`;

                    container.style.width =
                        `${input.offsetWidth}px`;

                    container.style.height =
                        "fit-content";

                    container.style.maxHeight =
                        "250px";

                    container.style.overflowY =
                        "auto";

                    container.style.background =
                        "#ffffff";

                    container.style.color =
                        "#222222";

                    container.style.border =
                        inputStyle.border;

                    container.style.borderRadius =
                        inputStyle.borderRadius;

                    container.style.boxShadow =
                        "0 4px 12px rgba(0,0,0,0.15)";

                    container.style.zIndex =
                        "1001";

                    parent.appendChild(container);

                    await this.createShadowDataCombo(
                        input,
                        container,
                        input.name,
                        display
                    );

                } else {

                    container.style.display = "block";
                }

            } else {

                input.dataClosed = true;

                const container =
                    root.querySelector(
                        `[data-jali-shadow-combo="${CSS.escape(input.id)}"]`
                    );

                if (container) {
                    container.style.display = "none";
                }
            }
        });
    });
}


async createShadowDataCombo(
    input,
    container,
    apiEndpoint,
    display
) {

    const response = await fetch(apiEndpoint);

    if (!response.ok) {
        throw new Error(
            `HTTP error while loading data-combo: ${response.status}`
        );
    }

    const data = await response.json();

    container.innerHTML = "";

    data.forEach(object => {

        const keys = Object.keys(object);

        const id = object[keys[0]];
        const text = object[keys[1]];

        const row =
            document.createElement("div");

        row.setAttribute("dc_row_id", id);
        row.setAttribute("dc_row_name", text);

        row.textContent = text;

        row.style.padding = "8px 10px";
        row.style.background = "#ffffff";
        row.style.color = "#222222";
        row.style.fontFamily =
            getComputedStyle(input).fontFamily;

        row.style.fontSize =
            getComputedStyle(input).fontSize;

        row.style.cursor = "pointer";

        row.addEventListener("mouseenter", () => {
            row.style.background = "#f1f5f9";
        });

        row.addEventListener("mouseleave", () => {
            row.style.background = "#ffffff";
        });

        row.addEventListener("click", () => {

            input.value = id;

            display.textContent = `▼ ${text}`;

            input.dataSelectedName = text;

            input.dataClosed = true;

            container.style.display = "none";
        });

        container.appendChild(row);
    });
}

}
customElements.define('fetch-data-table', FetchDataTable);
