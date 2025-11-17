# Product Requirements Document: Backoffice de gestión de agentes

### 1. Overview & Goal
*   **Problem:** Actualmente los agentes mostrados en el frontend se gestionan mediante un archivo JSON estático, lo que dificulta la edición, validación y publicación de cambios por parte de administradores no técnicos.
*   **Proposed Solution:** Implementar un backoffice administrativo para CRUD de agentes, con validación de esquema y flujo de publicación que alimente el catálogo consumido por el frontend. Para V1, se prioriza una solución mínima viable integrada en la app existente (ruta protegida) que opere sobre el catálogo actual o una capa de servicio equivalente.
*   **Success Metrics:**
    - Tiempo para crear/editar/publicar un agente < 5 minutos.
    - Cero entradas inválidas (validación de esquema 100%).
    - Reversión de cambios posible en < 2 minutos (historial o backup simple en V1).

### 2. Target Audience
*   **Primary User Role(s):** Administrador de contenido (Admin).
*   **Permissions:**
    - Admin: acceso al backoffice, CRUD de agentes, previsualización y publicación.

### 3. Functional Requirements & User Stories
*   **FR-01:** Autenticación básica para acceso al backoffice.
    *   **User Story:** Como Admin, quiero autenticarme para acceder al backoffice y proteger la gestión de agentes.
    *   **Acceptance Criteria:**
        *   Dado que no estoy autenticado, cuando intento acceder a la ruta del backoffice, entonces se me redirige a la pantalla de login.
        *   Dado que ingreso credenciales válidas, cuando inicio sesión, entonces accedo al backoffice.

*   **FR-02:** Listado y búsqueda de agentes existentes.
    *   **User Story:** Como Admin, quiero ver y buscar agentes para ubicarlos rápidamente.
    *   **Acceptance Criteria:**
        *   Dado que accedo al backoffice, cuando abro la vista de lista de agentes, entonces se muestran con paginación/búsqueda por nombre, grupo y etiquetas.

*   **FR-03:** Creación/edición de agentes con validación.
    *   **User Story:** Como Admin, quiero crear y editar agentes con validación de esquema para evitar datos inválidos.
    *   **Acceptance Criteria:**
        *   Dado un formulario de agente, cuando completo campos requeridos (id, name, description, group, tags, icon, sourceUrl, version, author, visibility), entonces el sistema valida tipos y formatos (URL, unicidad de id) antes de guardar.
        *   Dado que hay errores, cuando intento guardar, entonces veo mensajes claros y no se persiste hasta corregir.

*   **FR-04:** Eliminación de agentes.
    *   **User Story:** Como Admin, quiero eliminar un agente cuando ya no debe mostrarse.
    *   **Acceptance Criteria:**
        *   Dado un agente existente, cuando confirmo eliminar, entonces el agente deja de mostrarse en el frontend (borrado físico en V1).

*   **FR-05:** Previsualización y publicación.
    *   **User Story:** Como Admin, quiero previsualizar cambios y publicarlos al catálogo consumido por el frontend.
    *   **Acceptance Criteria:**
        *   Dado cambios locales, cuando abro previsualización, entonces veo cómo quedará el catálogo.
        *   Dado que confirmo publicar, cuando ejecuto la acción, entonces el catálogo fuente queda actualizado correctamente.

*   **FR-06:** Respaldo/rollback simple.
    *   **User Story:** Como Admin, quiero poder revertir el último cambio si algo sale mal.
    *   **Acceptance Criteria:**
        *   Dado un cambio publicado, cuando activo "revertir", entonces se restaura el archivo previo (backup del catálogo V1).

### 4. Out of Scope
*   Integraciones SSO/OAuth (V2).
*   Auditoría detallada y flujos de aprobación multiusuario (V2).
*   Gestión de permisos por roles múltiples (V2).
*   Backend y base de datos completa (si se decide mantener JSON en V1).
