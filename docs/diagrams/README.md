# Diagrams Documentation

This directory contains Mermaid diagrams documenting various features and flows of the Play2Learn backend system.

## Available Diagrams

### Project Entities & Enums

- **Diagrama de Clases Global**: [`project-entities-class-diagram.md`](./project-entities-class-diagram.md)
  - Incluye todas las entidades JPA y enumeraciones del backend Play2Learn.
  - Muestra herencias, asociaciones y dependencias entre dominios (actividades, administración, economía, inversiones, beneficios y perfiles).
  - Mantiene visibles las enumeraciones auxiliares, incluso cuando solo se utilizan a nivel lógico.

### Fixed Term Deposit Register Flow

Documentación completa del flujo de registro de plazo fijo (`cu92registerFixedTermDeposit`):

- **Diagrama de Secuencia**: [`fixed-term-deposit-register-sequence.md`](./fixed-term-deposit-register-sequence.md)
  - Flujo completo desde `FixedTermDepositRegisterController` hasta la persistencia
  - Incluye todas las capas: Controller → Service → Repository → Mapper → Strategy
  - Muestra la estrategia de transacción `FixedTermDepositTransactionService` (Strategy Pattern)
  - Incluye actualización de wallet y cálculo de saldo invertido

- **Entidades**:
  - [`fixed-term-deposit-register-entities/FixedTermDeposit.md`](./fixed-term-deposit-register-entities/FixedTermDeposit.md)
  - [`fixed-term-deposit-register-entities/Wallet.md`](./fixed-term-deposit-register-entities/Wallet.md)
  - [`fixed-term-deposit-register-entities/Student.md`](./fixed-term-deposit-register-entities/Student.md)
  - [`fixed-term-deposit-register-entities/Transaction.md`](./fixed-term-deposit-register-entities/Transaction.md)
  - [`fixed-term-deposit-register-entities/Reserve.md`](./fixed-term-deposit-register-entities/Reserve.md)

- **DTOs**:
  - [`fixed-term-deposit-register-dtos/FixedTermDepositRegisterRequestDto.md`](./fixed-term-deposit-register-dtos/FixedTermDepositRegisterRequestDto.md)
  - [`fixed-term-deposit-register-dtos/FixedTermDepositResponseDto.md`](./fixed-term-deposit-register-dtos/FixedTermDepositResponseDto.md)

### Fixed Term Deposit Automatic Ends Flow

Documentación completa del flujo de finalización automática de plazos fijos (`cu95fixedTermDepositAutomaticEnds`):

- **Diagrama de Secuencia**: [`fixed-term-deposit-automatic-ends-sequence.md`](./fixed-term-deposit-automatic-ends-sequence.md)
  - Flujo automático ejecutado diariamente a las 1:30 AM mediante `@Scheduled`
  - Incluye todas las capas: Service → Repository → Strategy → Transaction
  - Muestra la verificación de plazos fijos vencidos y cambio de estado a FINISHED
  - Incluye la generación de transacciones mediante estrategia `FixedTermDepositTransactionService` (Strategy Pattern)
  - Muestra el retorno del capital invertido al wallet del estudiante
  - Incluye la actualización del saldo invertido del wallet mediante recálculo completo
  - Documenta el movimiento de fondos desde reserva a circulación

- **Entidades**:
  - [`fixed-term-deposit-automatic-ends-entities/FixedTermDeposit.md`](./fixed-term-deposit-automatic-ends-entities/FixedTermDeposit.md)
  - [`fixed-term-deposit-automatic-ends-entities/Wallet.md`](./fixed-term-deposit-automatic-ends-entities/Wallet.md)
  - [`fixed-term-deposit-automatic-ends-entities/Transaction.md`](./fixed-term-deposit-automatic-ends-entities/Transaction.md)
  - [`fixed-term-deposit-automatic-ends-entities/Reserve.md`](./fixed-term-deposit-automatic-ends-entities/Reserve.md)

**Nota**: Este flujo no utiliza DTOs ya que se ejecuta automáticamente mediante scheduler y no recibe parámetros externos ni retorna respuestas HTTP.

### Saving Account Register Flow

Documentación completa del flujo de registro de caja de ahorro (`cu102registerSavingAccount`):

- **Diagrama de Secuencia**: [`saving-account-register-sequence.md`](./saving-account-register-sequence.md)
  - Endpoint `POST /investment/saving-accounts` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Resuelve el usuario autenticado mediante `SessionUserArgumentResolver` y obtiene el `Wallet` asociado al estudiante
  - Valida saldo disponible y unicidad del nombre antes de persistir la cuenta con `SavingAccountRepository.save`
  - Genera la transacción de depósito utilizando el Strategy Pattern (`DepositSavingAccountTransactionService`) y ajusta reserva, wallet y transacción
  - Recalcula el saldo invertido del wallet con `WalletUpdateInvestedBalanceService`, agregando montos de acciones, plazos fijos y cajas de ahorro
  - Finaliza con `ResponseFactory.created`, retornando `SavingAccountResponseDto` y el mensaje `SuccessfulMessages.createdSuccessfully("Caja de ahorro")`

### Saving Account Deposit Flow

Documentación completa del flujo de depósito en caja de ahorro (`cu103depositSavingAccount`):

- **Diagrama de Secuencia**: [`saving-account-deposit-sequence.md`](./saving-account-deposit-sequence.md)
  - Endpoint `POST /investment/saving-accounts/deposit` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Resuelve el usuario autenticado con `SessionUserArgumentResolver`, obtiene el `Wallet` del estudiante y valida la pertenencia de la caja de ahorro
  - Verifica saldo disponible, incrementa el monto actual de la caja y persiste el cambio con `SavingAccountRepository.save`
  - Genera la transacción `INGRESO_CAJA_AHORRO` mediante `TransactionGenerateService` y la estrategia `DepositSavingAccountTransactionService`, ajustando wallet y reserva
  - Recalcula el saldo invertido del wallet con `WalletUpdateInvestedBalanceService` y responde con `ResponseFactory.created` y el mensaje `SuccessfulMessages.createdSuccessfully("Deposito en caja de ahorro")`

### Saving Account Withdrawal Flow

Documentación completa del flujo de retiro de caja de ahorro (`cu104withdrawalSavingAccount`):

- **Diagrama de Secuencia**: [`saving-account-withdrawal-sequence.md`](./saving-account-withdrawal-sequence.md)
  - Endpoint `POST /investment/saving-accounts/withdrawal` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Resuelve el usuario autenticado y valida la pertenencia de la caja de ahorro antes de continuar
  - Genera la transacción `RETIRO_CAJA_AHORRO` mediante `TransactionGenerateService` y la estrategia `WithdrawalSavingAccountTransactionService`, acreditando el wallet y moviendo fondos a circulación
  - Recalcula el saldo invertido del wallet con `WalletUpdateInvestedBalanceService` sumando acciones, plazos fijos y cajas de ahorro vigentes
  - Finaliza con `ResponseFactory.created`, retornando `SavingAccountResponseDto` y el mensaje `SuccessfulMessages.createdSuccessfully("Retiro de caja de ahorro")`

### Saving Account Delete Flow

Documentación completa del flujo de eliminación de caja de ahorro (`cu105deleteSavingAccount`):

- **Diagrama de Secuencia**: [`saving-account-delete-sequence.md`](./saving-account-delete-sequence.md)
  - Endpoint `DELETE /investment/saving-accounts/{id}` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Resuelve el usuario autenticado, obtiene el `Wallet` asociado y valida que la caja de ahorro pertenezca al estudiante antes de eliminarla lógicamente (`deletedAt`)
  - Ejecuta la estrategia `WithdrawalSavingAccountTransactionService` solo si existe saldo pendiente, acreditando el wallet, ajustando la reserva y registrando la transacción `RETIRO_CAJA_AHORRO`
  - Recalcula el saldo invertido del wallet con `WalletUpdateInvestedBalanceService`, sumando inversiones en acciones, plazos fijos y cajas de ahorro activas
  - Finaliza con `ResponseFactory.noContent`, utilizando `SuccessfulMessages.createdSuccessfully("Caja de ahorro")` como mensaje convencional de confirmación

### Saving Account List Paginated Flow

Documentación completa del flujo de listado paginado de cajas de ahorro (`cu106listPaginatedSavingAccounts`):

- **Diagrama de Secuencia**: [`saving-account-list-paginated-sequence.md`](./saving-account-list-paginated-sequence.md)
  - Endpoint `GET /investment/saving-accounts/paginated` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Construye el objeto `Pageable` mediante `PaginatorUtils.buildPageable` y parte de la especificación base `SavingAccountSpecs.notDeleted()`
  - Aplica filtros dinámicos opcionales (`SavingAccountSpecs.genericFilter`) antes de consultar `ISavingAccountRepository.findAll(spec, pageable)`
  - Convierte entidades `SavingAccount` en `SavingAccountResponseDto` con `SavingAccountMapper.toDtoList`
  - Genera `PaginatedData` con `PaginationHelper.fromPage` y responde usando `ResponseFactory.paginated` junto a `SuccessfulMessages.okSuccessfully()`

### Saving Account Update Flow

Documentación completa del flujo de actualización diaria de cajas de ahorro (`cu107updateSavingAccounts`):

- **Diagrama de Secuencia**: [`saving-account-update-sequence.md`](./saving-account-update-sequence.md)
  - Scheduler de Spring Boot dispara `SavingAccountUpdateService` todos los días a la 1:35 AM (`@Scheduled(cron = "0 35 1 * * *")`)
  - Obtiene todas las cajas de ahorro activas con `ISavingAccountRepository.findAllByDeletedAtIsNull` y evalúa si requieren actualización según `lastUpdate`
  - Calcula el interés diario (0.1%), acumula el monto en `accumulatedInterest`, incrementa `currentAmount` y actualiza `lastUpdate` antes de persistir con `ISavingAccountRepository.save`
  - Recalcula el saldo invertido del wallet asociado mediante `WalletUpdateInvestedBalanceService`, sumando inversiones en acciones, plazos fijos y cajas de ahorro vigentes
  - Documenta el recálculo interno: `InvestmentCalculateTotalInvestedService` → `StockCalculateAmountInvestedService` (con `IStockRepository` y `IOrderRepository`), `FixedTermDepositCalculateAmountInvestedService` (con `IFixedTermDepositRepository`) y `SavingAccountCalculateAmountInvestedService` (con `ISavingAccountRepository`)

### Investment Total Invested Flow

Documentación completa del flujo de cálculo del total invertido (`cu110calculateTotalInvested`):

- **Diagrama de Secuencia**: [`investment-total-invested-sequence.md`](./investment-total-invested-sequence.md)
  - Servicio principal `InvestemtCalculateTotalInvestedService` invocado desde `WalletUpdateInvestedBalanceService`
  - Detalla el cálculo del monto invertido en acciones sumando precios actuales y cantidades ejecutadas (`StockCalculateAmountInvestedService` + `StockCalculateByWalletService`)
  - Muestra la agregación de montos en plazos fijos en progreso mediante `FixedTermDepositCalculateAmountInvestedService`
  - Incluye el cálculo de fondos en cajas de ahorro disponibles con `SavingAccountCalculateAmountInvestedService`
  - Finaliza con la actualización del wallet y persistencia a través de `IWalletRepository.save`

### Fixed Term Deposit List Paginated Flow

Documentación completa del flujo de listado paginado de plazos fijos (`cu99ListPaginatedFixedTermDeposits`):

- **Diagrama de Secuencia**: [`fixed-term-deposit-list-paginated-sequence.md`](./fixed-term-deposit-list-paginated-sequence.md)
  - Endpoint `GET /investment/fixed-term-deposit/paginated` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Construcción de paginación con `PaginatorUtils.buildPageable` y especificaciones dinámicas con `FixedTermDepositSpecs`
  - Soporte para filtros por estado (`FixedTermState`), duración (`FixedTermDays`) y filtros genéricos por campo
  - Conversión de entidades `FixedTermDeposit` a `FixedTermDepositResponseDto` mediante `FixedTermDepositMapper`
  - Respuesta paginada generada con `PaginationHelper.fromPage` y `ResponseFactory.paginated`

### Stock Register Flow

Documentación completa del flujo de registro de acciones (`cu77registerStock`):

- **Diagrama de Secuencia**: [`stock-register-sequence.md`](./stock-register-sequence.md)
  - Flujo completo desde `StockRegisterController` hasta la persistencia
  - Incluye todas las capas: Controller → Service → Repository → Mapper
  - Muestra la creación de la entidad Stock y su historial inicial
  - Incluye validaciones y manejo de transacciones

- **Entidades**:
  - [`stock-register-entities/Stock.md`](./stock-register-entities/Stock.md)
  - [`stock-register-entities/StockHistory.md`](./stock-register-entities/StockHistory.md)

- **DTOs**:
  - [`stock-register-dtos/StockRegisterRequestDto.md`](./stock-register-dtos/StockRegisterRequestDto.md)
  - [`stock-register-dtos/StockResponseDto.md`](./stock-register-dtos/StockResponseDto.md)

### Benefit Flows

Documentación de los flujos de beneficios para estudiantes:

- **Diagrama de Secuencia**: [`benefit-student-count-sequence.md`](./benefit-student-count-sequence.md)
  - Flujo `cu89CountByStudentState` desde `BenefitStudentCountController`
  - Clasifica beneficios en disponibles, comprados, uso solicitado, usados y expirados

- **Diagrama de Secuencia**: [`benefit-list-used-by-student-sequence.md`](./benefit-list-used-by-student-sequence.md)
  - Flujo `cu93ListUsedByStudent` desde `BenefitListUsedByStudentController`
  - Lista beneficios consumidos por un estudiante con estado `USED` y devuelve `BenefitPurchasedUsedResponseDto`

- **Diagrama de Secuencia**: [`benefit-list-used-paginated-sequence.md`](./benefit-list-used-paginated-sequence.md)
  - Flujo `cu109ListUsedPaginated` desde `BenefitListUsedPaginatedController`
  - Valida JWT, resuelve el usuario estudiante y obtiene el `Student` con `StudentGetByEmailService`
  - Construye el `Pageable` con `PaginatorUtils.buildPageable`, arma la especificación combinando filtros (`BenefitPurchasesSpecs`) y consulta `IBenefitPurchasePaginatedRepository`
  - Mapea los resultados a `BenefitPurchasedUsedResponseDto`, genera `PaginatedData` con `PaginationHelper` y responde vía `ResponseFactory.paginated` con `SuccessfulMessages.okSuccessfully()`

- **Diagrama de Secuencia**: [`benefit-list-purchases-sequence.md`](./benefit-list-purchases-sequence.md)
  - Flujo `cu98ListPurchasesByBenefitId` desde `BenefitListPurchasesController`
  - Valida JWT y resuelve el usuario docente antes de listar compras asociadas a un beneficio
  - Verifica pertenencia del beneficio al docente y devuelve `BenefitPurchaseSimpleResponseDto` vía `ResponseFactory.ok`

- **Diagrama de Secuencia**: [`benefit-list-purchases-paginated-sequence.md`](./benefit-list-purchases-paginated-sequence.md)
  - Flujo `cu101ListPurchasesPaginated` desde `BenefitListPurchasesPaginatedController`
  - Valida JWT y resuelve el usuario docente, garantizando que el beneficio pertenezca al profesor autenticado
  - Construye la paginación con `PaginatorUtils.buildPageable`, aplica filtros dinámicos mediante `BenefitPurchasesSpecs` y consulta `IBenefitPurchasePaginatedRepository`
  - Mapea los resultados a `BenefitPurchaseSimpleResponseDto`, arma `PaginatedData` con `PaginationHelper` y responde con `ResponseFactory.paginated`

- **Diagrama de Secuencia**: [`benefit-list-use-requested-paginated-sequence.md`](./benefit-list-use-requested-paginated-sequence.md)
  - Flujo `cu108ListUseRequestedPaginated` desde `BenefitListUseRequestedPaginatedController`
  - Valida JWT y resuelve el usuario docente, obteniendo el `Teacher` mediante `TeacherGetByEmailService`
  - Construye el `Pageable` con `PaginatorUtils`, arma la especificación con `BenefitPurchasesSpecs` filtrando por docente, estado `USE_REQUESTED`, búsqueda y filtros dinámicos
  - Consulta `IBenefitPurchasePaginatedRepository.findAll(spec, pageable)`, mapea los resultados con `BenefitPurchaseMapper.toSimpleDtoList`, genera `PaginatedData` con `PaginationHelper` y responde con `ResponseFactory.paginated`

- **Diagrama de Secuencia**: [`benefit-delete-sequence.md`](./benefit-delete-sequence.md)
  - Flujo `cu94DeleteBenefit` desde `BenefitDeleteController`
  - Valida pertenencia del beneficio al docente, ejecuta reembolsos mediante la estrategia `ReembolsoTransactionService` y marca el beneficio como eliminado

### Stock Update Flow

Documentación completa del flujo de actualización de stocks (`cu78updateStock`):

- **Diagrama de Secuencia**: [`stock-update-sequence.md`](./stock-update-sequence.md)
  - Flujo completo que se ejecuta automáticamente mediante scheduler (todos los días a la 1:00 AM)
  - Incluye todas las capas: Service → Repository → Mapper → Strategy
  - Muestra el cálculo de variación de precios basado en riesgo, tendencia y ventas recientes
  - Incluye la actualización de precios con límites superior e inferior
  - Muestra la ejecución de órdenes stop (pérdida/ganancia) pendientes
  - Incluye la generación de transacciones y actualización de wallets
  - Muestra el movimiento de acciones entre estados (vendidas ↔ disponibles)
  - Incluye la actualización del saldo invertido de los wallets

- **Entidades**:
  - [`stock-update-entities/Stock.md`](./stock-update-entities/Stock.md)
  - [`stock-update-entities/StockHistory.md`](./stock-update-entities/StockHistory.md)
  - [`stock-update-entities/Order.md`](./stock-update-entities/Order.md)
  - [`stock-update-entities/Wallet.md`](./stock-update-entities/Wallet.md)
  - [`stock-update-entities/Transaction.md`](./stock-update-entities/Transaction.md)
  - [`stock-update-entities/Reserve.md`](./stock-update-entities/Reserve.md)

**Nota**: Este flujo no utiliza DTOs ya que se ejecuta automáticamente mediante scheduler y no recibe parámetros externos ni retorna respuestas.

### Stock History List Flow

Documentación completa del flujo de listado de historial de acciones (`cu79getStockHistories`):

- **Diagrama de Secuencia**: [`stock-history-list-sequence.md`](./stock-history-list-sequence.md)
  - Flujo completo desde `StockHistoryListByStockController` hasta la respuesta
  - Incluye todas las capas: Controller → Service → Repository → Mapper
  - Muestra la búsqueda de la acción por ID y validación de existencia
  - Incluye la búsqueda del historial ordenado por fecha de creación ascendente
  - Muestra la conversión de entidades a DTOs y construcción de la respuesta HTTP

- **Entidades**:
  - [`stock-history-list-entities/Stock.md`](./stock-history-list-entities/Stock.md)
  - [`stock-history-list-entities/StockHistory.md`](./stock-history-list-entities/StockHistory.md)

- **DTOs**:
  - [`stock-history-list-dtos/StockHistoryResponseDto.md`](./stock-history-list-dtos/StockHistoryResponseDto.md)

### Stock Candlestick Get Flow

Documentación completa del flujo de obtención de valores candlestick de una acción (`cu83GetValuesCandleStick`):

- **Diagrama de Secuencia**: [`stock-candlestick-get-sequence.md`](./stock-candlestick-get-sequence.md)
  - Flujo completo desde `StockCandleStickGetController` hasta la respuesta
  - Incluye todas las capas: Controller → Service → Repository → Mapper
  - Muestra la búsqueda de la acción por ID y validación de existencia
  - Incluye el cálculo del rango de fechas según el `RangeValue` proporcionado
  - Muestra la búsqueda del historial según el rango (completo o por rango de fechas)
  - Incluye el procesamiento de cada registro histórico para calcular los valores candlestick (open, close, high, low)
  - Muestra la conversión de valores a DTOs y construcción de la respuesta HTTP
  - Soporta múltiples rangos: DIARIO, SEMANAL, QUINZENAL, MENSUAL, HISTORICO

### Stock Get Flow

Documentación completa del flujo de obtención de una acción por ID (`cu100GetStock`):

- **Diagrama de Secuencia**: [`stock-get-sequence.md`](./stock-get-sequence.md)
  - Endpoint `GET /investment/stocks/{id}` protegido con `@SessionRequired(ROLE_STUDENT)` y validado por `JwtSessionAspect`
  - Resuelve el usuario de sesión mediante `SessionUserArgumentResolver` y `IUserGetByEmailService`
  - Obtiene el estudiante asociado para acceder a su `Wallet`
  - Calcula la cantidad de acciones ejecutadas por wallet (`StockCalculateByWalletService`) y la lista de órdenes pendientes (`OrderFindPendingService`)
  - Construye el `StockResponseDto` mediante `StockMapper` y responde con `ResponseFactory.ok`

**Nota**: Este flujo no incluye diagramas de entidades ni DTOs ya que solo se requiere el diagrama de secuencia.

### Stock List Flow

Documentación completa del flujo de listado de acciones (`cu86listStocks`):

- **Diagrama de Secuencia**: [`stock-list-sequence.md`](./stock-list-sequence.md)
  - Flujo completo desde `StockListController` hasta la respuesta
  - Incluye todas las capas: Interceptor (JwtSessionAspect) → Controller → Service → Repository → Mapper
  - Muestra la validación completa de sesión y permisos mediante JWT
  - Incluye la validación del token (expiración, firma, usuario activo, roles)
  - Muestra la obtención de todas las acciones mediante `findAll()`
  - Incluye la conversión de entidades a DTOs y construcción de la respuesta HTTP
  - Soporta múltiples roles: ROLE_DEV, ROLE_ADMIN, ROLE_STUDENT, ROLE_TEACHER

**Nota**: Este flujo no incluye diagramas de entidades ni DTOs ya que solo se requiere el diagrama de secuencia.

### Stock List Paginated Flow

Documentación completa del flujo de listado paginado de acciones (`cu87ListPaginatedStock`):

- **Diagrama de Secuencia**: [`stock-list-paginated-sequence.md`](./stock-list-paginated-sequence.md)
  - Flujo completo desde `StockListPaginatedController` hasta la respuesta
  - Incluye todas las capas: Interceptor (JwtSessionAspect) → Controller → Service → Repository → Mapper
  - Muestra la validación completa de sesión y permisos mediante JWT
  - Incluye la validación del token (expiración, firma, usuario activo, roles)
  - Muestra la construcción del objeto `Pageable` mediante `PaginatorUtils.buildPageable`
  - Incluye la construcción de especificaciones JPA (`Specification<Stock>`) mediante `StockSpecs`
  - Muestra la búsqueda paginada con filtros dinámicos mediante `findAll(spec, pageable)`
  - Incluye la conversión de entidades a DTOs y construcción del objeto `PaginatedData`
  - Muestra la construcción de la respuesta HTTP 200 OK con datos paginados
  - Soporta múltiples roles: ROLE_DEV, ROLE_ADMIN, ROLE_STUDENT, ROLE_TEACHER
  - Permite búsqueda por texto (campo `name`) y filtros genéricos por cualquier campo
  - Soporta ordenamiento y paginación configurables

**Nota**: Este flujo no incluye diagramas de entidades ni DTOs ya que solo se requiere el diagrama de secuencia.

### Stock Buy Flow

Documentación completa del flujo de compra de acciones (`cu84buystocks`) desde `StockBuyController`:

- **Diagrama de Secuencia**: [`stock-buy-sequence.md`](./stock-buy-sequence.md)
  - Incluye validación de sesión y resolución del usuario mediante `JwtSessionAspect` y `SessionUserArgumentResolver`
  - Detalla la lógica de negocio en `StockBuyService`, incluyendo validaciones de disponibilidad y saldo
  - Muestra la creación de órdenes con `OrderMapper` y persistencia en `IOrderRepository`
  - Describe la generación de transacciones con `TransactionGenerateService` y la estrategia `StockTransactionService` (Strategy Pattern)
  - Incluye la actualización del stock (`StockMoveService`, `StockUpdateSpecificService`) y ejecución de órdenes stop
  - Documenta la actualización del balance invertido del wallet mediante `WalletUpdateInvestedBalanceService`
  - Termina con la construcción de la respuesta HTTP 201 Created vía `ResponseFactory.created`

### Stock Stop Register Flow

Documentación completa del flujo de registro de órdenes stop de acciones (`cu91registerStopStock`) desde `StockRegisterStopController`:

- **Diagrama de Secuencia**: [`stock-register-stop-sequence.md`](./stock-register-stop-sequence.md)
  - Incluye la validación de sesión con `JwtSessionAspect` y la resolución de usuario mediante `SessionUserArgumentResolver`
  - Detalla la validación de cantidad disponible mediante `StockCalculateByWalletService` y el cálculo de historial de órdenes ejecutadas
  - Muestra la construcción de la orden stop con `OrderMapper.toStopEntity` y su persistencia en `IOrderRepository` con estado `PENDIENTE`
  - Describe la conversión de la orden a `StockBuyResponseDto` y la respuesta HTTP 201 Created generada por `ResponseFactory.created`

### Admin Panel Diagrams

Documentación del Admin Panel for Agents Management feature (Vue 3 + Vuetify):

## Diagram Types and Conventions

### Spring Boot Backend Diagrams

#### Sequence Diagrams
- **Actor**: External user or system (estudiante, admin, etc.)
- **Controller (Boundary)**: Entry point of a request (blue-colored)
- **Service (Control)**: Business logic handlers (green-colored)
- **Repository**: Persistence layer (orange-colored)
- **Entity/DTO**: Domain or data transfer objects (gray-colored)
- **Mapper**: Object transformation layer (purple-colored)
- **Strategy Implementation**: Classes following the Strategy Pattern (red-colored)

#### Entity Diagrams
- Each entity has its own `.md` file
- Shows only the class and its attributes (no relationships)
- Uses `erDiagram` Mermaid syntax

#### DTO Diagrams
- Each DTO has its own `.md` file
- Shows only the DTO class and its attributes
- Uses `classDiagram` Mermaid syntax

### Frontend Diagrams (Admin Panel)

#### Color Coding
- 🔵 **Primary**: Main application components (Router, Views)
- 🟢 **Secondary**: Core components (Lists, Forms, Panels)
- 🟠 **External**: External dependencies (Vuetify, APIs)
- 🔴 **Validation**: Validation and error handling
- 🟣 **State**: State management and persistence

### Node Types
- **Rectangles**: Components, views, composables
- **Diamonds**: Decision points, validation gates
- **Cylinders**: Data storage, files, APIs
- **Rounded rectangles**: Processes, operations

### Flow Types
- **Solid arrows**: Primary data flow or component calls
- **Dashed arrows**: Optional flows, reactive updates, or indirect relationships
- **Colored arrows**: Specific relationship types (validation, events, etc.)

## How to Use These Diagrams

### For Development
1. **Architecture diagram**: Plan new features and understand system boundaries
2. **Component interaction**: Design new components and their relationships
3. **Data flow**: Implement state management and data transformations
4. **Sequence diagrams**: Debug complex interactions and workflows
5. **State diagrams**: Handle edge cases and error states

### For Code Review
- Verify implementation matches architectural decisions
- Check component communication patterns
- Validate state management approaches
- Ensure error handling coverage

### For Testing
- Use sequence diagrams to design integration tests
- Use state diagrams to identify test scenarios
- Use data flow diagrams to verify data transformations

## Updating Diagrams

### When to Update
- **New components added**: Update architecture and component interaction diagrams
- **Workflow changes**: Update sequence and data flow diagrams
- **State changes**: Update state diagrams
- **API changes**: Update data flow and sequence diagrams

### How to Update
1. Edit the corresponding `.md` file
2. Use Mermaid syntax for diagram creation
3. Follow established color and naming conventions
4. Test diagram rendering in a Mermaid-compatible viewer
5. Update this README if new diagrams are added

## Related Documentation

- [`../feature_brief.md`](../feature_brief.md) - Feature requirements and scope
- [`../tasks.json`](../tasks.json) - Implementation task breakdown
- [`../clarification_notes.md`](../clarification_notes.md) - User requirements and assumptions
- [`../active_context.md`](../active_context.md) - Current project status

## Tools and Viewers

- **VS Code**: Install "Mermaid Preview" extension
- **GitHub**: Automatic rendering in Markdown files
- **Online**: mermaid.live editor
- **Docs**: mermaid.js.org documentation

## Implementation Status

This documentation was created during the planning phase (Phase 1) of the admin panel development. Diagrams should be updated as implementation progresses to reflect actual code structure and behaviors.

---

*Generated for Admin Panel for Agents Management feature - Vue 3 + Vuetify*
