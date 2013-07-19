-- phpMyAdmin SQL Dump
-- version 3.5.8.1deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 20, 2013 at 03:15 AM
-- Server version: 5.5.31-0ubuntu0.13.04.1
-- PHP Version: 5.4.9-4ubuntu2.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `disastercenter`
--

-- --------------------------------------------------------

--
-- Table structure for table `apps`
--

CREATE TABLE IF NOT EXISTS `apps` (
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `gcm_id` varchar(255) NOT NULL,
  `crypt_key` int(11) NOT NULL,
  `secret_code` varchar(255) NOT NULL,
  `loc_lat` double DEFAULT NULL,
  `loc_lng` double DEFAULT NULL,
  `range` int(11) DEFAULT NULL,
  PRIMARY KEY (`idx`),
  UNIQUE KEY `gcm_id` (`gcm_id`),
  UNIQUE KEY `secret_code` (`secret_code`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Table structure for table `info`
--

CREATE TABLE IF NOT EXISTS `info` (
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_disaster` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `news`
--

CREATE TABLE IF NOT EXISTS `news` (
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `loc_lat` double DEFAULT NULL,
  `loc_lng` double DEFAULT NULL,
  `loc_name` varchar(255) DEFAULT NULL,
  `type_disaster` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
  `idx` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_idx` bigint(20) NOT NULL,
  `loc_lat` double NOT NULL,
  `loc_lng` double NOT NULL,
  `loc_accuracy` double NOT NULL,
  `loc_name` varchar(255) NOT NULL DEFAULT '""',
  `type_report` int(11) NOT NULL,
  `type_disaster` int(11) NOT NULL,
  `content` text NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;