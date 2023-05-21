DROP TABLE Wniosek_o_zmiane;
DROP TABLE Historia_kredyt;
DROP TABLE Kredyt;
DROP TABLE Historia;
DROP TABLE Wlasnosc;
DROP TABLE Konto;
DROP TABLE Pracownik;
DROP TABLE Klient;
DROP TABLE Uzytkownik;

CREATE TABLE Uzytkownik
(
    imie VARCHAR2(40) NOT NULL,
    nazwisko VARCHAR2(40) NOT NULL,
    haslo VARCHAR2(40) NOT NULL,
    mail VARCHAR2(40) NOT NULL,
    id NUMBER(4) PRIMARY KEY
);

CREATE TABLE Klient
(
    id NUMBER(4) REFERENCES Uzytkownik PRIMARY KEY,
    adres VARCHAR2(40) NOT NULl,
    numer_tel VARCHAR2(9) NOT NULL
);

CREATE TABLE Pracownik
(
    id NUMBER(4) REFERENCES Uzytkownik PRIMARY KEY,
    dzial VARCHAR2(40) NOT NULl,
    CONSTRAINT dobry_dzial CHECK (dzial IN ('Obslugi', 'Kredytu'))
);

CREATE TABLE Konto
(
    numer NUMBER(4) PRIMARY KEY,
    waluta CHAR(3) NOT NULL,
    stan NUMBER(9) NOT NULL
);

CREATE TABLE Wlasnosc
(
    id_wlasc NUMBER(4) NOT NULL REFERENCES Klient,
    nr_konto NUMBER(4) NOT NULL REFERENCES Konto,
    CONSTRAINT wlasnosc_pk PRIMARY KEY (id_wlasc, nr_konto)
);

CREATE TABLE Historia
(
    id NUMBER(4) PRIMARY KEY,
    nr_konto NUMBER(4) REFERENCES Konto,
    typ_operacji VARCHAR2(40) NOT NULL,
    kwota NUMBER(9) NOT NULL,
    data DATE NOT NULL,
    CONSTRAINT operacja_check CHECK (typ_operacji IN ('Wplata', 'Wyplata'))
);

CREATE TABLE Kredyt
(
    id NUMBER(4) PRIMARY KEY,
    nr_konta NUMBER(4) NOT NULL REFERENCES Konto,
    id_pracownik NUMBER(4) NOT NULL REFERENCES Pracownik,
    kwota_pozyczki NUMBER(7) NOT NULL,
    kwota_splacona NUMBER(7) NOT NULL,
    oprocentowanie NUMBER(4) NOT NULL,
    CONSTRAINT oprocentowanie_kredyt_check CHECK (oprocentowanie > 0),
    okres NUMBER(4) NOT NULL,
    CONSTRAINT okres_kredyt_check CHECK (okres > 0),
    data DATE NOT NULL,
    czy_zmienne NUMBER(1) NOT NULL,
    stan VARCHAR2(40) NOT NULL,
    CONSTRAINT stan_kredyt_check CHECK (stan IN ('nierozpatrzony', 'splacony', 'w trakcie', 'odrzucony')),
    raty VARCHAR2(40) NOT NULL,
    CONSTRAINT raty_check CHECK (raty IN ('stale', 'malejace')),
    kolejna_rata NUMBER(9) NOT NULL,
    ile_rat_zaplaconych NUMBER(3)
);

CREATE TABLE Historia_kredyt
(
    id NUMBER(4) PRIMARY KEY,
    nr_kredyt NUMBER(4) NOT NULL REFERENCES Kredyt,
    typ_operacji VARCHAR2(40) NOT NULL,
    kwota NUMBER(9) NOT NULL,
    data DATE NOT NULL,
    CONSTRAINT wplata_check CHECK (typ_operacji IN ('Wplata', 'Nadplata'))
);

CREATE TABLE Wniosek_o_zmiane
(
    id_klient NUMBER(4) NOT NULL REFERENCES Klient,
    nazwa_pola VARCHAR2(40) NOT NULL,
    CONSTRAINT dobre_pole CHECK (nazwa_pola IN ('imie', 'nazwisko', 'haslo', 'mail', 'adres', 'numer_tel')),
    nowa_wartosc VARCHAR2(40) NOT NULL,
    id NUMBER(4) PRIMARY KEY
);

INSERT INTO Uzytkownik (imie, nazwisko, haslo, mail, id) VALUES ('Kasia', 'Kowal', 'kasia123', 'kowalkatarzyna809@gmail.com', 0);
INSERT INTO Uzytkownik (imie, nazwisko, haslo, mail, id) VALUES ('Pawel', 'Pawlak', 'pawel123', 'kowalkatarzyna809@gmail.com', 1);
INSERT INTO Uzytkownik (imie, nazwisko, haslo, mail, id) VALUES ('Joanna', 'Koza', 'joanna123', 'kowalkatarzyna809@gmail.com', 2);
INSERT INTO Uzytkownik (imie, nazwisko, haslo, mail, id) VALUES ('Artur', 'Nowak', 'artur123', 'kowalkatarzyna809@gmail.com', 3);
INSERT INTO Klient (id, adres, numer_tel) VALUES (0, 'ulica1', '123456789');
INSERT INTO Klient (id, adres, numer_tel) VALUES (1, 'ulica2', '123456699');
INSERT INTO Pracownik (id, dzial) VALUES (2, 'Obslugi');
INSERT INTO Pracownik (id, dzial) VALUES (3, 'Kredytu');
INSERT INTO Konto (numer, waluta, stan) VALUES (1, 'PLN', 100000);
INSERT INTO Konto (numer, waluta, stan) VALUES (2, 'USD', 20000);
INSERT INTO Konto (numer, waluta, stan) VALUES (3, 'PLN', 2000);
INSERT INTO Wlasnosc (id_wlasc, nr_konto) VALUES (0, 1);
INSERT INTO Wlasnosc (id_wlasc, nr_konto) VALUES (0, 2);
INSERT INTO Wlasnosc (id_wlasc, nr_konto) VALUES (1, 3);
INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (1, 1, 'Wyplata', 1000, TO_DATE('17/12/2022', 'DD/MM/YYYY'));
INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (2, 3, 'Wplata', 1000, TO_DATE('17/12/2022', 'DD/MM/YYYY'));
INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (3, 2, 'Wyplata', 20000, TO_DATE('10/11/2022', 'DD/MM/YYYY'));
INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (4, 1, 'Wyplata', 2000, TO_DATE('15/12/2022', 'DD/MM/YYYY'));
INSERT INTO Historia (id, nr_konto, typ_operacji, kwota, data) VALUES (5, 3, 'Wyplata', 100, TO_DATE('20/12/2022', 'DD/MM/YYYY'));
INSERT INTO Kredyt (id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres, data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych)
    VALUES (0, 1, 2, 1000, 21, 10, 30, TO_DATE('08/02/2023', 'DD/MM/YYYY'), 0, 'w trakcie', 'stale', 21, 1);
INSERT INTO Kredyt (id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres, data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych)
    VALUES (1, 2, 2, 20000, 2500, 10, 90, TO_DATE('05/02/2023', 'DD/MM/YYYY'), 0, 'w trakcie', 'malejace', 2493, 1);
INSERT INTO Kredyt (id, nr_konta, id_pracownik, kwota_pozyczki, kwota_splacona, oprocentowanie, okres, data, czy_zmienne, stan, raty, kolejna_rata, ile_rat_zaplaconych)
    VALUES (2, 3, 2, 500000, 83333, 15, 90, TO_DATE('06/02/2023', 'DD/MM/YYYY'), 1, 'w trakcie', 'malejace', 83214, 1);
INSERT INTO Historia_kredyt (id, nr_kredyt, typ_operacji, kwota, data)
    VALUES (0, 0, 'Wplata', 21, TO_DATE('15/01/2023', 'DD/MM/YYYY'));
INSERT INTO Historia_kredyt (id, nr_kredyt, typ_operacji, kwota, data)
    VALUES (1, 1, 'Wplata', 2500, TO_DATE('25/01/2023', 'DD/MM/YYYY'));
INSERT INTO Historia_kredyt (id, nr_kredyt, typ_operacji, kwota, data)
    VALUES (2, 2, 'Wplata', 83333, TO_DATE('12/12/2022', 'DD/MM/YYYY'));