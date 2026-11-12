CREATE DATABASE  IF NOT EXISTS `epicentro_gourmet` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `epicentro_gourmet`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: epicentro_gourmet
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cajero`
--

DROP TABLE IF EXISTS `cajero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cajero` (
  `turno` enum('MANIANA','NOCHE') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK5j4pj5wrsg9jjujq2h5ga25hn` FOREIGN KEY (`id`) REFERENCES `personal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cajero`
--

LOCK TABLES `cajero` WRITE;
/*!40000 ALTER TABLE `cajero` DISABLE KEYS */;
INSERT INTO `cajero` VALUES ('MANIANA',14),('MANIANA',15),('MANIANA',16),('MANIANA',20),('MANIANA',21),('MANIANA',22),('MANIANA',23);
/*!40000 ALTER TABLE `cajero` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias_cocina`
--

DROP TABLE IF EXISTS `categorias_cocina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_cocina` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `plus_fijo` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias_cocina`
--

LOCK TABLES `categorias_cocina` WRITE;
/*!40000 ALTER TABLE `categorias_cocina` DISABLE KEYS */;
INSERT INTO `categorias_cocina` VALUES (1,'COCINERO',150000.00),(2,'AYUDANTE',100000.00),(3,'LAVAPLATOS',50000.00);
/*!40000 ALTER TABLE `categorias_cocina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cocinero`
--

DROP TABLE IF EXISTS `cocinero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cocinero` (
  `categoria` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `especialidad` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id` bigint NOT NULL,
  `categoria_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2ekwmvucwaw2as9i7lcgeqgc0` (`categoria_id`),
  CONSTRAINT `FK2ekwmvucwaw2as9i7lcgeqgc0` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_cocina` (`id`),
  CONSTRAINT `FKohklrcnxg2atfrpe4n8erickc` FOREIGN KEY (`id`) REFERENCES `personal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cocinero`
--

LOCK TABLES `cocinero` WRITE;
/*!40000 ALTER TABLE `cocinero` DISABLE KEYS */;
INSERT INTO `cocinero` VALUES (NULL,NULL,17,1),(NULL,NULL,18,1),(NULL,NULL,19,3);
/*!40000 ALTER TABLE `cocinero` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `costos`
--

DROP TABLE IF EXISTS `costos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `costos` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `costo_montaje` decimal(38,2) DEFAULT NULL,
  `costo_superficie` decimal(38,2) DEFAULT NULL,
  `plus_electricidad` decimal(38,2) DEFAULT NULL,
  `sueldo_base` decimal(38,2) DEFAULT NULL,
  `valor_antiguedad_por_anio` decimal(38,2) DEFAULT NULL,
  `dias_por_tramo` int DEFAULT NULL,
  `factor_festival` decimal(38,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `costos`
--

LOCK TABLES `costos` WRITE;
/*!40000 ALTER TABLE `costos` DISABLE KEYS */;
INSERT INTO `costos` VALUES (12,10.00,500.00,2000.00,100000.00,5000.00,15,0.50);
/*!40000 ALTER TABLE `costos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cantidad` int NOT NULL,
  `pedido_id` bigint DEFAULT NULL,
  `plato_id` bigint DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgqvba9e7dildyw45u0usdj1k2` (`pedido_id`),
  KEY `FK34hjnxptimd9vquggbgso4x2s` (`plato_id`),
  CONSTRAINT `FK34hjnxptimd9vquggbgso4x2s` FOREIGN KEY (`plato_id`) REFERENCES `plato` (`id`),
  CONSTRAINT `FKgqvba9e7dildyw45u0usdj1k2` FOREIGN KEY (`pedido_id`) REFERENCES `pedido` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
INSERT INTO `detalle_pedido` VALUES (5,1,3,3,20000),(6,1,3,1,20000),(7,4,4,11,70000),(8,1,4,12,21000),(9,3,5,15,13500),(10,3,6,13,46500),(11,1,7,16,30000),(12,1,8,18,9800),(13,2,9,20,27000),(14,2,10,11,35000),(15,1,10,12,21000),(16,3,11,1,60000),(17,2,12,16,60000),(18,2,12,17,25000),(19,1,13,11,17500),(20,2,13,13,31000),(21,1,14,1,20000),(22,1,14,2,25000),(23,3,15,16,90000),(24,2,16,11,35000),(25,1,16,14,6500),(26,4,17,1,80000),(27,3,18,2,75000),(28,1,19,2,25000),(29,1,19,1,20000),(30,1,19,5,3000),(31,1,20,2,25000),(32,2,21,2,70000),(33,2,22,4,16000),(34,1,22,5,3000),(35,1,23,18,19800),(36,2,23,17,85000),(37,1,23,20,26500),(38,1,24,20,26500),(39,1,24,16,30000),(40,2,25,27,5200),(41,2,25,22,40000),(42,4,26,11,70000),(43,1,27,2,35000),(44,1,28,44,15000);
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluacion`
--

DROP TABLE IF EXISTS `evaluacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluacion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `atencion_cliente` int NOT NULL,
  `fecha_evaluacion` date NOT NULL,
  `higiene_presentacion` int NOT NULL,
  `observacion` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `promedio` double NOT NULL,
  `puntualidad` int NOT NULL,
  `personal_id` bigint NOT NULL,
  `festival_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmpk8mr4huu40gigwvrgk495sf` (`personal_id`),
  KEY `FKhnpxfuuwcjvau44xx9dvri08n` (`festival_id`),
  CONSTRAINT `FKhnpxfuuwcjvau44xx9dvri08n` FOREIGN KEY (`festival_id`) REFERENCES `festival` (`id`),
  CONSTRAINT `FKmpk8mr4huu40gigwvrgk495sf` FOREIGN KEY (`personal_id`) REFERENCES `personal` (`id`),
  CONSTRAINT `evaluacion_chk_1` CHECK (((`atencion_cliente` >= 1) and (`atencion_cliente` <= 5))),
  CONSTRAINT `evaluacion_chk_2` CHECK (((`higiene_presentacion` >= 1) and (`higiene_presentacion` <= 5))),
  CONSTRAINT `evaluacion_chk_3` CHECK (((`puntualidad` >= 1) and (`puntualidad` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluacion`
--

LOCK TABLES `evaluacion` WRITE;
/*!40000 ALTER TABLE `evaluacion` DISABLE KEYS */;
INSERT INTO `evaluacion` VALUES (1,4,'2026-11-11',3,'excelente atencion, muy puntual.',4,5,14,12),(2,5,'2026-11-11',4,'',4.666666666666667,5,19,12),(3,2,'2026-11-11',2,'',2,2,17,12),(4,1,'2026-11-11',4,'',2.6666666666666665,3,19,13);
/*!40000 ALTER TABLE `evaluacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `festival`
--

DROP TABLE IF EXISTS `festival`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `festival` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_inicio` date NOT NULL,
  `imagen_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `temporada` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `costos_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKffmks63h5hcnid0s2v6vsvv3r` (`costos_id`),
  CONSTRAINT `FKffmks63h5hcnid0s2v6vsvv3r` FOREIGN KEY (`costos_id`) REFERENCES `costos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `festival`
--

LOCK TABLES `festival` WRITE;
/*!40000 ALTER TABLE `festival` DISABLE KEYS */;
INSERT INTO `festival` VALUES (11,'2026-11-05 13:09:54.000000','2024-12-02','2024-10-15','https://www.olavarria.gov.ar/wp-content/uploads/2018/03/Sabores-del-Mundo-III-6.jpg','festival gourmet','octubre 2024','2026-11-05 13:09:54.000000',NULL),(12,'2026-11-05 13:09:54.000000','2026-01-15','2025-10-15','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhXORhcJutv2t8EQXs7G8rarGykLK1X43GcXP0KnqIc4bB8rghJ1dhfrzs&s=10','Carnaval de Sabores','octubre 2025','2026-11-05 13:09:54.000000',NULL),(13,'2026-11-05 13:09:54.000000','2027-01-01','2026-11-03','https://images.unsplash.com/photo-1608270586620-248524c67de9','Oktoberfest','verano 2026','2026-11-05 13:09:54.000000',NULL),(14,'2026-11-05 13:28:59.000000','2023-04-15','2023-03-10',NULL,'Food Truck Fest','marzo 2023','2026-11-05 13:28:59.000000',NULL),(15,'2026-11-05 13:28:59.000000','2024-01-20','2023-11-20',NULL,'Verano Gourmet','noviembre 2023','2026-11-05 13:28:59.000000',NULL),(16,'2026-11-12 19:02:22.528991','2027-02-28','2026-12-01','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTLYQ_VtFHXhiBBgtrwniWnfpqt_lQ7shrGQX9XxWA65A&s=10','Verano Gourmet 2026','verano 2027','2026-11-12 19:02:22.528991',NULL);
/*!40000 ALTER TABLE `festival` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `festival_unidad_venta`
--

DROP TABLE IF EXISTS `festival_unidad_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `festival_unidad_venta` (
  `unidad_venta_id` bigint NOT NULL,
  `festival_id` bigint NOT NULL,
  PRIMARY KEY (`unidad_venta_id`,`festival_id`),
  KEY `FKgnl4llgnbqn63x58m3oi7m5h1` (`festival_id`),
  CONSTRAINT `FK5w821wcvbn1fjtfl5qb77mt6f` FOREIGN KEY (`unidad_venta_id`) REFERENCES `unidad_venta` (`id`),
  CONSTRAINT `FKgnl4llgnbqn63x58m3oi7m5h1` FOREIGN KEY (`festival_id`) REFERENCES `festival` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `festival_unidad_venta`
--

LOCK TABLES `festival_unidad_venta` WRITE;
/*!40000 ALTER TABLE `festival_unidad_venta` DISABLE KEYS */;
INSERT INTO `festival_unidad_venta` VALUES (2,11),(4,11),(5,11),(2,12),(4,12),(5,12),(2,13),(4,13),(5,13),(6,13),(7,13),(2,14),(4,14),(5,14),(2,15),(4,15),(5,15),(8,16);
/*!40000 ALTER TABLE `festival_unidad_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_truck`
--

DROP TABLE IF EXISTS `food_truck`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_truck` (
  `patente` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `requiere_conexion_electrica` bit(1) NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK9phcswewsv8d5110eb1ipo1ix` FOREIGN KEY (`id`) REFERENCES `unidad_venta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_truck`
--

LOCK TABLES `food_truck` WRITE;
/*!40000 ALTER TABLE `food_truck` DISABLE KEYS */;
INSERT INTO `food_truck` VALUES ('abc123',_binary '',1),('abc123',_binary '\0',2),('abc678',_binary '\0',4),('abc999',_binary '',5),('abc988',_binary '',7);
/*!40000 ALTER TABLE `food_truck` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidacion_de_haber`
--

DROP TABLE IF EXISTS `liquidacion_de_haber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidacion_de_haber` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dias_de_trabajo` int NOT NULL,
  `fecha_liquidacion` date DEFAULT NULL,
  `totalacobrar` double NOT NULL,
  `personal_id` bigint NOT NULL,
  `unidad_venta_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrsaarjf9wvrf18j8upkejk86k` (`personal_id`),
  KEY `FKtpa09gag5xnaculnkyuiptl32` (`unidad_venta_id`),
  CONSTRAINT `FKrsaarjf9wvrf18j8upkejk86k` FOREIGN KEY (`personal_id`) REFERENCES `personal` (`id`),
  CONSTRAINT `FKtpa09gag5xnaculnkyuiptl32` FOREIGN KEY (`unidad_venta_id`) REFERENCES `unidad_venta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidacion_de_haber`
--

LOCK TABLES `liquidacion_de_haber` WRITE;
/*!40000 ALTER TABLE `liquidacion_de_haber` DISABLE KEYS */;
INSERT INTO `liquidacion_de_haber` VALUES (15,60,'2026-11-10',250000,14,2),(16,58,'2026-11-10',500000,17,2),(17,60,'2026-11-10',375000,19,2),(18,60,'2026-11-12',275000,22,4);
/*!40000 ALTER TABLE `liquidacion_de_haber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metodo_pago`
--

DROP TABLE IF EXISTS `metodo_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodo_pago` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `descuento_porcentaje` double NOT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnrrsdg1qhbpakbl54c769qs64` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metodo_pago`
--

LOCK TABLES `metodo_pago` WRITE;
/*!40000 ALTER TABLE `metodo_pago` DISABLE KEYS */;
INSERT INTO `metodo_pago` VALUES (1,_binary '','2026-11-12 18:33:12.627363',0,'efectivo','2026-11-12 18:33:12.627363'),(2,_binary '','2026-11-12 18:33:23.597385',0,'transferencia','2026-11-12 18:33:23.597385'),(3,_binary '','2026-11-12 18:33:56.028177',15,'cuenta dni','2026-11-12 18:33:56.029694'),(4,_binary '','2026-11-12 18:34:20.380970',5,'mercado pago','2026-11-12 18:34:20.380970');
/*!40000 ALTER TABLE `metodo_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha_pedido` date DEFAULT NULL,
  `unidad_venta_id` bigint NOT NULL,
  `metodo_pago_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkyya82ya0nq3whl2j7wqfca88` (`unidad_venta_id`),
  KEY `FK6i3eclwd31gr6n7s63s0h7ihm` (`metodo_pago_id`),
  CONSTRAINT `FK6i3eclwd31gr6n7s63s0h7ihm` FOREIGN KEY (`metodo_pago_id`) REFERENCES `metodo_pago` (`id`),
  CONSTRAINT `FKkyya82ya0nq3whl2j7wqfca88` FOREIGN KEY (`unidad_venta_id`) REFERENCES `unidad_venta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` VALUES (3,'2026-11-05',2,NULL),(4,'2026-11-05',4,NULL),(5,'2026-11-05',4,NULL),(6,'2026-11-05',4,NULL),(7,'2026-11-05',5,NULL),(8,'2026-11-05',5,NULL),(9,'2026-11-05',5,NULL),(10,'2023-03-20',4,NULL),(11,'2023-03-25',2,NULL),(12,'2023-12-05',5,NULL),(13,'2023-12-15',4,NULL),(14,'2024-10-20',2,NULL),(15,'2024-11-10',5,NULL),(16,'2025-11-05',4,NULL),(17,'2025-12-01',2,NULL),(18,'2026-11-05',2,NULL),(19,'2026-11-06',2,NULL),(20,'2026-11-09',2,NULL),(21,'2026-11-09',2,NULL),(22,'2026-11-09',2,NULL),(23,'2026-11-11',5,NULL),(24,'2026-11-12',5,NULL),(25,'2026-11-12',6,NULL),(26,'2026-11-12',4,NULL),(27,'2026-11-12',2,3),(28,'2026-11-12',8,3);
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personal`
--

DROP TABLE IF EXISTS `personal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `apellido` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dni` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_ingreso` date DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_id` bigint DEFAULT NULL,
  `unidad_asignada_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKen7euhiw905uf89va05uq8i56` (`usuario_id`),
  UNIQUE KEY `UKb3wa2ur92ypvj27vgk3m14y5d` (`dni`),
  KEY `FKcsm4ftceuje3wmd4cua818789` (`unidad_asignada_id`),
  CONSTRAINT `FKcsm4ftceuje3wmd4cua818789` FOREIGN KEY (`unidad_asignada_id`) REFERENCES `unidad_venta` (`id`),
  CONSTRAINT `FKmt1wac55i4gdugj0uqskvkonx` FOREIGN KEY (`usuario_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personal`
--

LOCK TABLES `personal` WRITE;
/*!40000 ALTER TABLE `personal` DISABLE KEYS */;
INSERT INTO `personal` VALUES (14,_binary '','gomez','46888999','2026-08-12','2000-08-11','yolanda',24,2),(15,_binary '','flores','22222222','2025-11-12','1975-11-05','jessica',28,4),(16,_binary '','garcia','42333655','2026-05-14','1992-10-19','juliana',29,5),(17,_binary '','ludueña','25669888','2026-11-05','1998-10-06','maria',30,2),(18,_binary '','peralta','41223665','2026-11-05','1961-12-02','juliana',31,4),(19,_binary '','anderson','22222223','2025-08-11','2005-05-13','pamela',33,2),(20,_binary '','perez','42333656','2025-06-14','1992-04-27','juan ',34,6),(21,_binary '','gonzalez','42333657','2026-03-15','1991-12-18','joaquin',36,7),(22,_binary '','peralta','42333658','2024-03-26','1998-05-15','lorena',37,4),(23,_binary '','fantino','42588822','2026-06-15','1996-03-03','tomas ',39,8);
/*!40000 ALTER TABLE `personal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plato`
--

DROP TABLE IF EXISTS `plato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plato` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `costo_produccion` double NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `imagen_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `precio_venta` double NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `unidad_venta_id` bigint DEFAULT NULL,
  `stock_plato` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKu61y4ikx2a8s1di6vnaccdj` (`unidad_venta_id`),
  CONSTRAINT `FKu61y4ikx2a8s1di6vnaccdj` FOREIGN KEY (`unidad_venta_id`) REFERENCES `unidad_venta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plato`
--

LOCK TABLES `plato` WRITE;
/*!40000 ALTER TABLE `plato` DISABLE KEYS */;
INSERT INTO `plato` VALUES (1,_binary '',2000,'2026-11-05 13:09:54.000000','https://api-middleware-mcd.mcdonaldscupones.com/media/image/product$kqXXaUUP/200/200/original?country=ar','mc cheese',20000,'2026-11-05 13:09:54.000000',2,NULL),(2,_binary '',3000,'2026-11-05 13:09:54.000000','https://upload.wikimedia.org/wikipedia/commons/3/30/Big_Mac_hamburger_edit.jpg?utm_source=es.wikipedia.org&utm_campaign=index&utm_content=original','big mac',35000,'2026-11-09 22:44:45.892493',2,NULL),(3,_binary '',3000,'2026-11-05 13:09:54.000000','https://api-middleware-mcd.mcdonaldscupones.com/media/image/product$kQX3BMhy/200/200/original?country=ar','Grand Triple McBacon',20000,'2026-11-06 15:38:52.289339',2,NULL),(4,_binary '',1000,'2026-11-05 13:09:54.000000','https://api-middleware-mcd.mcdonaldscupones.com/media/image/product$sundae-frutilla.png/200/200/original?country=ar','sundae ',8000,'2026-11-06 15:40:13.577703',2,NULL),(5,_binary '',1000,'2026-11-05 13:09:54.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT25Msj9v_LQLRzCgBK8tJa6HbIBDHqhDQ5q6AiWdtNT2-H79Re14EGYYk&s=10','cono ',3000,'2026-11-06 15:39:36.641526',2,NULL),(11,_binary '',8500,'2026-11-05 13:09:54.000000','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/MAY0-4.png','Mega Cuarto',17500,'2026-11-12 18:18:32.388389',4,NULL),(12,_binary '',10500,'2026-11-05 13:09:54.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSPauDmtgj-Yo5vfdZo2b40ekMrdSsj6C8u2LCIFzbXuhlO92vGdv2Y5AzC&s=10','Mostaza Doble',21000,'2026-11-12 18:18:51.767658',4,NULL),(13,_binary '',7500,'2026-11-05 13:09:54.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgWCeJ2WEVLxb8-gpjs3j3pyFD4RYVlS_gbQLL8ReuFA&s=10','Pollo Mostaza',15500,'2026-11-12 18:19:13.841201',4,NULL),(14,_binary '',2200,'2026-11-05 13:09:54.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR9bOHnmN8eJuYeR10bUNwPXhzzRjE_3PWZ8IAigenGPA&s=10','Papas Grandes Mostaza',6500,'2026-11-12 18:19:30.246398',4,NULL),(15,_binary '',1800,'2026-11-05 13:09:54.000000','https://www.mostazaweb.com.ar/wp-content/uploads/2025/05/MAY0-18.png','Helado Mostaza',4500,'2026-11-12 18:19:48.681037',4,NULL),(16,_binary '',5000,'2026-11-05 13:09:54.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRU7X7Q_e2Y5VDYuvpjd9lysjWhLhHPj7G-PYRKSDvvSA&s=10','15 piezas de sushi ',30000,'2026-11-11 19:18:17.668714',5,NULL),(17,_binary '',5500,'2026-11-06 12:22:32.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR2ZpZ4d8dy-qVGmuworG0T_6EkNSVfej2-vIirSKoJVw&s','Sushi Pop Mix 15 piezas',42500,'2026-11-11 19:21:23.741907',5,NULL),(18,_binary '',4200,'2026-11-06 12:22:32.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZzRFYoV7EHd5MSu-NBRiMbWBOYHWumsWAIv2cavj7ejRRULoGjZLAKtA&s=10','Nigiri Salmón x 8',19800,'2026-11-11 19:20:57.461015',5,NULL),(19,_binary '',4800,'2026-11-06 12:22:32.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTp5OD8GxoYYbIhtyG3WbOzgy7_XiUpm24qtdFzujo-WtgUavis5DiN8POu&s=10','Roll Philadelphia Tempura',40000,'2026-11-11 19:19:23.675520',5,NULL),(20,_binary '',2500,'2026-11-06 12:22:32.000000','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZH6r6OOeg0Q1M-COpLaL4Oh7YFvv9oBs42_Js0uyODe8A17m9LmMMsCA&s=10','Wok de Langostinos',26500,'2026-11-11 19:20:42.134623',5,NULL),(21,_binary '',600,'2026-11-09 23:04:04.315611','https://i.blogs.es/44409f/sabores-express5/650_1200.jpg','empanada de jyq',1600,'2026-11-09 23:06:42.219736',6,NULL),(22,_binary '',2000,'2026-11-09 23:04:47.343651','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRCHN5gMMPNB7rTLFqeIWZRsCsLfQUufOg9MEQ1LrEllg&s=10','docena de empanadas',20000,'2026-11-09 23:05:17.321232',6,NULL),(23,_binary '',600,'2026-11-09 23:05:53.408243','https://images.rappi.com.ar/restaurants_background/saboresexpress-1661804971889.jpg?e=webp&d=700x100&q=10',' empanada de cheeseburger',1600,'2026-11-09 23:06:59.695588',6,NULL),(24,_binary '',600,'2026-11-09 23:07:41.474162','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQkx0ecFBKQ2a3yqBvXWnCfEYm20jXFGCVv5_GRlkZpKQ&s=10','empanada picante de carne ',1600,'2026-11-09 23:11:26.642714',6,NULL),(25,_binary '',120,'2026-11-09 23:08:20.596125','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR5WPc_CtOM9h4hSw_GgJo49y0xJ4c7De9Md9uoOQRV3ouUMh25TpTWV60&s=10','medialuna de manteca',566,'2026-11-09 23:11:54.410381',6,NULL),(26,_binary '',1000,'2026-11-09 23:08:52.888008','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTl4jRfQJ0TdK19_ye2vPyScpOBabcKttLEADykzJx4PLmQwUy64lJ7BZM&s=10','chipa 1/4',5000,'2026-11-09 23:12:09.017554',6,NULL),(27,_binary '',150,'2026-11-09 23:10:05.973400','https://6denoviembrecafe.com.ar/wp-content/uploads/2024/08/aquarius-renueva-su-identidad-e1723485226915.jpg','agua saborizada aquarius ',2600,'2026-11-09 23:12:36.386267',6,NULL),(28,_binary '',1500,'2026-11-09 23:13:10.344043','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTRvEsE8iokX6dtkylKD_9wwCWo9SqO8nCXz7OSVJ1bqw&s=10','docena de medialunas de manteca',6800,'2026-11-09 23:13:25.125349',6,NULL),(29,_binary '',120,'2026-11-09 23:14:42.412565','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSGTBdSe__4nFlxvvAMYISVSiIi_5wiOIFnOPzMv2d7Ft5EuY8Y9pemNSxO&s=10','medialuna de grasa',550,'2026-11-09 23:16:03.031486',6,NULL),(30,_binary '',1200,'2026-11-09 23:15:18.106056','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSGTBdSe__4nFlxvvAMYISVSiIi_5wiOIFnOPzMv2d7Ft5EuY8Y9pemNSxO&s=10','docena de medialunas de grasa',6600,'2026-11-09 23:15:57.073950',6,NULL),(31,_binary '',2000,'2026-11-12 16:37:00.760154','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvx378HEfcmt5SSai4Mk9jfoU_0tUuLrrXanrfVodj1w&s=10','Whopper',25000,'2026-11-12 16:43:52.909266',7,10),(32,_binary '',2600,'2026-11-12 16:38:50.318754','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS66rXzeGq8vqHvqQ_5mykZgGuKsecXXAUC7gzMzoPHIrQZ10omQKSJYq8&s=10','BK Stacker quintuple',30500,'2026-11-12 16:46:34.386134',7,2),(33,_binary '',3000,'2026-11-12 16:39:38.078877','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTJ9ZiJ8C5yN44d7_jIqKDpkdzZOVqq-WtqLIovTdPIQ&s=10',' Deluxe King',27000,'2026-11-12 16:47:06.518445',7,8),(34,_binary '',4000,'2026-11-12 16:40:07.114857','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0Snn9IJ7R1z9Gal04wA4yi1yhRurCHK1GFoL2f4H9j1msMecyIlQxLoGm&s=10','Bacon King XL',36000,'2026-11-12 16:47:26.595183',7,9),(35,_binary '',2988,'2026-11-12 16:40:43.400494','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyLhRL3OJdMRYm7cI0HJ9UxM4JMuR7-nVWKTYlZH2kGg&s=10','cuarto XL',35000,'2026-11-12 16:47:45.647725',7,5),(36,_binary '',1500,'2026-11-12 16:41:19.597638','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRfPsIIS1Va30WJND1I8ncYkzbn0hXl2-3-sRclKHORqg&s',' Classic Burger bk',16000,'2026-11-12 16:48:21.056681',7,4),(37,_binary '',500,'2026-11-12 16:42:04.465154','https://bk-latam-prod.s3.amazonaws.com/sites/burgerking.latam/files/BK_Web_BURGERKINGSANDYCHOCO_500X540px.png','helado bk sundae',5000,'2026-11-12 16:48:39.567309',7,9),(38,_binary '',2000,'2026-11-12 16:42:38.627654','https://tb-static.uber.com/prod/image-proc/processed_images/62eea340a5c5d1686d6a158cda0f82c5/d03e52b3c8af19d8fa8222e23efd9cfa.jpeg','balde de aros de cebolla bk',13000,'2026-11-12 16:49:03.376089',7,5),(39,_binary '',2000,'2026-11-12 16:43:24.581002','https://cdn.sanity.io/images/czqk28jt/staging_bk_ar/c1e144b4e537567721183998dc2cb10c1fd9653d-2000x1333.png','balde de papas xl bk',12000,'2026-11-12 16:49:40.166709',7,5),(40,_binary '',200,'2026-11-12 16:50:16.118530','https://i.redd.it/iyoivobbjaxc1.jpeg','vaso chico bk',2500,'2026-11-12 16:51:25.640974',7,5),(41,_binary '',300,'2026-11-12 16:50:32.774621','https://i.redd.it/iyoivobbjaxc1.jpeg','vaso mediano',4000,'2026-11-12 16:51:45.710635',7,6),(42,_binary '',500,'2026-11-12 16:51:05.665269','https://i.redd.it/iyoivobbjaxc1.jpeg','vaso xl',5000,'2026-11-12 16:51:56.277400',7,9),(43,_binary '',2056,'2026-11-12 16:52:52.180282','https://www.webretail.com.ar/wp-content/uploads/2022/08/Dia-de-la-Ninez-BK-1024x570.jpg','king jr. cajita ',13540,'2026-11-12 16:52:56.705403',7,8),(44,_binary '',1522,'2026-11-12 19:06:55.249475','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRIT940dn1zWlYQPAkKkIGY0RzUYfBMEGsZ-_jYYlZLE_mhbN0Edo_Ac8VYEtMSC3HEJ57Bnr810CJMOKZ2k_RwBKS1UkPccIfQT9i45o9ZrQ&s=10','Hamburguesa Clásica',15000,'2026-11-12 19:12:15.183134',8,4),(45,_binary '',2000,'2026-11-12 19:07:20.821244','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRYklPG2xVHeNJFRUJbu5LRZqQJJuKcPviIVT6hZqbBwQ&s=10','papas con cheddar',10000,'2026-11-12 19:08:41.181347',8,NULL),(46,_binary '',2000,'2026-11-12 19:07:49.975173','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSzstOQAygHK-afkw77BnJLzoXlzVPAE3EN36g66DkLhQ&s=10','cerveza artesanal',7000,'2026-11-12 19:08:57.553060',8,NULL);
/*!40000 ALTER TABLE `plato` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puesto_desarmable`
--

DROP TABLE IF EXISTS `puesto_desarmable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puesto_desarmable` (
  `cantidad_carpas` int NOT NULL,
  `tiempo_montaje_min` int NOT NULL,
  `id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKh5vlxk7bsmq3eorhhof3p0ulv` FOREIGN KEY (`id`) REFERENCES `unidad_venta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puesto_desarmable`
--

LOCK TABLES `puesto_desarmable` WRITE;
/*!40000 ALTER TABLE `puesto_desarmable` DISABLE KEYS */;
INSERT INTO `puesto_desarmable` VALUES (1,16,6),(1,20,8);
/*!40000 ALTER TABLE `puesto_desarmable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unidad_venta`
--

DROP TABLE IF EXISTS `unidad_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidad_venta` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_unico` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `nombre_comercial` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `superficie_m2` double NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `responsable_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK60ai1pof2adtk1c8xlhnkvoua` (`codigo_unico`),
  KEY `FK3k8ubn2uv6m645n7pp86x5pdi` (`responsable_id`),
  CONSTRAINT `FK3k8ubn2uv6m645n7pp86x5pdi` FOREIGN KEY (`responsable_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidad_venta`
--

LOCK TABLES `unidad_venta` WRITE;
/*!40000 ALTER TABLE `unidad_venta` DISABLE KEYS */;
INSERT INTO `unidad_venta` VALUES (2,'MC 1341003','2026-11-05 13:09:54.000000','mc donald',10,'2026-11-05 13:09:54.000000',23),(4,'MOS1308009','2026-11-05 13:09:54.000000','mostaza',10,'2026-11-12 18:19:48.679022',26),(5,'SUS2198009','2026-11-05 13:09:54.000000','sushipop',10,'2026-11-05 13:09:54.000000',27),(6,'SAB5148000','2026-11-05 21:23:07.960969','sabores express',15,'2026-11-09 23:15:18.102539',32),(7,'BUR7823001','2026-11-12 16:35:53.124652','burger king',16,'2026-11-12 16:52:52.180282',35),(8,'LA 6712007','2026-11-12 19:04:10.017928','la birra artesanal',0,'2026-11-12 19:08:57.552052',38);
/*!40000 ALTER TABLE `unidad_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rol` enum('ADMIN','EMPLEADO','RESPONSABLE') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8h16xrdskusbaukokdpb2ywm6` (`rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,'ADMIN'),(2,'EMPLEADO'),(3,'RESPONSABLE');
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `correo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqs4hlsdf7l1k1u4on057c0949` (`correo`),
  KEY `FK2ity5v5b8yajny48upehxos2y` (`rol_id`),
  CONSTRAINT `FK2ity5v5b8yajny48upehxos2y` FOREIGN KEY (`rol_id`) REFERENCES `user_role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (8,_binary '','admin@admin.com','$2a$10$zcdegTRceM7DegJatoI4zO1FXd4eabf3WgBnQUXPbWGYRPyYK96YO',1),(23,_binary '','yeyiimendez@gmail.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',3),(24,_binary '','yolanda@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(26,_binary '','mostaza@epicentro.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',3),(27,_binary '','sushipop@epicentro.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',3),(28,_binary '','jessica@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(29,_binary '','juliana@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(30,_binary '','ai2yh90q2l@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(31,_binary '','aatr0ccykb@olipii.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(32,_binary '','twjluhxvz1@ooynib.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',3),(33,_binary '','1tatc78z0r@olipii.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(34,_binary '','juanperez@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(35,_binary '','burgerking@epicentro.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',3),(36,_binary '','joaquin@lnovic.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(37,_binary '','walizim1990@gmail.com','$2a$10$8BR0VFYLQ1pfHMM9sgD12OfZ3zVKbzwNfEQo61XDw.VLlIfDM01SO',2),(38,_binary '','labirraartesanal@gmail.com','$2a$10$guXpiTnk6n09uPIOeBOEruv/DBNcheDJ3TNhe74tTsMzpxuLUDSMu',3),(39,_binary '','k86hod4ikj@olipii.com','$2a$10$hPYbjsp53UtW2BlQiK2vGuitSUMgOhqw8KRvaCsyENHv0Nk3Lh9au',2);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-12 16:17:12
