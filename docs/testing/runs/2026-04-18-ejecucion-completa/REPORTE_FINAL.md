# Reporte de ejecución — 2026-04-18

Carpeta generada para **no sobrescribir** los reportes históricos en `docs/testing/test_*.md`. Aquí quedan una copia de `jacoco.csv`, los Markdown generados por `testing_report.py` y este resumen.

---

## Cómo ejecutar los tests y obtener cobertura (JaCoCo)

Desde la raíz del backend (donde está `pom.xml` y `mvnw.cmd`):

**Flujo estándar** (el `pom.xml` ya incluye el agente JaCoCo y el goal `report` en la fase `test`):

```powershell
Set-Location "ruta\al\play2learn_backend"
.\mvnw.cmd test
```

Tras una ejecución correcta (o al menos si llega a generar el informe), el CSV agregado está en:

`target/site/jacoco/jacoco.csv`

y el informe HTML en:

`target/site/jacoco/index.html`

**Regla `jacoco:check`:** el proyecto exige al menos **60 %** de cobertura (instrucciones, ramas, líneas y métodos) sobre el *bundle* configurado. Si no se cumple, el build falla aunque los tests pasen. Para **solo generar el informe** sin que el check detenga el build:

```powershell
.\mvnw.cmd test "-Djacoco.haltOnFailure=false"
```

**Si hay tests en rojo** y Maven se detiene antes del reporte, puedes forzar que el build continúe (los fallos siguen listados en consola y en `target/surefire-reports`):

```powershell
.\mvnw.cmd test "-Dmaven.test.failure.ignore=true" "-Djacoco.haltOnFailure=false"
```

> En la corrida que originó esta carpeta se usó esta última variante: la suite completa tenía fallos y errores, pero se completó el informe JaCoCo. Los porcentajes reflejan **lo que realmente se ejecutó**; no son comparables con una corrida donde todo pase en verde.

**Equivalente explícito** (como en `.cursor/commands/testing.md`):

```powershell
.\mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report
```

---

## Para qué sirve `scripts/testing_report.py` y cómo se usa

**Función:** arma un archivo **Markdown** con:

1. **Cobertura JaCoCo** leyendo `target/site/jacoco/jacoco.csv`: totales del proyecto y del subárbol cuyo paquete empiece por `trinity.play2learn.backend.<módulo>` (por ejemplo `admin.year` si pasás `admin/year`).
2. **Listado tipo Gherkin** (`Feature:` / `Scenario:`) recorriendo `src/test/java`: solo clases cuyo nombre contiene `Service`, `Controller` o `Dto` y cuya ruta incluya el fragmento del módulo; toma `@DisplayName` de la clase (feature) y de métodos (escenarios).

**Uso** (siempre **después** de tener `jacoco.csv` generado):

```powershell
python scripts/testing_report.py --module "economy" --output "docs/testing/runs/MI_CARPETA/test_economy.md"
python scripts/testing_report.py --module "admin/year" --output "docs/testing/runs/MI_CARPETA/test_years.md"
```

- `--module`: ruta lógica del módulo (`economy`, `investment`, `admin/course`, …), sin el prefijo de paquete Java.
- `--output`: ruta del `.md` destino (podés apuntar a `docs/testing/runs/...` para no pisar reportes viejos).

Referencias en el repo: `docs/active_context.md`, `docs/testing/*-unit-test-strategy.md`, `.cursor/commands/testing.md`.

---

## Resultados de esta ejecución (Surefire)

Totales agregados desde `target/surefire-reports/TEST-*.xml`:

| Métrica   | Valor |
|-----------|------:|
| Tests     | 973   |
| Fallos    | 18    |
| Errores   | 166   |
| Omitidos  | 14    |

Convención: **fallos** = aserciones; **errores** = excepciones no esperadas (p. ej. contexto Spring en `@WebMvcTest`).

---

## Cobertura por módulo (JaCoCo — esta corrida)

Los bloques «Proyecto completo» coinciden en todos los informes generados el mismo día (mismo `jacoco.csv`). La fila útil por área es la del **módulo**.

| Módulo | Instrucciones | Ramas | Líneas | Archivo |
|--------|---------------:|------:|-------:|-----------|
| economy | 86.48 % | 88.24 % | 83.56 % | [test_economy.md](test_economy.md) |
| investment | 87.6 % | 87.16 % | 81.77 % | [test_investment.md](test_investment.md) |
| benefits | 74.78 % | 74.03 % | 82.09 % | [test_benefits.md](test_benefits.md) |
| activity | 34.11 % | 20.89 % | 35.68 % | [test_activity.md](test_activity.md) |
| admin/course | 80.0 % | 87.5 % | 70.18 % | [test_admin_course.md](test_admin_course.md) |
| admin/subject | 68.81 % | 54.0 % | 64.02 % | [test_admin_subject.md](test_admin_subject.md) |
| admin/student | 90.73 % | 85.0 % | 90.53 % | [test_admin_student.md](test_admin_student.md) |
| admin/teacher | 88.75 % | 81.25 % | 89.66 % | [test_admin_teacher.md](test_admin_teacher.md) |
| admin/year | 73.95 % | 85.0 % | 81.48 % | [test_years.md](test_years.md) |

**Proyecto completo** (misma corrida, desde cualquiera de los MD): instrucciones 54.62 %, ramas 49.07 %, líneas 53.21 %, complejidad 42.99 %, métodos 44.63 %.

---

## Archivos en esta carpeta

| Archivo | Descripción |
|---------|-------------|
| `jacoco.csv` | Copia del informe JaCoCo usado por el script |
| `test_*.md` | Reportes por módulo (cobertura + escenarios desde `@DisplayName`) |
| `REPORTE_FINAL.md` | Este documento |

Para una nueva corrida sin pisar esta carpeta, creá otra bajo `docs/testing/runs/<nombre>/`, volvé a ejecutar `mvn` y repetí las llamadas a `testing_report.py` con `--output` apuntando a la nueva ruta.
