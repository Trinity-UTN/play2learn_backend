# Variación diaria del precio de acciones (Stocks)

Este documento describe cómo se calcula y aplica la variación diaria del precio de las acciones en el módulo de **investment**. El algoritmo vive principalmente en `StockCalculateVariationService` y se ejecuta una vez por día para cada acción registrada.

## Cuándo se ejecuta

El proceso se dispara automáticamente mediante un scheduler de Spring (**CU78 - Actualización de Stocks**) todos los días a la **1:00 AM**. También puede ejecutarse manualmente a través del seed de simulación.

Flujo resumido:

1. Se obtienen todas las acciones del sistema.
2. Para cada acción se calcula una **variación porcentual** del día.
3. Se aplica esa variación al precio actual, respetando límites mínimo y máximo.
4. Se guarda un registro en `StockHistory`.
5. Se evalúan y ejecutan las órdenes stop pendientes.

```
StockUpdateService
  └── StockUpdateSpecificService
        ├── StockCalculateVariationService   ← cálculo de la variación (%)
        └── aplica el nuevo precio + guarda historial
```

## Fórmula final del precio

Una vez calculada la variación (`variation`, en porcentaje), el nuevo precio se obtiene así:

```
newPrice = clamp(currentPrice × (1 + variation / 100), piso, techo)
```

Donde:

| Parámetro | Valor |
|-----------|-------|
| **Piso** | `10.0` (precio mínimo absoluto) |
| **Techo** | `initialPrice × 2.5` (máximo 2.5 veces el precio inicial) |

**Ejemplo:** acción con precio inicial `1000`, precio actual `1050` y variación `+3%`:

```
newPrice = 1050 × 1.03 = 1081.5
```

Si el resultado cae por debajo de `10` o supera `2500` (en este ejemplo), se ajusta al límite correspondiente.

---

## Cálculo de la variación (paso a paso)

La variación diaria es un **porcentaje aleatorio** dentro de un rango `[min, max]` que se construye en cuatro etapas.

### 1. Rango base según nivel de riesgo

Cada acción tiene un `RiskLevel` que define la amplitud máxima de movimiento diario:

| Nivel de riesgo | Valor (`risk`) | Rango base |
|-----------------|----------------|------------|
| BAJO            | 5              | `[-5%, +5%]` |
| MEDIO           | 10             | `[-10%, +10%]` |
| ALTO            | 15             | `[-15%, +15%]` |

```
min = -risk
max = +risk
```

### 2. Sesgo por actividad del mercado interno

El simulador reacciona a las compras y ventas de los estudiantes. Se compara la cantidad vendida actual con la del último registro de historial:

```
stocksChange = soldAmount(actual) - soldAmount(último historial)
bias         = stocksChange / totalAmount
```

El sesgo desplaza el rango de variación:

```
min = max(-risk + risk × bias, -risk)
max = min(+risk + risk × bias, +risk)
```

| Situación | Efecto |
|-----------|--------|
| Más ventas que en el día anterior (`bias > 0`) | El rango se desplaza hacia valores **positivos** (presión alcista). |
| Menos ventas / más compras netas (`bias < 0`) | El rango se desplaza hacia valores **negativos** (presión bajista). |
| Sin cambio en ventas (`bias = 0`) | El rango queda centrado en cero. |

**Ejemplo** (riesgo MEDIO, `risk = 10`, `bias = 0.2`):

```
min = max(-10 + 2, -10) = -8
max = min(+10 + 2, +10)  = +10
→ rango: [-8%, +10%]
```

### 3. Ajuste contrario por tendencia reciente

Se analizan las **últimas 10 variaciones** guardadas en el historial. Si su suma es positiva, la tendencia se considera **alcista**; si es cero o negativa, **bajista**.

A diferencia de un modelo de momentum, aquí se aplica lógica **contraria** para evitar que el precio se dispare sostenidamente hacia un extremo:

| Tendencia | Ajuste | Intención |
|-----------|--------|-----------|
| Alcista (subió mucho recientemente) | `max = max / 2` | Limitar nuevas subidas. |
| Bajista (bajó mucho recientemente) | `min = min / 2` | Limitar nuevas caídas. |

**Ejemplo** (rango previo `[-10%, +10%]`, tendencia alcista):

```
min = -10
max = +10 / 2 = +5
→ rango: [-10%, +5%]
```

**Ejemplo** (rango previo `[-10%, +10%]`, tendencia bajista):

```
min = -10 / 2 = -5
max = +10
→ rango: [-5%, +10%]
```

### 4. Reversión a la media según posición en el rango

Se calcula en qué punto del intervalo permitido se encuentra el precio actual:

```
ceiling   = initialPrice × 2.5
position  = (currentPrice - 10) / (ceiling - 10)    → valor entre 0 y 1
meanReversion = (0.5 - position) × risk × 0.8
```

| Posición del precio | `position` | Efecto de `meanReversion` |
|---------------------|------------|----------------------------|
| Cerca del piso (10) | ≈ 0 | Positivo → empuja el rango hacia arriba. |
| En el centro del rango | ≈ 0.5 | Cero → sin sesgo adicional. |
| Cerca del techo | ≈ 1 | Negativo → empuja el rango hacia abajo. |

El sesgo se suma a ambos extremos del rango:

```
min = min + meanReversion
max = max + meanReversion
```

**Ejemplo** (precio inicial `1000`, precio actual `10`, riesgo MEDIO):

```
ceiling       = 2500
position      = (10 - 10) / (2500 - 10) = 0
meanReversion = (0.5 - 0) × 10 × 0.8 = +4
```

Si el rango previo era `[-5%, +10%]`, pasa a ser `[-1%, +14%]`, favoreciendo la recuperación desde el piso.

### 5. Sorteo final

Con el rango `[min, max]` ya calculado, la variación del día es un valor aleatorio uniforme dentro de ese intervalo:

```
variation = random(min, max)
```

---

## Ejemplo completo

**Datos de la acción:**

- Precio inicial: `1000`
- Precio actual: `800`
- Nivel de riesgo: MEDIO (`risk = 10`)
- Sin cambio en ventas desde el último historial (`bias = 0`)
- Tendencia bajista (suma de últimas 10 variaciones ≤ 0)

**Paso 1 — Rango base:** `[-10, +10]`

**Paso 2 — Sesgo de mercado:** sin cambio → `[-10, +10]`

**Paso 3 — Tendencia contraria (bajista):**

```
min = -10 / 2 = -5
max = +10
→ [-5, +10]
```

**Paso 4 — Reversión a la media:**

```
ceiling       = 2500
position      = (800 - 10) / (2500 - 10) ≈ 0.32
meanReversion = (0.5 - 0.32) × 10 × 0.8 ≈ +1.44

min = -5 + 1.44 = -3.56
max = +10 + 1.44 = +11.44
→ [-3.56%, +11.44%]
```

**Paso 5 — Sorteo:** supongamos `variation = +2.1%`

**Paso 6 — Nuevo precio:**

```
newPrice = 800 × 1.021 = 816.8
```

---

## Constantes configurables

Definidas en `StockCalculateVariationService`:

| Constante | Valor | Descripción |
|-----------|-------|-------------|
| `PRICE_FLOOR` | `10.0` | Precio mínimo absoluto. |
| `PRICE_CEILING_MULTIPLIER` | `2.5` | Multiplicador del precio inicial para el techo. |
| `MEAN_REVERSION_FACTOR` | `0.8` | Intensidad de la reversión a la media. Valores más altos acercan el precio al centro del rango con mayor fuerza. |

---

## Servicios relacionados

| Servicio | Responsabilidad |
|----------|-----------------|
| `StockCalculateVariationService` | Calcula el porcentaje de variación diaria. |
| `StockHistoryCalculateTrendService` | Determina si la tendencia reciente es alcista o bajista. |
| `StockHistoryFindLastService` | Obtiene el último registro de historial para calcular el sesgo de ventas. |
| `StockUpdateSpecificService` | Aplica la variación al precio, persiste el stock y el historial. |
| `OrderStopExecuteService` | Ejecuta órdenes stop tras la actualización de precio. |

---

## Comportamiento esperado del modelo

El diseño combina cuatro fuerzas:

1. **Volatilidad aleatoria** — el rango base según riesgo garantiza movimiento diario impredecible.
2. **Presión del mercado interno** — las compras/ventas de estudiantes inclinan el rango.
3. **Corrección contraria** — tras rachas alcistas o bajistas, se limita continuar en la misma dirección.
4. **Reversión a la media** — precios muy bajos o muy altos reciben un empuje de vuelta hacia el centro del rango permitido.

Esto evita el problema anterior en el que todas las acciones convergían y quedaban atrapadas en el piso (`10`) o en el techo (`initialPrice × 2.5`), manteniendo precios que fluctúan de forma más realista dentro del rango permitido.

---

## Referencias en código

- Cálculo de variación: `src/main/java/trinity/play2learn/backend/investment/stock/services/commons/StockCalculateVariationService.java`
- Aplicación del precio: `src/main/java/trinity/play2learn/backend/investment/stock/services/commons/StockUpdateSpecificService.java`
- Tendencia: `src/main/java/trinity/play2learn/backend/investment/stock/services/commons/StockHistoryCalculateTrendService.java`
- Diagrama de secuencia del flujo completo: `docs/diagrams/stock-update-sequence.md`
