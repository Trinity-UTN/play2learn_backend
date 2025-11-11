# Architecture Decisions: Backoffice de gestión de agentes (V1)

Fecha: 2025-10-31

## Contexto
El frontend actual (Vue + Vuetify + Vite) consume un catálogo estático desde `public/agents_catalog.json` a través de `src/services/catalogLoader.js`/`src/composables/useCatalog.js`.

## Objetivo
Habilitar un backoffice mínimo viable para CRUD de agentes con validación y publicación hacia el catálogo consumido por el frontend, minimizando cambios disruptivos.

## Decisiones

1) Persistencia en V1: archivo JSON con backups
- Mantener el catálogo en `public/agents_catalog.json` para V1.
- Implementar mecanismo de backups (p. ej., `public/agents_catalog.backup-<timestamp>.json`) previo a cada publicación.
- Validación de esquema JSON antes de permitir guardar/publicar.

Riesgos y mitigaciones:
- Riesgo: corrupción del JSON. Mitigación: validación estricta + backup y rollback.
- Riesgo: condiciones de carrera. Mitigación: bloqueo de escritura durante publicación en V1 (single-user asumido en entorno local).

2) Backoffice integrado (ruta protegida en la misma SPA)
- Añadir rutas `/admin/login` y `/admin/agents` a `router/index.js`.
- Reutilizar Vuetify para formularios y tablas.
- Guard simple de autenticación en el cliente (local) para V1.

Riesgos y mitigaciones:
- Seguridad limitada (cliente). Mitigación: V1 orientado a entorno controlado; V2 considerará backend+auth robusto.

3) Servicio de catálogo con capa de abstracción
- Crear `src/services/catalogAdmin.js` que gestione:
  - Cargar catálogo actual.
  - Validar contra un esquema (`src/types/catalog.js` puede ampliarse con validaciones).
  - Persistir cambios (escritura del JSON) y backups.
- Mantener compatibilidad con `catalogLoader.js` actual.

Riesgos y mitigaciones:
- Escribir archivos desde una SPA no es posible en producción. Mitigación: para V1 en entorno local, usar comandos de desarrollo (Node script) para persistir; la SPA emitirá acciones y un script Node hará la escritura.
  - Añadir scripts Node: `scripts/catalog/publish.js`, `scripts/catalog/validate.js`.

4) Validación de esquema
- Definir un esquema JSON para agentes (campos: id, name, description, group, tags[], icon, sourceUrl, version, author, visibility, featured, lastUpdated).
- Incorporar `ajv` para validar en scripts Node y en el front.

5) Despliegue y entorno
- V1 pensado para uso local (Windows PowerShell). Publicación mediante script Node que sobrescribe `public/agents_catalog.json`.
- En V2, migrar a API/DB y auth robusta.

## Alternativas consideradas
- Backend Express + DB (PostgreSQL/SQLite) en V1: descartado por aumentar alcance y complejidad.
- App de backoffice separada: descartada en V1; se prefiere integración rápida con la SPA.

## Impacto en el sistema
- Nuevos scripts Node para validar/publicar catálogo.
- Nuevas rutas y vistas de administración.
- Ampliación de tipos/esquema de `src/types/catalog.js` o un nuevo archivo de esquema JSON.

## Plan de migración
- Añadir esquema y validación.
- Implementar UI de backoffice (login, listado, formulario CRUD, previsualización).
- Implementar scripts de publicación/backup.
- Conectar UI a scripts mediante comandos de desarrollo (botones que muestren instrucción y verificación de resultado en V1).
