# Alcance del Diagrama de Clases (Entidades y Enums)

Este documento define el alcance exacto para el diagrama de clases que se generará para el backend.

## Definición de Entidad

Se considerará **entidad** toda clase Java que cumpla al menos una de estas condiciones:

1. Está anotada con `@Entity` (Javax/Jakarta).
2. (Opcional) Está anotada con `@Embeddable`.
3. (Opcional) Está anotada con `@MappedSuperclass`.

Anotaciones a detectar (equivalentes):

- `@jakarta.persistence.Entity`
- `@javax.persistence.Entity`
- `@jakarta.persistence.Embeddable`
- `@javax.persistence.Embeddable`
- `@jakarta.persistence.MappedSuperclass`
- `@javax.persistence.MappedSuperclass`

## Definición de Enum

Se considerará **enum** cualquier tipo Java declarado con la palabra clave `enum`.

## Regla de Relaciones (lo que se mostrará)

El diagrama incluirá relaciones únicamente cuando se puedan inferir desde tipos conocidos del índice, siguiendo estas reglas:

1. Si una entidad (o embeddable / mapped superclass) tiene un campo cuyo tipo coincide con otro tipo del índice (entidad o enum), se infiere una relación.
2. Si el campo es parametrizado (por ejemplo `List<Foo>`, `Set<Foo>`, `Optional<Foo>`), se infiere la relación hacia `Foo` si `Foo` existe dentro del índice.
3. En el resto de casos (p. ej. tipos no indexados, tipos genéricos sin resolución clara, o tipos puramente Java estándar), no se infiere relación.

## Regla de Exclusión

El diagrama **no** incluirá:

- Controladores (`*Controller`)
- Servicios (`*Service`)
- Repositorios (`*Repository`)
- DTOs / modelos de transferencia que no sean entidades
- Clases no anotadas con JPA (cuando no sean enums)

## Criterio de “Indexado”

Antes de generar el diagrama, se construirá un índice con:

- Todas las entidades detectadas por anotación JPA.
- Todos los enums detectados por construcción `enum`.

El diagrama final deberá estar restringido a ese conjunto.

