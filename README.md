# 🛠️ Find-Fix - Sistema de búsqueda y prestación de servicios

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-brightgreen?logo=springboot)
![Angular](https://img.shields.io/badge/Angular-v20%2B-dd0031?logo=angular)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-336791?logo=postgresql)
![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-yellow)

---

## 📌 Descripción

**Find-Fix** es una plataforma integral full-stack diseñada para conectar **clientes** con **especialistas** de diversos oficios.

La aplicación ofrece una experiencia completa donde los usuarios pueden buscar profesionales, contratar servicios y calificarlos. Los especialistas cuentan con un **Dashboard interactivo** para gestionar sus solicitudes, visualizar métricas de sus trabajos (ingresos, historial, calificaciones) mediante gráficos dinámicos y organizar su agenda tanto para trabajos dentro de la app como externos.

---

## ⚔️ Funcionalidades Principales

### 👤 Usuarios (Clientes y Especialistas)
- Registro e inicio de sesión seguro (JWT).
- Gestión de perfiles de usuario.

### 🔎 Clientes
- Búsqueda de especialistas con filtros por **oficio**, **ciudad** y **calificación**.
- Envío de solicitudes de trabajo detalladas.
- Gestión de favoritos.
- Sistema de reseñas y puntuación al finalizar un servicio.

### 👷 Especialistas
- **Dashboard interactivo** con métricas en tiempo real (Gráficos de ingresos, tasa de aceptación, etc.).
- Gestión de solicitudes (Aceptar/Rechazar).
- Organización de trabajos:
    - **TrabajoApp:** Gestionados dentro del flujo de la plataforma.
    - **TrabajoExterno:** Registro de trabajos particulares para control financiero.
- Visualización de historial y reseñas recibidas.

### 🛡️ Administrador
- Gestión de usuarios, roles y oficios disponibles.

---

## 🧱 Tecnologías Utilizadas

### 🔙 Backend
- **Java 21**
- **Spring Boot 3** (Spring Security, Web, Validation)
- **JPA / Hibernate** (Persistencia de datos)
- **Maven** (Gestión de dependencias)
- **Lombok**

### 🔜 Frontend
- **Angular v19/20** (Framework SPA)
- **TypeScript**
- **Chart.js / ng2-charts** (Visualización de datos y gráficas)
- **HTML5 / CSS3** (Diseño responsivo y moderno)

### 🗄️ Base de Datos
- **PostgreSQL** (Implementado en la nube con **Neon Tech**)

---

## ⚙️ Configuración del Proyecto

El proyecto está dividido en dos partes principales: `backend` y `frontend`. A continuación se detalla cómo ejecutar cada una.

### 📋 Requisitos previos
- Java 17 o superior (Recomendado Java 21).
- Node.js (v18 o superior) y npm.
- Angular CLI (`npm install -g @angular/cli`).
- Cuenta en Neon.tech (u otra instancia de PostgreSQL).

---

### 🛠️ 1. Configuración del Backend

1.  Navega a la carpeta del backend.
2.  Configura las variables de entorno o edita el archivo `src/main/resources/application.properties` con las credenciales de tu base de datos **Neon PostgreSQL**:

    ```properties
    # Conexión a Neon PostgreSQL
    spring.datasource.url=jdbc:postgresql://tu-host-de-neon.aws.neon.tech:5432/tu_base_de_datos?sslmode=require
    spring.datasource.username=tu_usuario_neon
    spring.datasource.password=tu_password_neon

    # Configuración JPA
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```

3.  Ejecuta la aplicación:
    - Desde tu IDE (IntelliJ/Eclipse) ejecutando `FindFixAppApplication.java`.
    - O vía terminal: `./mvnw spring-boot:run`.

> **Nota:** El sistema incluye un `DataInitializer` que precarga roles, oficios y ciudades automáticamente al iniciar si la base de datos está vacía.

---

### 💻 2. Configuración del Frontend

1.  Navega a la carpeta del frontend:
    `cd frontend/find-fix-app`

2.  **Instalar dependencias (IMPORTANTE):**
    Es crucial ejecutar este comando para descargar la carpeta `node_modules` con todas las librerías necesarias (Angular, Chart.js, etc.) que no se incluyen en el repositorio:
    `npm install`

3.  **Ejecutar el servidor de desarrollo:**
    `ng serve`

4.  Abre tu navegador en `http://localhost:4200/`.

---

## 🧪 Pruebas y Documentación

- **Postman:** Los endpoints del backend están probados y organizados.
- [Find-Fix - Endpoints](https://docs.google.com/document/d/1lvLfzfLlXB_Eut2KI4ePRHm3PPPWE8_JrSTTgYvkI00/edit?usp=sharing)

---

## 📌 Estado del Proyecto

- ✅ **Backend:** Completo, asegurado y conectado a la nube.
- ✅ **Frontend:** Interfaz funcional, integración de gráficos y dashboard implementada.
- 🔄 **En proceso:** Refinamiento de estilos y optimización de experiencia de usuario (UX).

---

## ✍️ Autores

### **Arjona Camila, Galeano Facundo, Figueroa Belén, Oliviero Marco**

---
