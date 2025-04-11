# Documentazione API Fabrick

## Indice
1. [Panoramica del Progetto](#panoramica-del-progetto)
2. [Architettura](#architettura)
3. [Componenti Principali](#componenti-principali)
4. [API REST Esposte](#api-rest-esposte)
5. [Integrazione con Fabrick API](#integrazione-con-fabrick-api)
6. [Persistenza Dati (Opzionale)](#persistenza-dati-opzionale)
7. [Testing](#testing)
8. [Gestione degli Errori](#gestione-degli-errori)
9. [Configurazione](#configurazione)
10. [Logging](#logging)
11. [Come Eseguire](#come-eseguire)
12. [Postman Collection](#postman-collection)

## Panoramica del Progetto

Questo progetto implementa un servizio REST che agisce come intermediario per l'accesso alle API bancarie esposte da Fabrick. Il servizio offre funzionalità per:

- Consultare il saldo di un conto
- Visualizzare l'elenco delle transazioni in un periodo specificato
- Effettuare bonifici (NON è funzionante in sandbox)

La soluzione è sviluppata in Java utilizzando il framework Spring Boot, con un'architettura a più livelli che garantisce una chiara separazione delle responsabilità.

## Architettura

L'applicazione segue un'architettura a strati con i seguenti componenti principali:

```mermaid
graph TD
    A[Client API Consumer] -->|REST Request| B[Controller Layer]
    B -->|Service Call| C[Service Layer]
    C -->|Repository Call| D[Repository Layer]
    C -->|API Call| E[External API Client]
    D -->|Data Access| F[(H2 Database)]
    E -->|HTTP Request| G[Fabrick API]
    
```

### Flusso di Elaborazione delle Richieste

```mermaid
sequenceDiagram
    participant Client
    participant Controller
    participant Service
    participant FabrickClient
    participant Repository
    participant FabrickAPI
    
    Client->>Controller: Richiesta REST
    Controller->>Service: Delega richiesta
    Service->>FabrickClient: Chiama API esterna
    FabrickClient->>FabrickAPI: Effettua richiesta HTTP
    FabrickAPI-->>FabrickClient: Risposta API
    FabrickClient-->>Service: Risultato elaborato
    
    alt Persistenza abilitata
        Service->>Repository: Salva dati (se necessario)
        Repository-->>Service: Conferma
    end
    
    Service-->>Controller: Risultato elaborato
    Controller-->>Client: Risposta REST
```

## Componenti Principali

### Moduli Principali

L'applicazione è organizzata nei seguenti moduli principali:

1. **Controller Layer**: Gestisce le richieste HTTP in entrata e restituisce le risposte appropriate
2. **Service Layer**: Contiene la logica di business e coordina l'interazione tra i repository e i client esterni
3. **Repository Layer**: Gestisce la persistenza dei dati
4. **Client Layer**: Interagisce con le API esterne di Fabrick
5. **DTO (Data Transfer Objects)**: Oggetti utilizzati per il trasferimento dei dati tra i vari strati
6. **Modello del Dominio**: Rappresenta le entità di business dell'applicazione
7. **Exception Handling**: Gestione centralizzata degli errori
8. **Configuration**: Configurazione dell'applicazione

### Struttura dei Pacchetti

```
com.orbyta_admission_quiz
├── client/                  # Client API esterni
    └── fabrick/             # Client delle API Fabrick
├── config/                  # Configurazioni Spring
├── controller/              # REST controllers
├── dto/                     # Data Transfer Objects
│   ├── account/             # Oggetti conto
│       ├──── request/       # Oggetti richiesta
│       ├──── response/      # Oggetti risposta
│   ├── payments/            # Oggetti pagamenti
│       ├──── request/       # Oggetti richiesta
│       ├──── response/      # Oggetti risposta
│   └── errors/              # Oggetti di errore
├── exception/               # Classi per la gestione delle eccezioni
├── logging/                 # Configurazione del logging, AOP, MDC
├── repository/              # Interfacce di accesso ai dati
├── service/                 # Servizi di business logic
│   └── impl/                # Implementazioni dei servizi
└── util/                    # Classi di utilità
```



## API REST Esposte

L'applicazione espone le seguenti API REST:

### 1. Consultazione Saldo

```
GET /api/v1/accounts/{accountId}/balance
```

**Parametri URL**:
- `accountId`: ID del conto (Long)

**Risposta di successo**:
```json
{
    "balance": -3.06,
    "availableBalance": -3.06,
    "currency": "EUR",
    "date": "2025-04-09"
}
```

### 2. Lista Transazioni

```
GET /api/v1/accounts/{accountId}/transactions?fromAccountingDate=2019-04-01&toAccountingDate=2019-06-01
```

**Parametri URL**:
- `accountId`: ID del conto (Long)

**Parametri Query**:
- `fromAccountingDate`: Data di inizio (formato YYYY-MM-DD)
- `toAccountingDate`: Data di fine (formato YYYY-MM-DD)

**Risposta di successo**:
```json
[
    {
        "transactionId": "314569",
        "operationId": "00000000314569",
        "accountingDate": "2019-05-31",
        "valueDate": "2019-06-01",
        "amount": -28.40,
        "currency": "EUR",
        "description": "PD VISA CORPORATE 04",
        "accountId": 14537780
    },
    {
        "transactionId": "038917",
        "operationId": "00000000038917",
        "accountingDate": "2019-04-30",
        "valueDate": "2019-05-01",
        "amount": -62.40,
        "currency": "EUR",
        "description": "PD VISA CORPORATE 03",
        "accountId": 14537780
    }
]
```

### 3. Esecuzione Bonifico

```
POST /api/v1/accounts/14537780/payments/money-transfers
```

**Parametri URL**:
- `accountId`: ID del conto (Long)

**Corpo della richiesta**:
```json
{
   "creditor": {
      "name": "TERRIBILE LUCA",
      "account": {
         "accountCode": "IT33U36772223000EM000002145"
      }
   },
   "executionDate": "2025-04-30",
   "description": "Payment invoice 5/2025",
   "amount": 20,
   "currency": "EUR"
}
```

**Risposta attuale** (nell'ambiente sandbox):
```json
{
  "moneyTransferId": "611010968",
  "status": "BOOKED",
  "direction": "OUTGOING",
  "creditor": {
    "name": "TERRIBILE LUCA",
    "account": {
      "accountCode": "IT33U36772223000EM000002145",
      "bicCode": "HYEEIT22XXX"
    }
  },
  "debtor": {
    "name": "LUCA TERRIBILE",
    "account": {
      "accountCode": "IT40L0326822311052923800661",
      "bicCode": null
    }
  },
  "cro": "1851425001003268",
  "uri": "NOTPROVIDED",
  "trn": "",
  "description": "PAYMENT INVOICE 5/2025",
  "createdDatetime": "2025-04-10T23:14:23.927+0200",
  "accountedDatetime": "",
  "debtorValueDate": "2025-04-30",
  "creditorValueDate": "2025-04-30",
  "amount": {
    "debtorAmount": 20,
    "debtorCurrency": "EUR",
    "creditorAmount": 20,
    "creditorCurrency": "EUR"
  },
  "isUrgent": false,
  "isInstant": false,
  "feeType": "SHA",
  "feeAccountId": "14537780",
  "fees": [],
  "hasTaxRelief": false
}
```

## Integrazione con Fabrick API

L'applicazione si integra con le seguenti API di Fabrick:

### Endpoint utilizzati

1. **Lettura Saldo**:
   ```
   GET {baseUrl}/api/gbs/banking/v4.0/accounts/{accountId}/balance
   ```

2. **Lista Transazioni**:
   ```
   GET {baseUrl}/api/gbs/banking/v4.0/accounts/{accountId}/transactions?fromAccountingDate={fromAccountingDate}&toAccountingDate={toAccountingDate}
   ```

3. **Bonifico**:
   ```
   POST {baseUrl}/api/gbs/banking/v4.0/accounts/{accountId}/payments/money-transfers
   ```

### Configurazione dell'Integrazione

```mermaid
graph LR
    A[FabrickApiClient] -->|config| B[interceptor]
    B -->|headers| C[Auth-Schema: S2S]
    B -->|headers| D[Api-Key: XXXXXXX...]
```

## Persistenza Dati (Opzionale)

L'applicazione include un modulo opzionale per la persistenza dei dati utilizzando un database H2 in memoria:

### Schema del Database

```mermaid
erDiagram
    TRANSACTION {
        string transactionId
        string operationId
        date accountingDate
        date valueDate
        decimal amount
        string currency
        string description
        long accountId
    }
```



## Testing

L'applicazione include test unitari e di integrazione per garantire la qualità e la robustezza del codice:

### Strategia di Test

```mermaid
graph TD
    A[Unit Tests] -->|Test| B[Controller Layer]
    A -->|Test| C[Service Layer]
    A -->|Test| D[Repository Layer]
    A -->|Test| E[Client Layer]
    
    F[Integration Tests] -->|Test| G[API Endpoints]
    F -->|Test| H[Database Integration]
    
    I[Mock Tests] -->|Mock| J[External API]
```

Le categorie principali di test includono:

1. **Test Unitari**:
   - Test dei controller con MockMvc
   - Test dei servizi con Mockito
   - Test dei repository

2. **Test di Integrazione**:
   - Test end-to-end con database in memoria
   - Test di integrazione delle API

## Gestione degli Errori

L'applicazione implementa una gestione centralizzata degli errori utilizzando `@RestControllerAdvice` e gestori di eccezioni personalizzati:

```mermaid
graph LR
    C[FabrickApiException] -->|handled by| B[GlobalExceptionHandler]
    E[ValidationException] -->|handled by| B[GlobalExceptionHandler]
    B -->|returns| F[ErrorResponse]
```

### Tipi di Errori Gestiti

1. **Errori API esterne**: Gestisce gli errori restituiti dalle API Fabrick
2. **Errori di validazione**: Gestisce gli errori di validazione dei dati in input
3. **Errori generici**: Gestisce qualsiasi altro tipo di errore imprevisto

## Configurazione

Le principali configurazioni dell'applicazione sono gestite tramite:
######  - `application.yml`:

```properties
spring:
  application:
    name: orbyta-quiz
  profiles:
    active: sandbox
```
Questo garantisce che l'applicazione utilizzi il file application-sandbox.yml per le configurazioni specifiche dell'ambiente.

######  - `application-sandbox.yml`:
```properties
spring:
  datasource:
    url: jdbc:h2:mem:fabrickdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
  h2:
    console:
      enabled: true
      path: /h2-console
logging:
  level:
    com.orbyta_admission_quiz: DEBUG
    org:
      springframework:
        web:
          client:
            RestTemplate: DEBUG
fabrick:
  api:
    baseUrl: https://sandbox.platfr.io
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
    tagsSorter: alpha
    expansion: none
server:
  port: 8081
```
## Logging
L'applicazione utilizza un sistema di logging avanzato basato su Spring AOP e SLF4j con MDC (Mapped Diagnostic Context) per tracciare le chiamate ai metodi e le richieste HTTP.

### Componenti di Logging
#### 1- MDCFilter: 

Un filtro servlet che intercetta ogni richiesta HTTP in entrata. Genera un requestId univoco (un UUID troncato) e lo aggiunge al contesto MDC di SLF4j. Questo ID viene incluso in tutti i log generati durante l'elaborazione di quella specifica richiesta, facilitando il tracciamento end-to-end. L'ID viene rimosso dall'MDC al termine della richiesta.


#### 2- LoggingAspect: 

Un aspect AOP che intercetta le chiamate ai metodi nei layer Controller, Service, Repository, Client e alle chiamate RestTemplate.
  - Utilizza LoggingUtils per gestire la profondità della chiamata (indentazione nei log tramite > e <) e per serializzare argomenti e risultati in formato JSON.
  - Logga l'ingresso nel metodo (classe, nome metodo, argomenti JSON).
  - Logga l'uscita dal metodo (classe, nome metodo, tempo di esecuzione, risultato JSON).
  - Logga eventuali eccezioni sollevate, includendo il messaggio, il tempo di esecuzione e gli argomenti originali.

### Formato dei Log
Il pattern di logging definito in application.yml (%d{yyyy-MM-dd HH:mm:ss} [%X{requestId}] %-5level - %msg%n) assicura che ogni riga di log includa:
- Timestamp
- requestId (preso dall'MDC)
- Livello di log (INFO, ERROR, etc.)
- Messaggio di log (generato dall'LoggingAspect)

### Esempio di Log
Questo esempio mostra come i log tracciano una richiesta per ottenere il saldo di un conto, includendo il requestId (fc2f5fbf), l'indentazione basata sulla profondità della chiamata, i tempi di esecuzione e i dati serializzati in JSON.
```plaintext
2025-04-11 00:26:09 [fc2f5fbf] INFO  - > AccountController.getAccountBalance() argsJson=[14537780]
2025-04-11 00:26:09 [fc2f5fbf] INFO  - >> AccountServiceImpl.getAccountBalance() argsJson=[14537780]
2025-04-11 00:26:09 [fc2f5fbf] INFO  - >>> FabrickClientImpl.getAccountBalance() argsJson=[14537780]
2025-04-11 00:26:09 [fc2f5fbf] INFO  - >>>> RestTemplate.exchange() argsJson=["/api/gbs/banking/v4.0/accounts/{accountId}/balance", "GET", {"headers":{"Content-Type":["application/json"],"Api-Key":["FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP"],"Auth-Schema":["S2S"],"X-Time-Zone":["Europe/Rome"]},"body":null}, "ParameterizedTypeReference<com.orbyta_admission_quiz.dto.FabrickResponse<com.orbyta_admission_quiz.dto.account.response.AccountBalanceResponse>>", {"accountId":14537780}]
2025-04-11 00:26:10 [fc2f5fbf] INFO  - <<<< RestTemplate.exchange() time=795ms resultJson={"headers":{"content-type":["application/json"],"date":["Thu, 10 Apr 2025 22:26:10 GMT"],"max-forwards":["19"],"server":[""],"strict-transport-security":["max-age=31536000; includeSubDomains"],"transfer-encoding":["chunked"],"via":["1.1 fbkprsndgtm01 (), 1.1 fbkprsndgtm01 ()"],"x-content-type-options":["nosniff"],"x-correlationid":["Id-8245f867b523685beb4ead84 0; Id-8245f86775a47043c4351d19 0"],"x-frame-options":["SAMEORIGIN"],"x-time-zone":["Europe/Rome"],"x-xss-protection":["1; mode=block"]},"body":{"status":"OK","error":[],"payload":{"balance":-3.96,"availableBalance":-3.96,"currency":"EUR","date":"2025-04-11"},"success":true},"statusCode":"OK","statusCodeValue":200}
2025-04-11 00:26:10 [fc2f5fbf] INFO  - <<< FabrickClientImpl.getAccountBalance() time=810ms resultJson={"balance":-3.96,"availableBalance":-3.96,"currency":"EUR","date":"2025-04-11"}
2025-04-11 00:26:10 [fc2f5fbf] INFO  - << AccountServiceImpl.getAccountBalance() time=810ms resultJson={"balance":-3.96,"availableBalance":-3.96,"currency":"EUR","date":"2025-04-11"}
2025-04-11 00:26:10 [fc2f5fbf] INFO  - < AccountController.getAccountBalance() time=817ms resultJson={"balance":-3.96,"availableBalance":-3.96,"currency":"EUR","date":"2025-04-11"}
```

## Come Eseguire

### Prerequisiti
- Java 17
- Maven

### Comandi

1. **Clonare il repository**:
   ```bash
   git clone https://github.com/SoyHub/OrbyaQuiz?tab=readme-ov-file#integrazione-con-fabrick-api
   ```

2. **Compilare e creare il pacchetto**:
   ```bash
   mvn clean package
   ```

3. **Eseguire l'applicazione**:
   ```bash
   java -jar target/orbyta-quiz-0.0.1-SNAPSHOT.jar
   ```

4. **Eseguire i test**:
   ```bash
   mvn test
   ```
   
## Postman Collection
La collezione Postman per testare le API è disponibile nel file `resources/postman/ORBYTA-QUIZ.postman_collection.json`. 
Si puo importare questo file in Postman per testare le API direttamente.
Si ricorda di importare anche l'ambiente Postman: 
- `Orbyta-Quiz-Local.postman_environment.json` (per l'esecuzione locale)
- `Orbyta-Quiz-Sandbox.postman_environment.json` (per l'esecuzione in sandbox)

