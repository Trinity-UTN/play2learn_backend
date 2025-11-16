# Auditoría de pruebas — Módulo Investment

## Resumen Ejecutivo
- **Alcance:** controllers, services (incluye commons) y DTOs del módulo `investment`, excluyendo models, repositories, mappers y specs según pauta.
- **Submódulos:** `fixedTermDeposit`, `investment`, `stock`, `savingAccount`.
- **Responsabilidades principales:** registro y gestión de inversiones (plazos fijos, cajas de ahorro, acciones), cálculo de intereses y variaciones de precios, ejecución automática de plazos fijos y actualización diaria de cajas de ahorro y acciones, gestión de órdenes de stop para acciones, obtención de historiales y gráficos de velas para análisis, cálculo de totales invertidos por tipo de inversión y exposición de DTOs específicos por contexto.
- **Validaciones clave:** validación de montos positivos, validación de balances suficientes (wallet, caja de ahorro, acciones disponibles), validación de disponibilidad de acciones para compra/venta, validación de propiedad de inversiones (cajas de ahorro pertenecen al estudiante), validación de nombres únicos para cajas de ahorro, validación de fechas de finalización de plazos fijos, validación de límites de precios de acciones (0 a 2.5x precio inicial), validación de estados de inversiones (plazo fijo: IN_PROGRESS, FINISHED) y resguardo de endpoints mediante `@SessionRequired` con roles específicos.

## Inventario de Controllers

| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `FixedTermDepositRegisterController` | `POST /investment/fixed-term-deposit` | `ROLE_STUDENT` | `IFixedTermDepositRegisterService` | `@Valid @RequestBody FixedTermDepositRegisterRequestDto`, `@SessionUser User`; registra nuevo plazo fijo del estudiante; retorna `FixedTermDepositResponseDto`. |
| `FixedTermDepositListPaginatedController` | `GET /investment/fixed-term-deposit/paginated` | `ROLE_STUDENT` | `IFixedTermDepositListPaginatedService` | Parámetros de paginación (`page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`), `@SessionUser User`; obtiene lista paginada de plazos fijos del estudiante; retorna `PaginatedData<FixedTermDepositResponseDto>`. |
| `SavingAccountRegisterController` | `POST /investment/saving-accounts` | `ROLE_STUDENT` | `ISavingAccountRegisterService` | `@Valid @RequestBody SavingAccountRegisterRequestDto`, `@SessionUser User`; registra nueva caja de ahorro del estudiante; retorna `SavingAccountResponseDto`. |
| `SavingAccountDepositController` | `POST /investment/saving-accounts/deposit` | `ROLE_STUDENT` | `ISavingAccountDepositService` | `@Valid @RequestBody SavingAccountDepositRequestDto`, `@SessionUser User`; deposita monto en caja de ahorro existente; retorna `SavingAccountResponseDto`. |
| `SavingAccountWithdrawalController` | `POST /investment/saving-accounts/withdrawal` | `ROLE_STUDENT` | `ISavingAccountWithdrawalService` | `@Valid @RequestBody SavingAccountWithdrawalRequestDto`, `@SessionUser User`; retira monto de caja de ahorro existente; retorna `SavingAccountResponseDto`. |
| `SavingAccountDeleteController` | `DELETE /investment/saving-accounts/{id}` | `ROLE_STUDENT` | `ISavingAccountDeleteService` | Path `id`, `@SessionUser User`; elimina (soft delete) caja de ahorro si tiene saldo cero o transfiere saldo restante; retorna 204 No Content. |
| `SavingAccountListPaginatedController` | `GET /investment/saving-accounts/paginated` | `ROLE_STUDENT` | `ISavingAccountListPaginatedService` | Parámetros de paginación (`page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`), `@SessionUser User`; obtiene lista paginada de cajas de ahorro del estudiante; retorna `PaginatedData<SavingAccountResponseDto>`. |
| `StockRegisterController` | `POST /investment/stocks` | `ROLE_DEV` | `IStockRegisterService` | `@Valid @RequestBody StockRegisterRequestDto`; registra nueva acción en el sistema; retorna `StockResponseDto`. |
| `StockBuyController` | `POST /investment/stocks/buy` | `ROLE_STUDENT` | `IStockBuyService` | `@Valid @RequestBody StockBuyRequestDto`, `@SessionUser User`; compra acciones disponibles; retorna `StockBuyResponseDto`. |
| `StockSellController` | `POST /investment/stocks/sell` | `ROLE_STUDENT` | `IStockSellService` | `@Valid @RequestBody StockBuyRequestDto` (reutiliza DTO de compra), `@SessionUser User`; vende acciones del wallet; retorna `StockSellResponseDto`. |
| `StockGetController` | `GET /investment/stocks/{id}` | `ROLE_STUDENT` | `IStockGetService` | Path `id`, `@SessionUser User`; obtiene detalle de acción con cantidad comprada por wallet y órdenes pendientes; retorna `StockResponseDto`. |
| `StockListController` | `GET /investment/stocks` | `ROLE_DEV`, `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_TEACHER` | `IStockListService` | Obtiene lista completa de acciones sin paginación; retorna `List<StockResponseDto>`. |
| `StockListPaginatedController` | `GET /investment/stocks/paginated` | `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`, `ROLE_DEV` | `IStockListPaginatedService` | Parámetros de paginación (`page`, `page_size`, `order_by`, `order_type`, `search`, `filters`, `filtersValues`); obtiene lista paginada de acciones; retorna `PaginatedData<StockResponseDto>`. |
| `StockHistoryListByStockController` | `GET /investment/stocks/histories/{id}` | `ROLE_DEV` | `IStockListHistoryService` | Path `id`; obtiene historial completo de variaciones de precio de una acción; retorna `List<StockHistoryResponseDto>`. |
| `StockCandleStickGetController` | `GET /investment/stocks/candlestick` | `ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN`, `ROLE_DEV` | `ICandleStickGetValuesService` | Query params `stockId` (default: 0), `rangeValue` (default: DIARIO); obtiene valores de gráfico de velas para análisis; retorna `List<CandleStickChartValueResponseDto>`. |
| `StockRegisterStopController` | `POST /investment/stocks/stop` | `ROLE_STUDENT` | `IStockRegisterStopService` | `@Valid @RequestBody StockOrderStopRequestDto`, `@SessionUser User`; registra orden de stop (venta automática al alcanzar precio); retorna `StockBuyResponseDto`. |

## Inventario de Services Principales

| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `FixedTermDepositRegisterService` | `cu92registerFixedTermDeposit` | `IStudentGetByEmailService`, `IFixedTermDepositCalculateInterestService`, `IFixedTermDepositRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | Valida que wallet tenga balance suficiente (`wallet.getBalance() >= amountInvested`); lanza `UnsupportedOperationException` si no hay suficiente dinero; calcula interés según días; crea plazo fijo con estado `IN_PROGRESS`; genera transacción `PLAZO_FIJO`; actualiza balance invertido; retorna `FixedTermDepositResponseDto`. |
| `FixedTermDepositListPaginatedService` | `cu99ListPaginatedFixedTermDeposits` | `IFixedTermDepositRepository`, `IStudentGetByEmailService` | Obtiene wallet del estudiante; construye especificaciones con filtros por estado y días; retorna `PaginatedData<FixedTermDepositResponseDto>` filtrado por wallet del estudiante. |
| `FixedTermDepositAutomaticEndsService` | `cu95fixedTermDepositAutomaticEnds` | `IFixedTermDepositFindAllByStateService`, `IFixedTermDepositRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | `@Scheduled(cron = "0 30 1 * * *")`; busca plazos fijos en estado `IN_PROGRESS`; para cada uno con `endDate <= LocalDate.now()`, cambia estado a `FINISHED`; genera transacción `PLAZO_FIJO` con retorno; actualiza balance invertido; método void. |
| `SavingAccountRegisterService` | `cu102registerSavingAccount` | `IStudentGetByEmailService`, `ISavingAccountExistsByNameAndWalletService`, `ISavingAccountRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | Valida que wallet tenga balance suficiente (`wallet.getBalance() >= initialAmount`); lanza `BadRequestException` si no hay suficiente dinero; valida que no exista caja de ahorro con mismo nombre para el wallet; lanza `BadRequestException` si ya existe; crea caja de ahorro; genera transacción `INGRESO_CAJA_AHORRO`; actualiza balance invertido; retorna `SavingAccountResponseDto`. |
| `SavingAccountDepositService` | `cu103depositSavingAccount` | `IStudentGetByEmailService`, `ISavingAccountFindByIdService`, `ISavingAccountRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | Valida que caja de ahorro pertenezca al wallet del estudiante; lanza `ConflictException` si no pertenece; valida que wallet tenga balance suficiente (`wallet.getBalance() >= amount`); lanza `BadRequestException` si no hay suficiente dinero; incrementa `currentAmount` de caja de ahorro; genera transacción `INGRESO_CAJA_AHORRO`; actualiza balance invertido; retorna `SavingAccountResponseDto`. |
| `SavingAccountWithdrawalService` | `cu104withdrawalSavingAccount` | `IStudentGetByEmailService`, `ISavingAccountFindByIdService`, `ISavingAccountRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | Valida que caja de ahorro pertenezca al wallet del estudiante; lanza `ConflictException` si no pertenece; valida que caja de ahorro tenga saldo suficiente (`savingAccount.getCurrentAmount() >= amount`); lanza `BadRequestException` si no hay suficiente dinero; genera transacción `RETIRO_CAJA_AHORRO`; decrementa `currentAmount` de caja de ahorro; actualiza balance invertido; retorna `SavingAccountResponseDto`. |
| `SavingAccountDeleteService` | `cu105deleteSavingAccount` | `IStudentGetByEmailService`, `ISavingAccountFindByIdService`, `ISavingAccountRepository`, `ITransactionGenerateService`, `IWalletUpdateInvestedBalanceService` | Valida que caja de ahorro pertenezca al wallet del estudiante; lanza `ConflictException` si no pertenece; si `currentAmount > 0`, genera transacción `RETIRO_CAJA_AHORRO` con monto completo; establece `currentAmount = 0.0`; establece `deletedAt = LocalDateTime.now()` (soft delete); actualiza balance invertido; método void. |
| `SavingAccountListPaginatedService` | `cu106listPaginatedSavingAccounts` | `ISavingAccountRepository`, `IStudentGetByEmailService` | Obtiene wallet del estudiante; construye especificaciones con filtros genéricos; retorna `PaginatedData<SavingAccountResponseDto>` filtrado por wallet y no eliminadas. |
| `SavingAccountUpdateService` | `cu107updateSavingAccounts` | `ISavingAccountRepository`, `IWalletUpdateInvestedBalanceService` | `@Scheduled(cron = "0 35 1 * * *")`; busca todas las cajas de ahorro no eliminadas; para cada una con `lastUpdate < LocalDate.now()`, calcula interés diario (0.1% del `currentAmount`); incrementa `accumulatedInterest` y `currentAmount`; actualiza `lastUpdate = LocalDate.now()`; actualiza balance invertido; método void. |
| `StockRegisterService` | `cu77registerStock` | `IStockRepository`, `IStockHistoryRepository` | Crea nueva acción con precio inicial; crea registro inicial en historial con variación 0.0; retorna `StockResponseDto`. |
| `StockBuyService` | `cu84buystocks` | `IStudentGetByEmailService`, `IStockFindByIdService`, `IOrderRepository`, `ITransactionGenerateService`, `IStockMoveService`, `IStockUpdateSpecificService`, `IWalletUpdateInvestedBalanceService` | Valida que stock tenga acciones disponibles suficientes (`stock.getAvailableAmount() >= quantity`); lanza `BadRequestException` si no hay suficientes acciones; valida que wallet tenga balance suficiente (`wallet.getBalance() >= currentPrice * quantity`); lanza `BadRequestException` si no hay suficiente dinero; crea orden tipo `COMPRA` estado `EJECUTADA`; genera transacción `STOCK`; mueve acciones de disponibles a vendidas; actualiza stock específico; actualiza balance invertido; retorna `StockBuyResponseDto`. |
| `StockSellService` | `cu90sellStock` | `IStockFindByIdService`, `IStudentGetByEmailService`, `IStockCalculateByWalletService`, `IOrderRepository`, `ITransactionGenerateService`, `IStockMoveService`, `IStockUpdateSpecificService`, `IWalletUpdateInvestedBalanceService` | Valida que wallet tenga suficientes acciones del stock (`stockCalculateByWalletService.execute(stock, wallet) >= quantity`); lanza `BadRequestException` si no hay suficientes acciones; crea orden tipo `VENTA` estado `EJECUTADA`; genera transacción `STOCK`; mueve acciones de vendidas a disponibles; actualiza stock específico; actualiza balance invertido; retorna `StockSellResponseDto`. |
| `StockGetService` | `cu100GetStock` | `IStockFindByIdService`, `IStudentGetByEmailService`, `IStockCalculateByWalletService`, `IOrderFindPendingService` | Obtiene stock por ID; calcula cantidad de acciones del wallet para el stock; obtiene órdenes pendientes del wallet; retorna `StockResponseDto` con cantidad comprada y órdenes pendientes. |
| `StockListService` | `cu86listStocks` | `IStockRepository` | Obtiene todas las acciones del sistema; retorna `List<StockResponseDto>`. |
| `StockListPaginatedService` | `cu87ListPaginatedStock` | `IStockRepository` | Construye especificaciones con búsqueda por nombre y filtros genéricos; retorna `PaginatedData<StockResponseDto>` paginado. |
| `StockListHistoryService` | `cu79getStockHistories` | `IStockFindByIdService`, `IStockHistoryFindByStockService` | Obtiene stock por ID; obtiene historial completo de stock; retorna `List<StockHistoryResponseDto>`. |
| `StockRegisterStopService` | `cu91registerStopStock` | `IStockFindByIdService`, `IStudentGetByEmailService`, `IStockCalculateByWalletService`, `IOrderRepository` | Valida que wallet tenga suficientes acciones del stock (`stockCalculateByWalletService.execute(stock, wallet) >= quantity`); lanza `BadRequestException` si no hay suficientes acciones; crea orden tipo `VENTA` estado `PENDIENTE` con precio por unidad y tipo de stop (LOSS/PROFIT); retorna `StockBuyResponseDto`. |
| `StockUpdateService` | `cu78updateStock` | `IStockFindAllService`, `IStockUpdateSpecificService` | `@Scheduled(cron = "0 0 1 * * *")`; recorre todas las acciones; para cada una, actualiza precio según variación calculada; método void. |
| `CandleStickGetValuesService` | `cu83GetValuesCandleStick` | `IStockFindByIdService`, `IStockHistoryFindByStockService`, `IStockHistoryFindByStockAndRangeService` | Obtiene stock por ID; calcula rango de fechas según `RangeValue` (si es `HISTORICO`, retorna null y obtiene todo el historial); obtiene historial por rango o completo; genera valores de gráfico de velas (open, close, high, low) calculados a partir del historial; retorna `List<CandleStickChartValueResponseDto>`. |
| `InvestemtCalculateTotalInvestedService` | `cu110calculateTotalInvested` | `IStockCalculateAmountInvestedService`, `IFixedTermDepositCalculateAmountInvestedService`, `SavingAccountCalculateAmountInvestedService` | Suma totales invertidos en acciones, plazos fijos y cajas de ahorro; retorna `Double` con total invertido. |

## Inventario de Services Commons

| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `FixedTermDepositCalculateAmountInvestedService` | Calcular monto invertido en plazos fijos | `IFixedTermDepositRepository` | Método `execute(Wallet wallet)`; **Nota:** actualmente no filtra por wallet, suma todos los plazos fijos en estado `IN_PROGRESS`; retorna `Double` con total (suma de `amountReward`). |
| `FixedTermDepositCalculateRewardAmountService` | Calcular monto de interés según días | - | Método `execute(Double amountInvested, FixedTermDays fixedTermDays)`; fórmula: `amountInvested * 1.5 * (fixedTermDays.getValor() / 365.0)`; retorna `Double` con interés calculado. |
| `FixedTermDepositFindAllByStateAndWalletService` | Buscar plazos fijos por estado y wallet | `IFixedTermDepositRepository` | Método `execute(FixedTermState fixedTermState, Wallet wallet)`; retorna `List<FixedTermDeposit>`. |
| `FixedTermDepositFindAllByStateService` | Buscar plazos fijos por estado | `IFixedTermDepositRepository` | Método `execute(FixedTermState fixedTermState)`; retorna `List<FixedTermDeposit>`. |
| `SavingAccountCalculateAmountInvestedService` | Calcular monto invertido en cajas de ahorro | `ISavingAccountRepository` | Método `execute(Wallet wallet)`; suma `currentAmount` de todas las cajas de ahorro del wallet no eliminadas; retorna `Double` con total. |
| `SavingAccountExistsByNameAndWalletService` | Verificar existencia de caja de ahorro por nombre y wallet | `ISavingAccountRepository` | Método `execute(String name, Wallet wallet)`; retorna `boolean` indicando si existe caja de ahorro con nombre y wallet, no eliminada. |
| `SavingAccountFindByIdService` | Buscar caja de ahorro por ID | `ISavingAccountRepository` | Método `execute(Long id)`; busca caja de ahorro no eliminada; lanza `NotFoundException` si no existe; retorna `SavingAccount`. |
| `OrderFindPendingService` | Buscar órdenes pendientes de stop | `IOrderRepository` | Método `execute(Stock stock, Wallet wallet)`; busca órdenes de venta en estado `PENDIENTE` del wallet y stock; retorna `List<StockSellResponseDto>`. |
| `OrderStopExecuteService` | Ejecutar órdenes de stop cuando se cumplan condiciones | `IOrderRepository`, `IStockCalculateByWalletService`, `IStockHistoryFindLastService`, `ITransactionGenerateService`, `IStockMoveService`, `IWalletUpdateInvestedBalanceService` | Método `execute(Stock stock)`; busca órdenes pendientes del stock; valida condiciones de stop (LOSS: precio <= precio orden, PROFIT: precio >= precio orden); si no se cumplen, continúa; valida que wallet tenga suficientes acciones; si no, cancela orden; si se cumplen, ejecuta orden (cambia estado a `EJECUTADA`, genera transacción, mueve acciones, actualiza balance); método void. |
| `StockCalculateAmountInvestedService` | Calcular monto invertido en acciones | `IStockRepository`, `IStockCalculateByWalletService` | Método `execute(Wallet wallet)`; recorre todas las acciones; suma `currentPrice * cantidadEnWallet`; retorna `Double` con total. |
| `StockCalculateByWalletService` | Calcular cantidad de acciones de un stock en wallet | `IOrderRepository` | Método `execute(Stock stock, Wallet wallet)`; suma órdenes de compra y resta órdenes de venta ejecutadas del wallet; retorna `BigInteger` con cantidad neta. |
| `StockCalculateVariationService` | Calcular variación de precio de stock | `IStockHistoryFindLastService`, `IStockHistoryCalculateTrendService` | Método `execute(Stock stock)`; obtiene último historial; calcula sesgo según cambio de acciones vendidas; calcula rango de variación basado en nivel de riesgo y sesgo; ajusta rango según tendencia (alcista/bajista); retorna `Double` con variación aleatoria dentro del rango. |
| `StockFindAllService` | Buscar todas las acciones | `IStockRepository` | Método `execute()`; retorna `Iterable<Stock>`. |
| `StockFindByIdService` | Buscar acción por ID | `IStockRepository` | Método `execute(Long stockId)`; lanza `NotFoundException` si no existe; retorna `Stock`. |
| `StockHistoryCalculateTrendService` | Calcular tendencia de stock (alcista/bajista) | `IStockHistoryRepository` | Método `execute(Stock stock)`; obtiene últimas 10 variaciones ordenadas por fecha ascendente; suma variaciones; si suma > 0, tendencia alcista; retorna `boolean`. |
| `StockHistoryFindByStockAndRangeService` | Buscar historial de stock por rango de fechas | `IStockHistoryRepository` | Método `execute(Stock stock, LocalDateTime startRange, LocalDateTime endRange)`; retorna `List<StockHistory>` ordenado por fecha ascendente. |
| `StockHistoryFindByStockService` | Buscar historial completo de stock | `IStockHistoryRepository` | Método `execute(Stock stock)`; retorna `List<StockHistory>` ordenado por fecha ascendente. |
| `StockHistoryFindLastService` | Buscar último registro de historial de stock | `IStockHistoryRepository` | Método `execute(Stock stock)`; busca último historial por fecha descendente; lanza `NotFoundException` si no existe; retorna `StockHistory`. |
| `StockMoveService` | Mover acciones entre disponibles y vendidas | `IStockRepository` | Métodos `toSold(Stock stock, BigInteger quantity)` y `toAvailable(Stock stock, BigInteger quantity)`; `toSold`: valida que haya acciones disponibles suficientes; lanza `BadRequestException` si no hay; mueve de `availableAmount` a `soldAmount`. `toAvailable`: valida que haya acciones vendidas suficientes; lanza `BadRequestException` si no hay; mueve de `soldAmount` a `availableAmount`. |
| `StockUpdateSpecificService` | Actualizar precio específico de stock | `IStockRepository`, `IStockHistoryRepository`, `IStockCalculateVariationService`, `IOrderStopExecuteService` | Método `execute(Stock stock)`; calcula variación; actualiza precio dentro de límites (mínimo 10.0, máximo 2.5x precio inicial); guarda stock actualizado; crea registro en historial con variación; ejecuta órdenes de stop; método void. |

## DTOs y Validaciones

| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| **Request DTOs** |
| `FixedTermDepositRegisterRequestDto` | `amountInvested` (Double), `fixedTermDays` (FixedTermDays) | `@NotNull` en ambos campos. |
| `SavingAccountRegisterRequestDto` | `initialAmount` (Double), `name` (String) | `@Positive`, `@NotNull(message = "El campo monto inicial no puede estar vacío")` en `initialAmount`; `@NotBlank(message = "El campo nombre no puede estar vacío")` en `name`. |
| `SavingAccountDepositRequestDto` | `id` (Long), `amount` (Double) | `@NotNull`, `@Positive` en ambos campos. |
| `SavingAccountWithdrawalRequestDto` | `id` (Long), `amount` (Double) | `@NotNull`, `@Positive` en ambos campos. |
| `StockRegisterRequestDto` | `name` (String), `abbreviation` (String), `initialPrice` (Double), `totalAmount` (BigInteger), `riskLevel` (RiskLevel) | `@NotBlank(message = "El campo nombre no puede estar vacío")`, `@Size(min = 1, max = 100, message = "La longitud máxima es de 100")` en `name`; `@NotBlank(message = "El campo abreviatura no puede estar vacío")`, `@Size(min = 1, max = 10, message = "La longitud máxima es de 10")` en `abbreviation`; `@Positive`, `@NotNull(message = "El campo precio inicial no puede estar vacío")` en `initialPrice`; `@Positive`, `@NotNull(message = "El campo cantidad total de acciones no puede estar vacío")` en `totalAmount`; `@NotNull(message = "El campo nivel de riesgo no puede estar vacío")` en `riskLevel`. |
| `StockBuyRequestDto` | `stockId` (Long), `quantity` (BigInteger) | `@NotNull` en ambos campos; `@Positive` en `quantity`. |
| `StockOrderStopRequestDto` | `stockId` (Long), `quantity` (BigInteger), `pricePerUnit` (Double), `orderStop` (OrderStop) | `@NotNull` en todos los campos; `@Positive` en `quantity` y `pricePerUnit`. |
| **Response DTOs** |
| `FixedTermDepositResponseDto` | `id`, `amountInvested`, `amountReward`, `fixedTermDays`, `startDate`, `endDate`, `fixedTermState` | Sin Bean Validation; usado como salida. |
| `SavingAccountResponseDto` | `id`, `initialAmount`, `currentAmount`, `accumulatedInterest`, `startDate`, `lastUpdate`, `name` | Sin Bean Validation; usado como salida. |
| `StockResponseDto` | `id`, `name`, `abbreviation`, `totalAmount`, `availableAmount`, `soldAmount`, `currentPrice`, `initialPrice`, `riskLevel`, `quantityBought`, `pendingOrders` | Sin Bean Validation; usado como salida; incluye lista de `StockSellResponseDto` con órdenes pendientes. |
| `StockBuyResponseDto` | `id`, `pricePerUnit`, `quantity`, `total`, `createdAt` | Sin Bean Validation; usado como salida. |
| `StockSellResponseDto` | `quantity`, `pricePerUnit`, `total` | Sin Bean Validation; usado como salida. |
| `StockHistoryResponseDto` | `id`, `price`, `soldAmount`, `availableAmount`, `timestamp`, `variation`, `createdAt` | Sin Bean Validation; usado como salida. |
| `CandleStickChartValueResponseDto` | `date`, `open`, `close`, `high`, `low` | Sin Bean Validation; usado como salida para gráficos. |

## Dependencias Externas Principales

### Módulo Admin
- `IStudentGetByEmailService`: Obtener estudiante por email

### Módulo Economy
- `ITransactionGenerateService`: Generar transacciones (tipo `PLAZO_FIJO`, `INGRESO_CAJA_AHORRO`, `RETIRO_CAJA_AHORRO`, `STOCK`)
- `IWalletUpdateInvestedBalanceService`: Actualizar balance invertido del wallet

### Models de otros módulos
- `Wallet`: Modelo de wallet del estudiante
- `User`: Modelo de usuario autenticado

### Utils y Config
- `PaginatorUtils`: Construcción de `Pageable` para paginación
- `PaginationHelper`: Conversión de `Page` a `PaginatedData`
- `ResponseFactory`: Factory para crear respuestas HTTP estandarizadas
- `SuccessfulMessages`: Mensajes de éxito estandarizados

## Excepciones Identificadas

| Excepción | Contexto | Mensaje |
|-----------|----------|---------|
| `BadRequestException` | `FixedTermDepositRegisterService`: wallet sin balance suficiente | "No hay saldo suficiente en la wallet" |
| `UnsupportedOperationException` | `FixedTermDepositRegisterService`: wallet sin balance suficiente | "No hay saldo suficiente en la wallet" |
| `BadRequestException` | `SavingAccountRegisterService`: wallet sin balance suficiente | "No hay saldo suficiente en la wallet" |
| `BadRequestException` | `SavingAccountRegisterService`: nombre duplicado | "Ya existe una cuenta de ahorro con ese nombre" |
| `ConflictException` | `SavingAccountDepositService`: cuenta no pertenece al estudiante | "La cuenta de ahorro no pertenece al estudiante" |
| `BadRequestException` | `SavingAccountDepositService`: wallet sin balance suficiente | "No hay saldo suficiente en la wallet" |
| `ConflictException` | `SavingAccountWithdrawalService`: cuenta no pertenece al estudiante | "La cuenta de ahorro no pertenece al estudiante" |
| `BadRequestException` | `SavingAccountWithdrawalService`: cuenta sin saldo suficiente | "No hay saldo suficiente en la caja de ahorro" |
| `ConflictException` | `SavingAccountDeleteService`: cuenta no pertenece al estudiante | "La cuenta de ahorro no pertenece al estudiante" |
| `BadRequestException` | `StockBuyService`: acciones no disponibles suficientes | "No hay acciones disponibles suficientes para comprar." |
| `BadRequestException` | `StockBuyService`: wallet sin balance suficiente | "No hay saldo suficiente en el wallet para realizar la compra." |
| `BadRequestException` | `StockSellService`: wallet sin acciones suficientes | "El wallet no cuenta con las acciones suficientes para realizar la venta" |
| `BadRequestException` | `StockRegisterStopService`: wallet sin acciones suficientes | "El wallet no cuenta con las acciones suficientes para realizar el stop." |
| `NotFoundException` | `SavingAccountFindByIdService`: caja de ahorro no encontrada | "No existe una caja de ahorro con el id proporcionado" |
| `NotFoundException` | `StockFindByIdService`: acción no encontrada | "Accion no encontrada" |
| `NotFoundException` | `StockHistoryFindLastService`: historial no encontrado | "No se encontro el stock" |
| `BadRequestException` | `StockMoveService.toSold`: acciones no disponibles suficientes | "No hay acciones disponibles suficientes para mover a vendidas." |
| `BadRequestException` | `StockMoveService.toAvailable`: acciones vendidas no suficientes | "No hay acciones vendidas suficientes para mover a disponibles." |

## Riesgos de Negocio sin Cobertura de Pruebas Actual

### Submódulo FixedTermDeposit
1. **Cálculo incorrecto de monto invertido**: `FixedTermDepositCalculateAmountInvestedService` no filtra por wallet, sumando todos los plazos fijos del sistema en lugar de solo los del wallet del estudiante.
2. **Finalización automática de plazos fijos**: `FixedTermDepositAutomaticEndsService` se ejecuta mediante scheduler; sin pruebas, puede haber plazos fijos que no finalicen correctamente o transacciones duplicadas.
3. **Validación de balance insuficiente**: `FixedTermDepositRegisterService` usa `UnsupportedOperationException` en lugar de `BadRequestException`, inconsistente con otros servicios.
4. **Cálculo de interés**: `FixedTermDepositCalculateRewardAmountService` usa fórmula fija (1.5% anual); sin pruebas, errores en cálculo pueden generar pérdidas o ganancias incorrectas.

### Submódulo SavingAccount
5. **Incremento diario de interés**: `SavingAccountUpdateService` se ejecuta mediante scheduler; sin pruebas, puede haber incrementos incorrectos o no aplicados.
6. **Validación de propiedad**: Servicios de depósito, retiro y eliminación validan propiedad de caja de ahorro; sin pruebas, estudiantes podrían acceder a cajas de ahorro de otros.
7. **Soft delete con saldo**: `SavingAccountDeleteService` transfiere saldo restante al wallet; sin pruebas, puede haber pérdidas de fondos o transacciones incorrectas.
8. **Validación de nombres únicos**: `SavingAccountRegisterService` valida nombres únicos por wallet; sin pruebas, pueden crearse duplicados.

### Submódulo Stock
9. **Cálculo de variación de precio**: `StockCalculateVariationService` usa lógica compleja con sesgo, tendencia y aleatoriedad; sin pruebas, precios pueden variar incorrectamente.
10. **Actualización automática de precios**: `StockUpdateService` se ejecuta mediante scheduler; sin pruebas, precios pueden quedar desactualizados o fuera de límites.
11. **Ejecución de órdenes de stop**: `OrderStopExecuteService` valida condiciones complejas (LOSS/PROFIT); sin pruebas, órdenes pueden ejecutarse incorrectamente o no ejecutarse cuando deberían.
12. **Cálculo de cantidad en wallet**: `StockCalculateByWalletService` suma compras y resta ventas; sin pruebas, cantidad calculada puede ser incorrecta, permitiendo ventas sin acciones.
13. **Validación de disponibilidad**: `StockBuyService` valida acciones disponibles; sin pruebas, pueden comprarse más acciones de las disponibles.
14. **Validación de propiedad de órdenes**: `StockRegisterStopService` valida acciones en wallet; sin pruebas, pueden registrarse stops sin acciones.
15. **Límites de precio**: `StockUpdateSpecificService` limita precios entre 10.0 y 2.5x precio inicial; sin pruebas, precios pueden salir de límites.
16. **Cálculo de gráfico de velas**: `CandleStickGetValuesService` calcula valores aleatorios para high/low; sin pruebas, gráficos pueden mostrar valores incorrectos.

### Submódulo Investment
17. **Cálculo de total invertido**: `InvestemtCalculateTotalInvestedService` suma tres servicios; sin pruebas, total puede ser incorrecto, afectando balance total del wallet.

