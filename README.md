/-------------------------------------------------
  Armory App
  Version 0.0.1
-------------------------------------------------/

/-------------------------
  Version History
-------------------------/
- 0.0.1 - Susanna Dong (2015/05/10)
    - Import armory Excel spreadsheet from hard drive and load data int app in-memory
    - Use a simple interface to designate armory equipment to fencers
    - Export rented equipment mapping to fencers into a PDF
    - Implemented framework for remote database communication

/-------------------------
  Current Development
-------------------------/
- Write Ant build file to compile
- Fix bugs
- Make this user-friendly
- Make a stable version

/-------------------------
  Hopes of Future Development
-------------------------/
- Connect to remote database server
    - PRO: enables multiuser capabilities, safer backup
    - NOTE: be sure that the server is maintained yearly to make sure
            no surprise fees pop up in incoming years. That being said,
            make sure someone knows how to maintain this code.
- Armory inventory management
    - More autonomous functions
    - Report generation
    - Semester inspection reminders
    - Easy integration with checkout
    - Overdue checkout alerts
    - Store historical data <- do note this needs to be cleared yearly
    - NOTE: recommended to have a database set up
- Import fencer data via club roster
- Scan Buzzcards (easy to do with available apps)
- Application on mobile platform


/-------------------------
  SQL CREATE statements 0.0.1
-------------------------/

createdb yjfcdb

CREATE TABLE Fencer (
    gtid        char(9),
    name        varchar(50)    NOT NULL,
    PRIMARY KEY (gtid)
);

CREATE TABLE Admin (
    gtid        char(9)        REFERENCES Fencer (gtid),
    PRIMARY KEY(gtid)
);

CREATE TABLE CheckableItem (
    type        char(2),
    num         int,
    size        varchar(5),
    condition   varchar(10),
    date_purch  date,
    last_insp   date,
    PRIMARY KEY (type, num)
);

CREATE TABLE Checkout (
    gtid        char(9)     REFERENCES Fencer(gtid),
    type        char(2)     REFERENCES CheckableItem(type),
    num         int         REFERENCES CheckableItem(num),
    date_out    date        NOT NULL,
    PRIMARY KEY (type, num, date_out)
);

CREATE TABLE ArmoryStock (
    name        varchar(35),
    quantity    int,
    low_q       int,
PRIMARY KEY (name)
);

CREATE TABLE ArmoryStockHistory (
    name        varchar(35)    REFERENCES ArmoryStock (name),
    date        date,
    amount      int,
    unit_price  float,
PRIMARY KEY (name, date)
);

CREATE VIEW Jackets AS
    SELECT *
    FROM CheckableItem
    WHERE type = ‘Jacket’;
