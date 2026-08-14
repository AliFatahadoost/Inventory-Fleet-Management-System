renderDataCombo();
export function renderDataCombo()
{
    document.querySelectorAll('input[type="data-combo"]').forEach(e => {

        e.value = "";
        e.readOnly = true;
        e.style.caretColor = "transparent";
        e.style.cursor = "default";

        e.id = e.title;
        e.dataClosed = true;

        // Create the visible overlay
        const display = document.createElement("div");

        display.id = `display_${e.id}`;
        display.setAttribute("data-jali-combo-display", "");

        display.textContent = "▼";

        const rect = e.getBoundingClientRect();

        display.style.position = "absolute";
        display.style.left = `${rect.left}px`;
        display.style.top = `${rect.top}px`;
        display.style.width = `${rect.width}px`;
        display.style.height = `${rect.height}px`;

        display.style.backgroundColor = getComputedStyle(e).backgroundColor;
        display.style.font = getComputedStyle(e).font;
        display.style.padding = getComputedStyle(e).padding;
        display.style.border = getComputedStyle(e).border;
        display.style.boxSizing = "border-box";

        display.style.cursor = "default";

        display.addEventListener("click", () => {
            e.click();
        });

        e.parentElement.appendChild(display);


        // Actual input click handler
        e.addEventListener("click", element => {

            if (e.dataClosed)
            {
                e.dataClosed = false;

                if (!document.getElementById(`div_${e.id}`))
                {
                    e.parentElement.insertAdjacentHTML(
                        "beforeend",
                        `<div id="div_${e.id}"></div>`
                    );

                    let div = document.getElementById(`div_${e.id}`);

                    const rect = e.getBoundingClientRect();

                    div.style.position = "absolute";
                    div.style.left = `${rect.left}px`;
                    div.style.top = `${rect.bottom}px`;
                    div.style.width = `${rect.width}px`;
                    div.style.height = "fit-content";

                    createDataCombo(
                        e.id,
                        `div_${e.id}`,
                        e.name,
                        display
                    );
                    
                }
                else
                {
                    document.getElementById(`div_${e.id}`).style.display = "block";
                }
            }
            else
            {
                e.dataClosed = true;

                document.getElementById(`div_${e.id}`).style.display = "none";
            }
        });
    });
}


async function createDataCombo(inputId, containerId, apiEndpoint, display)
{
    const input = document.getElementById(inputId);
    const container = document.getElementById(containerId);

    if (!input)
    {
        throw new Error(`Input with id '${inputId}' was not found.`);
    }

    if (!container)
    {
        throw new Error(`Container with id '${containerId}' was not found.`);
    }

    const response = await fetch(apiEndpoint);

    if (!response.ok)
    {
        throw new Error(`HTTP error: ${response.status}`);
    }

    const data = await response.json();

    container.innerHTML = "";

    data.forEach((object, i )=> {

        const keys = Object.keys(object);

        const id = object[keys[0]];
        const text = object[keys[1]];

        const row = document.createElement("div");

        row.setAttribute("dc_row_id", id);
        row.setAttribute("dc_row_name", text);

        row.textContent = text;

        row.addEventListener("click", () => {

            // Actual submitted/input value
            input.value = id;
            
            // What the user sees
            display.textContent = ` ▼  ${text}`;

            // Keep the selected name available
            input.dataSelectedName = text;

            // Close the combo
            input.dataClosed = true;
            container.style.display = "none";
            console.log(input.value);
        });

        container.appendChild(row);
    });
}