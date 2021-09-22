-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 22, 2021 at 12:23 AM
-- Server version: 8.0.22
-- PHP Version: 7.3.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pametna_kuca`
--
CREATE DATABASE IF NOT EXISTS `pametna_kuca` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `pametna_kuca`;

-- --------------------------------------------------------

--
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
CREATE TABLE IF NOT EXISTS `alarm` (
  `idAlarm` int NOT NULL AUTO_INCREMENT,
  `repeating` tinyint DEFAULT NULL,
  `repeatPeriod` int DEFAULT NULL,
  `time` datetime NOT NULL,
  `idSong` int DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `repeatCount` int DEFAULT NULL,
  `repeatCountTotal` int DEFAULT NULL,
  PRIMARY KEY (`idAlarm`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `alarm`
--


-- --------------------------------------------------------

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
CREATE TABLE IF NOT EXISTS `job` (
  `idJob` int NOT NULL AUTO_INCREMENT,
  `description` varchar(45) DEFAULT NULL,
  `location` varchar(100) NOT NULL,
  `time` datetime NOT NULL,
  `duration` int NOT NULL,
  `userid` int NOT NULL,
  `alarmid` int DEFAULT NULL,
  PRIMARY KEY (`idJob`),
  KEY `FK_job_user_idx` (`userid`),
  KEY `FK_job_alarm_idx` (`alarmid`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `job`
--


-- --------------------------------------------------------

--
-- Table structure for table `reproduction`
--

DROP TABLE IF EXISTS `reproduction`;
CREATE TABLE IF NOT EXISTS `reproduction` (
  `idReproduction` int NOT NULL AUTO_INCREMENT,
  `idUser` int NOT NULL,
  `idSong` int NOT NULL,
  PRIMARY KEY (`idReproduction`),
  KEY `idUser_idx` (`idUser`),
  KEY `FK_rep_idSong_idx` (`idSong`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `reproduction`
--


-- --------------------------------------------------------

--
-- Table structure for table `song`
--

DROP TABLE IF EXISTS `song`;
CREATE TABLE IF NOT EXISTS `song` (
  `idSong` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `url` varchar(45) NOT NULL,
  PRIMARY KEY (`idSong`),
  UNIQUE KEY `url_UNIQUE` (`url`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `song`
--


-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `idUser` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `location` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `idKorisnik_UNIQUE` (`idUser`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`idUser`, `username`, `password`, `location`) VALUES
(1, 'kostamat', '123', 'Julino brdo');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `job`
--
ALTER TABLE `job`
  ADD CONSTRAINT `FK_job_alarm` FOREIGN KEY (`alarmid`) REFERENCES `alarm` (`idAlarm`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_job_user` FOREIGN KEY (`userid`) REFERENCES `user` (`idUser`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reproduction`
--
ALTER TABLE `reproduction`
  ADD CONSTRAINT `FK_rep_idSong` FOREIGN KEY (`idSong`) REFERENCES `song` (`idSong`),
  ADD CONSTRAINT `FK_rep_idUser` FOREIGN KEY (`idUser`) REFERENCES `user` (`idUser`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
