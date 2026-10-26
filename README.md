Hito 2 - TP Epicentro Gourmet - Grupo 10
👩‍💻 Integrante del equipo 10: Livia Mendez - feature/LiiMendez

🚀 Cómo levantar el proyecto
1. Requisitos previos

   Java 17
   Maven (mvnw incluido)
   MySQL 8
   Git

2. Clonar repositorio

git clone https://github.com/liimendez/TPGrupalOO2Hito2Grupo10.git
cd TPGrupalOO2Hito2Grupo10
git checkout main
git pull origin main

3. Configurar Base de Datos

Crear una BD en MySQL:

CREATE DATABASE epicentro_gourmet;

Configurar src/main/resources/application.yml:

spring:
datasource:
url: jdbc:mysql://localhost:3306/epicentro_gourmet
username: root
password: TU_PASSWORD
jpa:
hibernate:
ddl-auto: update
show-sql: true

4. Cargar datos de prueba

El proyecto incluye script de carga con:

    Tabla costos: $100.000 sueldo_base, $150.000 cocinero, $100.000 ayudante, $50.000 lavaplatos, $500 área, $10 montaje, $2000 fijo foodtruck
    Festivales, unidades, empleados y pedidos de prueba
    Se ejecuta automático al iniciar o vía data.sql

5. Levantar la app

./mvnw spring-boot:run
# o
mvn spring-boot:run

Abrir en el navegador:

http://localhost:8080

6. Usuarios de prueba

   Admin: admin / admin (ve ranking y mayor canon)
   Responsable: responsable / 1234 (ve liquidación, cálculo canon)
   Unidad de venta: unidad / 1234

7. Endpoints clave para pre-defensa

   Ranking: /festival/{id}/ranking -> valida $105.000
   Mayor Canon: /festival/{id}/mayor-canon?x=3 -> valida $8.000 MC 8679004
   Liquidación: /empleados/{id}/liquidar -> valida $225k (38 días lavaplatos) y $230k (52 días cajero 3 años)
   Email: se dispara al dar alta empleado

8. Verificar que todo funciona

   Login como Admin -> Festival -> Ranking
   Login como Admin -> Festival -> Mayor Canon (probar x=3)
   Login como Responsable -> Empleados -> Liquidar -> ver fórmula oficial

CATEGORÍA: ADMINISTRADOR
3. Ranking de Unidades

Para un Festival, genera una lista ordenada descendente por recaudación.

    Endpoint: GET /festival/{id}/ranking
    Service: FestivalService.getRankingByRecaudacion()
    Template: festival/ranking.html
    Validación: MC Donald's $105.000 con 3 pedidos - orden descendente OK

4. Unidades con Mayor Canon

Dado un festival devuelve las "x" unidades que más gastaron en canon indicando nombre comercial, código, tipo y canon.

    Endpoint: GET /festival/{id}/mayor-canon?x=3
    DTO: ReporteMayoresCanonDTO (DetalleCanonDTO)
    Service: FestivalService.getUnidadesMayorCanon()
    Validación: MC Donald's MC 8679004 - $8.000 - top 3 con param x=3 OK

CATEGORÍA: RESPONSABLE
1. Altas, Bajas y Modificaciones

Métodos para agregar, modificar y eliminar empleados con baja lógica.

    Eliminación lógica con eliminado boolean
    Email automático al dar de alta

3. Cálculo de Canon

Método que devuelve el monto a pagar por cada unidad:

    Food Truck: (areaSup * $500) + $2000 + requiere conexión eléctrica
    Puesto: (areaSup * $500) - (Tiempo Montaje * $10)
    Tabla costos: costos - sueldo_base $100.000 / area $500 / montaje $10 / fijo_foodtruck $2000

4. Liquidación de Haberes: Fórmula OFICIAL PDF

Calcular el sueldo del personal según su tipo:

Datos base:

    Sueldo Base: $100.000
    Plus: Cocinero $150.000 / Ayudante $100.000 / Lavaplatos $50.000
    Antigüedad: $5.000 x año
    Cajero: Sueldo Base + antigüedad

Tabla oficial días:

    Hasta 15 días: 50% (sueldo base + plus)
    De 16 a 30 días: 100%
    Más de 30 días: suma proporcional

Fórmula implementada:

(1) totalACobrarCocina = (int(diasDurFest/15)+1) * 50% * (sueldoBase+plusCategoria)
Ej: festival 38 días, rol lavaplatos
totalACobrarCocinero = (int(38/15)+1) * 50% * ($100.000+$50.000) = $225.000
(2) totalACobrarCajero = (int(diasDurFest/15)+1) * 50% * (sueldoBase+antigüedad)
Ej: festival 52 días, 3 años antigüedad
totalACobrarCajero = (int(52/15)+1) * 50% * ($100.000+$5.000*3) = $230.000

    Service: LiquidacionDeHaberService.java
    Templates: empleados/liquidacion.html / historialLiquidaciones.html / listaEmpleado.html

CATEGORÍA: PRE-DEFENSA CHECKLIST

    ✅ 4. Alta pedido
    ✅ 5. Script de carga de datos prueba en la BD (tabla costos y datos festival)
    ✅ 6. Vista básica con liquidación de sueldo
    ✅ 8. Prueba de envío de e-mail (EmailService)

Stack

    Java 17 / Spring Boot / Thymeleaf
    MySQL / JPA / Maven
    Estilo templates: gris, amarillo, negro (responsable/unidades)

Commits LiiMendez

    c7124d6 - fix: liquidacion haberes formula oficial PDF (225k/230k) + ranking mayor canon
    4263129 - feat: fix liquidacion oficial 225k/230k + ranking mayor-canon + templates finales

Grupo 10 - OO2 - UNLa