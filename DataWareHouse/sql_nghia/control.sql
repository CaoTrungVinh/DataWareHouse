-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: control
-- ------------------------------------------------------
-- Server version	8.0.20

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
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `config` (
  `id` int NOT NULL,
  `source_host` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `list_file` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `folder_download` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `config_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `folder_error` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `extension_file` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `delimiter` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `staging_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `number_cols` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `datawarehouse_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `cols_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `field_name_dwh` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `number_cols_dwh` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'http://drive.ecepvn.org:5000/','guest_access','123456','/ECEP/song.nguyen/DW_2020/data/','D:\\Datawarehouse\\ListFolderDownload','f_sinhvien','D:\\Datawarehouse\\ListFolderError','xlsx','f_sinhvien',',','sinhvien','stt,MSSV,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,Dien_thoai,Email,Que_quan,Ghi_chu','11','sinhvien','Ngay_sinh','MSSV,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,Dien_thoai,Email,Que_quan,Ghi_chu,sk_date_dim',11),(2,'http://drive.ecepvn.org:5000/','guest_access','123456','/ECEP/song.nguyen/DW_2020/data/','D:\\Datawarehouse\\ListFolderDownload','f_monhoc','D:\\Datawarehouse\\ListFolderError','xlsx','f_monhoc',',','monhoc','stt,MaMh,TenMH,TC,Khoa_BM,Khoa_BM_DSD,Ghi_chu,date_expire','8','monhoc',NULL,'stt,MaMh,TenMH,TC,Khoa_BM,Khoa_BM_DSD,Ghi_chu,date_expire',8),(3,'http://drive.ecepvn.org:5000/','guest_access','123456','/ECEP/song.nguyen/DW_2020/data/','D:\\Datawarehouse\\ListFolderDownload','f_dangky','D:\\Datawarehouse\\ListFolderError','xlsx','f_dangky',',','dangky','stt,Ma_dang_ky,Ma_sinh_vien,Ma_lop_hoc,Thoi_gian_dang_ky','5','dangky',NULL,'stt,Ma_dang_ky,Ma_sinh_vien,Ma_lop_hoc,Thoi_gian_dang_ky',5),(4,'http://drive.ecepvn.org:5000/','guest_access','123456','/ECEP/song.nguyen/DW_2020/data/','D:\\Datawarehouse\\ListFolderDownload','f_lophoc','D:\\Datawarehouse\\ListFolderError','xlsx','f_lophoc',',','lophoc','stt,Ma_lop_hoc,Ma_mon_hoc,Nam_hoc','4','lophoc',NULL,'stt,Ma_lop_hoc,Ma_mon_hoc,Nam_hoc',4);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `time_download` timestamp NULL DEFAULT NULL,
  `time_Staging` timestamp NULL DEFAULT NULL,
  `time_Datawarehouse` timestamp NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `id_config` int DEFAULT NULL,
  `rows_update_datawarehouse` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id_config` (`id_config`) USING BTREE,
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`id_config`) REFERENCES `config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
INSERT INTO `logs` VALUES (9,'dangky_sang_nhom4_2020.xlsx',NULL,'2020-07-26 13:57:01','2020-07-26 13:57:09','SUCCESS',3,0),(13,'lophoc_sang_nhom4_2020.xlsx',NULL,'2020-07-26 13:58:31','2020-07-26 13:58:39','SUCCESS',4,0),(14,'monhoc2013.xlsx',NULL,'2020-07-26 13:54:57','2020-07-26 13:55:12','SUCCESS',2,0),(15,'monhoc2014.xlsx',NULL,'2020-07-26 13:55:13','2020-07-26 13:55:40','SUCCESS',2,0),(16,'sinhvien_sang_nhom1.xlsx',NULL,'2020-07-26 13:54:14','2020-07-26 13:54:23','SUCCESS',1,0),(17,'sinhvien_sang_nhom2.xlsx',NULL,'2020-07-26 13:54:24',NULL,'Not TR',1,NULL),(18,'SinhVien_Sang_Nhom3.xlsx',NULL,'2020-07-26 13:54:24','2020-07-26 13:54:32','SUCCESS',1,44),(19,'sinhvien_sang_nhom15.xlsx',NULL,'2020-07-26 13:54:33','2020-07-26 13:54:44','SUCCESS',1,36);
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-26 21:02:15
