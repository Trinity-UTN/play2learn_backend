# Arquitectura: Simulación del módulo Investment

Extensión del simulador de desarrollo (`configs/seed/simulation/`) para poblar cajas de ahorro, plazos fijos y acciones usando estudiantes del bootstrap.

## Prerequisitos

1. `POST /api/dev/seed` (bootstrap con wallets fondeadas).
2. `POST /api/dev/seed/simulate` con fases 1–5 (actividad académica) para saldos realistas.
3. `toDate` del rango simulado preferiblemente `<= hoy` para vencimientos de plazos fijos.

## Fases de inversión (6–8)

```
6. StockSimulationCatalogService     → catálogo de acciones (RiskLevel variado, precios accesibles)
7a. SavingAccountSimulationService   → cajas de ahorro (tenencia 7–15 días, interés 0.1% diario)
7b. FixedTermDepositSimulationService → plazos fijos 7/15/30 días
7c. StockTradeSimulationService      → compra/venta de acciones
8. StockHistorySimulationService     → variación diaria de precios (StockCalculateVariationService)
```

**Orden crítico:** 7c antes de 8 (trades actualizan `soldAmount` antes del historial diario).

## Reglas temporales (R9–R15)

| Regla | Descripción |
|-------|-------------|
| R9 | `savingAccount.startDate >= fromDate` y `lastUpdate >= startDate` |
| R10 | `fixedTermDeposit.endDate = startDate + fixedTermDays.valor` |
| R11 | `FINISHED` solo si `endDate <= hoy` |
| R12 | `order.createdAt >= stock.createdAt` y dentro de `[fromDate, toDate]` |
| R13 | `stockHistory.createdAt` estrictamente creciente; un registro por día simulado |
| R14 | Transacción PLAZO_FIJO de retorno solo cuando `endDate <= hoy` |
| R15 | Interés caja: `lastUpdate` avanza día a día sin retroceder |

## Algoritmo de precio inicial de stocks

1. Obtener balances de todos los estudiantes post-simulación académica.
2. Calcular percentil 25 (P25) de `wallet.balance`.
3. `initialPrice = clamp(P25 × stockInitialPriceMaxBalanceFactor, stockInitialPriceMin, 150)`.
4. Garantiza compra de al least 1 acción para estudiantes con saldo ≥ P25.

## Plazos fijos: FINISHED vs IN_PROGRESS

- **~75%** (`fixedTermFinishedBeforeTodayRate`): `endDate <= hoy` → `FINISHED` + transacción retorno (`amountReward`).
- **~25%**: `endDate > hoy` → `IN_PROGRESS` (pendientes, sin retorno).

## Entidades persistidas

| Entidad | Fase | Transacciones asociadas |
|---------|------|-------------------------|
| `Stock` | 6 | — |
| `StockHistory` | 6 (inicial), 8 (diario) | — |
| `SavingAccount` | 7a | `INGRESO_CAJA_AHORRO`, opcional `RETIRO_CAJA_AHORRO` |
| `FixedTermDeposit` | 7b | `PLAZO_FIJO` (inversión y retorno) |
| `Order` | 7c | `STOCK` |
| `Transaction` | 7a–7c | vía `ITransactionGenerateService` |

## Servicios reutilizados vs dev-only

| Reutilizar | Dev-only |
|------------|----------|
| `ITransactionGenerateService` | `StockSimulationCatalogService` |
| `IWalletUpdateInvestedBalanceService` | `SavingAccountSimulationService` |
| `IFixedTermDepositCalculateInterestService` | `FixedTermDepositSimulationService` |
| `StockCalculateVariationService` | `StockTradeSimulationService` |
| `IStockMoveService` | `StockHistorySimulationService` |
| Repositorios JPA | — |

**No invocar:** crons (`SavingAccountUpdate`, `FixedTermDepositAutomaticEnds`, `StockUpdate`), `StockUpdateSpecificService` en trades, notificaciones, `IProfileUpdateLevelService` (opcional omitido).

## Estrategia de timestamps

Mismo patrón que fases académicas:

1. Persistir entidad vía repositorio/mapper.
2. Actualizar campos temporales (`startDate`, `lastUpdate`, `createdAt`, `endDate`).
3. Segundo `save()`.

## Configuración

Propiedades bajo `app.seed.simulation.*` en `SimulationProperties`. Ver defaults en `application-desarrollo.properties`.
