# Diagrama de Secuencia: Registro de Acción (Stock)

Este diagrama muestra el flujo completo del endpoint `POST /investment/stocks`, desde el controlador `StockRegisterController` hasta todas las capas involucradas en el registro de una nueva acción en el sistema.

## Descripción del Flujo

El caso de uso `cu77registerStock` permite a un usuario con rol `ROLE_DEV` registrar una nueva acción en el sistema. El flujo incluye:

1. Validación de la sesión y permisos mediante `@SessionRequired`
2. Conversión del DTO de entrada a entidad mediante `StockMapper`
3. Persistencia de la entidad `Stock` en la base de datos
4. Creación de un registro inicial en el historial de la acción mediante `StockHistoryMapper`
5. Persistencia del historial en la base de datos
6. Conversión de la entidad persistida a DTO de respuesta
7. Retorno de la respuesta HTTP 201 Created al cliente

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor U as User (DEV)
    participant C as StockRegisterController
    participant Svc as StockRegisterService
    participant SM as StockMapper
    participant Stock as Stock
    participant Repo as IStockRepository
    participant SHM as StockHistoryMapper
    participant StockHist as StockHistory
    participant HistRepo as IStockHistoryRepository
    participant Resp as ResponseFactory

    U->>C: POST /investment/stocks<br/>body: StockRegisterRequestDto<br/>(name, abbreviation, initialPrice, totalAmount, riskLevel)
    Note right of C: @SessionRequired(ROLE_DEV)<br/>@Valid @RequestBody
    
    C->>Svc: cu77registerStock(stockDto: StockRegisterRequestDto)
    
    Svc->>SM: toModel(stockDto: StockRegisterRequestDto)
    Note right of SM: Convierte DTO a entidad<br/>- name: stockDto.getName()<br/>- abbreviation: stockDto.getAbbreviation()<br/>- initialPrice: stockDto.getInitialPrice()<br/>- currentPrice: stockDto.getInitialPrice()<br/>- availableAmount: stockDto.getTotalAmount()<br/>- totalAmount: stockDto.getTotalAmount()<br/>- soldAmount: BigInteger.ZERO<br/>- riskLevel: stockDto.getRiskLevel()
    SM->>Stock: builder()<br/>.name()<br/>.abbreviation()<br/>.initialPrice()<br/>.currentPrice()<br/>.availableAmount()<br/>.totalAmount()<br/>.soldAmount()<br/>.riskLevel()<br/>.build()
    Stock-->>SM: Stock (entidad sin id)
    SM-->>Svc: Stock
    
    Svc->>Repo: save(stock: Stock)
    Note right of Repo: @Transactional<br/>Persiste la entidad Stock
    Repo->>Stock: persist
    Stock-->>Repo: Stock (con id generado)
    Repo-->>Svc: Stock (entidad persistida)
    
    Svc->>SHM: toModel(stock: Stock, variation: 0.0)
    Note right of SHM: Crea registro inicial del historial<br/>- stock: stock<br/>- price: stock.getCurrentPrice()<br/>- availableAmount: stock.getAvailableAmount()<br/>- soldAmount: stock.getSoldAmount()<br/>- createdAt: LocalDateTime.now()<br/>- variation: 0.0
    SHM->>StockHist: builder()<br/>.stock(stock)<br/>.price(stock.getCurrentPrice())<br/>.availableAmount(stock.getAvailableAmount())<br/>.soldAmount(stock.getSoldAmount())<br/>.createdAt(LocalDateTime.now())<br/>.variation(0.0)<br/>.build()
    StockHist-->>SHM: StockHistory (entidad sin id)
    SHM-->>Svc: StockHistory
    
    Svc->>HistRepo: save(stockHistory: StockHistory)
    Note right of HistRepo: @Transactional<br/>Persiste el historial de la acción
    HistRepo->>StockHist: persist
    StockHist-->>HistRepo: StockHistory (con id generado)
    HistRepo-->>Svc: StockHistory (entidad persistida)
    
    Svc->>SM: toDto(stock: Stock, quantityBought: null, pendingOrders: null)
    Note right of SM: Convierte entidad a DTO de respuesta<br/>- id: stock.getId()<br/>- name: stock.getName()<br/>- abbreviation: stock.getAbbreviation()<br/>- totalAmount: stock.getTotalAmount()<br/>- availableAmount: stock.getAvailableAmount()<br/>- soldAmount: stock.getSoldAmount()<br/>- currentPrice: stock.getCurrentPrice()<br/>- initialPrice: stock.getInitialPrice()<br/>- riskLevel: stock.getRiskLevel()<br/>- quantityBought: BigInteger.ZERO<br/>- pendingOrders: null
    SM-->>Svc: StockResponseDto
    
    Svc-->>C: StockResponseDto
    
    C->>Resp: created(responseDto: StockResponseDto,<br/>message: SuccessfulMessages.createdSuccessfully("Accion"))
    Note right of Resp: Crea ResponseEntity con:<br/>- status: HttpStatus.CREATED (201)<br/>- body: BaseResponse<StockResponseDto><br/>- message: "Accion creada exitosamente"<br/>- timestamp: ISO_INSTANT format
    Resp-->>C: ResponseEntity<BaseResponse<StockResponseDto>>
    
    C-->>U: 201 Created<br/>BaseResponse<StockResponseDto>
    
    note over Svc: finCU()
```

## Componentes Involucrados

### Controller
- **StockRegisterController**: Punto de entrada del endpoint REST. Valida la sesión y permisos, recibe el DTO de entrada y retorna la respuesta HTTP.

### Service
- **StockRegisterService**: Contiene la lógica de negocio del caso de uso. Orquesta la creación de la acción y su historial inicial, manejando la transacción.

### Mappers
- **StockMapper**: Responsable de convertir entre DTOs y entidades de Stock.
- **StockHistoryMapper**: Responsable de convertir entre DTOs y entidades de StockHistory.

### Repositories
- **IStockRepository**: Interfaz de persistencia para la entidad Stock. Extiende `CrudRepository<Stock, Long>`.
- **IStockHistoryRepository**: Interfaz de persistencia para la entidad StockHistory. Extiende `CrudRepository<StockHistory, Long>`.

### Entities
- **Stock**: Entidad que representa una acción en el sistema. Contiene información como nombre, abreviación, precios, cantidades y nivel de riesgo.
- **StockHistory**: Entidad que representa un registro histórico de una acción. Almacena el precio, cantidades disponibles y vendidas, y la variación en un momento específico.

### DTOs
- **StockRegisterRequestDto**: DTO de entrada que contiene los datos necesarios para registrar una nueva acción.
- **StockResponseDto**: DTO de respuesta que contiene la información de la acción registrada.

## Transaccionalidad

El método `cu77registerStock` está marcado con `@Transactional`, lo que garantiza que:
- La creación de la entidad `Stock` y su historial inicial (`StockHistory`) se ejecuten en una única transacción.
- Si ocurre un error en cualquier punto del proceso, se revierte toda la operación.

## Validaciones

- **Nivel de Controller**: Validación mediante `@Valid` del DTO de entrada, verificando anotaciones como `@NotBlank`, `@NotNull`, `@Positive`, `@Size`.
- **Nivel de Service**: Validaciones de negocio (si aplican) y garantía de integridad transaccional.

## Notas Adicionales

- El precio inicial y el precio actual se establecen con el mismo valor al momento del registro.
- La cantidad disponible se establece igual a la cantidad total al momento del registro.
- La cantidad vendida se inicializa en cero.
- El historial inicial se crea con una variación de 0.0, ya que es el primer registro.

