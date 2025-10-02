# 👨‍💻 Gesa - Sistema de Gestión de Inventario

![Logo de Gesa](src/main/resources/static/images/gesa.png)

## 📝 Descripción del Proyecto

**Gesa** es un sistema de gestión de inventario, clientes, proveedores y ventas. La aplicación ofrece una interfaz web completa para administrar las operaciones diarias de un negocio, desde el registro de clientes hasta el seguimiento de ventas.

La página de inicio cuenta con un **dashboard** interactivo que proporciona una visión general del estado del negocio con métricas y gráficos dinámicos.

---

## ✨ Características Principales

* **Dashboard Interactivo**: Visualiza estadísticas clave como ventas totales, stock de productos y clientes registrados. Incluye un gráfico de ventas mensuales y listados de las últimas ventas y productos más populares.
* **Gestión de Clientes**: CRUD (Crear, Leer, Actualizar, Eliminar) completo para la administración de clientes. Permite buscar, paginar y cambiar el estado de los clientes.
* **Gestión de Proveedores**: Módulo para gestionar proveedores con funcionalidades de búsqueda, paginación y opciones para actualizar, eliminar y cambiar el estado.
* **Gestión de Productos**: Administra productos con información detallada como nombre, precio, stock, categoría y proveedor.
* **Gestión de Ventas**: Registra y anula ventas, con la capacidad de buscar y paginar registros.
* **Reportes en PDF**: Genera reportes en formato PDF para el listado de clientes y ventas, filtrados por rango de fechas.
* **Sistema de Autenticación**: Implementación de Spring Security para el inicio y registro de sesión seguro, con roles de usuario.
* **Diseño Responsivo**: La interfaz de usuario está optimizada con Tailwind CSS para una experiencia fluida tanto en dispositivos de escritorio como en móviles.

---

## 🛠️ Tecnologías Utilizadas

El proyecto fue desarrollado utilizando el siguiente stack tecnológico:

* **Backend**: Spring Boot
* **Base de Datos**: MySQL
* **Persistencia**: Spring Data JPA, Hibernate
* **Seguridad**: Spring Security
* **Templates**: Thymeleaf
* **Estilos**: Tailwind CSS
* **Reportes PDF**: OpenHTMLToPDF
* **Dependencias**: Lombok, Spring Boot DevTools, Spring Boot Validation, MySQL Connector/J.
* **Visualización de datos**: Chart.js

---

## 🚀 Guía de Instalación y Ejecución

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local.

### Prerrequisitos

* **Java 17** o superior
* **Maven**
* **MySQL** (servidor de base de datos)
* Un IDE compatible con Java y Spring Boot (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Pasos de Configuración

1.  **Clonar el Repositorio**:
    ```bash
    git clone [https://github.com/mquisped23/gestiondeinventariogesa.git](https://github.com/mquisped23/gestiondeinventariogesa.git)
    cd gestiondeinventariogesa
    ```

2.  **Configurar la Base de Datos**:
    * Abre tu cliente de MySQL.
    * Crea una base de datos con el nombre `gestion_inventario`.
    * Las tablas se generarán automáticamente al iniciar la aplicación gracias a la configuración de JPA.

3.  **Configurar `application.properties`**:
    * Abre el archivo `src/main/resources/application.properties`.
    * Asegúrate de que la configuración de la base de datos coincida con la tuya. La configuración por defecto es:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/gestion_inventario?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    spring.datasource.username=miguel
    spring.datasource.password=miguelelmejor
    ```

4.  **Ejecutar la Aplicación**:
    * Desde tu IDE, ejecuta la clase `GestionInventariadoApplication.java`.
    * Alternativamente, puedes usar Maven desde la terminal:
    ```bash
    mvn spring-boot:run
    ```

5.  **Acceder a la Aplicación**:
    * Abre tu navegador y ve a `http://localhost:8080/login`.

---

## 📁 Estructura del Proyecto

El proyecto sigue una estructura de capas típica de Spring Boot para mantener el código organizado y modular.

src/main/java/com/edu/
├── config/                  # Configuraciones de seguridad
├── controller/              # Controladores que manejan las peticiones HTTP
├── entity/                  # Entidades JPA y clases de modelo
├── exception/               # Clases para manejo de excepciones
├── repository/              # Interfaces de acceso a datos (JPA Repositories)
├── security/                # Lógica de autenticación de Spring Security
├── service/                 # Interfaces de la lógica de negocio
│   └── impl/                # Implementaciones de los servicios
└── GestionInventariadoApplication.java

---

## 👥 Contribución

¡Las contribuciones son bienvenidas! Si deseas mejorar el proyecto, no dudes en abrir un *issue* o enviar un *pull request*.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
