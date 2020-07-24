/*
 Navicat Premium Data Transfer

 Source Server         : dao
 Source Server Type    : MySQL
 Source Server Version : 100406
 Source Host           : localhost:3306
 Source Schema         : control

 Target Server Type    : MySQL
 Target Server Version : 100406
 File Encoding         : 65001

 Date: 23/07/2020 10:19:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` int(11) NOT NULL,
  `source_host` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `list_file` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `folder_download` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `folder_success` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `folder_error` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `extension_file` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `delimiter` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `staging_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `field_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `number_cols` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `datawarehouse_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `cols_date` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, 'http://drive.ecepvn.org:5000/', 'guest_access', '123456', '/ECEP/song.nguyen/DW_2020/data/', 'D:\\\\Datawarehouse\\\\ListFolderDownload\\\\', NULL, NULL, 'xlsx', 'f_sinhvien', '|', 'sinhvien', 'Ma_so_sinh_vien,Ho_lot,Ten,Ngay_sinh,Ma_lop,Ten_lop,Dien_thoai,Email,Que_quan,Ghi_chu', '10', 'sinhvien', NULL);
INSERT INTO `config` VALUES (2, 'http://drive.ecepvn.org:5000/', 'guest_access', '123456', '/ECEP/song.nguyen/DW_2020/data/', 'D:\\\\Datawarehouse\\\\ListFolderDownload\\\\', NULL, NULL, 'xlsx', 'f_monhoc', '|', 'monhoc', 'Ma_MH,Ten_Mon_hoc,TC,Khoa_BM_quan_ly,Khoa_BM,Ghi_chu', '6', 'monhoc', NULL);
INSERT INTO `config` VALUES (3, 'http://drive.ecepvn.org:5000/', 'guest_access', '123456', '/ECEP/song.nguyen/DW_2020/data/', 'D:\\\\Datawarehouse\\\\ListFolderDownload\\\\', NULL, NULL, 'xlsx', 'f_dangky', '|', 'dangky', 'Ma_dang_ky,Ma_sinh_vien,Ma_lop_hoc,Thoi_gian_dang_ky', '4', 'dangky', NULL);
INSERT INTO `config` VALUES (4, 'http://drive.ecepvn.org:5000/', 'guest_access', '123456', '/ECEP/song.nguyen/DW_2020/data/', 'D:\\\\Datawarehouse\\\\ListFolderDownload\\\\', NULL, NULL, 'xlsx', 'f_lophoc', '|', 'lophoc', 'Ma_lop_hoc,Ma_mon_hoc,Nam_hoc', '3', 'lophoc', NULL);

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `time_download` timestamp(0) NULL DEFAULT NULL,
  `time_Staging` timestamp(0) NULL DEFAULT NULL,
  `time_Datawarehouse` timestamp(0) NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `id_config` int(11) NULL DEFAULT NULL,
  `rows_update_datawarehouse` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `id_config`(`id_config`) USING BTREE,
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`id_config`) REFERENCES `config` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
