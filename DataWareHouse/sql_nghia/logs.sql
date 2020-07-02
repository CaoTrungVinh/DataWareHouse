use control_data;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time_download` timestamp NULL DEFAULT NULL,
  `time_Staging` timestamp NULL DEFAULT NULL,
  `time_Datawarehouse` timestamp NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `id_config`  int,
  `rows_update_datawarehouse`  int,
  PRIMARY KEY (`id`) USING BTREE,
  FOREIGN KEY (`id_config`) REFERENCES config(`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;