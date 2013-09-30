/*
SQLyog Community Edition- MySQL GUI v6.07
Host - 5.0.45-community-nt : Database - beehive
*********************************************************************
Server version : 5.0.45-community-nt
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

create database if not exists `beehive`;

USE `beehive`;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `code` */

DROP TABLE IF EXISTS `code`;

CREATE TABLE `code` (
  `oid` bigint(20) NOT NULL auto_increment,
  `comment` text,
  `name` varchar(255) NOT NULL,
  `value` text NOT NULL,
  `remote_section_oid` bigint(20) NOT NULL,
  PRIMARY KEY  (`oid`),
  KEY `FK20220D6709A57C` (`remote_section_oid`),
  CONSTRAINT `FK20220D6709A57C` FOREIGN KEY (`remote_section_oid`) REFERENCES `remote_section` (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

/*Data for the table `code` */

insert  into `code`(`oid`,`comment`,`name`,`value`,`remote_section_oid`) values (1,'','STANDBY/ON','0x000000000AF5E817',1),(2,'','VOLUME\'/\\\'','0x000000000AF548B7',1),(3,'','VOLUME\'\\/\'','0x000000000AF5A857',1),(4,'','MUTE','0x000000000AF5D02F',1),(5,'','INPUT,VIDEO_1/2','0x000000000AF504FB',1),(6,'','INPUT_RGB_1/2','0x000000000AF5C837',1),(7,'','\'MENU\'','0x000000000AF5D629',1),(8,'','\'ZOOM_+\'','0x000000000AF58E71',1),(9,'','\'ZOOM_-\'','0x000000000AF50EF1',1),(10,'','\'FOCUS_+\'','0x000000000AF54EB1',1),(11,'','\'FOCUS_-\'','0x000000000AF5CE31',1),(12,'','TIMER','0x000000000AF5D22D',1),(13,'','BLANK','0x000000000AF55CA3',1),(14,'','POSITION','0x000000000AF517E8',1),(15,'','RIGHT_MOUSE,RESET','0x000000000AF502FD',1),(16,'','LEFT_MOUSE','0x000000000AF59C63',1),(17,'','\'/\\\'','0x000000000AF5728D',1),(18,'','\'<\'','0x000000000AF5BA45',1),(19,'','\'>\'','0x000000000AF53AC5',1),(20,'','\'\\/\'','0x000000000AF5CA35',1);

/*Table structure for table `model` */

DROP TABLE IF EXISTS `model`;

CREATE TABLE `model` (
  `oid` bigint(20) NOT NULL auto_increment,
  `comment` text,
  `file_name` varchar(255) default NULL,
  `name` varchar(255) NOT NULL,
  `vendor_oid` bigint(20) NOT NULL,
  PRIMARY KEY  (`oid`),
  KEY `FK4710B09B0477395` (`vendor_oid`),
  CONSTRAINT `FK4710B09B0477395` FOREIGN KEY (`vendor_oid`) REFERENCES `vendor` (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `model` */

insert  into `model`(`oid`,`comment`,`file_name`,`name`,`vendor_oid`) values (1,'#\r\n# This config file has been automatically converted from a device file\r\n# found in the 06/26/00 release of the Windows Slink-e software\r\n# package.\r\n#\r\n# Many thanks to Colby Boles of Nirvis Systems Inc. for allowing us to\r\n# use these files.\r\n#\r\n# The original filename was: \"3m mp8640 3m lcd projector.cde\"\r\n#\r\n# The original description for this device was:\r\n#\r\n# 3m mp8640 3m lcd projector\r\n#\r\n','MP8640','MP8640',1);

/*Table structure for table `remote_option` */

DROP TABLE IF EXISTS `remote_option`;

CREATE TABLE `remote_option` (
  `oid` bigint(20) NOT NULL auto_increment,
  `comment` text,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `remote_section_oid` bigint(20) NOT NULL,
  PRIMARY KEY  (`oid`),
  KEY `FK20BAD28E6709A57C` (`remote_section_oid`),
  CONSTRAINT `FK20BAD28E6709A57C` FOREIGN KEY (`remote_section_oid`) REFERENCES `remote_section` (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `remote_option` */

insert  into `remote_option`(`oid`,`comment`,`name`,`value`,`remote_section_oid`) values (1,'','name','MP8640',1),(2,'','bits','32',1),(3,'','flags','SPACE_ENC',1),(4,'','eps','20',1),(5,'','aeps','200',1),(6,'\r\n','header','8800 4400',1),(7,'','one','550 1650',1),(8,'','zero','550 550',1),(9,'','ptrail','550',1),(10,'','repeat','8800 2200',1),(11,'','gap','38500',1),(12,'','toggle_bit','0',1),(13,'\r\n','frequency','38000',1);

/*Table structure for table `remote_section` */

DROP TABLE IF EXISTS `remote_section`;

CREATE TABLE `remote_section` (
  `oid` bigint(20) NOT NULL auto_increment,
  `comment` text,
  `name` varchar(255) NOT NULL,
  `raw` tinyint(1) NOT NULL,
  `model_oid` bigint(20) NOT NULL,
  PRIMARY KEY  (`oid`),
  KEY `FKB68877ECCE1CFFA3` (`model_oid`),
  CONSTRAINT `FKB68877ECCE1CFFA3` FOREIGN KEY (`model_oid`) REFERENCES `model` (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `remote_section` */

insert  into `remote_section`(`oid`,`comment`,`name`,`raw`,`model_oid`) values (1,'#\r\n# This config file has been automatically converted from a device file\r\n# found in the 06/26/00 release of the Windows Slink-e software\r\n# package.\r\n#\r\n# Many thanks to Colby Boles of Nirvis Systems Inc. for allowing us to\r\n# use these files.\r\n#\r\n# The original filename was: \"3m mp8640 3m lcd projector.cde\"\r\n#\r\n# The original description for this device was:\r\n#\r\n# 3m mp8640 3m lcd projector\r\n#\r\n\r\n\r\n','MP8640',0,1);

/*Table structure for table `vendor` */

DROP TABLE IF EXISTS `vendor`;

CREATE TABLE `vendor` (
  `oid` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY  (`oid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `vendor` */

insert  into `vendor`(`oid`,`name`) values (1,'3m');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

/*Table structure for table `vendor` */

DROP TABLE IF EXISTS `icon`;

create table `icon` (
    `oid` double ,
    `file_name` varchar (510),
    `name` varchar (510)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `icon` */

insert into `icon` (`oid`, `file_name`, `name`) values('1','menu.png','menu');
