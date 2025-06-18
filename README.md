# 🛠️ Find-Finx - Sistema de búsqueda y prestación de servicios.

![Java](https://img.shields.io/badge/Java-21-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-lightgrey?logo=mysql)
![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-yellow)
![Backend Only](https://img.shields.io/badge/Interfaz-Pendiente-lightblue)

---

## 📌 Descripción

Este proyecto tiene como objetivo conectar **clientes** con **especialistas** a través de una plataforma centralizada. Los usuarios pueden buscar profesionales según su **oficio** o **ciudad**, enviar solicitudes de trabajo, y dejar **reseñas** al finalizar un servicio.

Por otro lado, los especialistas pueden **gestionar solicitudes**, organizar sus **trabajos** (tanto dentro como fuera de la app) y mantener un historial de sus proyectos.

---

## 🚀 Funcionalidades Principales

- Registro e inicio de sesión de usuarios.
- Solicitud para convertirse en especialista.
- Búsqueda de especialistas según filtros.
- Envío y gestión de solicitudes de trabajo.
- Gestión de trabajos dentro de la app (**TrabajoApp**) y externos (**TrabajoExterno**).
- Dejar reseñas tras finalizar un trabajo.
- Gestión de usuarios con distintos **roles** (cliente, especialista, admin).

---

## 🧱 Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **JPA / Hibernate**
- **SQL**
- **Postman** (para pruebas)
- **Lombok**
- **Maven**

---

## 🧪 Pruebas

- Los endpoints están testeados en **Postman**.
- Se requiere autenticación para acceder a los recursos protegidos según el rol.
- Soporta filtrado de datos con **Specifications** dinámicas (por fecha, estado, título, etc.).

---

## 📌 Estado del Proyecto

- ✅ Backend funcional con endpoints listos.
- 🛠️ En desarrollo la interfaz visual (frontend).

---

## ⚙️ Configuración del proyecto

Este proyecto usa Spring Boot con precarga automática de datos al iniciar, y permite conexión con bases de datos relacionales como PostgreSQL (por ejemplo con Neon), MySQL, entre otras.

### ✅ Requisitos previos

- Java 17+

- Base de datos relacional (PostgreSQL, MySQL, etc.)

- Maven

- IDE como IntelliJ o Eclipse


## 🛠️ Configuración de la base de datos

En el archivo application.properties o application.yml, completá los datos de tu base. A continuación hay un ejemplo con PostgreSQL (Neon):

```properties
spring.datasource.url=jdbc:postgresql://tu-host.neon.tech:5432/tu_basededatos
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

También podés usar MySQL o cualquier base de datos compatible con JPA cambiando el url y el dialect correspondiente.

**¡No te olvides de agregar el driver o la dependencia correspondiente!**

---

## 📦 Precarga de datos

Cuando se inicia la aplicación, se cargan automáticamente roles, ciudades y otros datos base necesarios para que el sistema funcione sin necesidad de ingresar datos manuales.


---

## 🚀 Cómo ejecutar

1. Cloná el repositorio.


2. Configurá tu archivo application.properties.


3. Ejecutá la clase FindFixAppApplication.java.


4. Usá Postman (u otra herramienta) para probar las rutas.

---

## ✍️ Autores

**Arjona Camila, Galeano Facundo, Figueroa Belén, Oliviero Marco**

---

