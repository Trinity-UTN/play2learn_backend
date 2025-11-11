# Feature Brief — Admin Panel for Agents

## Problem Statement
Non-technical users need to manage the agents catalog without editing JSON manually. The app is a static Vue 3 + Vuetify frontend that reads `public/agents_catalog.json`. We need an admin panel to perform CRUD on groups and agents, reorder entries, validate data, and support import/export. Access must be gated by a simple secret key.

## Goals
- Separate admin route to manage the catalog.
- User-friendly CRUD for groups and agents with validation.
- Reordering of groups and agents.
- Import/Export full catalog JSON.
- Preview and confirm changes before exporting.
- Gate access via basic secret key entry.

## Non-Goals
- No server-side users/roles.
- No backend storage or server file writes at runtime.
- No version history.

## Success Criteria
- `/admin` accessible only after entering the secret key.
- Add/Edit/Delete groups and agents with validation feedback.
- Reorder groups and agents.
- Import JSON to replace working catalog; export modified catalog to file.
- Main catalog view can render from the working copy.

## Key Design Decisions
- Persistence: In-memory working copy with optional LocalStorage autosave; Import/Export for deployment. Document replacement of `public/agents_catalog.json` during deploy.
- Auth: Frontend gate comparing user secret with `import.meta.env.VITE_ADMIN_SECRET`.
- Validation: Required fields, unique IDs in scope, safe ID pattern, schema checks.
- UX: Vuetify dialogs/forms, lists/tables with drag-and-drop, confirmation modals.

## Architecture & Components
- Route: `/admin` with gate screen.
- Views/Components:
  - `AdminLoginView` (secret entry)
  - `AdminDashboardView` (tabs: Groups, Agents, Import/Export)
  - `GroupList` + `GroupFormDialog`
  - `AgentList` + `AgentFormDialog`
  - `ReorderList` utilities (drag-and-drop)
  - `ImportExportPanel`
- Composable:
  - `useCatalogEditor` to manage working copy, validation, CRUD, reorder, import/export, LocalStorage sync.

## Risks & Mitigations
- Frontend-only secret is discoverable: acceptable per requirement; documented.
- Data loss on refresh: mitigated via LocalStorage autosave and explicit Export.
- Schema drift: include validator and schema version field.

## Milestones
1) Routing + Auth Gate
2) Catalog Editor Composable
3) Group CRUD + Validation
4) Agent CRUD + Validation
5) Reordering UX
6) Import/Export Panel
7) Integration with main view + smoke tests
8) Documentation
