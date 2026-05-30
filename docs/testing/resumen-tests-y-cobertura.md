# Resumen consolidado — pruebas y cobertura (`docs/testing`)

Este documento sintetiza la información de todos los archivos Markdown bajo `docs/testing`: reportes con métricas JaCoCo, inventarios de escenarios, ejecución documentada (Surefire), estrategias y auditorías.

## Cómo interpretar las cifras de JaCoCo

- En cada `test_*.md` aparecen dos bloques: **proyecto completo** y **módulo** (`economy`, `investment`, `admin/course`, etc.).
- El porcentaje de **proyecto completo** **no es comparable entre reportes**: refleja la cobertura del bytecode cargado en esa corrida concreta (tests focalizados en un módulo suelen dejar el resto del monolito casi sin ejecutar). Para comparar calidad entre áreas, use la fila del **módulo** correspondiente.
- Las métricas listadas son las habituales de JaCoCo: **instrucciones**, **ramas**, **líneas**, **complejidad** y **métodos**.

## Tabla — cobertura por módulo (JaCoCo en reportes existentes)

Origen: secciones «Cobertura de código» o «Cobertura agregada» en los reportes enlazados en la columna «Fuente».

| Módulo / paquete | Instr. | Ramas | Líneas | Compl. | Métodos | Fuente principal |
|------------------|--------|-------|--------|--------|---------|------------------|
| `economy` | 99.56 % | 90.91 % | 99.51 % | 89.83 % | 100.0 % | [test_economy.md](test_economy.md) |
| `investment` | 99.16 % | 88.07 % | 99.12 % | 89.66 % | 100.0 % | [test_investment.md](test_investment.md) |
| `benefits` | 84.55 % | 74.03 % | 90.49 % | 66.2 % | 70.77 % | [test_benefits.md](test_benefits.md) |
| `activity` | 60.42 % | 33.45 % | 61.89 % | 38.44 % | 48.0 % | [test_activity.md](test_activity.md) |
| `admin/course` | 86.91 % | 88.89 % | 88.64 % | 73.81 % | 72.73 % | [test_admin_course.md](test_admin_course.md) |
| `admin/subject` | 72.28 % | 63.64 % | 69.1 % | 57.14 % | 59.68 % | [test_admin_subject.md](test_admin_subject.md) |
| `admin/student` | 90.31 % | 85.71 % | 94.05 % | 75.86 % | 77.27 % | [test_admin_student.md](test_admin_student.md) |
| `admin/teacher` | 88.52 % | 80.0 % | 89.9 % | 78.26 % | 83.33 % | [test_admin_teacher.md](test_admin_teacher.md) |
| `admin/year` | 78.80 % | 86.36 % | 84.62 % | 68.29 % | 66.67 % | [test_years.md](test_years.md) (fecha en doc: 2025-11-11) |

### Referencia — «proyecto completo» en la misma corrida (solo contexto)

| Reporte | Instr. | Ramas | Líneas |
|---------|--------|-------|--------|
| test_economy | 54.15 % | 47.71 % | 54.03 % |
| test_investment | 71.75 % | 61.46 % | 70.8 % |
| test_benefits | 42.96 % | 39.11 % | 44.08 % |
| test_activity | 19.76 % | 10.98 % | 21.31 % |
| test_admin_course | 4.75 % | 4.01 % | 5.46 % |
| test_admin_subject | 9.95 % | 7.81 % | 9.8 % |
| test_admin_student | 13.51 % | 10.91 % | 13.69 % |
| test_admin_teacher | 15.66 % | 12.4 % | 15.55 % |
| test_years (agregado) | 1.98 % | 2.18 % | 2.07 % |

## Lectura por módulo (resultado funcional según documentación)

- **Economy** ([test_economy.md](test_economy.md)): Cobertura de módulo muy alta; escenarios extensos sobre reserva, circulación, estadísticas de transacciones, estrategias de transacción (compra, asignación, actividad con límite 30 %, plazo fijo, acciones, cajas de ahorro, etc.), wallet y controladores (`GET /wallet`, asignación de prueba `ROLE_DEV`, etc.).
- **Investment** ([test_investment.md](test_investment.md)): Cobertura de módulo muy alta; plazos fijos, cajas de ahorro, acciones (compra/venta/stop), historiales, candlestick, intereses y actualizaciones automáticas documentadas en escenarios.
- **Benefits** ([test_benefits.md](test_benefits.md)): Buena cobertura de líneas en módulo (~90 %); ramas ~74 %; flujos de generación, compra, estados, listados paginados y validaciones de DTOs.
- **Activity** ([test_activity.md](test_activity.md)): Cobertura de módulo moderada (~60 % instrucciones, ramas ~33 %); el reporte es el más extenso en escenarios (múltiples tipos de actividad: ahorcado, árbol de decisión, clasificación, completar oración, memorama, no lúdica, ordenar secuencia, preguntados) más APIs de alumno/docente y validaciones Bean Validation.
- **Admin — course, subject, student, teacher** ([test_admin_course.md](test_admin_course.md), [test_admin_subject.md](test_admin_subject.md), [test_admin_student.md](test_admin_student.md), [test_admin_teacher.md](test_admin_teacher.md)): Módulos admin con cobertura de paquete en general **alta** (curso y estudiante destacan por encima del 88–90 % en instrucciones); subject algo más bajo en ramas y líneas.
- **Admin — year** ([test_years.md](test_years.md), [year-module-test-report.md](year-module-test-report.md)): Especificación Gherkin de escenarios unitarios (delete, register, update, listados, existencia, mapper, modelo, specs) más métricas JaCoCo del paquete `admin/year`. [year-module-test-report.md](year-module-test-report.md) documenta una ejecución `./mvnw.cmd test` con **éxito**, **42** pruebas ejecutadas, **14** omitidas (`@Disabled` en suites legacy), duración ~**18.5 s**, y lista de suites del módulo Year que pasaron.

## Otros archivos en `docs/testing` (sin cifras JaCoCo en el cuerpo)

| Archivo | Rol |
|---------|-----|
| `*-unit-test-strategy.md` (activity, benefits, course, economy, investment, student, subject, teacher, year) | Objetivos de calidad (umbrales sugeridos, orden de trabajo, riesgos). Ej.: investment pide ≥ 85 % líneas en services principales; year pide ≥ 85 % líneas / ≥ 80 % ramas para `admin/year` (en [year-unit-test-strategy.md](year-unit-test-strategy.md) figura cobertura JaCoCo como pendiente en una fila de tareas). |
| `*-unit-tests-audit.md` (activity, admin-course, benefits, economy, investment, student, subject, teacher, year) | Auditoría de alcance, dependencias y casos a cubrir; no sustituyen a los reportes numéricos. |
| [year-unit-tests-audit.md](year-unit-tests-audit.md) | Mapa de CUs y dependencias entre servicios del dominio año. |

## Conclusiones breves

1. **Módulos con mayor cobertura medida** en los MD actuales: **economy** e **investment** (instrucciones ~99 %, métodos 100 % en ambos reportes).
2. **Mayor brecha relativa en ramas / instrucciones a nivel módulo**: **activity** (ramas ~33 %), coherente con muchas ramas de validación y subtipos de actividad.
3. **Admin year**: cobertura de paquete sólida pero por debajo de economy/investment; ejecución global documentada el **2025-11-11** con parte de la suite desactivada por dependencias externas (ver [year-module-test-report.md](year-module-test-report.md)).
4. Para un **único porcentaje global del repositorio** hace falta un informe JaCoCo generado tras `mvn verify` (o equivalente) sobre todo el proyecto; los archivos aquí **no** unifican ese número.

## Índice de archivos `.md` en `docs/testing`

- Reportes con escenarios + JaCoCo: `test_activity.md`, `test_admin_course.md`, `test_admin_student.md`, `test_admin_subject.md`, `test_admin_teacher.md`, `test_benefits.md`, `test_economy.md`, `test_investment.md`, `test_years.md`
- Ejecución / plan año: `year-module-test-report.md`
- Estrategias: `activity-unit-test-strategy.md`, `benefits-unit-test-strategy.md`, `course-unit-test-strategy.md`, `economy-unit-test-strategy.md`, `investment-unit-test-strategy.md`, `student-unit-test-strategy.md`, `subject-unit-test-strategy.md`, `teacher-unit-test-strategy.md`, `year-unit-test-strategy.md`
- Auditorías: `activity-unit-tests-audit.md`, `admin-course-unit-tests-audit.md`, `benefits-unit-tests-audit.md`, `economy-unit-tests-audit.md`, `investment-unit-tests-audit.md`, `student-unit-tests-audit.md`, `subject-unit-tests-audit.md`, `teacher-unit-tests-audit.md`, `year-unit-tests-audit.md`

---
*Documento generado a partir del contenido de `docs/testing` al consolidar reportes; si se actualizan los `test_*.md`, conviene regenerar o ajustar las tablas de este resumen.*
