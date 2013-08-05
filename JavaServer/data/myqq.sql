create database myqq  CHARACTER SET  utf8  COLLATE utf8_general_ci;

/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50022
Source Host           : localhost:3306
Source Database       : myqq

Target Server Type    : MYSQL
Target Server Version : 50022
File Encoding         : 65001

Date: 2013-07-21 12:45:45
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `numberpool`
-- ----------------------------
DROP TABLE IF EXISTS `numberpool`;
CREATE TABLE `numberpool` (
  `numberstart` int(11) default NULL,
  `numberend` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of numberpool
-- ----------------------------
INSERT INTO numberpool VALUES ('10000', '100000');

-- ----------------------------
-- Table structure for `photo`
-- ----------------------------
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo` (
  `photoId` int(11) NOT NULL auto_increment,
  `userId` varchar(100) NOT NULL,
  `photoBlob` longblob,
  `isUse` int(10) default NULL,
  PRIMARY KEY  (`photoId`),
  KEY `userId_fk` (`userId`),
  CONSTRAINT `userId_fk` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of photo
-- ----------------------------

-- ----------------------------
-- Table structure for `status`
-- ----------------------------
DROP TABLE IF EXISTS `status`;
CREATE TABLE `status` (
  `statusId` int(11) NOT NULL auto_increment,
  `status` int(11) default NULL,
  PRIMARY KEY  (`statusId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of status 1.在线 2.离线 3.隐身 4.离开
-- ----------------------------
INSERT INTO status VALUES ('1', '1');
INSERT INTO status VALUES ('2', '2');
INSERT INTO status VALUES ('3', '3');
INSERT INTO status VALUES ('4', '4');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` varchar(50) NOT NULL,
  `userName` varchar(100) default NULL,
  `userPass` varchar(100) default NULL,
  `userAddress` varchar(100) default NULL,
  `userSex` varchar(10) default NULL,
  `statusId` int(11) default NULL,
  `registerTime` datetime default NULL,
  `lastLoginTime` datetime default NULL,
  `signature` varchar(100) default NULL,
  `birthDate` datetime default NULL,
  PRIMARY KEY  (`userId`),
  KEY `status_fk` (`statusId`),
  CONSTRAINT `status_fk` FOREIGN KEY (`statusId`) REFERENCES `status` (`statusId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO user VALUES ('10000', '黄华', '3629226', '江西', '1', '4', '2013-07-21 12:45:26', null, null, null);

INSERT INTO user VALUES ('10001', '朱燕', '3629226', '湖北', '1', '4', '2013-07-21 12:45:26', null, null, null);


CREATE TABLE `newmessage` (
  `id` int(11) NOT NULL auto_increment,
  `userId` int(11) default NULL,
  `send_userId` int(11) default NULL,
  `from_userId` int(11) default NULL,
  `to_userId` int(11) default NULL,
  `context` varchar(1000) default NULL,
  `messageDate` datetime default NULL,
  `isNew` int(10) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
