-- phpMyAdmin SQL Dump
-- version 3.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 30, 2017 at 11:29 PM
-- Server version: 5.5.25a
-- PHP Version: 5.4.4

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `roseau`
--

-- --------------------------------------------------------

--
-- Table structure for table `messenger_friendships`
--

CREATE TABLE IF NOT EXISTS `messenger_friendships` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender` int(11) NOT NULL,
  `receiver` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `messenger_friendships`
--

INSERT INTO `messenger_friendships` (`id`, `sender`, `receiver`) VALUES
(1, 2, 5);

-- --------------------------------------------------------

--
-- Table structure for table `messenger_messages`
--

CREATE TABLE IF NOT EXISTS `messenger_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to_id` int(11) NOT NULL,
  `from_id` int(11) NOT NULL,
  `time_sent` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `unread` tinyint(1) NOT NULL,
  `message` varchar(300) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `messenger_requests`
--

CREATE TABLE IF NOT EXISTS `messenger_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_id` int(11) NOT NULL,
  `to_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE IF NOT EXISTS `rooms` (
  `id` int(100) NOT NULL AUTO_INCREMENT,
  `name` varchar(75) NOT NULL,
  `room_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 = private room, 1 = public',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `owner_id` int(100) NOT NULL DEFAULT '-1',
  `description` varchar(250) DEFAULT NULL,
  `password` varchar(25) NOT NULL DEFAULT '',
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `users_now` int(11) NOT NULL DEFAULT '0',
  `users_max` int(11) NOT NULL DEFAULT '25',
  `cct` varchar(50) NOT NULL DEFAULT 'floor1',
  `model` varchar(15) NOT NULL DEFAULT '0',
  `wallpaper` varchar(5) NOT NULL DEFAULT '0',
  `floor` varchar(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=22 ;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`id`, `name`, `room_type`, `date_created`, `owner_id`, `description`, `password`, `state`, `users_now`, `users_max`, `cct`, `model`, `wallpaper`, `floor`) VALUES
(12, 'Main Lobby', 1, '2017-03-30 18:12:13', -1, NULL, '', 0, 0, 25, 'lobby', 'lobby_a', '0', '0'),
(13, 'Habbo Lido', 1, '2017-03-30 18:46:42', -1, NULL, '', 0, 0, 25, 'lido', 'pool_a', '0', '0'),
(14, 'The Dirty Duck Pub', 1, '2017-03-30 20:36:00', -1, NULL, '', 0, 0, 25, 'pub', 'pub_a', '0', '0'),
(16, 'Hotel Kitchen', 1, '2017-03-30 20:58:19', -1, NULL, '', 0, 0, 25, 'kitchen', 'cr_kitchen', '0', '0'),
(17, 'Cafe Ole', 1, '2017-03-30 20:58:19', -1, NULL, '', 0, 0, 25, 'cafe', 'taivas_cafe', '0', '0'),
(19, 'Habburger''s', 1, '2017-03-30 21:04:13', -1, NULL, '', 0, 0, 25, 'habburger', 'habburger', '0', '0'),
(20, 'Club Massiva', 1, '2017-03-30 20:36:00', -1, NULL, '', 0, 0, 25, 'club', 'bar_a', '0', '0'),
(21, 'Habbo Lido II', 1, '2017-03-30 21:14:46', -1, NULL, '', 0, 0, 25, 'lido', 'pool_b', '0', '0');

-- --------------------------------------------------------

--
-- Table structure for table `room_models`
--

CREATE TABLE IF NOT EXISTS `room_models` (
  `id` varchar(100) NOT NULL,
  `door_x` int(11) NOT NULL,
  `door_y` int(11) NOT NULL,
  `door_z` double NOT NULL,
  `door_dir` int(4) NOT NULL DEFAULT '2',
  `heightmap` text NOT NULL,
  `public_items` text NOT NULL,
  `club_only` enum('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `room_models`
--

INSERT INTO `room_models` (`id`, `door_x`, `door_y`, `door_z`, `door_dir`, `heightmap`, `public_items`, `club_only`) VALUES
('model_a', 3, 5, 0, 2, 'xxxxxxxxxxxx\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxx00000000\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx', '', '0'),
('model_b', 0, 5, 0, 2, 'xxxxxxxxxxxx\r\nxxxxx0000000\r\nxxxxx0000000\r\nxxxxx0000000\r\nxxxxx0000000\r\nx00000000000\r\nx00000000000\r\nx00000000000\r\nx00000000000\r\nx00000000000\r\nx00000000000\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx', '', '0'),
('model_c', 4, 7, 0, 2, 'xxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx', '', '0'),
('model_d', 4, 7, 0, 2, 'xxxxxxxxxxxx\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxx000000x\r\nxxxxxxxxxxxx', '', '0'),
('model_e', 1, 5, 0, 2, 'xxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxx0000000000\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx', '', '0'),
('model_f', 2, 5, 0, 2, 'xxxxxxxxxxxx\r\nxxxxxxx0000x\r\nxxxxxxx0000x\r\nxxx00000000x\r\nxxx00000000x\r\nxxx00000000x\r\nxxx00000000x\r\nx0000000000x\r\nx0000000000x\r\nx0000000000x\r\nx0000000000x\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx\r\nxxxxxxxxxxxx', '', '0'),
('lobby_a', 0, 3, 0, 2, 'XXXXXXXXX77777777777XXXXX\r\nXXXXXXXXX777777777777XXXX\r\nXXXXXXXXX777777777766XXXX\r\nXXXXXXXXX777777777755XXXX\r\nXX333333333333333334433XX\r\nXX333333333333333333333XX\r\nXX333333333333333333333XX\r\n33333333333333333333333XX\r\n333333XXXXXXX3333333333XX\r\n333333XXXXXXX2222222222XX\r\n333333XXXXXXX2222222222XX\r\nXX3333XXXXXXX2222222222XX\r\nXX3333XXXXXXX222222221111\r\nXX3333XXXXXXX111111111111\r\n333333XXXXXXX111111111111\r\n3333333222211111111111111\r\n3333333222211111111111111\r\n3333333222211111111111111\r\nXX33333222211111111111111\r\nXX33333222211111111111111\r\nXX3333322221111111XXXXXXX\r\nXXXXXXX22221111111XXXXXXX\r\nXXXXXXX22221111111XXXXXXX\r\nXXXXXXX22221111111XXXXXXX\r\nXXXXXXX22221111111XXXXXXX\r\nXXXXXXX222X1111111XXXXXXX\r\nXXXXXXX222X1111111XXXXXXX\r\nXXXXXXXXXXXX11XXXXXXXXXXX\r\nXXXXXXXXXXXX11XXXXXXXXXXX\r\nXXXXXXXXXXXX11XXXXXXXXXXX\r\nXXXXXXXXXXXX11XXXXXXXXXXX', '', '0');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `rank` tinyint(1) NOT NULL DEFAULT '1',
  `join_date` bigint(20) DEFAULT NULL,
  `last_online` bigint(20) DEFAULT NULL,
  `email` varchar(256) DEFAULT NULL,
  `mission` varchar(50) NOT NULL DEFAULT '',
  `figure` longtext NOT NULL,
  `credits` int(11) NOT NULL DEFAULT '0',
  `sex` varchar(10) NOT NULL DEFAULT 'M',
  `country` varchar(10) NOT NULL DEFAULT 'UK',
  `badge` varchar(10) NOT NULL DEFAULT '',
  `birthday` varchar(30) NOT NULL DEFAULT '01/01/1970',
  `has_logged_in` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `rank`, `join_date`, `last_online`, `email`, `mission`, `figure`, `credits`, `sex`, `country`, `badge`, `birthday`, `has_logged_in`) VALUES
(2, 'test', '123', 1, 44324323, 1490046606, 'ereewr@wwwwaaac.com', 'eating more cake, k?', 'sd=001/0&hr=001/255,255,255&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=001/232,177,55&ls=001/232,177,55&rs=001/232,177,55&lg=001/119,159,187&sh=003/121,94,83', 0, 'Female', 'UK', '', '01.01.1997', 1),
(5, 'Alex', '123', 1, 1489384512, 1490799539, 'we3rejfpef3@cefc.com', 'so i herd u liek duckz', 'sd=001/0&hr=008/231,201,163&hd=002/255,204,153&ey=001/0&fc=001/255,204,153&bd=001/255,204,153&lh=001/255,204,153&rh=001/255,204,153&ch=008/255,237,179&ls=002/255,237,179&rs=002/255,237,179&lg=006/149,120,78&sh=003/121,94,83', 5000, 'Male', 'UK', '', '01.01.1997', 1);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
