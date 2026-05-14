# NorthWind Gruppo 2

Applicazione web Java per la consultazione di prodotti e categorie del database Northwind, realizzata con:

- Java 17
- Jakarta Servlet
- Hibernate
- SQLite
- Maven
- Tomcat 10
- Frontend HTML, CSS, JavaScript

## Contenuto del progetto

Il progetto espone due endpoint REST:

- `/api/products`
- `/api/categories`

Il frontend richiama queste servlet e visualizza i dati in tabella.

## Database

Il file database usato dal progetto e' gia' incluso nel repository:

- [northwind.db](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/resources/northwind.db)

Non e' necessario creare il database manualmente.

Durante il deploy, il file viene copiato nelle risorse dell'applicazione e Hibernate lo carica automaticamente dal classpath.

## Requisiti

Per avviare il progetto servono:

- JDK 17 installato
- NetBeans con supporto Java Web / Jakarta EE
- Apache Tomcat 10.x configurato in NetBeans

## Configurazione di Tomcat in NetBeans

Se NetBeans mostra l'errore `No suitable Deployment Server is defined for the project or globally`, significa che Tomcat non e' ancora associato al progetto.

### 1. Installare Tomcat 10

Scaricare Apache Tomcat 10.x dal sito ufficiale:

- [Apache Tomcat](https://tomcat.apache.org/)

Estrarre Tomcat in una cartella locale, per esempio:

```text
C:\apache-tomcat-10
```

### 2. Aggiungere Tomcat a NetBeans

In NetBeans:

1. Aprire `Services`
2. Espandere `Servers`
3. Click destro su `Servers`
4. Selezionare `Add Server...`
5. Scegliere `Apache Tomcat or TomEE`
6. Indicare la cartella di installazione di Tomcat 10
7. Completare il wizard

### 3. Associare Tomcat al progetto

1. Click destro sul progetto
2. Selezionare `Properties`
3. Aprire la sezione `Run`
4. Nel campo `Server` scegliere `Apache Tomcat 10`
5. Salvare con `OK`

## Avvio del progetto

### Metodo consigliato: da NetBeans

1. Aprire il progetto in NetBeans
2. Eseguire `Clean and Build`
3. Avviare Tomcat con `Run Project`
4. Aprire l'applicazione nel browser all'URL pubblicato da NetBeans

Esempio tipico:

```text
http://localhost:8080/northwind-gruppo2/
```

Nota:

- Non aprire `index.html` con doppio click dal file system
- Il frontend funziona solo tramite Tomcat, perche' i dati arrivano dalle servlet Java

## Endpoint disponibili

Una volta avviato Tomcat, puoi testare direttamente anche gli endpoint:

- [Products API](http://localhost:8080/northwind-gruppo2/api/products)
- [Categories API](http://localhost:8080/northwind-gruppo2/api/categories)

Esempi:

```text
GET /northwind-gruppo2/api/products
GET /northwind-gruppo2/api/products?id=1
GET /northwind-gruppo2/api/categories
GET /northwind-gruppo2/api/categories?id=1
```

## Struttura utile del progetto

- [index.html](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/webapp/index.html)
- [app.js](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/webapp/js/app.js)
- [hibernate.cfg.xml](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/resources/hibernate.cfg.xml)
- [HibernateUtil.java](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/java/it/northwind/gruppo2/utils/HibernateUtil.java)
- [ProductServlet.java](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/java/it/northwind/gruppo2/servlets/ProductServlet.java)
- [CategoryServlet.java](C:/Users/Matteo/OneDrive/Documenti/NetBeansProjects/NorthWind_Gruppo2-main/src/main/java/it/northwind/gruppo2/servlets/CategoryServlet.java)

## Risoluzione problemi

### Errore HTTP 500

Se compare un errore `HTTP error! status: 500`, controllare:

- che Tomcat sia avviato correttamente
- che il progetto sia stato rieseguito dopo `Clean and Build`
- che il database `northwind.db` sia presente in `src/main/resources`
- che non si stia aprendo `index.html` direttamente dal file system

### Pagina caricata ma tabella vuota

Controllare:

- che l'URL dell'app sia quello di Tomcat
- che gli endpoint `/api/products` e `/api/categories` rispondano nel browser
- che non ci siano errori nella console di NetBeans o di Tomcat

### NetBeans non mostra Servers

Se in NetBeans non compare la sezione `Servers`, installare il supporto web:

1. Aprire `Tools`
2. Selezionare `Plugins`
3. Installare o abilitare i moduli relativi a `Java Web` o `Jakarta EE`

## Comandi Maven utili

Se Maven e' installato nel sistema:

```bash
mvn clean
mvn package
mvn test
```

Il file WAR generato si trova in:

```text
target/northwind-gruppo2-1.0.0-SNAPSHOT.war
```

## Autori

NorthWind Gruppo 2:

- Butt Abdul Mohid
- Galdini Matteo
- Prodigo Lobo Anna
- Sabattini Riccardo
- Vignali Nicolo'
