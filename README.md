# Play2Learn - Backend

**Play2Learn** es un proyecto realizado para la Cátedra de Proyecto Final de la carrera de Ingeniería en Sistemas. Es una aplicación de gamificación educativa diseñada para el nivel secundario, que tiene como objetivo facilitar la enseñanza al docente y motivar el aprendizaje del estudiante mediante un sistema de actividades, beneficios y recompensas virtuales.

Este repositorio contiene el código fuente del backend para la plataforma. El sistema está construido utilizando Java y Spring Boot, proporcionando una API REST robusta para gestionar la lógica de negocio, usuarios, actividades educativas y el sistema de economía virtual.

## 🛠️ Tecnologías

El proyecto utiliza las siguientes tecnologías y librerías principales:

*   **Java 21**: Lenguaje de programación.
*   **Spring Boot 3.4.5**: Framework principal.
*   **Spring Security**: Para autenticación y autorización (JWT).
*   **Spring Data JPA**: Para la persistencia de datos.
*   **PostgreSQL**: Base de datos relacional.
*   **Lombok**: Para reducir el código boilerplate.
*   **ModelMapper**: Para el mapeo entre Entidades y DTOs.
*   **Maven**: Gestión de dependencias y construcción.

## 📦 Módulos Principales

El backend está organizado en módulos funcionales para mantener una arquitectura limpia y escalable. A continuación se detallan los principales paquetes:

### 1. 👤 User (`/user`)
Gestiona la identidad y acceso de los usuarios.
*   **Autenticación**: Login, registro y gestión de tokens JWT.
*   **Roles**: Gestión de permisos y roles de usuario.

### 2. 🖼️ Profile (`/profile`)
Encargado de la personalización y perfil del usuario.
*   **Avatar**: Gestión de avatares personalizados para los estudiantes.
*   **Datos de Perfil**: Información personal y configuraciones de cuenta.

### 3. 🎓 Admin (`/admin`)
Módulo administrativo para la gestión académica e institucional.
*   **Course**: Gestión de cursos y divisiones.
*   **Subject**: Administración de materias.
*   **Teacher**: Gestión de docentes.
*   **Year**: Configuración de años lectivos.
*   **Student**: Gestión administrativa de alumnos.

### 4. 🎮 Activity (`/activity`)
El núcleo de la gamificación. Contiene la lógica para las diferentes actividades educativas:
*   **Ahorcado**: Juego clásico de palabras.
*   **Memorama**: Juego de memoria.
*   **Preguntados**: Trivias de preguntas y respuestas.
*   **Completar Oración**: Ejercicios de gramática y sintaxis.
*   **Árbol de Decisión**: Actividades de lógica ramificada.
*   **Clasificación**: Ejercicios de categorización.
*   **Ordenar Secuencia**: Actividades de ordenamiento lógico.

### 5. 💰 Economy (`/economy`)
Simula un sistema económico virtual para enseñar educación financiera.
*   **Wallet**: Billeteras virtuales de los usuarios.
*   **Transaction**: Historial y procesamiento de transacciones.
*   **Reserve**: Gestión de reservas financieras.

### 6. 📈 Investment (`/investment`)
Módulo avanzado de educación financiera que permite a los estudiantes simular inversiones.
*   **Fixed Term Deposit**: Simulación de Plazos Fijos.
*   **Stock**: Simulación de compra/venta de acciones.
*   **Saving Account**: Cajas de ahorro con rendimiento.

### 7. 🎁 Benefits (`/benefits`)
Sistema de recompensas y beneficios canjeables por los puntos o moneda virtual obtenida en las actividades.

### 8. 📊 Statistics (`/statistics`)
Recopilación y análisis de datos sobre el rendimiento de los estudiantes en las actividades y su progreso en el juego.

## 🚀 Configuración y Ejecución

### Prerrequisitos
*   JDK 21 instalado.
*   Maven instalado.
*   PostgreSQL corriendo localmente o accesible.

### Variables de Entorno
Asegúrate de configurar las variables de entorno necesarias en un archivo `.env` o en las propiedades de tu IDE, incluyendo la conexión a la base de datos y secretos JWT.

### Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación se iniciará por defecto en el puerto `8080`.
