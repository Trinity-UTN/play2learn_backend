# Estrategia de pruebas unitarias — Módulo Investment

## 1. Objetivo
Definir un plan de pruebas unitarias coherente para services, controllers y DTOs del módulo `investment` (incluyendo submódulos `fixedTermDeposit`, `investment`, `stock` y `savingAccount`), garantizando cobertura de flujos críticos (registro y gestión de inversiones, cálculo de intereses y variaciones de precios, ejecución automática de plazos fijos y actualización diaria de cajas de ahorro y acciones, gestión de órdenes de stop, obtención de historiales y gráficos de velas, cálculo de totales invertidos) con suites aisladas, repetibles y alineadas a los estándares del repositorio.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios principales (`FixedTermDepositRegisterService`, `FixedTermDepositListPaginatedService`, `FixedTermDepositAutomaticEndsService`, `SavingAccountRegisterService`, `SavingAccountDepositService`, `SavingAccountWithdrawalService`, `SavingAccountDeleteService`, `SavingAccountListPaginatedService`, `SavingAccountUpdateService`, `StockRegisterService`, `StockBuyService`, `StockSellService`, `StockGetService`, `StockListService`, `StockListPaginatedService`, `StockListHistoryService`, `StockRegisterStopService`, `StockUpdateService`, `CandleStickGetValuesService`, `InvestemtCalculateTotalInvestedService`).
  - Servicios commons bajo `investment/fixedTermDeposit/services/commons`, `investment/savingAccount/services/commons`, `investment/stock/services/commons`.
  - Controladores HTTP publicados bajo `/investment/fixed-term-deposit`, `/investment/saving-accounts`, `/investment/stocks`.
  - DTOs de respuesta definidos en `investment/fixedTermDeposit/dtos`, `investment/savingAccount/dtos`, `investment/stock/dtos`.
- **Excluye**:
  - Modelos JPA (`FixedTermDeposit`, `SavingAccount`, `Stock`, `StockHistory`, `Order`), repositorios (`IFixedTermDepositRepository`, `ISavingAccountRepository`, `IStockRepository`, `IStockHistoryRepository`, `IOrderRepository`, etc.), mappers (`FixedTermDepositMapper`, `SavingAccountMapper`, `StockMapper`, `StockHistoryMapper`, `OrderMapper`) y specs; se validarán mediante pruebas de integración o de otro nivel.

## 3. Métricas y criterios de calidad
- Cobertura objetivo por categoría:
  - Services principales: ≥ 85 % líneas / ≥ 75 % ramas.
  - Services commons: ≥ 90 % líneas / ≥ 80 % ramas.
  - Controllers (`@WebMvcTest`): ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs (construcción y serialización): ≥ 95 % de escenarios ejercitados (valores válidos e inválidos).
- Todas las suites deben correr limpias con `./mvnw.cmd test` sin warnings de Mockito ni logs inesperados.
- Duración total del módulo ≤ 300 s en entorno local al ejecutar `./mvnw.cmd -pl investment -am test`.
- Casos documentados con Given/When/Then mediante `@DisplayName` y asserts descriptivos (AssertJ + JSONPath).
- Suites deben validar cálculos financieros (intereses, variaciones de precio, totales invertidos), ejecución de schedulers y gestión de órdenes de stop.

## 4. Priorización de riesgos

| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| `FixedTermDepositCalculateAmountInvestedService` no filtra por wallet | Alto | Alto | Escenarios en `FixedTermDepositCalculateAmountInvestedServiceTest` verificando que se filtre por wallet del estudiante y no se sumen plazos fijos de otros wallets. |
| `FixedTermDepositAutomaticEndsService` no finaliza plazos fijos correctamente | Alto | Medio | Tests en `FixedTermDepositAutomaticEndsServiceTest` verificando cambio de estado a `FINISHED` cuando `endDate <= LocalDate.now()`, generación de transacción de retorno y actualización de balance invertido. |
| `FixedTermDepositRegisterService` usa excepción inconsistente (`UnsupportedOperationException` vs `BadRequestException`) | Medio | Medio | Casos en `FixedTermDepositRegisterServiceTest` verificando lanzamiento de excepción apropiada cuando wallet no tiene balance suficiente. |
| `FixedTermDepositCalculateRewardAmountService` calcula interés incorrectamente | Alto | Medio | Escenarios en `FixedTermDepositCalculateRewardAmountServiceTest` verificando fórmula correcta: `amountInvested * 1.5 * (fixedTermDays.getValor() / 365.0)`. |
| `SavingAccountUpdateService` no incrementa interés diario correctamente | Alto | Medio | Tests en `SavingAccountUpdateServiceTest` verificando cálculo de interés (0.1% del `currentAmount`), incremento de `accumulatedInterest` y `currentAmount`, actualización de `lastUpdate`. |
| `SavingAccountDepositService` no valida propiedad de caja de ahorro | Alto | Alto | Casos en `SavingAccountDepositServiceTest` verificando validación de pertenencia al wallet del estudiante y lanzamiento de `ConflictException` si no pertenece. |
| `SavingAccountDeleteService` no transfiere saldo restante correctamente | Alto | Alto | Escenarios en `SavingAccountDeleteServiceTest` verificando generación de transacción `RETIRO_CAJA_AHORRO` cuando `currentAmount > 0`, establecimiento de `currentAmount = 0.0` y soft delete. |
| `SavingAccountRegisterService` no valida nombres únicos por wallet | Alto | Alto | Tests en `SavingAccountRegisterServiceTest` verificando validación de existencia de caja de ahorro con mismo nombre y wallet, lanzamiento de `BadRequestException` si ya existe. |
| `StockCalculateVariationService` calcula variación incorrectamente | Alto | Alto | Casos en `StockCalculateVariationServiceTest` verificando cálculo de sesgo según cambio de acciones vendidas, rango de variación basado en nivel de riesgo y tendencia (alcista/bajista), retorno de variación aleatoria dentro del rango. |
| `StockUpdateService` no actualiza precios correctamente | Alto | Medio | Escenarios en `StockUpdateServiceTest` verificando invocación correcta a `StockUpdateSpecificService.execute()` para cada acción del sistema. |
| `OrderStopExecuteService` ejecuta órdenes de stop incorrectamente | Alto | Alto | Tests en `OrderStopExecuteServiceTest` verificando validación de condiciones de stop (LOSS: precio <= precio orden, PROFIT: precio >= precio orden), validación de acciones suficientes en wallet, ejecución de orden (cambio de estado, generación de transacción, movimiento de acciones, actualización de balance). |
| `StockCalculateByWalletService` calcula cantidad incorrectamente | Alto | Alto | Casos en `StockCalculateByWalletServiceTest` verificando suma de órdenes de compra y resta de órdenes de venta ejecutadas del wallet, retorno de cantidad neta correcta. |
| `StockBuyService` no valida disponibilidad de acciones | Alto | Alto | Escenarios en `StockBuyServiceTest` verificando validación de `stock.getAvailableAmount() >= quantity` y lanzamiento de `BadRequestException` si no hay suficientes acciones disponibles. |
| `StockRegisterStopService` no valida acciones en wallet | Alto | Alto | Tests en `StockRegisterStopServiceTest` verificando validación de cantidad de acciones en wallet mediante `StockCalculateByWalletService` y lanzamiento de `BadRequestException` si no hay suficientes acciones. |
| `StockUpdateSpecificService` no respeta límites de precio | Alto | Medio | Casos en `StockUpdateSpecificServiceTest` verificando límites de precio (mínimo 10.0, máximo 2.5x precio inicial), actualización de precio dentro de límites, creación de registro en historial con variación, ejecución de órdenes de stop. |
| `CandleStickGetValuesService` calcula valores de gráfico incorrectamente | Medio | Medio | Escenarios en `CandleStickGetValuesServiceTest` verificando cálculo correcto de valores candlestick (open, close, high, low) a partir del historial según rango de fechas (`RangeValue`). |
| `InvestemtCalculateTotalInvestedService` calcula total incorrectamente | Alto | Medio | Tests en `InvestemtCalculateTotalInvestedServiceTest` verificando suma correcta de totales invertidos en acciones (`StockCalculateAmountInvestedService`), plazos fijos (`FixedTermDepositCalculateAmountInvestedService`) y cajas de ahorro (`SavingAccountCalculateAmountInvestedService`). |
| `StockMoveService` no valida disponibilidad al mover acciones | Alto | Alto | Casos en `StockMoveServiceTest` verificando validación de acciones disponibles suficientes para `toSold`, validación de acciones vendidas suficientes para `toAvailable`, lanzamiento de `BadRequestException` si no hay suficientes acciones. |
| `SavingAccountWithdrawalService` no valida saldo suficiente | Alto | Alto | Escenarios en `SavingAccountWithdrawalServiceTest` verificando validación de `savingAccount.getCurrentAmount() >= amount` y lanzamiento de `BadRequestException` si no hay suficiente saldo en la caja de ahorro. |
| `StockSellService` no valida acciones suficientes en wallet | Alto | Alto | Tests en `StockSellServiceTest` verificando validación de cantidad de acciones en wallet mediante `StockCalculateByWalletService` y lanzamiento de `BadRequestException` si no hay suficientes acciones. |

## 5. Suites planificadas

### Services Principales

1. **Servicios de plazo fijo**
   - `FixedTermDepositRegisterServiceTest`, `FixedTermDepositListPaginatedServiceTest`, `FixedTermDepositAutomaticEndsServiceTest`.
   - Cobertura: validación de balance suficiente, cálculo de interés según días, creación de plazo fijo con estado `IN_PROGRESS`, generación de transacción `PLAZO_FIJO`, actualización de balance invertido, finalización automática mediante scheduler, paginación con filtros por estado y wallet.

2. **Servicios de caja de ahorro**
   - `SavingAccountRegisterServiceTest`, `SavingAccountDepositServiceTest`, `SavingAccountWithdrawalServiceTest`, `SavingAccountDeleteServiceTest`, `SavingAccountListPaginatedServiceTest`, `SavingAccountUpdateServiceTest`.
   - Cobertura: validación de balance suficiente, validación de nombres únicos por wallet, validación de propiedad de caja de ahorro, incremento diario de interés (0.1%), generación de transacciones (`INGRESO_CAJA_AHORRO`, `RETIRO_CAJA_AHORRO`), soft delete con transferencia de saldo, actualización automática mediante scheduler, paginación con filtros.

3. **Servicios de acciones (registro y consulta)**
   - `StockRegisterServiceTest`, `StockGetServiceTest`, `StockListServiceTest`, `StockListPaginatedServiceTest`, `StockListHistoryServiceTest`.
   - Cobertura: creación de acción con precio inicial, creación de registro inicial en historial, obtención de stock con cantidad comprada y órdenes pendientes, listado completo y paginado con filtros, obtención de historial completo ordenado por fecha.

4. **Servicios de acciones (compra y venta)**
   - `StockBuyServiceTest`, `StockSellServiceTest`, `StockRegisterStopServiceTest`.
   - Cobertura: validación de acciones disponibles suficientes, validación de balance suficiente del wallet, validación de acciones en wallet para venta/stop, creación de órdenes (COMPRA/VENTA ejecutadas, VENTA pendiente para stop), generación de transacción `STOCK`, movimiento de acciones entre disponibles y vendidas, actualización de balance invertido.

5. **Servicios de acciones (actualización y análisis)**
   - `StockUpdateServiceTest`, `CandleStickGetValuesServiceTest`.
   - Cobertura: actualización automática de precios mediante scheduler, cálculo de variación de precio, actualización de precio dentro de límites, ejecución de órdenes de stop, cálculo de valores candlestick según rango de fechas (`RangeValue`).

6. **Servicio de cálculo de total invertido**
   - `InvestemtCalculateTotalInvestedServiceTest`.
   - Cobertura: suma correcta de totales invertidos en acciones, plazos fijos y cajas de ahorro, invocación correcta a servicios de cálculo por tipo de inversión.

### Services Commons

1. **Servicios commons de plazo fijo**
   - `FixedTermDepositCalculateAmountInvestedServiceTest`, `FixedTermDepositCalculateRewardAmountServiceTest`, `FixedTermDepositFindAllByStateAndWalletServiceTest`, `FixedTermDepositFindAllByStateServiceTest`.
   - Cobertura: cálculo de monto invertido filtrado por wallet, cálculo de interés según fórmula, búsqueda de plazos fijos por estado y wallet, búsqueda de plazos fijos por estado.

2. **Servicios commons de caja de ahorro**
   - `SavingAccountCalculateAmountInvestedServiceTest`, `SavingAccountExistsByNameAndWalletServiceTest`, `SavingAccountFindByIdServiceTest`.
   - Cobertura: cálculo de monto invertido sumando `currentAmount` de cajas de ahorro no eliminadas, verificación de existencia por nombre y wallet, búsqueda por ID con validación de existencia y soft delete.

3. **Servicios commons de acciones (cálculos)**
   - `StockCalculateAmountInvestedServiceTest`, `StockCalculateByWalletServiceTest`, `StockCalculateVariationServiceTest`.
   - Cobertura: cálculo de monto invertido en acciones (suma de `currentPrice * cantidadEnWallet`), cálculo de cantidad neta en wallet (suma compras - resta ventas), cálculo de variación de precio con sesgo, tendencia y aleatoriedad.

4. **Servicios commons de acciones (búsqueda)**
   - `StockFindAllServiceTest`, `StockFindByIdServiceTest`.
   - Cobertura: búsqueda de todas las acciones, búsqueda por ID con validación de existencia.

5. **Servicios commons de historial de acciones**
   - `StockHistoryCalculateTrendServiceTest`, `StockHistoryFindByStockAndRangeServiceTest`, `StockHistoryFindByStockServiceTest`, `StockHistoryFindLastServiceTest`.
   - Cobertura: cálculo de tendencia (alcista/bajista) según últimas 10 variaciones, búsqueda de historial por rango de fechas, búsqueda de historial completo, búsqueda de último registro con validación de existencia.

6. **Servicios commons de órdenes**
   - `OrderFindPendingServiceTest`, `OrderStopExecuteServiceTest`.
   - Cobertura: búsqueda de órdenes pendientes de stop por stock y wallet, ejecución de órdenes de stop cuando se cumplan condiciones (LOSS/PROFIT), validación de acciones suficientes, generación de transacciones y movimiento de acciones.

7. **Servicios commons de movimiento y actualización**
   - `StockMoveServiceTest`, `StockUpdateSpecificServiceTest`.
   - Cobertura: movimiento de acciones entre disponibles y vendidas con validación de disponibilidad, actualización específica de precio con límites, creación de registro en historial, ejecución de órdenes de stop.

### Controllers

1. **Controllers de plazo fijo**
   - `FixedTermDepositRegisterControllerTest`, `FixedTermDepositListPaginatedControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), payloads JSON (`FixedTermDepositRegisterRequestDto`), parámetros de paginación, respuestas 201/200, path params.

2. **Controllers de caja de ahorro**
   - `SavingAccountRegisterControllerTest`, `SavingAccountDepositControllerTest`, `SavingAccountWithdrawalControllerTest`, `SavingAccountDeleteControllerTest`, `SavingAccountListPaginatedControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), payloads JSON (`SavingAccountRegisterRequestDto`, `SavingAccountDepositRequestDto`, `SavingAccountWithdrawalRequestDto`), path params (`id`), respuestas 201/204/200, códigos de error 400/404/409.

3. **Controllers de acciones (registro y consulta)**
   - `StockRegisterControllerTest`, `StockGetControllerTest`, `StockListControllerTest`, `StockListPaginatedControllerTest`, `StockHistoryListByStockControllerTest`, `StockCandleStickGetControllerTest`.
   - Validación de roles (`ROLE_DEV`, `ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN`), payloads JSON (`StockRegisterRequestDto`), query params (`stockId`, `rangeValue`), path params (`id`), respuestas 200/201.

4. **Controllers de acciones (compra, venta y stop)**
   - `StockBuyControllerTest`, `StockSellControllerTest`, `StockRegisterStopControllerTest`.
   - Validación de roles (`ROLE_STUDENT`), payloads JSON (`StockBuyRequestDto`, `StockOrderStopRequestDto`), respuestas 201, códigos de error 400.

### DTOs

1. **DTOs de plazo fijo**
   - `FixedTermDepositResponseDtoTest`.
   - Verifica construcción correcta con valores válidos, serialización a JSON, manejo de fechas (`LocalDate`), estados (`FixedTermState`).

2. **DTOs de caja de ahorro**
   - `SavingAccountResponseDtoTest`.
   - Verifica construcción correcta con valores válidos, serialización a JSON, manejo de fechas (`LocalDate`).

3. **DTOs de acciones**
   - `StockResponseDtoTest`, `StockBuyResponseDtoTest`, `StockSellResponseDtoTest`, `StockHistoryResponseDtoTest`, `CandleStickChartValueResponseDtoTest`.
   - Verifica construcción correcta con valores válidos, serialización a JSON, manejo de listas anidadas (`pendingOrders` en `StockResponseDto`), fechas (`LocalDateTime`, `LocalDate`), tipos numéricos (`BigInteger`, `Double`).

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`) con `ArgumentCaptor` para validar interacciones críticas (repositorios, servicios externos, servicios commons).
- `@WebMvcTest` con `@MockBean` para inyectar servicios simulados en controllers.
- `@AutoConfigureMockMvc(addFilters = false)` para deshabilitar filtros de Spring Security en tests de controllers.
- `SessionUserArgumentResolver` mockeado para simular inyección de `User` mediante `@SessionUser`.
- AssertJ para aserciones descriptivas y navegación de objetos anidados.
- `ObjectMapper` para serialización/deserialización JSON en tests de controllers.
- `MockedStatic` (Mockito) para mockear llamadas estáticas en servicios de paginación (`PaginatorUtils`, `PaginationHelper`) y schedulers.
- `@Nested` para organizar tests por escenario (éxito, excepciones, validaciones).
- `LocalDate.now()` y `LocalDateTime.now()` mockeados cuando sea necesario para tests de schedulers y fechas.

## 7. Fixtures compartidos (InvestmentTestMother)
Clase centralizada `InvestmentTestMother` ubicada en `src/test/java/trinity/play2learn/backend/investment/InvestmentTestMother.java` con los siguientes builders y helpers:

### Constantes comunes
- `DEFAULT_FIXED_TERM_DEPOSIT_ID`, `DEFAULT_SAVING_ACCOUNT_ID`, `DEFAULT_STOCK_ID`, `DEFAULT_ORDER_ID`, `DEFAULT_STOCK_HISTORY_ID`, `DEFAULT_WALLET_ID`, `DEFAULT_STUDENT_ID`, `DEFAULT_STUDENT_EMAIL`, `DEFAULT_AMOUNT`, `DEFAULT_INITIAL_PRICE`, `DEFAULT_QUANTITY`, etc.

### Builders para FixedTermDeposit
- `fixedTermDeposit(Long id, Double amountInvested, Double amountReward, FixedTermDays fixedTermDays, FixedTermState state, Wallet wallet)`: Plazo fijo básico.
- `fixedTermDepositInProgress(Long id, Double amountInvested, LocalDate endDate)`: Plazo fijo en progreso.
- `fixedTermDepositFinished(Long id, Double amountReward)`: Plazo fijo finalizado.
- `defaultFixedTermDeposit()`: Plazo fijo con valores por defecto.

### Builders para SavingAccount
- `savingAccount(Long id, String name, Double initialAmount, Double currentAmount, Double accumulatedInterest, Wallet wallet)`: Caja de ahorro básica.
- `savingAccountWithBalance(Long id, String name, Double currentAmount)`: Caja de ahorro con saldo específico.
- `savingAccountDeleted(Long id, LocalDateTime deletedAt)`: Caja de ahorro eliminada (soft delete).
- `defaultSavingAccount()`: Caja de ahorro con valores por defecto.

### Builders para Stock
- `stock(Long id, String name, String abbreviation, Double initialPrice, Double currentPrice, BigInteger totalAmount, BigInteger availableAmount, BigInteger soldAmount, RiskLevel riskLevel)`: Acción básica.
- `stockWithPrice(Long id, Double currentPrice, Double initialPrice)`: Acción con precio específico.
- `stockWithAvailability(Long id, BigInteger availableAmount, BigInteger soldAmount)`: Acción con disponibilidad específica.
- `defaultStock()`: Acción con valores por defecto.

### Builders para Order
- `order(Long id, Stock stock, Wallet wallet, OrderType orderType, OrderState orderState, BigInteger quantity, Double pricePerUnit, OrderStop orderStop)`: Orden básica.
- `orderExecuted(Long id, OrderType orderType, BigInteger quantity)`: Orden ejecutada.
- `orderPending(Long id, OrderStop orderStop, Double pricePerUnit)`: Orden pendiente de stop.
- `defaultOrder()`: Orden con valores por defecto.

### Builders para StockHistory
- `stockHistory(Long id, Stock stock, Double price, Double variation, BigInteger soldAmount, BigInteger availableAmount, LocalDateTime createdAt)`: Historial básico.
- `stockHistoryWithVariation(Long id, Double variation, Double price)`: Historial con variación específica.
- `defaultStockHistory()`: Historial con valores por defecto.

### Builders para DTOs
- `fixedTermDepositResponseDto(Long id, Double amountInvested, Double amountReward, FixedTermDays fixedTermDays, FixedTermState state)`: `FixedTermDepositResponseDto` básico.
- `savingAccountResponseDto(Long id, String name, Double initialAmount, Double currentAmount, Double accumulatedInterest)`: `SavingAccountResponseDto` básico.
- `stockResponseDto(Long id, String name, String abbreviation, Double currentPrice, BigInteger quantityBought, List<StockSellResponseDto> pendingOrders)`: `StockResponseDto` básico.
- `stockBuyResponseDto(Long id, Double pricePerUnit, BigInteger quantity, Double total)`: `StockBuyResponseDto` básico.
- `stockSellResponseDto(BigInteger quantity, Double pricePerUnit, Double total)`: `StockSellResponseDto` básico.
- `stockHistoryResponseDto(Long id, Double price, Double variation, LocalDateTime createdAt)`: `StockHistoryResponseDto` básico.
- `candleStickChartValueResponseDto(LocalDate date, Double open, Double close, Double high, Double low)`: `CandleStickChartValueResponseDto` básico.

### Helpers para entidades relacionadas
- `wallet(Long id, Double balance, Double invertedBalance)`: Wallet básico con balances.
- `student(Long id, String email, Wallet wallet)`: Estudiante básico con wallet asociado.
- `user(String email, String role)`: Usuario autenticado con rol.

### Helpers para casos especiales
- `walletWithInsufficientBalance(Long id, Double balance, Double amount)`: Wallet sin balance suficiente para operación.
- `stockWithInsufficientAvailability(Long id, BigInteger availableAmount, BigInteger quantity)`: Acción sin disponibilidad suficiente.
- `savingAccountWithInsufficientBalance(Long id, Double currentAmount, Double amount)`: Caja de ahorro sin saldo suficiente.
- `fixedTermDepositList(List<FixedTermDeposit> deposits)`: Lista de plazos fijos para paginación.
- `stockHistoryList(List<StockHistory> histories)`: Lista de historiales para cálculo de tendencia y candlestick.

## 8. Secuencia de trabajo

### Fase 1: Services principales (T03, T04)
- Generar tests para servicios principales (20 services: FixedTermDeposit, SavingAccount, Stock, Investment).
- Refinar tests usando `InvestmentTestMother` y eliminar duplicidades.

### Fase 2: Services commons (T05, T06)
- Generar tests para servicios commons (20 services: FixedTermDeposit, SavingAccount, Stock).
- Refinar tests usando `InvestmentTestMother` y validar casos de excepción y cálculos.

### Fase 3: Controllers (T07, T08)
- Generar tests `@WebMvcTest` para todos los controllers (16 controllers).
- Refinar tests centralizando configuración MockMvc y validando roles y respuestas JSON.

### Fase 4: DTOs (T09, T10)
- Generar tests de construcción y serialización para DTOs de respuesta (7 DTOs).
- Refinar tests mejorando reutilización de fixtures y cobertura de combinaciones límite.

### Fase 5: Ejecución y reporte (T11, T12)
- Ejecutar todas las suites con JaCoCo y generar reporte de cobertura.
- Generar reporte automatizado consolidando métricas y hallazgos.

## 9. Consideraciones especiales

1. **Schedulers automáticos:** `FixedTermDepositAutomaticEndsService`, `SavingAccountUpdateService` y `StockUpdateService` se ejecutan mediante `@Scheduled`. Los tests deben validar lógica de ejecución sin depender del scheduler real, usando mocks de `LocalDate.now()` y `LocalDateTime.now()` cuando sea necesario.

2. **Cálculos financieros:** Los servicios de cálculo (intereses, variaciones de precio, totales invertidos) usan fórmulas específicas. Los tests deben validar precisión numérica y manejo de casos límite (montos cero, negativos, muy grandes).

3. **Gestión de órdenes de stop:** `OrderStopExecuteService` valida condiciones complejas (LOSS/PROFIT) y ejecuta órdenes automáticamente. Los tests deben cubrir todos los escenarios: condiciones cumplidas, no cumplidas, acciones insuficientes, cancelación de orden.

4. **Movimiento de acciones:** `StockMoveService` mueve acciones entre estados (disponibles ↔ vendidas). Los tests deben validar integridad de cantidades y lanzamiento de excepciones cuando no hay suficientes acciones.

5. **Validación de propiedad:** Los servicios de caja de ahorro validan que la cuenta pertenezca al wallet del estudiante. Los tests deben cubrir escenarios de propiedad válida e inválida.

6. **Soft delete:** `SavingAccountDeleteService` realiza soft delete y transfiere saldo restante. Los tests deben validar generación de transacción cuando hay saldo y establecimiento correcto de `deletedAt`.

7. **Límites de precio:** `StockUpdateSpecificService` limita precios entre 10.0 y 2.5x precio inicial. Los tests deben validar que los precios se mantengan dentro de estos límites.

8. **Cálculo de gráficos de velas:** `CandleStickGetValuesService` calcula valores aleatorios para high/low basados en historial. Los tests deben validar estructura correcta de valores (open, close, high, low) y manejo de diferentes rangos (`RangeValue`).

9. **Paginación y filtros:** Los servicios de listado paginado usan `PaginatorUtils`, `PaginationHelper` y specs. Los tests deben validar construcción correcta de especificaciones y `PaginatedData` con filtros dinámicos.

10. **Dependencias externas:** Los servicios dependen de `IStudentGetByEmailService` (Admin), `ITransactionGenerateService` y `IWalletUpdateInvestedBalanceService` (Economy). Los tests deben mockear estas dependencias y validar invocaciones correctas.

