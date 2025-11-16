# Testing Orchestrator

Automatiza el flujo de pruebas unitarias para un módulo determinado del backend, centrándose únicamente en **services**, **controllers** y **DTOs** (excluyendo models, repositories, mappers y specs).

## Pasos a seguir
1. Pregunta al usuario qué módulo desea evaluar (por ejemplo `admin/year`). Usa esta variable en todos los pasos siguientes.
2. Ejecuta `/plan-tasks` solicitando explícitamente un plan de testing para ese módulo, dejando claro que:
   - Solo deben considerarse **services**, **controllers** y **DTOs**.
   - Se deben **excluir** models, repositories, mappers y specs.
3. Ejecuta `/task_executor` para que Cursor lleve a cabo las tareas registradas en `docs/tasks.json`.
4. Corre la suite de pruebas con cobertura JaCoCo:
   ```bash
   ./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report
   ```
5. Genera un reporte Gherkin y resumen de cobertura ejecutando:
   ```bash
   python scripts/testing_report.py --module "<MODULO>" --output "docs/testing/test_<MODULO_SANITIZADO>.md"
   ```
   - Sustituye `<MODULO>` por el valor recibido (ej. `admin/year`).
   - Usa un nombre de archivo sin barras (`<MODULO_SANITIZADO>` reemplaza `/` por `_`).
6. Comparte al usuario la ruta del archivo generado (`docs/testing/test_<MODULO_SANITIZADO>.md`) junto con un breve resumen de cobertura.

# Template para testear
Quiero un plan de testing para el módulo "<investment>", junto con sus submodulos "<fixedTermDeposit>", "<investment>", "<stock>" y "<savingAccount>".
Enfócate únicamente en services, controllers y DTOs; excluye models, repositories, mappers y specs. El plan debe:
- Detallar tareas secuenciales y numeradas.
- Incluir refinamientos tras cada entrega de código relevante.
- Preparar las suites necesarias para ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report`.
- Terminar con una tarea que deje listo el comando `python scripts/testing_report.py --module "<MODULO>" --output "docs/testing/test_<MODULO_SANITIZADO>.md"`.

Provee el plan en el formato JSON esperado por `docs/tasks.json`, siguiendo la convención de IDs `T##` y asegurando que cada tarea tenga `title`, `description`, `command`, `params`, `status` inicial en `todo` y `acceptance_criteria` verificables.

No quiero tareas de escaneo de seguridad.

 