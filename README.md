# Documentazione API Fabrick

## Indice
1. [Panoramica del Progetto](#panoramica-del-progetto)
2. [Architettura](#architettura)
3. [Componenti Principali](#componenti-principali)
4. [API REST Esposte](#api-rest-esposte)
5. [Integrazione con Fabrick API](#integrazione-con-fabrick-api)

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
        "name": "John Doe",
        "account": {
            "accountCode": "IT23A0336844430152923804660",
            "bicCode": "SELBIT2BXXX"
        },
        "address": {
            "address": null,
            "city": null,
            "countryCode": null
        }
    },
    "executionDate": "2025-05-01",
    "uri": "REMITTANCE_INFORMATION",
    "description": "Payment invoice 75/2017",
    "amount": 800,
    "currency": "EUR",
    "isUrgent": false,
    "isInstant": false,
    "feeType": "SHA",
    "feeAccountId": "14537780",
    "taxRelief": {
        "taxReliefId": "L449",
        "isCondoUpgrade": false,
        "creditorFiscalCode": "56258745832",
        "beneficiaryType": "NATURAL_PERSON",
        "naturalPersonBeneficiary": {
            "fiscalCode1": "MRLFNC81L04A859L"
        },
        "legalPersonBeneficiary": {
            "fiscalCode": null,
            "legalRepresentativeFiscalCode": null
        }
    }
}
```

**Risposta attuale** (nell'ambiente sandbox):
```json
{
    "method": "createMoneyTransfer",
    "step": "Fabrick API",
    "rawErrorMessage": "400 Bad Request on POST request for \"https://sandbox.platfr.io/api/gbs/banking/v4.0/accounts/14537780/payments/money-transfers\": \"{<EOL><EOL>  \"status\" : \"KO\",<EOL><EOL>  \"errors\" :  [<EOL><EOL>??{<EOL><EOL>???\"code\" : \"API000\",<EOL><EOL>???\"description\" : \"IbanBeneficiario è obbligatorio\",<EOL><EOL>???\"params\" : \"\"<EOL><EOL>??}<EOL><EOL>?],<EOL><EOL>  \"payload\": {}<EOL><EOL>}\"",
    "timestamp": "2025-04-09T01:22:42.554071",
    "errorDetails": {
        "status": "KO",
        "errors": [
            {
                "code": "API000",
                "description": "IbanBeneficiario è obbligatorio",
                "params": ""
            }
        ],
        "payload": {}
    },
    "status": 400
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
    A[FabrickApiClient] -->|config| B[RestTemplate con interceptor]
    B -->|headers| C[Auth-Schema: S2S]
    B -->|headers| D[Api-Key: FXOVVXXH...]
```
