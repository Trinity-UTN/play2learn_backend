# Reporte de pruebas — módulo economy

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 54.15 %
- Ramas: 47.71 %
- Líneas: 54.03 %
- Complejidad: 44.96 %
- Métodos: 48.87 %

### Módulo `economy`
- Instrucciones: 99.56 %
- Ramas: 90.91 %
- Líneas: 99.51 %
- Complejidad: 89.83 %
- Métodos: 100.0 %

## Escenarios (services/controllers/dtos)
Feature: moveToReserve
  Scenario: moveToReserve
  Scenario: Given reserve with sufficient circulation balance and amount When moving to reserve Then transfers amount and persists reserve
  Scenario: Given reserve and zero amount When moving to reserve Then throws BadRequestException
  Scenario: Given reserve and negative amount When moving to reserve Then throws BadRequestException
  Scenario: Given reserve with insufficient circulation balance When moving to reserve Then throws BadRequestException
  Scenario: Given reserve with circulation balance equal to amount When moving to reserve Then transfers amount successfully
  Scenario: moveToCirculation
  Scenario: Given reserve with sufficient reserve balance and amount When moving to circulation Then transfers amount and persists reserve
  Scenario: Given reserve and zero amount When moving to circulation Then throws BadRequestException
  Scenario: Given reserve and negative amount When moving to circulation Then throws BadRequestException
  Scenario: Given reserve with insufficient reserve balance When moving to circulation Then adjusts reserve and transfers amount
  Scenario: Given reserve with reserve balance equal to amount When moving to circulation Then transfers amount successfully

Feature: get
  Scenario: get
  Scenario: Given existing reserve When getting last reserve Then returns reserve
  Scenario: Given no existing reserve When getting last reserve Then throws NotFoundException
  Scenario: Given reserve with specific balances When getting last reserve Then returns reserve with correct balances

Feature: GET /transactions
  Scenario: GET /transactions
  Scenario: Given authenticated dev When getting statistics Then returns 200 OK with list of statistics
  Scenario: Given authenticated dev with multiple statistics When getting statistics Then returns 200 OK with multiple statistics
  Scenario: Given authenticated dev with no statistics When getting statistics Then returns 200 OK with empty list

Feature: TransactionResponseDto construction
  Scenario: TransactionResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero amount Then creates DTO with zero
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: TransactionResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: TransactionStatisticsResponseDto construction
  Scenario: TransactionStatisticsResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: TransactionStatisticsResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: generate
  Scenario: generate
  Scenario: Given valid type transaction and positive amount When generating Then delegates to strategy and returns transaction
  Scenario: Given zero amount When generating Then throws ConflictException
  Scenario: Given negative amount When generating Then throws ConflictException
  Scenario: Given unsupported transaction type When generating Then throws ConflictException
  Scenario: Given valid type and minimum amount (0.01) When generating Then delegates to strategy successfully

Feature: execute
  Scenario: execute
  Scenario: Given empty transaction list When calculating statistics Then returns empty list
  Scenario: Given transaction list with monthly assignment When calculating statistics Then calculates totals correctly
  Scenario: Given transaction list with activity When calculating statistics Then calculates totals correctly
  Scenario: Given transaction list with reward When calculating statistics Then calculates totals correctly
  Scenario: Given transaction list with purchase When calculating statistics Then calculates totals correctly
  Scenario: Given transaction list with refund When calculating statistics Then calculates totals correctly
  Scenario: Given transaction list with multiple types When calculating statistics Then calculates cumulative totals correctly
  Scenario: Given transaction with null amount When calculating statistics Then treats as zero

Feature: execute
  Scenario: execute
  Scenario: Given wallet with transactions When getting last transactions Then returns list of transactions ordered by date descending
  Scenario: Given wallet with no transactions When getting last transactions Then returns empty list
  Scenario: Given wallet with more than 10 transactions When getting last transactions Then returns only top 10
  Scenario: Given wallet with exactly 10 transactions When getting last transactions Then returns all 10 transactions

Feature: execute
  Scenario: execute
  Scenario: Given subject with sufficient balance and amount within 30% limit When executing actividad transaction Then creates transaction, removes from subject and adds to activity
  Scenario: Given amount exceeding 30% of initial balance When executing actividad transaction Then throws ConflictException
  Scenario: Given amount exceeding subject actual balance When executing actividad transaction Then throws ConflictException

Feature: execute
  Scenario: execute
  Scenario: Given subject with correct assign amount When executing asignacion transaction Then creates transaction, adds balance to subject and moves to circulation
  Scenario: Given amount not equal to assign amount When executing asignacion transaction Then throws UnsupportedOperationException
  Scenario: Given reserve with insufficient balance When executing asignacion transaction Then adjusts reserve and creates transaction

Feature: execute
  Scenario: execute
  Scenario: Given wallet with sufficient balance When executing compra transaction Then creates transaction, removes amount from wallet and moves to reserve
  Scenario: Given wallet with insufficient balance When executing compra transaction Then throws IllegalArgumentException
  Scenario: Given wallet with balance equal to amount When executing compra transaction Then creates transaction successfully

Feature: execute
  Scenario: execute
  Scenario: Given wallet with sufficient balance and positive amount When executing deposit transaction Then creates transaction, removes from wallet and moves to reserve
  Scenario: Given zero amount When executing deposit transaction Then throws BadRequestException
  Scenario: Given wallet with insufficient balance When executing deposit transaction Then throws BadRequestException

Feature: execute
  Scenario: execute
  Scenario: Given fixed term deposit with IN_PROGRESS state and wallet with sufficient balance When executing transaction Then creates transaction, removes from wallet and moves to reserve
  Scenario: Given fixed term deposit with FINISHED state When executing transaction Then creates transaction, adds to wallet and moves to circulation
  Scenario: Given fixed term deposit with IN_PROGRESS state and wallet with insufficient balance When executing transaction Then throws BadRequestException

Feature: execute
  Scenario: execute
  Scenario: Given any parameters When executing inversion transaction Then throws UnsupportedOperationException

Feature: execute
  Scenario: execute
  Scenario: Given activity with sufficient balance When executing recompensa transaction Then creates transaction, removes amount from activity and adds to wallet
  Scenario: Given activity with insufficient balance When executing recompensa transaction Then throws ConflictException
  Scenario: Given activity with balance equal to amount When executing recompensa transaction Then creates transaction successfully

Feature: execute
  Scenario: execute
  Scenario: Given wallet and benefit When executing reembolso transaction Then creates transaction, adds amount to wallet and moves to circulation

Feature: execute
  Scenario: execute
  Scenario: Given order with COMPRA type and wallet with sufficient balance When executing stock transaction Then creates transaction, removes from wallet and moves to reserve
  Scenario: Given order with VENTA type When executing stock transaction Then creates transaction, adds to wallet and moves to circulation
  Scenario: Given order with COMPRA type and wallet with insufficient balance When executing stock transaction Then throws BadRequestException

Feature: execute
  Scenario: execute
  Scenario: Given saving account with sufficient balance and positive amount When executing withdrawal transaction Then creates transaction, adds to wallet and moves to circulation
  Scenario: Given zero amount When executing withdrawal transaction Then throws BadRequestException
  Scenario: Given saving account with insufficient balance When executing withdrawal transaction Then throws BadRequestException

Feature: POST /wallet/test/{id}
  Scenario: POST /wallet/test/{id}
  Scenario: Given valid student ID When assigning amount Then returns 204 No Content
  Scenario: Given non-existing student ID When assigning amount Then returns 404 Not Found
  Scenario: Given valid student ID but non-existing subject When assigning amount Then returns 404 Not Found

Feature: GET /wallet
  Scenario: GET /wallet
  Scenario: Given authenticated student When getting wallet Then returns 200 OK with wallet complete response
  Scenario: Given authenticated student with empty transactions When getting wallet Then returns 200 OK with wallet without transactions

Feature: GET /wallet/last-transactions
  Scenario: GET /wallet/last-transactions
  Scenario: Given authenticated student When getting last transactions Then returns 200 OK with list of transactions
  Scenario: Given authenticated student with no transactions When getting last transactions Then returns 200 OK with empty list

Feature: WalletCompleteResponseDto construction
  Scenario: WalletCompleteResponseDto construction
  Scenario: When building with valid values and transactions Then creates DTO successfully
  Scenario: When building with empty transactions list Then creates DTO with empty list
  Scenario: When building with null transactions Then creates DTO with null list
  Scenario: WalletCompleteResponseDto serialization
  Scenario: When serializing to JSON with transactions Then produces valid JSON string
  Scenario: When serializing to JSON with empty transactions Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: WalletResponseDto construction
  Scenario: WalletResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: WalletResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: execute
  Scenario: execute
  Scenario: Given wallet and positive amount When adding amount Then increments balance and persists wallet
  Scenario: Given wallet and zero amount When adding amount Then throws IllegalArgumentException
  Scenario: Given wallet and negative amount When adding amount Then throws IllegalArgumentException
  Scenario: Given wallet and minimum amount (0.01) When adding amount Then increments balance successfully

Feature: cu65GetLastTransactions
  Scenario: cu65GetLastTransactions
  Scenario: Given user with wallet and transactions When getting last transactions Then returns list of transaction DTOs
  Scenario: Given user with wallet and no transactions When getting last transactions Then returns empty list
  Scenario: Given user with wallet and multiple transactions When getting last transactions Then returns list with all transaction DTOs

Feature: cu70GetWallet
  Scenario: cu70GetWallet
  Scenario: Given user with wallet and transactions When getting wallet Then returns complete wallet DTO
  Scenario: Given user with wallet and no transactions When getting wallet Then returns complete wallet DTO with empty transactions list
  Scenario: Given user with wallet and multiple transactions When getting wallet Then returns complete wallet DTO with all transactions

Feature: execute
  Scenario: execute
  Scenario: Given wallet with sufficient balance and amount When removing amount Then decrements balance and persists wallet
  Scenario: Given wallet with balance equal to amount When removing amount Then decrements balance to zero and persists wallet
  Scenario: Given wallet with insufficient balance When removing amount Then throws IllegalArgumentException and does not persist
  Scenario: Given wallet with balance less than amount When removing amount Then throws IllegalArgumentException

Feature: execute
  Scenario: execute
  Scenario: Given wallet When updating invested balance Then calculates total invested and updates wallet
  Scenario: Given wallet with zero invested When updating invested balance Then sets inverted balance to zero
  Scenario: Given wallet with investments When updating invested balance Then updates inverted balance correctly
