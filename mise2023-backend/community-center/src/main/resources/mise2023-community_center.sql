-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: community_center
-- ------------------------------------------------------
-- Server version	8.0.35

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
-- Table structure for table `application`
--

DROP TABLE IF EXISTS `application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application` (
  `application_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `community_id` int NOT NULL,
  `status` enum('Pending','Approved','Declined') NOT NULL,
  `apply_time` datetime NOT NULL,
  `handle_time` datetime DEFAULT NULL,
  `handler_id` bigint DEFAULT NULL,
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application`
--

LOCK TABLES `application` WRITE;
/*!40000 ALTER TABLE `application` DISABLE KEYS */;
INSERT INTO `application` VALUES (2,1,2,'Approved','2023-11-14 22:30:46','2023-11-15 00:27:33',10086),(3,1,3,'Declined','2023-11-14 22:30:49','2023-11-15 00:27:47',10081),(4,1,4,'Pending','2023-11-14 22:30:51',NULL,0),(5,2,2,'Pending','2023-11-14 22:30:51',NULL,0),(6,4,3,'Pending','2023-11-14 22:30:51',NULL,0);
/*!40000 ALTER TABLE `application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community`
--

DROP TABLE IF EXISTS `community`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community` (
  `community_id` int NOT NULL AUTO_INCREMENT,
  `is_public` tinyint(1) NOT NULL,
  `create_time` date NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '社区名字',
  PRIMARY KEY (`community_id`, `name`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community`
--

LOCK TABLES `community` WRITE;
/*!40000 ALTER TABLE `community` DISABLE KEYS */;
INSERT INTO `community` VALUES (1,1,'2023-11-01','薅羊毛小队'),(2,1,'2023-11-01','薅羊毛小分队'),(3,1,'2023-11-02','华为鸿蒙'),(4,1,'2023-11-03','汽车'),(5,1,'2023-11-04','壁纸'),(6,1,'2023-11-05','好物安利'),(7,1,'2023-11-06','谷歌相机'),(8,1,'2023-11-07','今日热点'),(9,1,'2023-11-08','美食家'),(10,1,'2023-11-09','沙雕乐园'),(11,1,'2023-11-10','手机摄影'),(12,1,'2023-11-11','开箱评测'),(13,1,'2023-11-12','爱桌面'),(14,1,'2023-11-13','考研'),(15,1,'2023-11-14','鸿蒙4.0'),(16,1,'2023-11-15','键盘'),(17,1,'2023-11-16','机械键盘'),(18,1,'2023-11-17','刷机'),(19,1,'2023-11-18','原神'),(20,1,'2023-11-19','玩机技巧'),(21,1,'2023-11-20','耳机发烧友'),(22,1,'2023-11-21','LCD永不为奴'),(23,1,'2023-11-22','骑行'),(24,1,'2023-11-23','好电影'),(25,1,'2023-11-24','每日游戏资讯'),(26,1,'2023-11-25','电动车'),(27,1,'2023-11-26','跑步'),(28,1,'2023-11-27','穿衣搭配'),(29,1,'2023-11-28','提车报告'),(30,1,'2023-11-29','超级壁纸'),(31,1,'2023-11-30','宿舍生活好物');
/*!40000 ALTER TABLE `community` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_user`
--

DROP TABLE IF EXISTS `community_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_user` (
  `community_id` int NOT NULL,
  `user_id` int NOT NULL,
  `role` int NOT NULL,
  PRIMARY KEY (`community_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_user`
--

LOCK TABLES `community_user` WRITE;
/*!40000 ALTER TABLE `community_user` DISABLE KEYS */;
INSERT INTO `community_user` VALUES (1,1,0),(1,2,1),(1,3,1),(1,4,2),(1,5,2),(1,6,2),(1,7,2),(1,8,2),(1,9,2),(1,10,2),(1,111,2),(2,1,0);
/*!40000 ALTER TABLE `community_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_post`
--

DROP TABLE IF EXISTS `community_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_post` (
  `community_id` int NOT NULL,
  `post_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_post`
--

LOCK TABLES `community_post` WRITE;
/*!40000 ALTER TABLE `community_post` DISABLE KEYS */;
INSERT INTO `community_post` VALUES (1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20);
/*!40000 ALTER TABLE `community_post` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-09 21:42:20
