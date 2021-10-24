-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 25, 2021 at 12:20 AM
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
  `name` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`customer_id`, `name`, `street`, `number`, `city`, `country`) VALUES
(4, 'Datatrion AB', 'Roslagsgatan', '24b', 'Stockholm', 'Sweden'),
(5, 'JanData Consulting', 'Södra Lundavägen', '10 245 32', 'Staffanstorp', 'Sweden');

-- --------------------------------------------------------

--
-- Table structure for table `license`
--

CREATE TABLE `license` (
  `license_id` bigint(20) NOT NULL,
  `licenseCode` varchar(255) DEFAULT NULL,
  `smaCode` varchar(255) DEFAULT NULL,
  `expDate` date DEFAULT NULL,
  `software_id` bigint(20) DEFAULT NULL,
  `status_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `license`
--

INSERT INTO `license` (`license_id`, `licenseCode`, `smaCode`, `expDate`, `software_id`, `status_id`) VALUES
(5, 'NSPN92301', 'ECCCF9', '2021-12-18', 8, 4),
(6, 'NSPN150390', 'E26A75', '2022-05-17', 9, 4);

-- --------------------------------------------------------

--
-- Table structure for table `license_customer`
--

CREATE TABLE `license_customer` (
  `lc_id` bigint(20) NOT NULL,
  `license_id` bigint(20) DEFAULT NULL,
  `customer_id` bigint(20) DEFAULT NULL,
  `end_user` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `license_customer`
--

INSERT INTO `license_customer` (`lc_id`, `license_id`, `customer_id`, `end_user`) VALUES
(18, 6, 5, 1),
(19, 5, 4, 1);

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
(6, 4, 'Robert', 'Mikkelsen', 'robmik@datatrionab.com', '0701-650-680'),
(7, 5, 'Robert', 'Mikkelsen', 'robmik@jandata.com', '+46-463-042-06');

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE `settings` (
  `setting_id` bigint(20) NOT NULL,
  `stat_with_sma` bigint(20) DEFAULT NULL,
  `stat_without_sma` bigint(20) DEFAULT NULL,
  `show_deleted` tinyint(1) DEFAULT '0',
  `stat_deleted` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`setting_id`, `stat_with_sma`, `stat_without_sma`, `show_deleted`, `stat_deleted`) VALUES
(2, 4, 5, 1, 6);

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
(8, 'Teklynx'),
(9, 'Bartender');

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
(6, 'deleted');

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
  MODIFY `customer_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `license`
--
ALTER TABLE `license`
  MODIFY `license_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `license_customer`
--
ALTER TABLE `license_customer`
  MODIFY `lc_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `person`
--
ALTER TABLE `person`
  MODIFY `person_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `settings`
--
ALTER TABLE `settings`
  MODIFY `setting_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `software`
--
ALTER TABLE `software`
  MODIFY `software_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `status`
--
ALTER TABLE `status`
  MODIFY `status_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

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
