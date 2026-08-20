// ============================================================
// JALIFRAME DATE BOX
// ============================================================

class DateBox extends HTMLElement {

    constructor() {
        super();

        // ========================================================
        // STATE
        // ========================================================

        this._value = "";
        this.selectedDate = null;

        this.currentMonth = new Date().getMonth();
        this.currentYear = new Date().getFullYear();

        this.opened = false;
        this.initialized = false;


        // ========================================================
        // LOAD CSS
        // ========================================================

        if (!document.querySelector('link[data-jali-date-box-css]')) {

            const style = document.createElement("link");

            style.rel = "stylesheet";
            style.href = "/dateBoxCss";
            style.dataset.jaliDateBoxCss = "true";

            document.head.appendChild(style);
        }


        // ========================================================
        // ROOT
        // ========================================================

        this.root = document.createElement("div");
        this.root.className = "jali-date-box";


        // ========================================================
        // DISPLAY
        // ========================================================

        this.display = document.createElement("div");
        this.display.className = "jali-date-box-display";


        // ========================================================
        // INPUT
        // ========================================================

        this.input = document.createElement("input");

        this.input.type = "text";
        this.input.className = "jali-date-box-input";
        this.input.readOnly = true;
        this.input.placeholder = "Select date...";
        this.input.setAttribute("aria-label", "Selected date");


        // ========================================================
        // BUTTON
        // ========================================================

        this.toggleButton = document.createElement("button");

        this.toggleButton.type = "button";
        this.toggleButton.className = "jali-date-box-toggle";
        this.toggleButton.setAttribute("aria-label", "Open calendar");

        this.toggleButton.innerHTML = "📅";


        // ========================================================
        // BUILD DISPLAY
        // ========================================================

        this.display.appendChild(this.input);
        this.display.appendChild(this.toggleButton);

        this.root.appendChild(this.display);


        // ========================================================
        // CALENDAR POPUP
        // ========================================================

        this.dropdown = document.createElement("div");

        this.dropdown.className = "jali-date-box-dropdown";
        this.dropdown.setAttribute("role", "dialog");
        this.dropdown.setAttribute("aria-label", "Calendar");

        this.dropdown.style.display = "none";


        // ========================================================
        // CALENDAR
        // ========================================================

        this.calendar = document.createElement("div");
        this.calendar.className = "jali-date-box-calendar";


        // ========================================================
        // HEADER
        // ========================================================

        this.header = document.createElement("div");
        this.header.className = "jali-date-box-header";


        this.previousButton = document.createElement("button");

        this.previousButton.type = "button";
        this.previousButton.className = "jali-date-box-nav previous";
        this.previousButton.innerHTML = "‹";
        this.previousButton.setAttribute(
            "aria-label",
            "Previous month"
        );


        this.monthTitle = document.createElement("button");

        this.monthTitle.type = "button";
        this.monthTitle.className = "jali-date-box-month-title";


        this.nextButton = document.createElement("button");

        this.nextButton.type = "button";
        this.nextButton.className = "jali-date-box-nav next";
        this.nextButton.innerHTML = "›";
        this.nextButton.setAttribute(
            "aria-label",
            "Next month"
        );


        this.header.appendChild(this.previousButton);
        this.header.appendChild(this.monthTitle);
        this.header.appendChild(this.nextButton);


        // ========================================================
        // WEEKDAY HEADER
        // ========================================================

        this.weekHeader = document.createElement("div");
        this.weekHeader.className = "jali-date-box-week-header";

        const weekdays = [
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        ];

        weekdays.forEach(day => {

            const element = document.createElement("div");

            element.className = "jali-date-box-weekday";
            element.textContent = day;

            this.weekHeader.appendChild(element);
        });


        // ========================================================
        // DAY GRID
        // ========================================================

        this.dayGrid = document.createElement("div");
        this.dayGrid.className = "jali-date-box-day-grid";


        // ========================================================
        // FOOTER
        // ========================================================

        this.footer = document.createElement("div");
        this.footer.className = "jali-date-box-footer";


        this.todayButton = document.createElement("button");

        this.todayButton.type = "button";
        this.todayButton.className = "jali-date-box-today";
        this.todayButton.textContent = "Today";


        this.clearButton = document.createElement("button");

        this.clearButton.type = "button";
        this.clearButton.className = "jali-date-box-clear";
        this.clearButton.textContent = "Clear";


        this.footer.appendChild(this.clearButton);
        this.footer.appendChild(this.todayButton);


        // ========================================================
        // BUILD CALENDAR
        // ========================================================

        this.calendar.appendChild(this.header);
        this.calendar.appendChild(this.weekHeader);
        this.calendar.appendChild(this.dayGrid);
        this.calendar.appendChild(this.footer);

        this.dropdown.appendChild(this.calendar);

        this.root.appendChild(this.dropdown);

        this.appendChild(this.root);


        // ========================================================
        // EVENTS
        // ========================================================

        this.display.addEventListener("click", (event) => {

            event.stopPropagation();

            this.toggle();
        });


        this.dropdown.addEventListener("click", (event) => {

            event.stopPropagation();
        });


        this.previousButton.addEventListener("click", () => {

            this.changeMonth(-1);
        });


        this.nextButton.addEventListener("click", () => {

            this.changeMonth(1);
        });


        this.monthTitle.addEventListener("click", () => {

            this.goToToday();
        });


        this.todayButton.addEventListener("click", () => {

            this.selectDate(new Date());

            this.close();
        });


        this.clearButton.addEventListener("click", () => {

            this.clear();
        });


        this._outsideClick = (event) => {

            if (!this.contains(event.target)) {

                this.close();
            }
        };


        document.addEventListener(
            "click",
            this._outsideClick
        );


        // ========================================================
        // KEYBOARD
        // ========================================================

        this.addEventListener("keydown", (event) => {

            if (!this.opened) {
                return;
            }

            switch (event.key) {

                case "Escape":

                    event.preventDefault();

                    this.close();

                    break;


                case "ArrowLeft":

                    event.preventDefault();

                    this.moveSelectedDay(-1);

                    break;


                case "ArrowRight":

                    event.preventDefault();

                    this.moveSelectedDay(1);

                    break;


                case "ArrowUp":

                    event.preventDefault();

                    this.moveSelectedDay(-7);

                    break;


                case "ArrowDown":

                    event.preventDefault();

                    this.moveSelectedDay(7);

                    break;
            }
        });
    }


    // ============================================================
    // CONNECTED
    // ============================================================

    connectedCallback() {

        if (this.initialized) {
            return;
        }

        this.initialized = true;

        this.tabIndex = 0;

        this.renderCalendar();


        // If HTML has:
        //
        // <date-box value="2026-08-20"></date-box>
        //
        const attributeValue = this.getAttribute("value");

        if (attributeValue) {

            this.value = attributeValue;
        }
    }


    // ============================================================
    // OPEN / CLOSE
    // ============================================================

    open() {

        this.opened = true;

        this.dropdown.style.display = "block";

        this.setAttribute("aria-expanded", "true");

        this.renderCalendar();
    }


    close() {

        this.opened = false;

        this.dropdown.style.display = "none";

        this.setAttribute("aria-expanded", "false");
    }


    toggle() {

        if (this.opened) {

            this.close();

        } else {

            this.open();
        }
    }


    // ============================================================
    // MONTH NAVIGATION
    // ============================================================

    changeMonth(amount) {

        this.currentMonth += amount;


        if (this.currentMonth < 0) {

            this.currentMonth = 11;

            this.currentYear--;
        }


        if (this.currentMonth > 11) {

            this.currentMonth = 0;

            this.currentYear++;
        }


        this.renderCalendar();
    }


    goToToday() {

        const today = new Date();

        this.currentMonth = today.getMonth();
        this.currentYear = today.getFullYear();

        this.renderCalendar();
    }


    // ============================================================
    // RENDER CALENDAR
    // ============================================================

    renderCalendar() {

        const monthNames = [
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        ];


        this.monthTitle.textContent =
            `${monthNames[this.currentMonth]} ${this.currentYear}`;


        this.dayGrid.innerHTML = "";


        // ========================================================
        // FIRST DAY OF MONTH
        // ========================================================

        const firstDay = new Date(
            this.currentYear,
            this.currentMonth,
            1
        );


        const firstWeekday = firstDay.getDay();


        // ========================================================
        // DAYS IN MONTH
        // ========================================================

        const daysInMonth = new Date(
            this.currentYear,
            this.currentMonth + 1,
            0
        ).getDate();


        // ========================================================
        // DAYS IN PREVIOUS MONTH
        // ========================================================

        const daysInPreviousMonth = new Date(
            this.currentYear,
            this.currentMonth,
            0
        ).getDate();


        // ========================================================
        // TOTAL 42 CELLS
        // ========================================================

        for (let i = 0; i < 42; i++) {

            let dayNumber;
            let month = this.currentMonth;
            let year = this.currentYear;

            let outsideMonth = false;


            // Previous month
            if (i < firstWeekday) {

                dayNumber =
                    daysInPreviousMonth -
                    firstWeekday +
                    i +
                    1;

                month--;

                if (month < 0) {

                    month = 11;
                    year--;
                }

                outsideMonth = true;
            }


            // Current month
            else if (
                i <
                firstWeekday + daysInMonth
            ) {

                dayNumber =
                    i -
                    firstWeekday +
                    1;
            }


            // Next month
            else {

                dayNumber =
                    i -
                    firstWeekday -
                    daysInMonth +
                    1;

                month++;

                if (month > 11) {

                    month = 0;
                    year++;
                }

                outsideMonth = true;
            }


            const date = new Date(
                year,
                month,
                dayNumber
            );


            const button = document.createElement("button");

            button.type = "button";

            button.className =
                "jali-date-box-day";


            button.textContent = dayNumber;


            // ====================================================
            // OTHER MONTH
            // ====================================================

            if (outsideMonth) {

                button.classList.add(
                    "other-month"
                );
            }


            // ====================================================
            // TODAY
            // ====================================================

            if (this.isToday(date)) {

                button.classList.add(
                    "today"
                );
            }


            // ====================================================
            // SELECTED
            // ====================================================

            if (
                this.selectedDate &&
                this.sameDate(
                    date,
                    this.selectedDate
                )
            ) {

                button.classList.add(
                    "selected"
                );
            }


            // ====================================================
            // CLICK
            // ====================================================

            button.addEventListener(
                "click",
                () => {

                    this.selectDate(date);

                    this.close();
                }
            );


            this.dayGrid.appendChild(button);
        }
    }


    // ============================================================
    // SELECT DATE
    // ============================================================

    selectDate(date) {

        if (!(date instanceof Date)) {
            return;
        }


        this.selectedDate = new Date(
            date.getFullYear(),
            date.getMonth(),
            date.getDate()
        );


        this.currentYear =
            this.selectedDate.getFullYear();


        this.currentMonth =
            this.selectedDate.getMonth();


        const year =
            this.selectedDate.getFullYear();


        const month =
            String(
                this.selectedDate.getMonth() + 1
            ).padStart(2, "0");


        const day =
            String(
                this.selectedDate.getDate()
            ).padStart(2, "0");


        this._value =
            `${year}-${month}-${day}`;


        this.input.value =
            this._value;


        this.dataset.selectedDate =
            this._value;


        this.renderCalendar();


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
    // CLEAR
    // ============================================================

    clear() {

        this._value = "";

        this.selectedDate = null;

        this.input.value = "";

        delete this.dataset.selectedDate;

        this.renderCalendar();


        this.dispatchEvent(
            new Event(
                "change",
                {
                    bubbles: true
                }
            )
        );


        this.close();
    }


    // ============================================================
    // MOVE SELECTED DATE
    // ============================================================

    moveSelectedDay(amount) {

        let date;


        if (this.selectedDate) {

            date = new Date(
                this.selectedDate
            );

        } else {

            date = new Date();
        }


        date.setDate(
            date.getDate() + amount
        );


        this.selectDate(date);
    }


    // ============================================================
    // VALUE GETTER
    // ============================================================

    get value() {

        return this._value;
    }


    // ============================================================
    // VALUE SETTER
    // ============================================================

    set value(value) {

        if (
            value === null ||
            value === undefined ||
            value === ""
        ) {

            this.clear();

            return;
        }


        // Expected:
        // YYYY-MM-DD

        const match =
            /^(\d{4})-(\d{2})-(\d{2})$/.exec(
                String(value)
            );


        if (!match) {

            console.warn(
                "DateBox: invalid date format. Expected YYYY-MM-DD"
            );

            return;
        }


        const year =
            Number(match[1]);


        const month =
            Number(match[2]);


        const day =
            Number(match[3]);


        const date =
            new Date(
                year,
                month - 1,
                day
            );


        // Prevent JS Date from accepting nonsense
        // such as 2026-02-31.

        if (
            date.getFullYear() !== year ||
            date.getMonth() !== month - 1 ||
            date.getDate() !== day
        ) {

            console.warn(
                "DateBox: invalid calendar date:",
                value
            );

            return;
        }


        this.selectedDate = date;

        this.currentYear = year;

        this.currentMonth = month - 1;

        this._value =
            `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(2, "0")}`;


        if (this.input) {

            this.input.value =
                this._value;
        }


        this.dataset.selectedDate =
            this._value;


        if (this.initialized) {

            this.renderCalendar();
        }
    }


    // ============================================================
    // HELPERS
    // ============================================================

    sameDate(a, b) {

        return (
            a.getFullYear() === b.getFullYear() &&
            a.getMonth() === b.getMonth() &&
            a.getDate() === b.getDate()
        );
    }


    isToday(date) {

        return this.sameDate(
            date,
            new Date()
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
}


// ============================================================
// REGISTER
// ============================================================

if (!customElements.get("date-box")) {

    customElements.define(
        "date-box",
        DateBox
    );
}