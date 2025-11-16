# Auditoría de pruebas — Módulo Economy

## Resumen Ejecutivo
- **Alcance:** controllers, services (incluye commons y strategy) y DTOs del módulo `economy`, excluyendo models, repositories, mappers y specs según pauta.
- **Submódulos:** `reserve`, `transaction`, `wallet`.
- **Responsabilidades principales:** generación de transacciones por estrategia, gestión de wallets de estudiantes, gestión de reservas del sistema, cálculo de estadísticas de transacciones, movimiento de fondos entre reserva y circulación, y exposición de DTOs específicos por contexto.
- **Validaciones clave:** validación de montos positivos, validación de balances suficientes (wallet, reserva, materia, actividad, caja de ahorro), validación de tipos de transacción soportados, validación de límites de actividad (30% del balance inicial), validación de estados de inversiones (plazo fijo, acciones), validación de asignación correcta de montos y resguardo de endpoints mediante `@SessionRequired` con roles específicos.

## Inventario de Controllers

| Controller | Endpoint(s) | Roles requeridos | Service(s) consumidos | Validaciones / Notas |
|------------|-------------|------------------|------------------------|----------------------|
| `WalletGetController` | `GET /wallet` | `ROLE_STUDENT` | `IWalletGetService` | `@SessionUser User`; obtiene wallet completo del estudiante con últimas transacciones; retorna `WalletCompleteResponseDto`. |
| `WalletAmountAssingController` | `POST /wallet/test/{id}` | `ROLE_DEV` | `ITransactionGenerateService`, `IStudentGetByIdService`, `ISubjectGetByIdService` | Path `id` (estudiante); endpoint de prueba que genera transacción `RECOMPENSA` fija (25000.0) para el estudiante; retorna 204 No Content. |
| `WalletGetLastTransactionsController` | `GET /wallet/last-transactions` | `ROLE_STUDENT` | `IWalletGetLastTransactionsService` | `@SessionUser User`; obtiene últimas 10 transacciones del estudiante; retorna `List<TransactionResponseDto>`. |
| `TransactionStatisticsController` | `GET /transactions` | `ROLE_DEV` | `ITransactionListStatisticsService` | Devuelve estadísticas de todas las transacciones del sistema; calcula totales por fecha (total emitido, circulación, reserva, materia, estudiante, actividad); retorna `List<TransactionStatisticsResponseDto>`. |

## Inventario de Services Principales

| Service | Método(s) clave | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|-----------------|--------------------------|---------------------------------------|
| `TransactionGenerateService` | `generate` | `Map<String, ITransactionStrategyService>` | Valida que `amount > 0`; lanza `ConflictException` si `amount <= 0`; valida que tipo de transacción esté soportado; lanza `ConflictException` si estrategia no existe; delega ejecución a estrategia correspondiente según `TypeTransaction`; retorna `Transaction` creada. |
| `TransactionListStatisticsService` | `execute` | `ITransactionRepository` | Procesa todas las transacciones ordenadas por fecha ascendente; calcula totales acumulados por tipo de transacción según descripción; maneja casos especiales: asignación mensual (toma de reserva o emite nuevas), actividad de materia, recompensa por actividad, compras (beneficio, aspecto, acciones, plazo fijo), ventas/reembolsos; retorna `List<TransactionStatisticsResponseDto>` con snapshots por fecha. |
| `WalletGetService` | `cu70GetWallet` | `IStudentGetByEmailService`, `ITransactionGetLastTransaccionsService` | Obtiene estudiante por email; obtiene últimas transacciones del wallet; mapea wallet y transacciones a `WalletCompleteResponseDto`; retorna DTO completo. |
| `WalletAddAmountService` | `execute` | `IWalletRepository` | Valida que `amount > 0`; lanza `IllegalArgumentException` si `amount <= 0`; incrementa balance del wallet; persiste wallet actualizado; retorna wallet modificado. |
| `WalletRemoveAmountService` | `execute` | `IWalletRepository` | Valida que wallet tenga balance suficiente (`wallet.getBalance() >= amount`); lanza `IllegalArgumentException` si no hay suficiente dinero; decrementa balance del wallet; persiste wallet actualizado; retorna wallet modificado. |
| `WalletUpdateInvestedBalanceService` | `execute` | `IWalletRepository`, `IInvestmentCalculateTotalInvestedService` | Calcula balance invertido total mediante `investmentCalculateTotalInvestedService.cu110calculateTotalInvested`; actualiza `invertedBalance` del wallet; persiste wallet actualizado; método void. |
| `WalletGetLastTransactionsService` | `cu65GetLastTransactions` | `IStudentGetByEmailService`, `ITransactionGetLastTransaccionsService` | Obtiene estudiante por email; obtiene últimas transacciones del wallet; mapea transacciones a `List<TransactionResponseDto>`; retorna lista de DTOs. |
| `ReserveModifyService` | `moveToReserve`, `moveToCirculation` | `IReserveRepository` | `moveToReserve`: valida que `amount > 0`; valida que `amount <= reserve.getCirculationBalance()`; lanza `BadRequestException` si no hay suficiente dinero en circulación; transfiere de circulación a reserva; actualiza `lastUpdateAt`; retorna reserva modificada. `moveToCirculation`: valida que `amount > 0`; si `amount > reserve.getReserveBalance()`, ajusta reserva creando la diferencia faltante (permite valores negativos temporales); transfiere de reserva a circulación; actualiza `lastUpdateAt`; retorna reserva modificada. |

## Inventario de Services de Estrategia de Transacciones

| Service | Tipo de Transacción | Dependencias principales | Validaciones / Excepciones relevantes |
|---------|---------------------|--------------------------|---------------------------------------|
| `CompraTransactionService` | `COMPRA` | `ITransactionRepository`, `IWalletRemoveAmountService`, `IReserveModifyService`, `IReserveFindLastService` | Valida que wallet tenga balance suficiente (`wallet.getBalance() >= amount`); lanza `IllegalArgumentException` si no hay suficiente dinero; obtiene última reserva; crea transacción; resta monto del wallet; mueve monto a reserva; retorna transacción guardada. |
| `RecompensaTransactionService` | `RECOMPENSA` | `ITransactionRepository`, `IWalletAddAmountService`, `IActivityRemoveBalanceService`, `IReserveFindLastService` | Valida que actividad tenga balance suficiente (`activity.getActualBalance() >= amount`); lanza `ConflictException` si no hay suficiente dinero en la materia; obtiene última reserva; crea transacción; resta monto de actividad; suma monto al wallet; retorna transacción guardada. |
| `AsignacionTransactionService` | `ASIGNACION` | `IReserveFindLastService`, `ISubjectAddBalanceService`, `ITransactionRepository`, `IReserveModifyService` | Valida que `assignAmount` (diferencia entre `initialBalance` y `actualBalance` de la materia) sea igual a `amount`; lanza `UnsupportedOperationException` si no coincide; obtiene última reserva; si reserva no tiene suficiente balance, lo ajusta creando la diferencia faltante; crea transacción; suma balance a materia; mueve monto de reserva a circulación; retorna transacción guardada. |
| `ReembolsoTransactionService` | `REEMBOLSO` | `IReserveFindLastService`, `ITransactionRepository`, `IWalletAddAmountService`, `IReserveModifyService` | Obtiene última reserva; crea transacción asociada a beneficio; guarda transacción; suma monto al wallet; mueve monto de reserva a circulación; retorna transacción guardada. |
| `ActividadTransactionService` | `ACTIVIDAD` | `ITransactionRepository`, `ISubjectRemoveBalanceService`, `IReserveFindLastService`, `IActivityAddBalanceService` | Valida que `amount <= 0.3 * subject.getInitialBalance()` (30% del balance inicial); lanza `ConflictException` si excede límite; valida que `amount <= subject.getActualBalance()`; lanza `ConflictException` si materia no tiene balance suficiente; obtiene última reserva; crea transacción; resta balance de materia; suma balance a actividad; retorna transacción guardada. |
| `DepositSavingAccountTransactionService` | `INGRESO_CAJA_AHORRO` | `ITransactionRepository`, `IWalletRemoveAmountService`, `IReserveModifyService`, `IReserveFindLastService` | Valida que `amount > 0`; lanza `BadRequestException` si `amount <= 0`; valida que `amount <= wallet.getBalance()`; lanza `BadRequestException` si wallet no tiene balance suficiente; obtiene última reserva; crea transacción asociada a caja de ahorro; guarda transacción; resta monto del wallet; mueve monto a reserva; retorna transacción guardada. |
| `WithdrawalSavingAccountTransactionService` | `RETIRO_CAJA_AHORRO` | `ITransactionRepository`, `IReserveModifyService`, `IReserveFindLastService`, `IWalletAddAmountService` | Valida que `amount > 0`; lanza `BadRequestException` si `amount <= 0`; valida que `amount <= savingAccount.getCurrentAmount()`; lanza `BadRequestException` si caja de ahorro no tiene saldo suficiente; obtiene última reserva; crea transacción asociada a caja de ahorro; guarda transacción; suma monto al wallet; mueve monto de reserva a circulación; retorna transacción guardada. |
| `StockTransactionService` | `STOCK` | `ITransactionRepository`, `IWalletRemoveAmountService`, `IReserveModifyService`, `IReserveFindLastService`, `IWalletAddAmountService` | Si `order.getOrderType() == OrderType.COMPRA`: valida que `amount <= wallet.getBalance()`; lanza `BadRequestException` si wallet no tiene balance suficiente; crea transacción asociada a orden de compra; resta monto del wallet; mueve monto a reserva; retorna transacción guardada. Si `order.getOrderType() == OrderType.VENTA`: crea transacción asociada a orden de venta; suma monto al wallet; mueve monto de reserva a circulación; retorna transacción guardada. |
| `FixedTermDepositTransactionService` | `PLAZO_FIJO` | `ITransactionRepository`, `IWalletRemoveAmountService`, `IReserveModifyService`, `IReserveFindLastService`, `IWalletAddAmountService` | Si `fixedTermDeposit.getFixedTermState() == FixedTermState.IN_PROGRESS`: valida que `amount <= wallet.getBalance()`; lanza `BadRequestException` si wallet no tiene balance suficiente; crea transacción asociada a plazo fijo; resta monto del wallet; mueve monto a reserva; retorna transacción guardada. Si estado es finalizado: crea transacción asociada a plazo fijo; suma monto al wallet; mueve monto de reserva a circulación; retorna transacción guardada. |
| `InversionTransactionService` | `INVERSION` | - | Implementación no completada; lanza `UnsupportedOperationException` con mensaje "Unimplemented method 'execute'"; requiere implementación futura. |

## Servicios Commons

| Service | Responsabilidad | Dependencias | Notas de validación |
|---------|-----------------|--------------|---------------------|
| `TransactionGetLastTransactionsService` | Obtener últimas 10 transacciones de un wallet | `ITransactionRepository` | Método `execute(Wallet wallet)` busca últimas 10 transacciones ordenadas por fecha descendente; retorna `List<Transaction>`. |
| `ReserveFindLastService` | Buscar última reserva creada | `IReserveRepository` | Método `get()` busca primera reserva ordenada por fecha de creación descendente; lanza `NotFoundException` si no encuentra reserva. |

## DTOs y Validaciones

| DTO | Campos relevantes | Restricciones |
|-----|-------------------|---------------|
| `WalletResponseDto` | `id`, `balance`, `invertedBalance`, `totalBalance` | Sin Bean Validation; usado como salida; representa wallet básico. |
| `WalletCompleteResponseDto` | `id`, `balance`, `invertedBalance`, `totalBalance`, `transactions` | Sin Bean Validation; usado como salida; incluye lista de `TransactionResponseDto` con últimas transacciones. |
| `TransactionResponseDto` | `amount`, `createdAt`, `description`, `type` | Sin Bean Validation; usado como salida; representa transacción básica. |
| `TransactionStatisticsResponseDto` | `date`, `total`, `totalCirculation`, `totalReserve`, `totalSubject`, `totalStudent`, `totalActivity` | Sin Bean Validation; usado como salida para estadísticas; incluye snapshot de totales por fecha. |

## Dependencias Externas Principales

### Módulo Admin
- `IStudentGetByEmailService`: Obtener estudiante por email
- `IStudentGetByIdService`: Obtener estudiante por ID
- `ISubjectGetByIdService`: Obtener materia por ID
- `ISubjectAddBalanceService`: Agregar balance a materia
- `ISubjectRemoveBalanceService`: Remover balance de materia

### Módulo Activity
- `IActivityAddBalanceService`: Agregar balance a actividad
- `IActivityRemoveBalanceService`: Remover balance de actividad

### Módulo Investment
- `IInvestmentCalculateTotalInvestedService`: Calcular total invertido (`cu110calculateTotalInvested`)

### Módulo Economy (interno)
- `IWalletAddAmountService`: Agregar monto al wallet
- `IWalletRemoveAmountService`: Remover monto del wallet
- `IReserveModifyService`: Modificar reserva (mover a reserva/circulación)
- `IReserveFindLastService`: Obtener última reserva

### Models de otros módulos
- `Activity`: Modelo de actividad
- `Subject`: Modelo de materia
- `Benefit`: Modelo de beneficio
- `Order`: Modelo de orden de acciones
- `FixedTermDeposit`: Modelo de plazo fijo
- `SavingAccount`: Modelo de caja de ahorro
- `Wallet`: Modelo de wallet
- `Reserve`: Modelo de reserva

### Utils y Config
- `EconomyMessages`: Mensajes de validación del módulo economy
- `NotFoundExceptionMesagges`: Mensajes de recursos no encontrados

## Excepciones Identificadas

| Excepción | Contexto | Mensaje |
|-----------|----------|---------|
| `ConflictException` | `TransactionGenerateService` | Monto debe ser mayor a 0, tipo de transacción no soportado |
| `ConflictException` | `RecompensaTransactionService` | La materia no tiene suficiente dinero para realizar la transacción |
| `ConflictException` | `ActividadTransactionService` | Monto de actividad excede 30% del balance inicial, materia no cuenta con balance suficiente |
| `BadRequestException` | `ReserveModifyService` | Monto debe ser mayor a 0, no hay suficiente dinero en la circulación/reserva |
| `BadRequestException` | `DepositSavingAccountTransactionService` | Monto debe ser mayor a 0, wallet no cuenta con balance suficiente |
| `BadRequestException` | `WithdrawalSavingAccountTransactionService` | Monto debe ser mayor a 0, monto es mayor al saldo actual de la caja de ahorro |
| `BadRequestException` | `FixedTermDepositTransactionService` | Wallet no cuenta con balance suficiente para realizar el plazo fijo |
| `BadRequestException` | `StockTransactionService` | Wallet no cuenta con balance suficiente para realizar la compra de acciones |
| `IllegalArgumentException` | `WalletAddAmountService`, `WalletRemoveAmountService`, `CompraTransactionService` | Monto debe ser mayor a 0, estudiante no tiene suficiente dinero |
| `UnsupportedOperationException` | `AsignacionTransactionService`, `InversionTransactionService` | Monto incorrecto, método no implementado |
| `NotFoundException` | `ReserveFindLastService` | Reserva no encontrada (última) |

## Riesgos de Negocio Sin Cobertura de Pruebas Actual

### Módulo Reserve
1. **Movimiento de fondos entre reserva y circulación:** Sin pruebas para validar la integridad de balances al transferir montos, especialmente el comportamiento de `moveToCirculation` cuando el monto excede el balance de reserva (ajuste automático que crea diferencia faltante).
2. **Obtención de última reserva:** Sin pruebas para validar el caso cuando no existe reserva en el sistema (debe lanzar `NotFoundException`).
3. **Validación de límites de circulación:** Sin pruebas para validar que no se puedan transferir montos negativos o superiores al balance disponible en circulación.

### Módulo Transaction
1. **Generación de transacciones por estrategia:** Sin pruebas para validar que cada tipo de transacción ejecute la estrategia correcta y que se lancen excepciones apropiadas cuando el tipo no esté soportado.
2. **Validación de montos:** Sin pruebas para validar que todos los servicios de estrategia rechacen montos menores o iguales a cero.
3. **Validación de balances:** Sin pruebas para validar que cada estrategia valide correctamente los balances necesarios antes de ejecutar (wallet, reserva, materia, actividad, caja de ahorro).
4. **Estrategias específicas:**
   - **Asignación:** Sin pruebas para validar la lógica de ajuste de reserva cuando no hay suficiente balance y la validación de montos correctos.
   - **Actividad:** Sin pruebas para validar el límite del 30% del balance inicial de la materia y la validación de balance suficiente.
   - **Compra/Recompensa/Reembolso:** Sin pruebas para validar el flujo completo de creación de transacción, actualización de wallet y movimiento de reserva.
   - **Caja de Ahorro (depósito/retiro):** Sin pruebas para validar que no se puedan depositar/retirar montos mayores al balance disponible.
   - **Plazo Fijo:** Sin pruebas para validar el comportamiento según el estado (IN_PROGRESS vs finalizado) y la validación de balance suficiente.
   - **Acciones (Stock):** Sin pruebas para validar el comportamiento según el tipo de orden (COMPRA vs VENTA) y la validación de balance suficiente.
   - **Inversión:** Sin pruebas (método no implementado, debe lanzar `UnsupportedOperationException`).
5. **Cálculo de estadísticas:** Sin pruebas para validar que `TransactionListStatisticsService` calcule correctamente los totales acumulados por tipo de transacción y maneje correctamente los casos especiales (asignación mensual, actividad, recompensa, compras, ventas/reembolsos).

### Módulo Wallet
1. **Obtención de wallet completo:** Sin pruebas para validar que se obtenga correctamente el estudiante y se mapeen correctamente las últimas transacciones.
2. **Agregar monto al wallet:** Sin pruebas para validar que se rechacen montos menores o iguales a cero y que se incremente correctamente el balance.
3. **Remover monto del wallet:** Sin pruebas para validar que se rechacen operaciones cuando el balance es insuficiente y que se decremente correctamente el balance.
4. **Actualización de balance invertido:** Sin pruebas para validar que se calcule correctamente el total invertido mediante el servicio externo y se actualice el wallet.
5. **Obtención de últimas transacciones:** Sin pruebas para validar que se obtengan correctamente las últimas 10 transacciones del estudiante y se mapeen a DTOs.

## Notas Adicionales

1. **Patrón Strategy:** El módulo transaction utiliza el patrón Strategy para manejar diferentes tipos de transacciones. Cada estrategia implementa `ITransactionStrategyService` y se registra en el Map de Spring mediante `@Service("TIPO")`. El `TransactionGenerateService` delega la ejecución a la estrategia correspondiente según el `TypeTransaction`.

2. **Transaccionalidad:** Todos los servicios de estrategia están marcados con `@Transactional` para garantizar atomicidad en las operaciones que involucran múltiples entidades (crear transacción, actualizar wallet, modificar reserva, etc.).

3. **Reserva:** El sistema mantiene una reserva central que se actualiza con cada transacción. La reserva tiene dos balances: `reserveBalance` (dinero en reserva) y `circulationBalance` (dinero en circulación). Las operaciones de compra mueven dinero a la reserva, mientras que las operaciones de reembolso/venta mueven dinero de la reserva a la circulación.

4. **Endpoint de prueba:** `WalletAmountAssingController` es un endpoint de prueba (`/wallet/test/{id}`) que genera una transacción de recompensa fija (25000.0) para un estudiante. Este endpoint está restringido a `ROLE_DEV` y no debe estar en producción.

5. **Método no implementado:** `InversionTransactionService` lanza `UnsupportedOperationException` porque el método `execute` no está implementado. Esto requiere atención para completar la funcionalidad o remover el servicio si no se utilizará.

6. **DTOs de respuesta únicamente:** Todos los DTOs del módulo economy son de respuesta; no hay DTOs de request porque los controllers no reciben payloads JSON (excepto `WalletAmountAssingController` que recibe un ID por path variable). Las validaciones se realizan a nivel de servicio mediante excepciones de negocio.

7. **Cálculo de estadísticas:** `TransactionListStatisticsService` procesa todas las transacciones del sistema en orden cronológico para calcular totales acumulados. Esto puede ser costoso en términos de rendimiento si hay muchas transacciones; se recomienda considerar optimizaciones o caché para sistemas grandes.

