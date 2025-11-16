# Estrategia de pruebas unitarias — Módulo Economy

## 1. Objetivo
Definir un plan de pruebas unitarias coherente para services, controllers y DTOs del módulo `economy` (incluyendo submódulos `reserve`, `transaction` y `wallet`), garantizando cobertura de flujos críticos (generación de transacciones por estrategia, gestión de wallets, gestión de reservas, cálculo de estadísticas, movimiento de fondos entre reserva y circulación) con suites aisladas, repetibles y alineadas a los estándares del repositorio.

## 2. Alcance y exclusiones
- **Incluye**:
  - Implementaciones de servicios principales (`TransactionGenerateService`, `TransactionListStatisticsService`, `WalletGetService`, `WalletAddAmountService`, `WalletRemoveAmountService`, `WalletUpdateInvestedBalanceService`, `WalletGetLastTransactionsService`, `ReserveModifyService`).
  - Servicios de estrategia de transacciones (`CompraTransactionService`, `RecompensaTransactionService`, `AsignacionTransactionService`, `ReembolsoTransactionService`, `ActividadTransactionService`, `DepositSavingAccountTransactionService`, `WithdrawalSavingAccountTransactionService`, `StockTransactionService`, `FixedTermDepositTransactionService`, `InversionTransactionService`).
  - Servicios commons bajo `economy/transaction/services/commons` y `economy/reserve/services/commons`.
  - Controladores HTTP publicados bajo `/wallet` y `/transactions`.
  - DTOs de respuesta definidos en `economy/wallet/dtos/response` y `economy/transaction/dtos`.
- **Excluye**:
  - Modelos JPA (`Transaction`, `Wallet`, `Reserve`), repositorios (`ITransactionRepository`, `IWalletRepository`, `IReserveRepository`, etc.), mappers (`TransactionMapper`, `WalletMapper`) y specs; se validarán mediante pruebas de integración o de otro nivel.

## 3. Métricas y criterios de calidad
- Cobertura objetivo por categoría:
  - Services principales y de estrategia: ≥ 85 % líneas / ≥ 75 % ramas.
  - Services commons: ≥ 90 % líneas / ≥ 80 % ramas.
  - Controllers (`@WebMvcTest`): ≥ 85 % líneas / ≥ 70 % ramas.
  - DTOs (construcción y serialización): ≥ 95 % de escenarios ejercitados (valores válidos e inválidos).
- Todas las suites deben correr limpias con `./mvnw.cmd test` sin warnings de Mockito ni logs inesperados.
- Duración total del módulo ≤ 180 s en entorno local al ejecutar `./mvnw.cmd -pl economy -am test`.
- Casos documentados con Given/When/Then mediante `@DisplayName` y asserts descriptivos (AssertJ + JSONPath).
- Suites deben validar estrategias de transacción dinámicas y movimiento de fondos entre reserva y circulación.

## 4. Priorización de riesgos

| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| `TransactionGenerateService` no valida montos positivos correctamente | Alto | Alto | Escenarios en `TransactionGenerateServiceTest` verificando validación `amount > 0` y lanzamiento de `ConflictException` cuando `amount <= 0`. |
| `TransactionGenerateService` no valida tipo de transacción soportado | Alto | Medio | Tests en `TransactionGenerateServiceTest` verificando validación de tipo de transacción en `Map<String, ITransactionStrategyService>` y lanzamiento de `ConflictException` si estrategia no existe. |
| `TransactionGenerateService` no delega correctamente a estrategia correspondiente | Alto | Medio | Casos en `TransactionGenerateServiceTest` verificando invocación correcta a `strategy.execute()` según `TypeTransaction` y retorno de `Transaction` creada. |
| `CompraTransactionService` no valida balance suficiente del wallet | Alto | Alto | Escenarios en `CompraTransactionServiceTest` verificando validación `wallet.getBalance() >= amount` y lanzamiento de `IllegalArgumentException` si no hay suficiente dinero. |
| `CompraTransactionService` no actualiza correctamente wallet y reserva | Alto | Alto | Tests en `CompraTransactionServiceTest` verificando invocación correcta a `removeAmountWalletService.execute()` y `modifyReserveService.moveToReserve()` después de crear transacción. |
| `RecompensaTransactionService` no valida balance suficiente de actividad | Alto | Alto | Casos en `RecompensaTransactionServiceTest` verificando validación `activity.getActualBalance() >= amount` y lanzamiento de `ConflictException` si no hay suficiente dinero en la materia. |
| `RecompensaTransactionService` no actualiza correctamente actividad y wallet | Alto | Alto | Escenarios en `RecompensaTransactionServiceTest` verificando invocación correcta a `activityRemoveBalanceService.execute()` y `addAmountWalletService.execute()` después de crear transacción. |
| `AsignacionTransactionService` no valida montos correctos | Alto | Medio | Tests en `AsignacionTransactionServiceTest` verificando validación `assignAmount == amount` (diferencia entre `initialBalance` y `actualBalance`) y lanzamiento de `UnsupportedOperationException` si no coincide. |
| `AsignacionTransactionService` no ajusta reserva cuando no hay suficiente balance | Alto | Medio | Casos en `AsignacionTransactionServiceTest` verificando ajuste de reserva cuando `amount > reserve.getReserveBalance()` (crea diferencia faltante). |
| `ActividadTransactionService` no valida límite del 30% del balance inicial | Alto | Alto | Escenarios en `ActividadTransactionServiceTest` verificando validación `amount <= 0.3 * subject.getInitialBalance()` y lanzamiento de `ConflictException` si excede límite. |
| `ActividadTransactionService` no valida balance suficiente de materia | Alto | Alto | Tests en `ActividadTransactionServiceTest` verificando validación `amount <= subject.getActualBalance()` y lanzamiento de `ConflictException` si materia no tiene balance suficiente. |
| `DepositSavingAccountTransactionService` no valida balance suficiente del wallet | Alto | Alto | Casos en `DepositSavingAccountTransactionServiceTest` verificando validación `amount <= wallet.getBalance()` y lanzamiento de `BadRequestException` si wallet no tiene balance suficiente. |
| `WithdrawalSavingAccountTransactionService` no valida saldo suficiente de caja de ahorro | Alto | Alto | Escenarios en `WithdrawalSavingAccountTransactionServiceTest` verificando validación `amount <= savingAccount.getCurrentAmount()` y lanzamiento de `BadRequestException` si caja de ahorro no tiene saldo suficiente. |
| `StockTransactionService` no maneja correctamente tipo de orden (COMPRA vs VENTA) | Alto | Alto | Tests en `StockTransactionServiceTest` verificando comportamiento según `order.getOrderType()` (COMPRA: valida balance, resta del wallet, mueve a reserva; VENTA: suma al wallet, mueve de reserva a circulación). |
| `FixedTermDepositTransactionService` no maneja correctamente estado (IN_PROGRESS vs finalizado) | Alto | Alto | Casos en `FixedTermDepositTransactionServiceTest` verificando comportamiento según `fixedTermDeposit.getFixedTermState()` (IN_PROGRESS: valida balance, resta del wallet, mueve a reserva; finalizado: suma al wallet, mueve de reserva a circulación). |
| `InversionTransactionService` lanza excepción no manejada | Medio | Alto | Tests en `InversionTransactionServiceTest` verificando lanzamiento de `UnsupportedOperationException` con mensaje "Unimplemented method 'execute'" cuando se invoca. |
| `ReserveModifyService.moveToReserve` no valida balance suficiente de circulación | Alto | Alto | Escenarios en `ReserveModifyServiceTest` verificando validación `amount <= reserve.getCirculationBalance()` y lanzamiento de `BadRequestException` si no hay suficiente dinero en circulación. |
| `ReserveModifyService.moveToCirculation` ajusta reserva incorrectamente cuando no hay suficiente balance | Alto | Medio | Tests en `ReserveModifyServiceTest` verificando ajuste de reserva cuando `amount > reserve.getReserveBalance()` (crea diferencia faltante: `reserveBalance + diferenciaFaltante`). |
| `ReserveFindLastService` no maneja correctamente cuando no existe reserva | Alto | Medio | Casos en `ReserveFindLastServiceTest` verificando lanzamiento de `NotFoundException` cuando `findFirstByOrderByCreatedAtDesc()` retorna `Optional.empty()`. |
| `WalletAddAmountService` no valida montos positivos | Alto | Alto | Escenarios en `WalletAddAmountServiceTest` verificando validación `amount > 0` y lanzamiento de `IllegalArgumentException` si `amount <= 0`. |
| `WalletRemoveAmountService` no valida balance suficiente | Alto | Alto | Tests en `WalletRemoveAmountServiceTest` verificando validación `wallet.getBalance() >= amount` y lanzamiento de `IllegalArgumentException` si no hay suficiente dinero. |
| `WalletUpdateInvestedBalanceService` no calcula correctamente balance invertido | Alto | Medio | Casos en `WalletUpdateInvestedBalanceServiceTest` verificando invocación correcta a `investmentCalculateTotalInvestedService.cu110calculateTotalInvested(wallet)` y actualización de `invertedBalance`. |
| `TransactionListStatisticsService` calcula incorrectamente totales acumulados | Alto | Medio | Escenarios en `TransactionListStatisticsServiceTest` verificando cálculo correcto de totales por tipo de transacción según descripción (asignación mensual, actividad, recompensa, compras, ventas/reembolsos) y manejo de casos especiales. |
| `TransactionListStatisticsService` procesa incorrectamente asignación mensual | Medio | Medio | Tests en `TransactionListStatisticsServiceTest` verificando lógica de asignación mensual (toma de reserva si hay, sino emite nuevas) y actualización correcta de totales. |
| `WalletGetService` no obtiene correctamente últimas transacciones | Medio | Bajo | Casos en `WalletGetServiceTest` verificando invocación correcta a `transactionGetLastTransaccionsService.execute()` y mapeo a `WalletCompleteResponseDto`. |
| `WalletGetLastTransactionsService` no obtiene correctamente últimas 10 transacciones | Medio | Bajo | Escenarios en `WalletGetLastTransactionsServiceTest` verificando invocación correcta a `transactionGetLastTransaccionsService.execute()` y mapeo a `List<TransactionResponseDto>`. |
| `TransactionGetLastTransactionsService` no retorna correctamente últimas 10 transacciones | Medio | Bajo | Tests en `TransactionGetLastTransactionsServiceTest` verificando invocación correcta a `transactionRepository.findTop10ByWalletOrderByCreatedAtDesc(wallet)` y retorno de `List<Transaction>`. |

## 5. Suites planificadas

### Services Principales

1. **Servicio de generación de transacciones**
   - `TransactionGenerateServiceTest`.
   - Cobertura: validación de montos positivos, validación de tipo de transacción soportado, delegación correcta a estrategia según `TypeTransaction`, lanzamiento de excepciones apropiadas.

2. **Servicio de estadísticas de transacciones**
   - `TransactionListStatisticsServiceTest`.
   - Cobertura: procesamiento de transacciones ordenadas por fecha ascendente, cálculo de totales acumulados por tipo de transacción según descripción, manejo de casos especiales (asignación mensual, actividad, recompensa, compras, ventas/reembolsos), generación de snapshots por fecha.

3. **Servicios de wallet (obtención y actualización)**
   - `WalletGetServiceTest`, `WalletGetLastTransactionsServiceTest`, `WalletUpdateInvestedBalanceServiceTest`.
   - Cobertura: obtención de estudiante por email, obtención de wallet completo con últimas transacciones, obtención de últimas 10 transacciones, cálculo de balance invertido total, mapeo a DTOs.

4. **Servicios de wallet (modificación de balance)**
   - `WalletAddAmountServiceTest`, `WalletRemoveAmountServiceTest`.
   - Cobertura: validación de montos positivos, validación de balance suficiente para remover, incremento/decremento de balance, persistencia de wallet actualizado.

5. **Servicio de modificación de reserva**
   - `ReserveModifyServiceTest`.
   - Cobertura: validación de montos positivos, validación de balance suficiente de circulación para mover a reserva, ajuste de reserva cuando no hay suficiente balance para mover a circulación, transferencia correcta entre reserva y circulación, actualización de `lastUpdateAt`.

### Services de Estrategia de Transacciones

6. **Estrategia de compra**
   - `CompraTransactionServiceTest`.
   - Cobertura: validación de balance suficiente del wallet, obtención de última reserva, creación de transacción, actualización de wallet (resta monto), actualización de reserva (mueve a reserva).

7. **Estrategia de recompensa**
   - `RecompensaTransactionServiceTest`.
   - Cobertura: validación de balance suficiente de actividad, obtención de última reserva, creación de transacción, actualización de actividad (resta monto), actualización de wallet (suma monto).

8. **Estrategia de asignación**
   - `AsignacionTransactionServiceTest`.
   - Cobertura: validación de montos correctos (`assignAmount == amount`), obtención de última reserva, ajuste de reserva cuando no hay suficiente balance, creación de transacción, actualización de materia (suma balance), actualización de reserva (mueve de reserva a circulación).

9. **Estrategia de reembolso**
   - `ReembolsoTransactionServiceTest`.
   - Cobertura: obtención de última reserva, creación de transacción asociada a beneficio, actualización de wallet (suma monto), actualización de reserva (mueve de reserva a circulación).

10. **Estrategia de actividad**
    - `ActividadTransactionServiceTest`.
    - Cobertura: validación de límite del 30% del balance inicial, validación de balance suficiente de materia, obtención de última reserva, creación de transacción, actualización de materia (resta balance), actualización de actividad (suma balance).

11. **Estrategia de depósito en caja de ahorro**
    - `DepositSavingAccountTransactionServiceTest`.
    - Cobertura: validación de montos positivos, validación de balance suficiente del wallet, obtención de última reserva, creación de transacción asociada a caja de ahorro, actualización de wallet (resta monto), actualización de reserva (mueve a reserva).

12. **Estrategia de retiro de caja de ahorro**
    - `WithdrawalSavingAccountTransactionServiceTest`.
    - Cobertura: validación de montos positivos, validación de saldo suficiente de caja de ahorro, obtención de última reserva, creación de transacción asociada a caja de ahorro, actualización de wallet (suma monto), actualización de reserva (mueve de reserva a circulación).

13. **Estrategia de acciones (stock)**
    - `StockTransactionServiceTest`.
    - Cobertura: comportamiento según tipo de orden (COMPRA vs VENTA), validación de balance suficiente para compra, creación de transacción asociada a orden, actualización de wallet y reserva según tipo.

14. **Estrategia de plazo fijo**
    - `FixedTermDepositTransactionServiceTest`.
    - Cobertura: comportamiento según estado (IN_PROGRESS vs finalizado), validación de balance suficiente para IN_PROGRESS, creación de transacción asociada a plazo fijo, actualización de wallet y reserva según estado.

15. **Estrategia de inversión**
    - `InversionTransactionServiceTest`.
    - Cobertura: lanzamiento de `UnsupportedOperationException` con mensaje "Unimplemented method 'execute'" cuando se invoca.

### Services Commons

1. **Servicio de obtención de últimas transacciones**
   - `TransactionGetLastTransactionsServiceTest`.
   - Cobertura: búsqueda de últimas 10 transacciones ordenadas por fecha descendente, retorno de `List<Transaction>`.

2. **Servicio de obtención de última reserva**
   - `ReserveFindLastServiceTest`.
   - Cobertura: búsqueda de primera reserva ordenada por fecha de creación descendente, lanzamiento de `NotFoundException` si no encuentra reserva.

### Controllers

1. **Controllers de wallet**
   - `WalletGetControllerTest`, `WalletGetLastTransactionsControllerTest`, `WalletAmountAssingControllerTest`.
   - Validación de roles (`ROLE_STUDENT` para obtención, `ROLE_DEV` para asignación de prueba), payloads JSON, códigos 2xx/204, path params.

2. **Controller de estadísticas de transacciones**
   - `TransactionStatisticsControllerTest`.
   - Validación de roles (`ROLE_DEV`), respuestas `List<TransactionStatisticsResponseDto>`, códigos 2xx.

### DTOs

1. **DTOs de wallet**
   - `WalletResponseDtoTest`, `WalletCompleteResponseDtoTest`.
   - Verifica construcción correcta con valores válidos, serialización a JSON, manejo de listas anidadas (`transactions` en `WalletCompleteResponseDto`).

2. **DTOs de transacción**
   - `TransactionResponseDtoTest`, `TransactionStatisticsResponseDtoTest`.
   - Verifica construcción correcta con valores válidos, serialización a JSON, manejo de fechas (`LocalDateTime`).

## 6. Dobles de prueba y herramientas
- Mockito + JUnit 5 (`@ExtendWith(MockitoExtension.class)`) con `ArgumentCaptor` para validar interacciones críticas (`ITransactionRepository`, `IWalletRepository`, `IReserveRepository`, servicios externos).
- `@WebMvcTest` con `@MockBean` para inyectar servicios simulados en controllers.
- `@AutoConfigureMockMvc(addFilters = false)` para deshabilitar filtros de Spring Security en tests de controllers.
- `SessionUserArgumentResolver` mockeado para simular inyección de `User` mediante `@SessionUser`.
- AssertJ para aserciones descriptivas y navegación de objetos anidados.
- `ObjectMapper` para serialización/deserialización JSON en tests de controllers.
- `MockedStatic` (Mockito) para mockear llamadas estáticas si es necesario en servicios de estadísticas.
- `@Nested` para organizar tests por escenario (éxito, excepciones, validaciones).

## 7. Fixtures compartidos (EconomyTestMother)
Clase centralizada `EconomyTestMother` ubicada en `src/test/java/trinity/play2learn/backend/economy/EconomyTestMother.java` con los siguientes builders y helpers:

### Constantes comunes
- `DEFAULT_WALLET_ID`, `DEFAULT_TRANSACTION_ID`, `DEFAULT_RESERVE_ID`, `DEFAULT_STUDENT_ID`, `DEFAULT_STUDENT_EMAIL`, `DEFAULT_AMOUNT`, etc.

### Builders para Wallet
- `wallet(Long id, Double balance, Double invertedBalance)`: Wallet básico.
- `walletWithBalance(Long id, Double balance)`: Wallet con balance específico.
- `defaultWallet()`: Wallet con valores por defecto.

### Builders para Transaction
- `transaction(Long id, Double amount, String description, TypeTransaction type, Wallet wallet, Reserve reserve)`: Transacción básica.
- `transactionWithDescription(Long id, String description, Double amount)`: Transacción con descripción específica.
- `defaultTransaction()`: Transacción con valores por defecto.

### Builders para Reserve
- `reserve(Long id, Double reserveBalance, Double circulationBalance, Double initialBalance)`: Reserva básica.
- `reserveWithBalances(Long id, Double reserveBalance, Double circulationBalance)`: Reserva con balances específicos.
- `defaultReserve()`: Reserva con valores por defecto.

### Builders para DTOs
- `walletResponseDto(Long id, Double balance, Double invertedBalance, Double totalBalance)`: `WalletResponseDto` básico.
- `walletCompleteResponseDto(Long id, Double balance, Double invertedBalance, Double totalBalance, List<TransactionResponseDto> transactions)`: `WalletCompleteResponseDto` completo.
- `transactionResponseDto(Double amount, LocalDateTime createdAt, String description, String type)`: `TransactionResponseDto` básico.
- `transactionStatisticsResponseDto(LocalDateTime date, Double total, Double totalCirculation, Double totalReserve, Double totalSubject, Double totalStudent, Double totalActivity)`: `TransactionStatisticsResponseDto` completo.

### Helpers para entidades relacionadas
- `student(Long id, String email)`: Estudiante básico con wallet asociado.
- `subject(Long id, Double initialBalance, Double actualBalance)`: Materia con balances.
- `activity(Long id, Double actualBalance)`: Actividad con balance.
- `benefit(Long id)`: Beneficio básico.
- `savingAccount(Long id, Double currentAmount)`: Caja de ahorro con saldo actual.
- `fixedTermDeposit(Long id, FixedTermState state)`: Plazo fijo con estado.
- `order(Long id, OrderType orderType)`: Orden de acciones con tipo.

### Helpers para casos especiales
- `reserveWithInsufficientBalance(Long id, Double reserveBalance, Double amount)`: Reserva sin balance suficiente para operación.
- `walletWithInsufficientBalance(Long id, Double balance, Double amount)`: Wallet sin balance suficiente para operación.
- `transactionList(List<String> descriptions, List<Double> amounts)`: Lista de transacciones con descripciones específicas para estadísticas.

## 8. Secuencia de trabajo

### Fase 1: Fixtures compartidos (T04 - Refinamiento)
- Crear `EconomyTestMother` con builders y helpers centralizados.
- Establecer constantes comunes y valores por defecto.
- Documentar uso de builders en tests.

### Fase 2: Services principales (T03, T04)
- Generar tests para `TransactionGenerateService`, `TransactionListStatisticsService`, `WalletGetService`, `WalletAddAmountService`, `WalletRemoveAmountService`, `WalletUpdateInvestedBalanceService`, `WalletGetLastTransactionsService`, `ReserveModifyService`.
- Refinar tests usando `EconomyTestMother` y eliminar duplicidades.

### Fase 3: Services de estrategia (T03, T04)
- Generar tests para todos los servicios de estrategia de transacciones (10 services).
- Refinar tests usando `EconomyTestMother` y validar comportamiento específico de cada estrategia.

### Fase 4: Services commons (T05, T06)
- Generar tests para `TransactionGetLastTransactionsService`, `ReserveFindLastService`.
- Refinar tests usando `EconomyTestMother` y validar casos de excepción.

### Fase 5: Controllers (T07, T08)
- Generar tests `@WebMvcTest` para todos los controllers (4 controllers).
- Refinar tests centralizando configuración MockMvc y validando roles y respuestas JSON.

### Fase 6: DTOs (T09, T10)
- Generar tests de construcción y serialización para DTOs de respuesta (4 DTOs).
- Refinar tests mejorando reutilización de fixtures y cobertura de combinaciones límite.

### Fase 7: Ejecución y reporte (T11, T12)
- Ejecutar todas las suites con JaCoCo y generar reporte de cobertura.
- Generar reporte automatizado consolidando métricas y hallazgos.

## 9. Consideraciones especiales

1. **Patrón Strategy en transacciones:** Cada estrategia implementa `ITransactionStrategyService` y se registra en el Map de Spring mediante `@Service("TIPO")`. Los tests deben validar que `TransactionGenerateService` delegue correctamente a la estrategia correspondiente según `TypeTransaction`.

2. **Transaccionalidad:** Todos los servicios de estrategia están marcados con `@Transactional`. Los tests deben validar atomicidad de operaciones que involucran múltiples entidades (crear transacción, actualizar wallet, modificar reserva, etc.).

3. **Reserva:** El sistema mantiene una reserva central que se actualiza con cada transacción. Los tests deben validar integridad de balances al transferir montos entre reserva y circulación, especialmente el comportamiento de `moveToCirculation` cuando el monto excede el balance de reserva.

4. **Cálculo de estadísticas:** `TransactionListStatisticsService` procesa todas las transacciones del sistema en orden cronológico. Los tests deben validar cálculo correcto de totales acumulados por tipo de transacción según descripción y manejo de casos especiales (asignación mensual, actividad, recompensa, compras, ventas/reembolsos).

5. **DTOs de respuesta únicamente:** Todos los DTOs del módulo economy son de respuesta; no hay DTOs de request porque los controllers no reciben payloads JSON (excepto `WalletAmountAssingController` que recibe un ID por path variable). Los tests de DTOs deben validar construcción correcta y serialización a JSON.

6. **Servicios no implementados:** `InversionTransactionService` lanza `UnsupportedOperationException` porque el método `execute` no está implementado. El test debe validar este comportamiento.

7. **Endpoints de prueba:** `WalletAmountAssingController` es un endpoint de prueba (`/wallet/test/{id}`) que genera una transacción de recompensa fija (25000.0) para un estudiante. Este endpoint está restringido a `ROLE_DEV` y no debe estar en producción. El test debe validar este comportamiento.

