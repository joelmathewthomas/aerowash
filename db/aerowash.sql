-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: aerowash
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank` (
  `bank_id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int NOT NULL,
  `bank_ifsc_code` varchar(45) NOT NULL,
  `bank_account_no` varchar(45) NOT NULL,
  PRIMARY KEY (`bank_id`),
  UNIQUE KEY `bank_account_no_UNIQUE` (`bank_account_no`),
  UNIQUE KEY `staff_id_UNIQUE` (`staff_id`),
  KEY `fk_bank_1_idx` (`staff_id`),
  CONSTRAINT `fk_bank_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
INSERT INTO `bank` VALUES (21,29,'SBIN0070120','234242344234'),(22,30,'SBIN0070121','234242344232');
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `customer_fname` varchar(45) NOT NULL,
  `customer_mname` varchar(45) DEFAULT NULL,
  `customer_lname` varchar(45) NOT NULL,
  `customer_phone` varchar(15) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_id_UNIQUE` (`customer_id`),
  UNIQUE KEY `customer_phone_UNIQUE` (`customer_phone`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (13,'Bill','','Gates','1234567890'),(14,'Linus','','Torvalds','7894561230');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expense`
--

DROP TABLE IF EXISTS `expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expense` (
  `expense_id` int NOT NULL AUTO_INCREMENT,
  `wash_id` int NOT NULL,
  `expense_item` varchar(45) NOT NULL,
  `expense_amount` decimal(10,0) NOT NULL,
  `expense_date` date NOT NULL,
  PRIMARY KEY (`expense_id`),
  UNIQUE KEY `expense_id_UNIQUE` (`expense_id`),
  KEY `fk_expense_1_idx` (`wash_id`),
  CONSTRAINT `fk_expense_1` FOREIGN KEY (`wash_id`) REFERENCES `wash` (`wash_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expense`
--

LOCK TABLES `expense` WRITE;
/*!40000 ALTER TABLE `expense` DISABLE KEYS */;
INSERT INTO `expense` VALUES (31,17,'Service Charge',500,'2025-11-17'),(32,17,'Premium Care',2000,'2025-11-17'),(33,18,'Service Charge',500,'2025-11-17'),(34,19,'Service Charge',500,'2025-11-17'),(35,20,'Service Charge',500,'2025-11-17'),(36,21,'Service Charge',500,'2025-11-17');
/*!40000 ALTER TABLE `expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flat`
--

DROP TABLE IF EXISTS `flat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flat` (
  `flat_id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `flat_name` varchar(100) NOT NULL,
  `flat_location` varchar(255) NOT NULL,
  `flat_added_date` date NOT NULL,
  PRIMARY KEY (`flat_id`),
  UNIQUE KEY `flat_id_UNIQUE` (`flat_id`),
  KEY `fk_flat_1_idx` (`customer_id`),
  CONSTRAINT `fk_flat_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flat`
--

LOCK TABLES `flat` WRITE;
/*!40000 ALTER TABLE `flat` DISABLE KEYS */;
INSERT INTO `flat` VALUES (25,13,'Hill Palace','Atlanta','2025-11-17'),(27,13,'Green Palace','Atlanta','2025-11-17'),(28,14,'City View','Kochi','2025-11-17'),(29,14,'Green View','Idukki','2025-11-17');
/*!40000 ALTER TABLE `flat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salary`
--

DROP TABLE IF EXISTS `salary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salary` (
  `salary_id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int NOT NULL,
  `salary_month` enum('JANUARY','FEBRUARY','MARCH','APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER') NOT NULL,
  `salary_year` int NOT NULL,
  `salary_amount` decimal(10,0) NOT NULL,
  PRIMARY KEY (`salary_id`),
  UNIQUE KEY `salary_id_UNIQUE` (`salary_id`),
  KEY `fk_salary_1_idx` (`staff_id`),
  CONSTRAINT `fk_salary_1` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salary`
--

LOCK TABLES `salary` WRITE;
/*!40000 ALTER TABLE `salary` DISABLE KEYS */;
INSERT INTO `salary` VALUES (7,29,'NOVEMBER',2025,10000),(8,30,'NOVEMBER',2025,10000);
/*!40000 ALTER TABLE `salary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `staff_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `staff_fname` varchar(45) NOT NULL,
  `staff_mname` varchar(45) DEFAULT NULL,
  `staff_lname` varchar(45) NOT NULL,
  `staff_phone` varchar(15) NOT NULL,
  `staff_email` varchar(100) NOT NULL,
  `staff_address` varchar(255) NOT NULL,
  `staff_aadhaar` varchar(12) NOT NULL,
  `staff_status` enum('active','inactive') NOT NULL DEFAULT 'active',
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `auth_id_UNIQUE` (`staff_id`),
  UNIQUE KEY `staff_phone_UNIQUE` (`staff_phone`),
  UNIQUE KEY `staff_email_UNIQUE` (`staff_email`),
  UNIQUE KEY `staff_aadhaar_UNIQUE` (`staff_aadhaar`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  CONSTRAINT `fk_staff_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (29,48,'Joel','Mathew','Thomas','1852545859','joel@ex.co','Kottayam','147852369151','active'),(30,49,'John',NULL,'Doe','1254525695','john@ex.co','Idukki','147852369152','inactive');
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transactions` (
  `transaction_id` int NOT NULL AUTO_INCREMENT,
  `transaction_date` date DEFAULT NULL,
  `transaction_amount` decimal(10,0) NOT NULL,
  `transaction_status` enum('COMPLETE','INCOMPLETE') NOT NULL DEFAULT 'INCOMPLETE',
  `transaction_mode` enum('ONLINE','OFFLINE') NOT NULL,
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `transaction_id_UNIQUE` (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transactions`
--

LOCK TABLES `transactions` WRITE;
/*!40000 ALTER TABLE `transactions` DISABLE KEYS */;
INSERT INTO `transactions` VALUES (15,'2025-11-17',2500,'COMPLETE','ONLINE'),(16,NULL,500,'INCOMPLETE','OFFLINE'),(17,'2025-11-17',500,'COMPLETE','OFFLINE'),(18,'2025-11-17',500,'COMPLETE','ONLINE'),(19,'2025-11-17',500,'COMPLETE','ONLINE');
/*!40000 ALTER TABLE `transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `user_password` varchar(255) NOT NULL,
  `user_role` enum('admin','staff') NOT NULL DEFAULT 'staff',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (47,'admin','admin','admin'),(48,'joel','joel123','staff'),(49,'john','john123','staff');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle`
--

DROP TABLE IF EXISTS `vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vehicle` (
  `vehicle_id` int NOT NULL AUTO_INCREMENT,
  `flat_id` int NOT NULL,
  `customer_id` int NOT NULL,
  `vehicle_name` varchar(45) NOT NULL,
  `vehicle_license_number` varchar(45) NOT NULL,
  `vehicle_added_date` date NOT NULL,
  PRIMARY KEY (`vehicle_id`),
  UNIQUE KEY `vehicle_id_UNIQUE` (`vehicle_id`),
  UNIQUE KEY `vehicle_number_UNIQUE` (`vehicle_license_number`),
  KEY `fk_vehicle_2_idx` (`flat_id`),
  KEY `fk_vehicle_1_idx` (`customer_id`),
  CONSTRAINT `fk_vehicle_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_2` FOREIGN KEY (`flat_id`) REFERENCES `flat` (`flat_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (26,25,13,'Maruti','MH12AC1002','2025-11-17'),(27,27,13,'Swift','MH12AC1003','2025-11-17'),(28,28,14,'Skoda','DH12AC1001','2025-11-17');
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wash`
--

DROP TABLE IF EXISTS `wash`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wash` (
  `wash_id` int NOT NULL AUTO_INCREMENT,
  `staff_id` int NOT NULL,
  `vehicle_id` int NOT NULL,
  `transaction_id` int NOT NULL,
  `wash_date` date NOT NULL,
  PRIMARY KEY (`wash_id`),
  UNIQUE KEY `wash_id_UNIQUE` (`wash_id`),
  UNIQUE KEY `transaction_id_UNIQUE` (`transaction_id`),
  KEY `fk_wash_1_idx` (`vehicle_id`),
  KEY `fk_wash_3_idx` (`staff_id`),
  CONSTRAINT `fk_wash_1` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wash_2` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wash_3` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wash`
--

LOCK TABLES `wash` WRITE;
/*!40000 ALTER TABLE `wash` DISABLE KEYS */;
INSERT INTO `wash` VALUES (17,29,28,15,'2025-11-17'),(18,29,27,16,'2025-11-17'),(19,29,28,17,'2025-11-17'),(20,30,28,18,'2025-11-17'),(21,30,26,19,'2025-11-17');
/*!40000 ALTER TABLE `wash` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-17  3:31:01
