# Reporte de pruebas — módulo investment

## Cobertura de código (JaCoCo)

### Proyecto completo
- Instrucciones: 71.75 %
- Ramas: 61.46 %
- Líneas: 70.8 %
- Complejidad: 58.06 %
- Métodos: 62.61 %

### Módulo `investment`
- Instrucciones: 99.16 %
- Ramas: 88.07 %
- Líneas: 99.12 %
- Complejidad: 89.66 %
- Métodos: 100.0 %

## Escenarios (services/controllers/dtos)
Feature: GET /investment/fixed-term-deposit/paginated
  Scenario: GET /investment/fixed-term-deposit/paginated
  Scenario: Given authenticated student with fixed term deposits When listing paginated Then returns 200 OK with paginated data
  Scenario: Given authenticated student with no fixed term deposits When listing paginated Then returns 200 OK with empty paginated data

Feature: POST /investment/fixed-term-deposit
  Scenario: POST /investment/fixed-term-deposit
  Scenario: Given valid fixed term deposit request When registering Then returns 201 Created with fixed term deposit response
  Scenario: Given invalid fixed term deposit request When registering Then returns 400 Bad Request
  Scenario: Given insufficient balance When registering Then returns 409 Conflict

Feature: FixedTermDepositResponseDto construction
  Scenario: FixedTermDepositResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: FixedTermDepositResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: cu95fixedTermDepositAutomaticEnds
  Scenario: cu95fixedTermDepositAutomaticEnds
  Scenario: Given expired fixed term deposits When executing automatic ends Then finishes them and generates transactions
  Scenario: Given fixed term deposits not yet expired When executing automatic ends Then skips them
  Scenario: Given mixed expired and active deposits When executing automatic ends Then only finishes expired ones
  Scenario: Given empty list of fixed term deposits When executing automatic ends Then does nothing

Feature: cu99ListPaginatedFixedTermDeposits
  Scenario: cu99ListPaginatedFixedTermDeposits
  Scenario: Given valid pagination parameters When listing Then returns paginated fixed term deposits
  Scenario: Given filters for state and days When listing Then applies all filters correctly
  Scenario: Given empty results When listing Then returns empty paginated data

Feature: cu92registerFixedTermDeposit
  Scenario: cu92registerFixedTermDeposit
  Scenario: Given valid request and sufficient wallet balance When registering fixed term deposit Then creates deposit and generates transaction
  Scenario: Given wallet with insufficient balance When registering fixed term deposit Then throws UnsupportedOperationException
  Scenario: Given valid request with different fixed term days When registering fixed term deposit Then calculates correct end date

Feature: execute
  Scenario: execute
  Scenario: Given wallet with fixed term deposits in progress When calculating amount invested Then returns sum of reward amounts
  Scenario: Given wallet with no fixed term deposits in progress When calculating amount invested Then returns zero
  Scenario: Given fixed term deposits with different reward amounts When calculating amount invested Then returns correct sum

Feature: execute
  Scenario: execute
  Scenario: Given amount invested and monthly fixed term days When calculating reward amount Then returns correct interest
  Scenario: Given amount invested and weekly fixed term days When calculating reward amount Then returns correct interest
  Scenario: Given different amounts invested When calculating reward amount Then returns proportional interest

Feature: execute
  Scenario: execute
  Scenario: Given wallet and fixed term state When finding all by state and wallet Then returns list of deposits
  Scenario: Given wallet with no deposits in state When finding all by state and wallet Then returns empty list
  Scenario: Given different wallets When finding all by state and wallet Then returns only deposits for specified wallet

Feature: execute
  Scenario: execute
  Scenario: Given fixed term state When finding all by state Then returns list of deposits with that state
  Scenario: Given fixed term state with no deposits When finding all by state Then returns empty list
  Scenario: Given different fixed term states When finding all by state Then returns only deposits with specified state

Feature: cu110calculateTotalInvested
  Scenario: cu110calculateTotalInvested
  Scenario: Given wallet with investments When calculating total Then returns sum of all investments
  Scenario: Given wallet with zero investments When calculating total Then returns zero

Feature: DELETE /investment/saving-accounts/{id}
  Scenario: DELETE /investment/saving-accounts/{id}
  Scenario: Given valid saving account id When deleting Then returns 204 No Content
  Scenario: Given non-existing saving account id When deleting Then returns 404 Not Found
  Scenario: Given saving account with balance When deleting Then returns 400 Bad Request

Feature: POST /investment/saving-accounts/deposit
  Scenario: POST /investment/saving-accounts/deposit
  Scenario: Given valid deposit request When depositing Then returns 201 Created with updated saving account response
  Scenario: Given invalid deposit request When depositing Then returns 400 Bad Request
  Scenario: Given non-existing saving account When depositing Then returns 404 Not Found
  Scenario: Given insufficient wallet balance When depositing Then returns 400 Bad Request

Feature: GET /investment/saving-accounts/paginated
  Scenario: GET /investment/saving-accounts/paginated
  Scenario: Given authenticated student with saving accounts When listing paginated Then returns 200 OK with paginated data
  Scenario: Given authenticated student with no saving accounts When listing paginated Then returns 200 OK with empty paginated data

Feature: POST /investment/saving-accounts
  Scenario: POST /investment/saving-accounts
  Scenario: Given valid saving account request When registering Then returns 201 Created with saving account response
  Scenario: Given invalid saving account request When registering Then returns 400 Bad Request
  Scenario: Given duplicate saving account name When registering Then returns 409 Conflict

Feature: POST /investment/saving-accounts/withdrawal
  Scenario: POST /investment/saving-accounts/withdrawal
  Scenario: Given valid withdrawal request When withdrawing Then returns 201 Created with updated saving account response
  Scenario: Given invalid withdrawal request When withdrawing Then returns 400 Bad Request
  Scenario: Given non-existing saving account When withdrawing Then returns 404 Not Found
  Scenario: Given insufficient saving account balance When withdrawing Then returns 400 Bad Request

Feature: SavingAccountResponseDto construction
  Scenario: SavingAccountResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: SavingAccountResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: cu105deleteSavingAccount
  Scenario: cu105deleteSavingAccount
  Scenario: Given saving account with zero balance When deleting Then performs soft delete without transaction
  Scenario: Given saving account with positive balance When deleting Then transfers balance and performs soft delete
  Scenario: Given saving account not belonging to student When deleting Then throws ConflictException

Feature: cu103depositSavingAccount
  Scenario: cu103depositSavingAccount
  Scenario: Given valid request and sufficient wallet balance When depositing Then increments current amount and generates transaction
  Scenario: Given saving account not belonging to student When depositing Then throws ConflictException
  Scenario: Given wallet with insufficient balance When depositing Then throws BadRequestException

Feature: cu106listPaginatedSavingAccounts
  Scenario: cu106listPaginatedSavingAccounts
  Scenario: Given valid pagination parameters When listing Then returns paginated saving accounts
  Scenario: Given filters When listing Then applies all filters correctly

Feature: cu102registerSavingAccount
  Scenario: cu102registerSavingAccount
  Scenario: Given valid request and sufficient wallet balance When registering saving account Then creates account and generates transaction
  Scenario: Given wallet with insufficient balance When registering saving account Then throws BadRequestException
  Scenario: Given duplicate account name for wallet When registering saving account Then throws BadRequestException

Feature: cu107updateSavingAccounts
  Scenario: cu107updateSavingAccounts
  Scenario: Given saving accounts with outdated lastUpdate When updating Then calculates and applies daily interest
  Scenario: Given saving accounts with current lastUpdate When updating Then skips them
  Scenario: Given empty list of saving accounts When updating Then does nothing

Feature: cu104withdrawalSavingAccount
  Scenario: cu104withdrawalSavingAccount
  Scenario: Given valid request and sufficient account balance When withdrawing Then decrements current amount and generates transaction
  Scenario: Given saving account not belonging to student When withdrawing Then throws ConflictException
  Scenario: Given saving account with insufficient balance When withdrawing Then throws BadRequestException

Feature: execute
  Scenario: execute
  Scenario: Given wallet with saving accounts When calculating amount invested Then returns sum of current amounts
  Scenario: Given wallet with no saving accounts When calculating amount invested Then returns zero
  Scenario: Given saving accounts with different current amounts When calculating amount invested Then returns correct sum

Feature: execute
  Scenario: execute
  Scenario: Given existing saving account name and wallet When checking existence Then returns true
  Scenario: Given non-existing saving account name and wallet When checking existence Then returns false
  Scenario: Given same name but different wallet When checking existence Then returns false

Feature: execute
  Scenario: execute
  Scenario: Given valid saving account id When finding by id Then returns saving account
  Scenario: Given non-existing saving account id When finding by id Then throws NotFoundException
  Scenario: Given deleted saving account id When finding by id Then throws NotFoundException

Feature: POST /investment/stocks/buy
  Scenario: POST /investment/stocks/buy
  Scenario: Given valid stock buy request When buying Then returns 201 Created with buy response
  Scenario: Given invalid stock buy request When buying Then returns 400 Bad Request
  Scenario: Given non-existing stock When buying Then returns 404 Not Found
  Scenario: Given insufficient wallet balance When buying Then returns 400 Bad Request
  Scenario: Given insufficient stock availability When buying Then returns 400 Bad Request

Feature: GET /investment/stocks/candlestick
  Scenario: GET /investment/stocks/candlestick
  Scenario: Given valid stock id and range When getting candlestick values Then returns 200 OK with list of values
  Scenario: Given valid stock id with no candlestick values When getting values Then returns 200 OK with empty list

Feature: GET /investment/stocks/{id}
  Scenario: GET /investment/stocks/{id}
  Scenario: Given valid stock id When getting stock Then returns 200 OK with stock response
  Scenario: Given non-existing stock id When getting stock Then returns 404 Not Found

Feature: GET /investment/stocks/histories/{id}
  Scenario: GET /investment/stocks/histories/{id}
  Scenario: Given valid stock id with history When getting histories Then returns 200 OK with list of histories
  Scenario: Given valid stock id with no history When getting histories Then returns 200 OK with empty list
  Scenario: Given non-existing stock id When getting histories Then returns 404 Not Found

Feature: GET /investment/stocks
  Scenario: GET /investment/stocks
  Scenario: Given stocks exist When listing Then returns 200 OK with list of stocks
  Scenario: Given no stocks exist When listing Then returns 200 OK with empty list

Feature: GET /investment/stocks/paginated
  Scenario: GET /investment/stocks/paginated
  Scenario: Given stocks exist When listing paginated Then returns 200 OK with paginated data
  Scenario: Given no stocks exist When listing paginated Then returns 200 OK with empty paginated data

Feature: POST /investment/stocks
  Scenario: POST /investment/stocks
  Scenario: Given valid stock request When registering Then returns 201 Created with stock response
  Scenario: Given invalid stock request When registering Then returns 400 Bad Request
  Scenario: Given duplicate stock abbreviation When registering Then returns 409 Conflict

Feature: POST /investment/stocks/stop
  Scenario: POST /investment/stocks/stop
  Scenario: Given valid stop order request When registering stop Then returns 201 Created with buy response
  Scenario: Given invalid stop order request When registering stop Then returns 400 Bad Request
  Scenario: Given non-existing stock When registering stop Then returns 404 Not Found
  Scenario: Given insufficient stock quantity in wallet When registering stop Then returns 400 Bad Request

Feature: POST /investment/stocks/sell
  Scenario: POST /investment/stocks/sell
  Scenario: Given valid stock sell request When selling Then returns 201 Created with sell response
  Scenario: Given invalid stock sell request When selling Then returns 400 Bad Request
  Scenario: Given non-existing stock When selling Then returns 404 Not Found
  Scenario: Given insufficient stock quantity in wallet When selling Then returns 400 Bad Request

Feature: CandleStickChartValueResponseDto construction
  Scenario: CandleStickChartValueResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: CandleStickChartValueResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: StockBuyResponseDto construction
  Scenario: StockBuyResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: StockBuyResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: StockHistoryResponseDto construction
  Scenario: StockHistoryResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: StockHistoryResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: StockResponseDto construction
  Scenario: StockResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: StockResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: StockSellResponseDto construction
  Scenario: StockSellResponseDto construction
  Scenario: When building with valid values Then creates DTO successfully
  Scenario: When building with zero values Then creates DTO with zeros
  Scenario: When building with null values Then creates DTO with nulls
  Scenario: StockSellResponseDto serialization
  Scenario: When serializing to JSON Then produces valid JSON string
  Scenario: When deserializing from JSON Then creates DTO correctly

Feature: cu83GetValuesCandleStick
  Scenario: cu83GetValuesCandleStick
  Scenario: Given valid stock id and range value When getting candlestick values Then returns calculated values
  Scenario: Given HISTORICO range value When getting candlestick values Then returns all history

Feature: cu84buystocks
  Scenario: cu84buystocks
  Scenario: Given valid request with sufficient stock availability and wallet balance When buying Then creates order and generates transaction
  Scenario: Given insufficient stock availability When buying Then throws BadRequestException
  Scenario: Given insufficient wallet balance When buying Then throws BadRequestException

Feature: cu100GetStock
  Scenario: cu100GetStock
  Scenario: Given valid stock id and user When getting stock Then returns stock with quantity and pending orders
  Scenario: Given stock with no pending orders When getting stock Then returns stock with empty pending orders

Feature: cu79getStockHistories
  Scenario: cu79getStockHistories
  Scenario: Given valid stock id When getting history Then returns list of history ordered by date
  Scenario: Given stock with no history When getting history Then returns empty list

Feature: cu87ListPaginatedStock
  Scenario: cu87ListPaginatedStock
  Scenario: Given valid pagination parameters with search When listing Then returns paginated stocks
  Scenario: Given filters When listing Then applies all filters correctly

Feature: cu86listStocks
  Scenario: cu86listStocks
  Scenario: Given stocks exist When listing Then returns all stocks
  Scenario: Given no stocks exist When listing Then returns empty list

Feature: cu77registerStock
  Scenario: cu77registerStock
  Scenario: Given valid request When registering stock Then creates stock and initial history

Feature: cu91registerStopStock
  Scenario: cu91registerStopStock
  Scenario: Given valid request with sufficient stock quantity in wallet When registering stop Then creates pending order
  Scenario: Given insufficient stock quantity in wallet When registering stop Then throws BadRequestException

Feature: cu90sellStock
  Scenario: cu90sellStock
  Scenario: Given valid request with sufficient stock quantity in wallet When selling Then creates order and generates transaction
  Scenario: Given insufficient stock quantity in wallet When selling Then throws BadRequestException

Feature: cu78updateStock
  Scenario: cu78updateStock
  Scenario: Given multiple stocks When updating Then updates all stocks
  Scenario: Given empty list of stocks When updating Then does nothing

Feature: execute
  Scenario: execute
  Scenario: Given wallet and stock with pending orders When finding pending orders Then returns list of sell response DTOs
  Scenario: Given wallet and stock with no pending orders When finding pending orders Then returns empty list

Feature: execute
  Scenario: execute
  Scenario: Given stock with executable loss stop order When executing stop orders Then executes order and generates transaction
  Scenario: Given stock with non-executable loss stop order When executing stop orders Then skips order
  Scenario: Given stock with no pending stop orders When executing stop orders Then does nothing
  Scenario: Given stock with order having insufficient quantity When executing stop orders Then cancels order

Feature: execute
  Scenario: execute
  Scenario: Given wallet with stocks and quantities When calculating amount invested Then returns sum of stock prices times quantities
  Scenario: Given wallet with no stocks When calculating amount invested Then returns zero
  Scenario: Given wallet with stocks but zero quantities When calculating amount invested Then returns zero

Feature: execute
  Scenario: execute
  Scenario: Given wallet with executed buy orders When calculating quantity Then returns sum of bought quantities
  Scenario: Given wallet with executed sell orders When calculating quantity Then returns difference
  Scenario: Given wallet with no executed orders When calculating quantity Then returns zero

Feature: execute
  Scenario: execute
  Scenario: Given stock with bullish trend When calculating variation Then returns value within expected range
  Scenario: Given stock with bearish trend When calculating variation Then returns value within expected range
  Scenario: Given stock with different risk levels When calculating variation Then returns values within respective ranges

Feature: execute
  Scenario: execute
  Scenario: Given stocks exist When finding all Then returns all stocks
  Scenario: Given no stocks exist When finding all Then returns empty list

Feature: execute
  Scenario: execute
  Scenario: Given valid stock id When finding by id Then returns stock
  Scenario: Given non-existing stock id When finding by id Then throws NotFoundException

Feature: execute
  Scenario: execute
  Scenario: Given stock with positive variations sum When calculating trend Then returns true (bullish)
  Scenario: Given stock with negative variations sum When calculating trend Then returns false (bearish)
  Scenario: Given stock with zero variations sum When calculating trend Then returns false
  Scenario: Given stock with no history When calculating trend Then returns false

Feature: execute
  Scenario: execute
  Scenario: Given stock and date range When finding by stock and range Then returns list of history in range
  Scenario: Given stock and date range with no history When finding by stock and range Then returns empty list

Feature: execute
  Scenario: execute
  Scenario: Given stock with history When finding by stock Then returns list of history ordered by date ascending
  Scenario: Given stock with no history When finding by stock Then returns empty list

Feature: execute
  Scenario: execute
  Scenario: Given stock with history When finding last history Then returns most recent history
  Scenario: Given stock with no history When finding last history Then throws NotFoundException

Feature: toSold
  Scenario: toSold
  Scenario: Given stock with sufficient available amount When moving to sold Then decrements available and increments sold
  Scenario: Given stock with insufficient available amount When moving to sold Then throws BadRequestException
  Scenario: toAvailable
  Scenario: Given stock with sufficient sold amount When moving to available Then decrements sold and increments available
  Scenario: Given stock with insufficient sold amount When moving to available Then throws BadRequestException

Feature: execute
  Scenario: execute
  Scenario: Given stock with variation When updating specific Then updates price within limits and saves history
  Scenario: Given stock with variation exceeding upper limit When updating specific Then sets price to upper limit
  Scenario: Given stock with variation below lower limit When updating specific Then sets price to lower limit
