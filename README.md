# 🍽️ Epicentro Gourmet
### Sistema de Gestión de Festivales Gastronómicos

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Maven-Build-red?logo=apachemaven)
![Status](https://img.shields.io/badge/Proyecto-Hito%202-purple)

Aplicación web desarrollada para la gestión integral de **Epicentro Gourmet**, un centro de convenciones y predio ferial que organiza festivales gastronómicos temáticos.

El sistema permite administrar festivales, unidades de venta, empleados, platos, pedidos y generar reportes para analizar el rendimiento económico de los eventos.

---

## 👥 Equipo 10

| Integrante | Usuario de GitHub |
|---|---|
| Livia Méndez | [@liimendez](https://github.com/liimendez) |
| Santiago Sosa | [@santisosa-gh](https://github.com/santisosa-gh) |

---

## 📌 Descripción del sistema

Epicentro Gourmet es un sistema de gestión desarrollado para facilitar la administración de festivales gastronómicos y sus respectivas unidades de venta.

La aplicación contempla distintos roles de usuario, cada uno con funcionalidades específicas:

- **Administrador:** consulta festivales, unidades de venta, rankings de recaudación y reportes de mayores cánones.
- **Responsable:** administra empleados (envios de email)  y platos, consulta información de su unidad de venta y genera reportes de recaudación y rentabilidad.
- **Empleado:** consulta su información laboral, festivales en los que trabajó, pdf de haberes y unidad de trabajo actual.
- **Cajero:** cuenta con la funcionalidad de cierre de caja diario y alta de pedidos, puede ver su informacion laboral, descargar en formato pdf sus haberes, ver en que unidades trabajo, tiene tickets de los pedidos realizados.

También permite registrar pedidos, calcular costos, liquidar haberes y analizar la rentabilidad de las unidades de venta.

---

## 🛠️ Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security
- Maven
- MySQL
- Thymeleaf
- Git y GitHub

---

## 🚀 Cómo levantar el proyecto

### 1. Requisitos previos

Antes de ejecutar la aplicación, es necesario contar con:

- Java JDK 17.
- MySQL instalado y funcionando.
- IntelliJ IDEA, Eclipse o Visual Studio Code.
- Maven (o utilizar el Maven Wrapper incluido en el proyecto).
- Git.

### 2. Clonar el repositorio

```bash
git clone [URL_DEL_REPOSITORIO](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10)
```

Ingresar a la carpeta del proyecto:

```bash
cd NOMBRE_DEL_PROYECTO
```

### 3. Crear la base de datos

Desde MySQL ejecutar:

```sql
CREATE DATABASE epicentro_gourmet;

copiar la bd del proyecto que incluye datos ya precargados
en la mayoria de usuarios la password es admin o password 

```

### 4. Configurar las variables de entorno

El proyecto utiliza variables de entorno para mantener separadas las credenciales y la configuración de conexión a la base de datos del código fuente.

En la raíz del proyecto se encuentra:

- **`.env`**: archivo local que contiene las variables de entorno reales para ejecutar la aplicación. No debe subirse al repositorio.
- **`.env.example`**: archivo de ejemplo que contiene las mismas variables, pero con valores ficticios. Sirve como plantilla para que cada integrante pueda configurar su propio entorno.

Para comenzar, copiar el archivo de ejemplo y crear el archivo local:

```bash
cp .env.example .env
```

En Windows también se puede realizar esta copia manualmente.

Completar el archivo `.env` con los datos correspondientes a la instalación local de MySQL.

**Ejemplo de configuración (`.env.example`):**

```env

DB_URL=jdbc:mysql://localhost:3306/epicentro_gourmet?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull
USERNAME=root
PASSWORD=TU_CLAVE
MAIL_USERNAME=TU_CORREO
MAIL_PASSWORD=TU_CLAVE_DE_APLICACION

```

> ⚠️ Los valores del archivo `.env.example` son ficticios y deben reemplazarse por las credenciales correspondientes a cada entorno.

**Importante:** Spring Boot no carga automáticamente los archivos `.env` por defecto. Las variables deben estar disponibles en el entorno de ejecución de IntelliJ IDEA o mediante el mecanismo de carga de variables configurado en el proyecto.

### 5. Ejecutar la aplicación

Desde la terminal, ubicada en la carpeta raíz del proyecto:

Con Maven:

```bash
mvn spring-boot:run
```

O utilizando Maven Wrapper:

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

También es posible ejecutar la clase principal de Spring Boot desde IntelliJ IDEA.

### 6. Acceder al sistema

Una vez iniciada la aplicación, abrir el navegador en:

```text
http://localhost:8080
```

---

## 📋 Casos de uso implementados

A continuación se detallan los casos de uso adicionales desarrollados por cada integrante.

### 👩💻 Livia Méndez — [@liimendez](https://github.com/liimendez)

| Caso de uso implementado | Descripción | Pull Request |
|---|---|---|
| Evaluación de desempeño de empleados | Permite al responsable evaluar a los empleados de su unidad en un festival, mediante criterios de puntualidad, atención al cliente e higiene. | [PR #19](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/19) |
| Historial de ventas por día y entre fechas | Permite consultar el historial de ventas de una unidad de venta, por día o dentro de un período determinado. | [PR #20](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/20) |
| Reporte de desempeño histórico | Permite al responsable consultar el historial de evaluaciones de un empleado por festival, con promedio general, mejor y peor festival. | [PR #24](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/24) |
| Reporte de evolución de recaudación | Permite consultar la evolución diaria de la recaudación de una unidad, con total del período, mejor y peor día y variación entre días. | [PR #25](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/25) |

### 👨‍💻 Santiago Sosa — @santisosa-gh

| Caso de uso implementado | Descripción | Pull Request |
|---|---|---|
| Gestión de Stock de Platos | Permite al Responsable registrar, modificar y controlar el stock disponible de los platos de la unidad, descontando automáticamente las unidades al registrar un pedido y bloqueando la operación en caso de no haber stock suficiente. | [PR #26](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/26) |
| Gestión de Métodos de Pago | Permite al Responsable de la unidad realizar el ABM (Alta, Baja, Modificación) de los métodos de pago disponibles (ej. Efectivo, Tarjeta, Transferencia), configurando para cada uno su nombre y un porcentaje de descuento comercial aplicable a los pedidos. | [PR #27](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/pull/27) |


---

## 📐 Documentación del proyecto

- 📊 **Diagrama de clases:** [Ver diagrama de clases (PDF)](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10/blob/main/diagrama.drawio.pdf)
- 💻 **Repositorio GitHub:** [TPGrupalOO2Hito2Grupo10](https://github.com/liimendez/TPGrupalOO2Hito2Grupo10)

## 🌿 Metodología de trabajo

El desarrollo se organiza mediante Git y GitHub.

Cada funcionalidad se trabaja en una rama independiente y posteriormente se incorpora a `main` mediante Pull Requests.

Se busca mantener un desarrollo organizado, facilitar la revisión del código y permitir el trabajo colaborativo entre los integrantes.

---

**Universidad Nacional de Lanús (UNLa)**  
Licenciatura en Sistemas – Orientación a Objetos II  
Trabajo Práctico Grupal 2026 – Hito 2

**Equipo 10 | Epicentro Gourmet** 🍽️
