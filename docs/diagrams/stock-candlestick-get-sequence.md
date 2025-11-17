# Diagrama de Secuencia: Obtención de Valores Candlestick de Acción

Este diagrama muestra el flujo completo del endpoint `GET /investment/stocks/candlestick`, desde el controlador `StockCandleStickGetController` hasta todas las capas involucradas en la obtención de los valores candlestick (gráfico de velas) de una acción específica.

## Descripción del Flujo

El caso de uso `cu83GetValuesCandleStick` permite a un usuario con rol `ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN` o `ROLE_DEV` obtener los valores candlestick de una acción para visualizarlos en un gráfico. El flujo incluye:

1. Validación de la sesión y permisos mediante `@SessionRequired`
2. Búsqueda de la acción por ID mediante `StockFindByIdService`
3. Validación de existencia de la acción (lanza `NotFoundException` si no existe)
4. Cálculo del rango de fechas según el `RangeValue` proporcionado (DIARIO, SEMANAL, QUINZENAL, MENSUAL o HISTORICO)
5. Búsqueda del historial de la acción según el rango:
   - Si el rango es `HISTORICO`: busca todo el historial
   - Si el rango es otro: busca el historial dentro del rango de fechas calculado
6. Procesamiento de cada registro histórico para calcular los valores candlestick (open, close, high, low)
7. Conversión de los valores a DTOs mediante `StockHistoryMapper`
8. Retorno de la respuesta HTTP 200 OK con la lista de valores candlestick

```mermaid
%%{init: {'version': '11.12.1'}}%%
sequenceDiagram
    autonumber
    actor U as User (STUDENT/TEACHER/ADMIN/DEV)
    participant C as StockCandleStickGetController
    participant CS as CandleStickGetValuesService
    participant SFS as StockFindByIdService
    participant SR as IStockRepository
    participant Stock as Stock
    participant SHFS as StockHistoryFindByStockService
    participant SHFRS as StockHistoryFindByStockAndRangeService
    participant SHR as IStockHistoryRepository
    participant StockHist as StockHistory
    participant SHM as StockHistoryMapper
    participant DTO as CandleStickChartValueResponseDto
    participant Resp as ResponseFactory
    participant SM as SuccessfulMessages

    U->>C: GET /investment/stocks/candlestick<br/>?stockId=0&rangeValue=DIARIO
    Note right of C: @SessionRequired(ROLE_STUDENT,<br/>ROLE_TEACHER, ROLE_ADMIN, ROLE_DEV)<br/>@GetMapping<br/>Validación de sesión JWT y roles
    Note over C: Validación de sesión ejecutada<br/>por JwtSessionAspect (AOP)<br/>antes de invocar el método
    
    C->>CS: cu83GetValuesCandleStick(stockId: Long,<br/>rangeValue: RangeValue)
    
    CS->>SFS: execute(stockId: Long)
    Note right of SFS: Busca la acción por ID<br/>Valida que exista
    SFS->>SR: findById(stockId: Long)
    Note right of SR: Busca en la base de datos<br/>la acción con el ID proporcionado
    SR->>Stock: query by id
    Stock-->>SR: Optional<Stock>
    alt Stock encontrado
        SR-->>SFS: Stock (entidad)
        SFS-->>CS: Stock
    else Stock no encontrado
        SR-->>SFS: Optional.empty()
        SFS->>SFS: throw NotFoundException("Accion no encontrada")
        SFS-->>CS: NotFoundException
        CS-->>C: NotFoundException
        C-->>U: 404 Not Found
    end
    
    CS->>CS: calculateStartRange(rangeValue: RangeValue)
    Note right of CS: Calcula la fecha de inicio<br/>según el rango:<br/>- HISTORICO: return null<br/>- DIARIO: now().minusDays(1)<br/>- SEMANAL: now().minusDays(7)<br/>- QUINZENAL: now().minusDays(15)<br/>- MENSUAL: now().minusDays(30)
    CS-->>CS: LocalDateTime startRange
    
    alt startRange == null (HISTORICO)
        CS->>SHFS: execute(stock: Stock)
        Note right of SHFS: Busca todo el historial<br/>de la acción ordenado<br/>por fecha ascendente
        SHFS->>SHR: findByStockOrderByCreatedAtAsc(stock: Stock)
        Note right of SHR: Consulta el historial completo<br/>de la acción ordenado<br/>por createdAt ASC
        SHR->>StockHist: query by stock_id<br/>ORDER BY created_at ASC
        StockHist-->>SHR: List<StockHistory>
        SHR-->>SHFS: List<StockHistory>
        SHFS-->>CS: List<StockHistory>
    else startRange != null (Rango específico)
        CS->>SHFRS: execute(stock: Stock,<br/>startRange: LocalDateTime,<br/>endRange: LocalDateTime.now())
        Note right of SHFRS: Busca el historial de la acción<br/>dentro del rango de fechas<br/>ordenado por fecha ascendente
        SHFRS->>SHR: findByStockAndCreatedAtBetweenOrderByCreatedAtAsc(<br/>stock: Stock,<br/>startRange: LocalDateTime,<br/>endRange: LocalDateTime)
        Note right of SHR: Consulta el historial de la acción<br/>entre las fechas startRange y endRange<br/>ordenado por createdAt ASC
        SHR->>StockHist: query by stock_id<br/>AND created_at BETWEEN startRange AND endRange<br/>ORDER BY created_at ASC
        StockHist-->>SHR: List<StockHistory>
        SHR-->>SHFRS: List<StockHistory>
        SHFRS-->>CS: List<StockHistory>
    end
    
    CS->>CS: List<CandleStickChartValueResponseDto> candleStickValues<br/>Double lastClose = null<br/>Random random = new Random()
    Note right of CS: Inicializa lista de valores candlestick,<br/>último precio de cierre y generador aleatorio
    
    loop Para cada StockHistory en stockHistories
        alt lastClose == null (Primera iteración)
            CS->>CS: lastClose = stockHistory.getPrice()
            Note right of CS: Establece el precio de apertura<br/>como el precio del primer registro
        end
        
        CS->>CS: high = Math.max(lastClose, stockHistory.getPrice())<br/>+ random.nextDouble()
        Note right of CS: Calcula el precio máximo (high):<br/>máximo entre último cierre y precio actual<br/>+ valor aleatorio
        CS->>CS: low = Math.min(lastClose, stockHistory.getPrice())<br/>- random.nextDouble()
        Note right of CS: Calcula el precio mínimo (low):<br/>mínimo entre último cierre y precio actual<br/>- valor aleatorio
        
        CS->>SHM: toCandleStickValueDto(<br/>date: stockHistory.getCreatedAt(),<br/>open: lastClose,<br/>close: stockHistory.getPrice(),<br/>high: high,<br/>low: low)
        Note right of SHM: Convierte los valores calculados<br/>a DTO candlestick
        SHM->>DTO: builder()<br/>.date(date)<br/>.open(open)<br/>.close(close)<br/>.high(high)<br/>.low(low)<br/>.build()
        DTO-->>SHM: CandleStickChartValueResponseDto
        SHM-->>CS: CandleStickChartValueResponseDto
        
        CS->>CS: candleStickValues.add(dto)
        CS->>CS: lastClose = stockHistory.getPrice()
        Note right of CS: Actualiza el último precio de cierre<br/>para la siguiente iteración
    end
    
    CS-->>C: List<CandleStickChartValueResponseDto>
    
    C->>SM: okSuccessfully()
    SM-->>C: "OK"
    C->>Resp: ok(data: List<CandleStickChartValueResponseDto>,<br/>message: "OK")
    Note right of Resp: Crea ResponseEntity con:<br/>- status: HttpStatus.OK (200)<br/>- body: BaseResponse<List<CandleStickChartValueResponseDto>><br/>- message: "OK"<br/>- timestamp: ISO_INSTANT format
    Resp-->>C: ResponseEntity<BaseResponse<List<CandleStickChartValueResponseDto>>>
    
    C-->>U: 200 OK<br/>BaseResponse<List<CandleStickChartValueResponseDto>>

    note over CS: finCU()
```

## Componentes Involucrados

### Controller
- **StockCandleStickGetController**: Punto de entrada del endpoint REST. Valida la sesión y permisos, recibe los parámetros `stockId` y `rangeValue`, y retorna la respuesta HTTP con la lista de valores candlestick.

### Services
- **CandleStickGetValuesService**: Contiene la lógica de negocio del caso de uso. Orquesta la búsqueda de la acción, el cálculo del rango de fechas, la búsqueda del historial según el rango, el procesamiento de cada registro histórico para calcular los valores candlestick (open, close, high, low), y la conversión a DTOs.
- **StockFindByIdService**: Servicio común que busca una acción por ID. Lanza `NotFoundException` si la acción no existe.
- **StockHistoryFindByStockService**: Servicio común que busca todo el historial de una acción ordenado por fecha de creación ascendente.
- **StockHistoryFindByStockAndRangeService**: Servicio común que busca el historial de una acción dentro de un rango de fechas específico, ordenado por fecha de creación ascendente.

### Mappers
- **StockHistoryMapper**: Responsable de convertir los valores calculados a DTOs `CandleStickChartValueResponseDto`. Incluye el método `toCandleStickValueDto` que recibe los valores date, open, close, high y low.

### Repositories
- **IStockRepository**: Interfaz de persistencia para la entidad `Stock`. Extiende `CrudRepository<Stock, Long>`. Proporciona el método `findById` para buscar acciones por ID.
- **IStockHistoryRepository**: Interfaz de persistencia para la entidad `StockHistory`. Extiende `CrudRepository<StockHistory, Long>`. Proporciona los métodos:
  - `findByStockOrderByCreatedAtAsc`: Busca todo el historial de una acción ordenado por fecha ascendente.
  - `findByStockAndCreatedAtBetweenOrderByCreatedAtAsc`: Busca el historial de una acción dentro de un rango de fechas, ordenado por fecha ascendente.

### Entities
- **Stock**: Entidad que representa una acción en el sistema. Contiene información como nombre, abreviación, precios, cantidades y nivel de riesgo.
- **StockHistory**: Entidad que representa un registro histórico de una acción. Almacena el precio, cantidades disponibles y vendidas, y la variación en un momento específico.

### DTOs
- **CandleStickChartValueResponseDto**: DTO de respuesta que contiene los valores candlestick para un punto en el tiempo. Incluye date (fecha del registro), open (precio de apertura), close (precio de cierre), high (precio máximo) y low (precio mínimo).

### Response Factory
- **ResponseFactory**: Utilidad para crear respuestas HTTP estandarizadas. Proporciona el método `ok` que crea una respuesta HTTP 200 OK con un `BaseResponse` que incluye los datos, mensaje y timestamp.

### Successful Messages
- **SuccessfulMessages**: Utilidad para obtener mensajes de éxito estandarizados. Proporciona el método `okSuccessfully()` que retorna el mensaje "OK".

## Validaciones

- **Nivel de Controller**: Validación de sesión y permisos mediante `@SessionRequired(roles = {Role.ROLE_STUDENT, Role.ROLE_TEACHER, Role.ROLE_ADMIN, Role.ROLE_DEV})`. La validación se ejecuta mediante AOP (`JwtSessionAspect`) antes de invocar el método del controlador. Valida:
  - Presencia del header `Authorization` con formato `Bearer {token}`
  - Expiración del token JWT
  - Validez de la firma del token
  - Actividad del usuario en la base de datos
  - Que el rol del usuario esté en la lista de roles permitidos
- **Nivel de Service**: Validación de existencia de la acción mediante `StockFindByIdService`. Si la acción no existe, se lanza `NotFoundException` con el mensaje "Accion no encontrada".

## Cálculo de Valores Candlestick

Los valores candlestick se calculan de la siguiente manera:

1. **Open (Apertura)**: Para el primer registro, se usa el precio del registro. Para los siguientes, se usa el precio de cierre (`lastClose`) del registro anterior.
2. **Close (Cierre)**: Se usa el precio (`price`) del registro histórico actual.
3. **High (Máximo)**: Se calcula como el máximo entre el último precio de cierre y el precio actual, más un valor aleatorio: `Math.max(lastClose, stockHistory.getPrice()) + random.nextDouble()`
4. **Low (Mínimo)**: Se calcula como el mínimo entre el último precio de cierre y el precio actual, menos un valor aleatorio: `Math.min(lastClose, stockHistory.getPrice()) - random.nextDouble()`

**Nota**: Los valores `high` y `low` incluyen un componente aleatorio para simular variaciones en los precios máximos y mínimos durante el período representado por cada registro histórico.

## Rangos de Fechas

El sistema soporta los siguientes rangos de fechas mediante el enum `RangeValue`:

- **DIARIO**: Últimos 1 día (`now().minusDays(1)`)
- **SEMANAL**: Últimos 7 días (`now().minusDays(7)`)
- **QUINZENAL**: Últimos 15 días (`now().minusDays(15)`)
- **MENSUAL**: Últimos 30 días (`now().minusDays(30)`)
- **HISTORICO**: Todo el historial disponible (sin filtro de fechas)

## Ordenamiento

El historial se retorna ordenado por fecha de creación ascendente (`ORDER BY created_at ASC`), lo que permite visualizar la evolución de la acción desde el punto inicial del rango seleccionado hasta el momento actual.

## Notas Adicionales

- Si la acción no existe, se retorna un error HTTP 404 Not Found con el mensaje "Accion no encontrada".
- Si el historial está vacío, se retorna una lista vacía (no se lanza ninguna excepción).
- Los valores `high` y `low` incluyen un componente aleatorio para simular variaciones en los precios. Este componente se genera mediante `Random.nextDouble()` en cada iteración.
- El precio de apertura (`open`) del primer registro es igual al precio de cierre (`close`) de ese mismo registro (ya que no hay un registro anterior).
- La respuesta incluye un timestamp en el `BaseResponse` que indica el momento en que se generó la respuesta, no el timestamp de los registros históricos.
- El parámetro `stockId` tiene un valor por defecto de `0` si no se proporciona.
- El parámetro `rangeValue` tiene un valor por defecto de `DIARIO` si no se proporciona.

