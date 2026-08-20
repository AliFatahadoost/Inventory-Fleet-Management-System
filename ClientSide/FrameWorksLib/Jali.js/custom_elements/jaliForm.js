// ============================================================
//
// JALI FRAME FORM
//
// ============================================================

import "http://127.0.0.1:8080/dataCombo";
import "http://127.0.0.1:8080/dateBox";
import "http://127.0.0.1:8080/findObjectBox";



class JaliForm extends HTMLElement {

    constructor() {

        super();

        this.attachShadow({
            mode: "open"
        });


        // ========================================================
        // STYLES
        // ========================================================

        const dataFormStyle =
            document.createElement("link");

        dataFormStyle.rel =
            "stylesheet";

        dataFormStyle.href =
            "http://127.0.0.1:8080/cssDataForm";


        const dateBoxStyle =
            document.createElement("link");

        dateBoxStyle.rel =
            "stylesheet";

        dateBoxStyle.href =
            "http://127.0.0.1:8080/dateBoxCss";


        const dataComboStyle =
            document.createElement("link");

        dataComboStyle.rel =
            "stylesheet";

        dataComboStyle.href =
            "http://127.0.0.1:8080/dataComboCss";


        this.shadowRoot.appendChild(
            dataFormStyle
        );

        this.shadowRoot.appendChild(
            dateBoxStyle
        );

        this.shadowRoot.appendChild(
            dataComboStyle
        );


        // ========================================================
        // CONTAINER
        // ========================================================

        this.container =
            document.createElement("div");

        this.shadowRoot.appendChild(
            this.container
        );


        // ========================================================
        // STATE
        // ========================================================

        this._inputElements = {};

        this._values = {};

    }



    // ============================================================
    // CONNECTED
    // ============================================================

    connectedCallback() {

        this.render();

    }



    // ============================================================
    // ATTRIBUTES
    // ============================================================

    get title() {

        return this.getAttribute("title") || "";

    }


    get classes() {

        return this.getAttribute("classes") || "";

    }


    get inputs() {

        return this.getAttribute("inputs") || "";

    }


    get api() {

        return this.getAttribute("api") || "";

    }


    get apiMethod() {

        return (
            this.getAttribute("api-method") ||
            "POST"
        ).toUpperCase();

    }


    get buttonCount() {

        let count =
            parseInt(
                this.getAttribute("btn-count") || "2",
                10
            );


        if (isNaN(count)) {

            count = 2;

        }


        if (count < 1) {

            count = 1;

        }


        if (count > 2) {

            count = 2;

        }


        return count;

    }


    get buttonNames() {

        return (
            this.getAttribute("btn-names") ||
            "Save Cancel"
        )
        .trim()
        .split(/\s+/);

    }



    // ============================================================
    // RENDER
    // ============================================================

    render() {

        this.container.innerHTML = "";

        this._inputElements = {};


        // ========================================================
        // BUTTONS
        // ========================================================

        const names =
            this.buttonNames;


        const firstButtonName =
            names[0] || "Save";


        const secondButtonName =
            names[1] || "Cancel";


        let buttonsHTML =
            '<button ' +
            'id="jaliFormFirstButton" ' +
            'class="btn btnSave" ' +
            'type="button">' +
            firstButtonName +
            '</button>';


        if (
            this.buttonCount === 2
        ) {

            buttonsHTML +=
                '<button ' +
                'id="jaliFormSecondButton" ' +
                'class="btn btnDelete" ' +
                'type="button">' +
                secondButtonName +
                '</button>';

        }



        // ========================================================
        // INPUTS
        // ========================================================

        const inputHTML =
            this.generateInputHTML(
                this.inputs
            );



        // ========================================================
        // FORM HTML
        // ========================================================

        let HTML =
            '<div class="containerInputForm ' +
            this.classes +
            '">' +

            '<div class="backGround">' +
            '</div>' +

            '<div class="dataCard">' +

            '<div class="titleHolder">' +

            '<h1 class="Title">' +
            this.title +
            '</h1>' +

            '</div>' +

            '<div class="inputSection">' +

            inputHTML +

            '</div>' +

            '<div class="buttonsPlace">' +

            buttonsHTML +

            '</div>' +

            '</div>' +

            '</div>';



        // ========================================================
        // TEMPLATE
        // ========================================================

        const template =
            document.createElement(
                "template"
            );


        template.innerHTML =
            HTML;


        this.container.appendChild(
            template.content.cloneNode(true)
        );



        // ========================================================
        // STORE INPUT ELEMENTS
        // ========================================================

        this.container
            .querySelectorAll(
                "input, data-combo, date-box, find-object-box"
            )
            .forEach(
                element => {

                    const name =
                        element.getAttribute(
                            "name"
                        );


                    if (
                        name
                    ) {

                        this._inputElements[name] =
                            element;

                    }

                }
            );



        // ========================================================
        // FIRST BUTTON = API
        // ========================================================

        const firstButton =
            this.shadowRoot.getElementById(
                "jaliFormFirstButton"
            );


        firstButton.addEventListener(
            "click",
            async () => {

                try {

                    await this.sendToAPI();

                }

                catch (error) {

                    console.error(
                        "JaliForm API error:",
                        error
                    );

                }

            }
        );



        // ========================================================
        // SECOND BUTTON = CLOSE
        // ========================================================

        const secondButton =
            this.shadowRoot.getElementById(
                "jaliFormSecondButton"
            );


        if (
            secondButton
        ) {

            secondButton.addEventListener(
                "click",
                () => {

                    this.dispatchEvent(
                        new CustomEvent(
                            "cancel",
                            {
                                bubbles: true,
                                composed: true
                            }
                        )
                    );


                    this.remove();

                }
            );

        }

    }



    // ============================================================
    // GENERATE INPUT HTML
    //
    // THIS IS BASED DIRECTLY ON YOUR FETCHDATATABLE
    // ============================================================

    generateInputHTML(
        inputString
    ) {

        let inputList = "";

        let tempText =
            inputString;


        for (
            let i = 0;

            i < 1000 &&
            tempText.indexOf("|value::") != -1;

            i++
        ) {

            let tempNameIndex =
                tempText.indexOf("|name::");

            let tempTitleIndex =
                tempText.indexOf("|title::");

            let tempTypeIndex =
                tempText.indexOf("|type::");

            let tempValueIndex =
                tempText.indexOf("|value::");

            let tempSemiColumn =
                tempText.indexOf(";;");


            if (
                tempNameIndex == -1 ||
                tempTitleIndex == -1 ||
                tempTypeIndex == -1 ||
                tempValueIndex == -1 ||
                tempSemiColumn == -1
            ) {

                break;

            }


            let tempName =
                tempText.slice(
                    tempNameIndex +
                    "|name::".length,
                    tempTitleIndex
                );


            let tempTitle =
                tempText.slice(
                    tempTitleIndex +
                    "|title::".length,
                    tempTypeIndex
                );


            let tempType =
                tempText.slice(
                    tempTypeIndex +
                    "|type::".length,
                    tempValueIndex
                );


            let tempDefaultValue =
                tempText.slice(
                    tempValueIndex +
                    "|value::".length,
                    tempSemiColumn
                );



            // ====================================================
            // CHECKBOX
            // ====================================================

            if (
                tempType == "checkbox"
            ) {

                inputList +=

                    '<div>' +

                    '<label>' +
                    tempTitle +
                    '</label>' +

                    '<input ' +

                    'class="jali-form-input" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'type="checkbox" ' +

                    (
                        tempDefaultValue == "1"
                            ? "checked"
                            : ""
                    ) +

                    ' placeholder="' +
                    tempTitle +
                    '">' +

                    '</div>';

            }



            // ====================================================
            // RADIO
            // ====================================================

            else if (
                tempType == "radio"
            ) {

                inputList +=

                    '<div>' +

                    '<input ' +

                    'class="jali-form-input" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'type="radio" ' +

                    'value="' +
                    tempDefaultValue +
                    '" ' +

                    (
                        tempDefaultValue != ""
                            ? "checked"
                            : ""
                    ) +

                    '>' +

                    '<label>' +
                    tempTitle +
                    '</label>' +

                    '</div>';

            }



            // ====================================================
            // DATA COMBO
            //
            // EXACT SAME STRUCTURE AS YOUR DATATABLE
            // ====================================================

            else if (
                tempType == "data-combo"
            ) {

                inputList +=

                    '<div>' +

                    '<label for="' +
                    tempName +
                    '">' +

                    tempTitle +

                    '</label>' +

                    '<data-combo ' +

                    'class="jali-form-input" ' +

                    'type="data-combo" ' +

                    'id="' +
                    tempName +
                    '" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'title="' +
                    tempTitle +
                    '" ' +

                    'value="' +
                    (tempDefaultValue || "") +
                    '">' +

                    '</data-combo>' +

                    '</div>';

            }



            // ====================================================
            // DATE BOX
            //
            // EXACT SAME STRUCTURE AS YOUR DATATABLE
            // ====================================================

            else if (
                tempType == "date-box"
            ) {

                inputList +=

                    '<div>' +

                    '<label for="' +
                    tempName +
                    '">' +

                    tempTitle +

                    '</label>' +

                    '<date-box ' +

                    'class="jali-form-input" ' +

                    'type="date-box" ' +

                    'id="' +
                    tempName +
                    '" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'title="' +
                    tempTitle +
                    '" ' +

                    'value="' +
                    (tempDefaultValue || "") +
                    '">' +

                    '</date-box>' +

                    '</div>';

            }



            // ====================================================
            // FIND OBJECT BOX
            // ====================================================

            else if (
                tempType == "find-object-box"
            ) {

                inputList +=

                    '<div>' +

                    '<label for="' +
                    tempName +
                    '">' +

                    tempTitle +

                    '</label>' +

                    '<find-object-box ' +

                    'class="jali-form-input" ' +

                    'type="find-object-box" ' +

                    'id="' +
                    tempName +
                    '" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'title="' +
                    tempTitle +
                    '" ' +

                    'value="' +
                    (tempDefaultValue || "") +
                    '">' +

                    '</find-object-box>' +

                    '</div>';

            }



            // ====================================================
            // NORMAL INPUT
            // ====================================================

            else {

                inputList +=

                    '<div>' +

                    '<label>' +

                    tempTitle +

                    '</label>' +

                    '<input ' +

                    'class="jali-form-input" ' +

                    'name="' +
                    tempName +
                    '" ' +

                    'type="' +
                    tempType +
                    '" ' +

                    'value="' +
                    tempDefaultValue +
                    '" ' +

                    'placeholder="' +
                    tempTitle +
                    '">' +

                    '</div>';

            }


            // ====================================================
            // NEXT INPUT
            // ====================================================

            tempText =
                tempText.slice(
                    tempSemiColumn + 2
                );

        }


        return inputList;

    }



    // ============================================================
    // GET TYPE
    // ============================================================

    getElementType(
        element
    ) {

        const tag =
            element.tagName.toLowerCase();


        if (
            tag == "data-combo"
        ) {

            return "data-combo";

        }


        if (
            tag == "date-box"
        ) {

            return "date-box";

        }


        if (
            tag == "find-object-box"
        ) {

            return "find-object-box";

        }


        if (
            tag == "input"
        ) {

            return (
                element.type ||
                "text"
            ).toLowerCase();

        }


        return tag;

    }



    // ============================================================
    // COLLECT VALUES
    // ============================================================

    collectValues() {

        const values = {};


        Object.entries(
            this._inputElements
        )
        .forEach(
            ([name, element]) => {

                const type =
                    this.getElementType(
                        element
                    );


                if (
                    type == "checkbox"
                ) {

                    values[name] =
                        element.checked
                            ? 1
                            : 0;

                }


                else if (
                    type == "radio"
                ) {

                    if (
                        element.checked
                    ) {

                        values[name] =
                            element.value;

                    }

                }


                else if (
                    type == "data-combo"
                ) {

                    values[name] =
                        element.dataset.selectedId;

                }


                else if (
                    type == "find-object-box"
                ) {

                    values[name] =
                        element.dataset.selectedId;

                }


                else {

                    values[name] =
                        element.value;

                }

            }
        );


        return values;

    }



    // ============================================================
    // CREATE API JSON
    //
    // SAME FORMAT AS FETCHDATATABLE
    // ============================================================

    createAPIPayload() {

        let sendingJSON = {};


        Object.values(
            this._inputElements
        )
        .forEach(
            (element, index) => {

                const type =
                    this.getElementType(
                        element
                    );


                if (
                    type == "checkbox"
                ) {

                    sendingJSON[
                        "input" + index
                    ] =
                        element.checked
                            ? 1
                            : 0;

                }


                else if (
                    type == "radio"
                ) {

                    if (
                        element.checked
                    ) {

                        sendingJSON[
                            "input" + index
                        ] =
                            element.value;

                    }

                }


                else if (
                    type == "data-combo"
                ) {

                    sendingJSON[
                        "input" + index
                    ] =
                        element.dataset.selectedId;

                }


                else if (
                    type == "find-object-box"
                ) {

                    sendingJSON[
                        "input" + index
                    ] =
                        element.dataset.selectedId;

                }


                else {

                    sendingJSON[
                        "input" + index
                    ] =
                        element.value;

                }

            }
        );


        return sendingJSON;

    }



    // ============================================================
    // SEND TO API
    // ============================================================

    async sendToAPI() {

        if (
            this.api == ""
        ) {

            console.error(
                "JaliForm: api attribute is missing."
            );

            return;

        }


        let sendingJSON =
            this.createAPIPayload();


        console.log(
            "JaliForm sending:",
            sendingJSON
        );


        try {

            const response =
                await fetch(
                    this.api,
                    {

                        method:
                            this.apiMethod,

                        headers:
                            {
                                "Content-Type":
                                    "application/json"
                            },

                        body:
                            JSON.stringify(
                                sendingJSON
                            )

                    }
                );


            if (
                !response.ok
            ) {

                throw new Error(
                    "HTTP error! status: " +
                    response.status
                );

            }


            let result;


            const contentType =
                response.headers.get(
                    "content-type"
                );


            if (
                contentType &&
                contentType.includes(
                    "application/json"
                )
            ) {

                result =
                    await response.json();

            }

            else {

                result =
                    await response.text();

            }


            this._values =
                this.collectValues();


            // ====================================================
            // SUBMIT EVENT
            // ====================================================

            this.dispatchEvent(
                new CustomEvent(
                    "submit",
                    {
                        bubbles: true,
                        composed: true,

                        detail: {
                            values:
                                this._values,

                            payload:
                                sendingJSON,

                            response:
                                result,

                            status:
                                response.status
                        }
                    }
                )
            );


            // ====================================================
            // API SUCCESS EVENT
            // ====================================================

            this.dispatchEvent(
                new CustomEvent(
                    "api-success",
                    {
                        bubbles: true,
                        composed: true,

                        detail: {
                            values:
                                this._values,

                            payload:
                                sendingJSON,

                            response:
                                result,

                            status:
                                response.status
                        }
                    }
                )
            );


            return result;

        }

        catch (error) {

            this.dispatchEvent(
                new CustomEvent(
                    "api-error",
                    {
                        bubbles: true,
                        composed: true,

                        detail: {
                            error:
                                error,

                            payload:
                                sendingJSON
                        }
                    }
                )
            );


            throw error;

        }

    }



    // ============================================================
    // PUBLIC VALUE API
    // ============================================================

    get values() {

        return this.collectValues();

    }


    set values(values) {

        if (
            !values
        ) {

            return;

        }


        Object.entries(
            values
        )
        .forEach(
            ([name, value]) => {

                this.setValue(
                    name,
                    value
                );

            }
        );

    }



    // ============================================================
    // GET SINGLE VALUE
    // ============================================================

    getValue(
        name
    ) {

        const element =
            this._inputElements[name];


        if (
            !element
        ) {

            return undefined;

        }


        const type =
            this.getElementType(
                element
            );


        if (
            type == "checkbox"
        ) {

            return element.checked
                ? 1
                : 0;

        }


        if (
            type == "radio"
        ) {

            if (
                !element.checked
            ) {

                return undefined;

            }


            return element.value;

        }


        if (
            type == "data-combo"
        ) {

            return element.dataset.selectedId;

        }


        if (
            type == "find-object-box"
        ) {

            return element.dataset.selectedId;

        }


        return element.value;

    }



    // ============================================================
    // SET SINGLE VALUE
    // ============================================================

    setValue(
        name,
        value
    ) {

        const element =
            this._inputElements[name];


        if (
            !element
        ) {

            return;

        }


        const type =
            this.getElementType(
                element
            );


        if (
            type == "checkbox"
        ) {

            element.checked =
                value == 1;

            return;

        }


        if (
            type == "radio"
        ) {

            element.checked =
                String(
                    element.value
                ) ===
                String(
                    value
                );

            return;

        }


        element.value =
            value;

    }

}



// ============================================================
// REGISTER
// ============================================================

if (
    !customElements.get("jali-form")
) {

    customElements.define(
        "jali-form",
        JaliForm
    );

}