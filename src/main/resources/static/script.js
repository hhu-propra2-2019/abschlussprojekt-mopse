namesCounter = 1;
ownersCounter = 1;
typesCounter = 1;
tagsCounter = 1;

function addNewInputLine(type, placeholder) {
    const counter = window[type + "Counter"];

    const parent = document.getElementById(type);
    const field = document.createElement("div");
    const buttonContainer = document.createElement("div");
    const text = document.createElement("input");
    const button = document.createElement("input");

    text.type = "text";
    text.name = type + "[" + counter + "]";
    text.placeholder = placeholder;
    text.setAttribute("th:field", type + "[" + counter + "]");
    text.setAttribute("class", "form-control");
    button.type = "button";
    button.value = "+";
    button.setAttribute("class", "btn btn-outline-warning");
    button.setAttribute("onClick", "javascript: addNewInputLine('" + type + "', '" + placeholder + "');");
    field.id = text.name;
    field.setAttribute("class", "material1-search-form-field input-group");
    buttonContainer.setAttribute("class", "input-group-append");

    buttonContainer.appendChild(button);
    field.appendChild(text);
    field.appendChild(buttonContainer);
    parent.appendChild(field);

    window[type + "Counter"]++;

    const delButtonName = type + "[" + counter + "]";
    const delButtonID = type + "Delete";
    document.getElementById(delButtonID)
        .setAttribute("onclick", "javascript: deleteLastInputLine('" + delButtonName + "')");
}

function deleteLastInputLine(del) {
    const idLength = del.length;

    var ifOver9 = 0;
    const substringTestIfOver9 = del.substring(0, idLength - 4);
    if (window[substringTestIfOver9 + "Counter"] > 9) {
        ifOver9 = -1;
    }

    const substring = del.substring(0, idLength - 3 + ifOver9);
    const counter = del.substring(idLength - 2 + ifOver9, idLength - 1);
    if (counter > 0) {
        // delete element
        const child = document.getElementById(del);
        child.parentNode.removeChild(child);

        // update deleteButton
        window[substring + "Counter"]--;
        const newOnclick = substring + "[" + (window[substring + "Counter"] - 1) + "]";
        const delButtonID = substring + "Delete";

        document.getElementById(delButtonID)
            .setAttribute("onclick", "javascript: deleteLastInputLine('" + newOnclick + "')");
    }
}

function showSearchForm() {
    document.getElementById("show-search-button").style.display = "none";
    document.getElementById("material1-search-form").style.display = "flex";
}

function hideSearchForm() {
    document.getElementById("show-search-button").style.display = "block";
    document.getElementById("material1-search-form").style.display = "none";
}

function showPermissionChange() {
    document.getElementById("show-permissions").style.display = "none";
    document.getElementById("hide-permissions").style.display = "block";
}

function hidePermissionChange() {
    document.getElementById("show-permissions").style.display = "block";
    document.getElementById("hide-permissions").style.display = "none";
}

