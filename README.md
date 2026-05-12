# NorthWind_Gruppo2
NorthWind, Gruppo 2: Butt Abdul Mohid, Galdini Matteo, Prodigo Lobo Anna, Sabattini Riccardo, Vignali Nicolò


Progetto: Northwind Entity Manager 🚀
Obiettivo: Realizzare un'applicazione web completa per la gestione CRUD di una tabella del database Northwind, utilizzando Java Servlet, Hibernate e un frontend dinamico in JavaScript.

 
🏗️ Architettura del Sistema
Il progetto deve seguire una struttura a livelli per separare le responsabilità:

Livello di Persistenza (Hibernate): Si occupa di mappare le classi Java sulle tabelle del DB Northwind.

Livello di Logica (Servlet): Funge da controller, riceve le richieste HTTP, dialoga con il DAO e restituisce risposte in formato JSON.

Livello di Presentazione (HTML/JS): L'utente interagisce con una pagina singola (SPA - Single Page Application) che comunica con il server in modo asincrono.

🛠️ Requisiti Tecnici e Consegna
1. Backend (Java)
Hibernate: Utilizzare le annotazioni JPA (es. @Entity, @Table, @Id) per mappare la tabella scelta.

Servlet: La servlet deve rispondere all'URL /api/data (o simile) e gestire:

GET: Restituisce la lista di tutti i record in formato JSON.

POST: Inserisce un nuovo record.

PUT: Aggiorna un record esistente (passando l'ID).

DELETE: Rimuove un record tramite ID.

Dipendenze: Gestire il progetto tramite Maven.

2. Frontend (Web)
Interfaccia: Una tabella HTML dinamica per visualizzare i dati e un form per l'inserimento/modifica.

JavaScript:

Utilizzare XMLHttpRequest per chiamare la Servlet.

Manipolare il DOM per aggiornare la tabella senza ricaricare la pagina.

Gestire gli eventi sui bottoni "Modifica" ed "Elimina".

3. Database
Utilizzare il database Northwind che trovate qui: https://github.com/jpwhite3/northwind-SQLite3.

Consiglio: Evitate la tabella Orders per l'inizio, è troppo complessa a causa delle relazioni. Meglio Categories, Shippers o Suppliers.

📝 Modalità di Consegna
Gli studenti dovranno consegnare un archivio .zip e un link a una repository GitHub contenente:

Il codice sorgente completo.

Un file README.md con le istruzioni per configurare il database e avviare il server (Tomcat).

Qualche screenshot di operatività dell'applicazione durante i test fatti a casa.
