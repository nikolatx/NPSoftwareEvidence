-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 24, 2021 at 01:40 PM
-- Server version: 10.1.40-MariaDB
-- PHP Version: 7.3.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `npsoftwareevidence`
--

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `customer_id` bigint(20) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `city`, `country`, `name`, `number`, `street`) VALUES
(1, 'City1', 'Country1', 'Company11', '1', 'str11'),
(2, 'ci2', 'co2', 'Company21', '2', 'str2');

-- --------------------------------------------------------

--
-- Table structure for table `license`
--

CREATE TABLE `license` (
  `license_id` bigint(20) NOT NULL,
  `expDate` date DEFAULT NULL,
  `licenseCode` varchar(255) DEFAULT NULL,
  `smaCode` varchar(255) DEFAULT NULL,
  `software_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `license`
--

INSERT INTO `license` (`license_id`, `expDate`, `licenseCode`, `smaCode`, `software_id`, `status_id`) VALUES
(1, NULL, '111-111-111', NULL, 1, 5),
(2, NULL, '222-222-222', '', 2, 5);

-- --------------------------------------------------------

--
-- Table structure for table `license_customer`
--

CREATE TABLE `license_customer` (
  `lc_id` bigint(20) NOT NULL,
  `end_user` tinyint(1) DEFAULT '0',
  `customer_id` bigint(20) DEFAULT NULL,
  `license_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `license_customer`
--

INSERT INTO `license_customer` (`lc_id`, `end_user`, `customer_id`, `license_id`) VALUES
(1, 0, 2, 1),
(12, 1, 1, 1),
(13, 0, NULL, 2),
(14, 1, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `person_id` bigint(20) NOT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `person`
--

INSERT INTO `person` (`person_id`, `customer_id`, `first_name`, `last_name`, `email`, `phone`) VALUES
(2, 2, 'fn211', 'ln2', 'em2', '2222'),
(4, 1, 'fn111', 'ln111', 'em11@s2.com', '12313121');

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE `settings` (
  `setting_id` bigint(20) NOT NULL,
  `show_deleted` tinyint(1) DEFAULT '0',
  `stat_deleted` bigint(20) DEFAULT NULL,
  `stat_with_sma` bigint(20) DEFAULT NULL,
  `stat_without_sma` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`setting_id`, `show_deleted`, `stat_deleted`, `stat_with_sma`, `stat_without_sma`) VALUES
(2, 1, 6, 4, 5);

-- --------------------------------------------------------

--
-- Table structure for table `software`
--

CREATE TABLE `software` (
  `software_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `software`
--

INSERT INTO `software` (`software_id`, `name`) VALUES
(1, 'Software 1'),
(2, 'Software 2'),
(3, 'Software 3'),
(4, 'Software 4'),
(5, 'Software 5'),
(6, 'Software 6');

-- --------------------------------------------------------

--
-- Table structure for table `status`
--

CREATE TABLE `status` (
  `status_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `status`
--

INSERT INTO `status` (`status_id`, `name`) VALUES
(4, 'active'),
(5, 'not activated'),
(6, 'deleted'),
(7, 'test1'),
(8, 'novi1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`);

--
-- Indexes for table `license`
--
ALTER TABLE `license`
  ADD PRIMARY KEY (`license_id`),
  ADD KEY `FK_license_software_id` (`software_id`),
  ADD KEY `FK_license_status_id` (`status_id`);

--
-- Indexes for table `license_customer`
--
ALTER TABLE `license_customer`
  ADD PRIMARY KEY (`lc_id`),
  ADD KEY `FK_license_customer_license_id` (`license_id`),
  ADD KEY `FK_license_customer_customer_id` (`customer_id`);

--
-- Indexes for table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`person_id`),
  ADD KEY `FK_person_customer_id` (`customer_id`);

--
-- Indexes for table `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`setting_id`),
  ADD KEY `FK_settings_stat_deleted` (`stat_deleted`),
  ADD KEY `FK_settings_stat_without_sma` (`stat_without_sma`),
  ADD KEY `FK_settings_stat_with_sma` (`stat_with_sma`);

--
-- Indexes for table `software`
--
ALTER TABLE `software`
  ADD PRIMARY KEY (`software_id`);

--
-- Indexes for table `status`
--
ALTER TABLE `status`
  ADD PRIMARY KEY (`status_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `customer_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `license`
--
ALTER TABLE `license`
  MODIFY `license_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `license_customer`
--
ALTER TABLE `license_customer`
  MODIFY `lc_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `person`
--
ALTER TABLE `person`
  MODIFY `person_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `settings`
--
ALTER TABLE `settings`
  MODIFY `setting_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `software`
--
ALTER TABLE `software`
  MODIFY `software_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `status`
--
ALTER TABLE `status`
  MODIFY `status_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `license`
--
ALTER TABLE `license`
  ADD CONSTRAINT `FK_license_software_id` FOREIGN KEY (`software_id`) REFERENCES `software` (`software_id`),
  ADD CONSTRAINT `FK_license_status_id` FOREIGN KEY (`status_id`) REFERENCES `status` (`status_id`);

--
-- Constraints for table `license_customer`
--
ALTER TABLE `license_customer`
  ADD CONSTRAINT `FK_license_customer_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `FK_license_customer_license_id` FOREIGN KEY (`license_id`) REFERENCES `license` (`license_id`);

--
-- Constraints for table `person`
--
ALTER TABLE `person`
  ADD CONSTRAINT `FK_person_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

--
-- Constraints for table `settings`
--
ALTER TABLE `settings`
  ADD CONSTRAINT `FK_settings_stat_deleted` FOREIGN KEY (`stat_deleted`) REFERENCES `status` (`status_id`),
  ADD CONSTRAINT `FK_settings_stat_with_sma` FOREIGN KEY (`stat_with_sma`) REFERENCES `status` (`status_id`),
  ADD CONSTRAINT `FK_settings_stat_without_sma` FOREIGN KEY (`stat_without_sma`) REFERENCES `status` (`status_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
