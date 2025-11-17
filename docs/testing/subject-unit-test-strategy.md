# Estrategia de pruebas unitarias — Módulo Admin Subject

## 1. Objetivo
Garantizar cobertura unitaria robusta sobre servicios, controladores y DTOs del módulo `admin/subject`, asegurando que las reglas de negocio críticas (asignación de docentes/alumnos, control de saldos y restricciones de eliminación/restauración) se validen mediante suites aisladas, repetibles y de rápida ejecución.

## 2. Alcance y exclusiones
- **Incluye**:  
  - Servicios del módulo (implementaciones y commons).  
  - Controladores HTTP bajo `/admin/subjects`.  
  - DTOs de entrada y salida con Bean Validation.
- **Excluye**: models JPA, repositories, mappers y specs (`SubjectMapper`, `SubjectSpecs`, `ISubjectRepository`, `ISubjectPaginatedRepository`, etc.), los cuales se cubrirán mediante pruebas dedicadas a integración o ya cuentan con verificaciones previas.

## 3. Métricas y criterios de calidad
- Cobertura mínima por categoría:  
  - Servicios: ≥ 90 % líneas / ≥ 80 % ramas.  
  - Controladores: ≥ 85 % líneas / ≥ 70 % ramas.  
  - DTOs: ≥ 95 % de anotaciones ejercitadas (mediante escenarios positivos/negativos).
- Todos los tests deben pasar con `./mvnw.cmd test` sin warnings ni flakiness.
- Cada suite debe documentar Given/When/Then mediante `@DisplayName` o comentarios breves.
- Mantener tiempo total de ejecución del módulo < 90 s en pipelines locales (`mvn -pl admin -am test`).

## 4. Priorización de riesgos
| Riesgo | Impacto | Probabilidad | Mitigación en pruebas |
|--------|---------|--------------|-----------------------|
| Eliminación/restauración de materias con alumnos asignados | Alto | Medio | Casos negativos en `SubjectDeleteServiceTest` y `SubjectRestoreServiceTest`. |
| Asignación masiva de estudiantes (duplicados, listas vacías) | Medio | Alto | Suites para `SubjectAddStudentsService` y `SubjectRemoveStudentsService` con datos variados. |
| Gestión de saldos y cron de recarga | Alto | Medio | Tests sobre `SubjectRefillBalanceService` y `SubjectRefillBalanceCronService` usando mocks de transacciones/cron. |
| Falta de protección en endpoints sin `@SessionRequired` | Alto | Medio | WebMvc tests verificando filtros/headers y discusión con seguridad si se detecta gap. |
| Dependencias externas (Course/Teacher/Student services) | Medio | Medio | Uso de mocks/dobles controlados y verificación de interacciones obligatorias. |

## 5. Suites planificadas
1. **Servicios de registro/actualización**  
   - `SubjectRegisterServiceTest`, `SubjectUpdateServiceTest`.  
   - Validar registros felices, conflictos por nombre/curso, docente inexistente, auto-asignación de estudiantes.
2. **Servicios de eliminación/restauración**  
   - `SubjectDeleteServiceTest`, `SubjectRestoreServiceTest`.  
   - Cobertura de soft delete, asociaciones bloqueantes, restauración exitosa.
3. **Gestión de estudiantes**  
   - `SubjectAddStudentsServiceTest`, `SubjectRemoveStudentsServiceTest`, `SubjectAddStudentByCourseServiceTest`.  
   - Duplicados, listas mixtas, transacciones en lote.
4. **Asignación de docentes**  
   - `SubjectAssignTeacherServiceTest`, `SubjectUnassignTeacherServiceTest`, `SubjectListTeacherServiceTest`.  
   - NotFound, actualizaciones idempotentes, listados filtrados por usuario de sesión.
5. **Servicios de balance**  
   - `SubjectAddBalanceServiceTest`, `SubjectRemoveBalanceServiceTest`, `SubjectRefillBalanceServiceTest`, `SubjectRefillBalanceCronServiceTest`.  
   - Límites de montos, generación de transacciones, cálculo de `initialBalance`.
6. **Servicios de consulta**  
   - `SubjectGetServiceTest`, `SubjectListServiceTest`, `SubjectListPaginatedServiceTest`, `SubjectGetByStudentServiceTest`.  
   - Filtrado, paginación, delegación a helpers.
7. **Servicios de validación commons**  
   - `SubjectExistsByNameAndCourseServiceTest`, `SubjectExistsByCourseServiceTest`, `SubjectExistsByServiceTest`, `SubjectHasStudentServiceTest`.  
   - Conflictos y mensajes esperados.
8. **Controladores WebMvc**  
   - `SubjectWriteControllersTest` (POST/PUT/DELETE/PATCH).  
   - `SubjectReadControllersTest` (GET/PAGINATED/teacher).  
   - `SubjectBalanceControllersTest` (refill-balance).  
   - Validar roles, payloads, headers y códigos de estado.
9. **DTO Validation**  
   - `SubjectDtoValidationTest` para `SubjectRequestDto`, `SubjectUpdateRequestDto`, `SubjectAssignTeacherRequestDto`.  
   - Escenarios válidos e inválidos con mensajes de `ValidationMessages`.

## 6. Dobles de prueba y herramientas
- Mock frameworks: Mockito + JUnit 5 con `@ExtendWith(MockitoExtension.class)` o `@MockBean` en WebMvc.
- `@TestConfiguration` para inyectar dobles en suites WebMvc.
- `Clock` o `Scheduler` manual para forzar ejecución del cron (usar `@Import` y mocks).
- `ArgumentCaptor` para validar datos enviados a `ITransactionGenerateService` y repositorios.

## 7. Fixtures y utilidades compartidas
- Builders estáticos para `Subject`, `Student`, `Teacher`, `Course` con datos coherentes (usar patrones existentes en módulo course). 
- Helper para construir listas paginadas y `PageImpl`.
- Utilidad para crear `MockHttpServletRequestBuilder` comunes con headers de sesión simulada.
- Fixture JSON en línea para payloads de controllers (registrar en `src/test/resources/data/subjects/` si se requiere).

## 8. Secuencia de trabajo recomendada
1. Generar suites de servicios críticos (registro/actualización/eliminación/restauración).  
2. Añadir suites de gestión de estudiantes/docentes.  
3. Cubrir servicios de balance y cron.  
4. Implementar servicios de consulta y commons.  
5. Crear pruebas WebMvc (escritura → lectura → balance).  
6. Finalizar con Bean Validation.  
7. Ejecutar refinamientos (`/refine`) tras cada bloque para consolidar helpers compartidos.

## 9. Integración con quality gates
- Tras completar suites principales, ejecutar `./mvnw.cmd org.jacoco:jacoco-maven-plugin:prepare-agent test org.jacoco:jacoco-maven-plugin:report` para verificar cobertura global.
- Agendar `./mvnw.cmd org.owasp:dependency-check-maven:check -DfailOnError=false -DdataDirectory=./.owasp-cache` luego de integrar nuevas dependencias de test.
- Generar reporte consolidado con `python scripts/testing_report.py --module "admin/subject" --output "docs/testing/test_admin_subject.md"` una vez cerradas las suites.

## 10. Riesgos residuales y seguimiento
- Confirmar con equipo de seguridad si `SubjectAddStudentsController` y `SubjectRemoveStudentsController` deben exigir sesión; de ser así, planificar ajuste posterior del código y de las pruebas.
- Revisar posibles efectos colaterales con módulos `admin/course`, `admin/student` y economía cuando se mockean servicios externos; mantener contratos actualizados.
- Documentar cualquier hallazgo o deuda técnica derivada de las suites en `docs/testing/test_admin_subject.md` durante el reporte final.

