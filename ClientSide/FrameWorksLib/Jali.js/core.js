import "http://127.0.0.1:8080/tableFormElement"
import "http://127.0.0.1:8080/dataCombo"
import { renderDataCombo } from "http://127.0.0.1:8080/dataCombo";




const dataCombo = document.createElement("link");
        dataCombo.rel = "stylesheet";
        dataCombo.href = "http://127.0.0.1:8080/dataComboCss";

document.body.appendChild(dataCombo);
