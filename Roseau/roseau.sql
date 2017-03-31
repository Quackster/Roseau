-- phpMyAdmin SQL Dump
-- version 3.5.2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 31, 2017 at 07:40 AM
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
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `hidden` tinyint(1) NOT NULL DEFAULT '0',
  `date_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `owner_id` int(100) NOT NULL DEFAULT '-1',
  `description` varchar(250) DEFAULT NULL,
  `password` varchar(25) NOT NULL DEFAULT '',
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `users_now` int(11) NOT NULL DEFAULT '0',
  `users_max` int(11) NOT NULL DEFAULT '25',
  `cct` varchar(50) NOT NULL DEFAULT 'floor1',
  `model` varchar(15) NOT NULL DEFAULT '',
  `wallpaper` varchar(5) NOT NULL DEFAULT '0',
  `floor` varchar(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=31 ;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`id`, `name`, `room_type`, `enabled`, `hidden`, `date_created`, `owner_id`, `description`, `password`, `state`, `users_now`, `users_max`, `cct`, `model`, `wallpaper`, `floor`) VALUES
(12, 'Main Lobby', 1, 1, 0, '2017-03-30 18:12:13', -1, NULL, '', 0, 0, 25, 'lobby', 'lobby_a', '0', '0'),
(13, 'Habbo Lido', 1, 1, 0, '2017-03-30 18:46:42', -1, NULL, '', 0, 0, 25, 'lido', 'pool_a', '0', '0'),
(14, 'The Dirty Duck Pub', 1, 1, 0, '2017-03-30 20:36:00', -1, NULL, '', 0, 0, 25, 'pub', 'pub_a', '0', '0'),
(16, 'Hotel Kitchen', 1, 1, 0, '2017-03-30 20:58:19', -1, NULL, '', 0, 0, 25, 'kitchen', 'cr_kitchen', '0', '0'),
(17, 'Cafe Ole', 1, 1, 0, '2017-03-30 20:58:19', -1, NULL, '', 0, 0, 25, 'cafe', 'taivas_cafe', '0', '0'),
(19, 'Habburger''s', 1, 1, 0, '2017-03-30 21:04:13', -1, NULL, '', 0, 0, 25, 'habburger', 'habburger', '0', '0'),
(21, 'Habbo Lido II', 1, 1, 0, '2017-03-30 21:14:46', -1, NULL, '', 0, 0, 25, 'lido', 'pool_b', '0', '0'),
(22, 'Club Massiva', 1, 1, 0, '2017-03-31 03:10:05', -1, NULL, '', 0, 0, 25, 'club', 'bar_a', '0', '0'),
(24, 'Theatredrome', 1, 1, 0, '2017-03-31 03:12:18', -1, NULL, '', 0, 0, 25, 'theatredrome', 'theater', '0', '0'),
(25, 'Basement Lobby', 1, 1, 0, '2017-03-31 03:25:13', -1, NULL, '', 0, 0, 25, 'lobby', 'floorlobby_a', '0', '0'),
(26, 'Median Lobby', 1, 1, 0, '2017-03-31 03:28:12', -1, NULL, '', 0, 0, 25, 'lobby', 'floorlobby_b', '0', '0'),
(27, 'Skylight Lobby', 1, 1, 0, '2017-03-31 03:28:12', -1, NULL, '', 0, 0, 25, 'lobby', 'floorlobby_c', '0', '0'),
(29, 'Club Massiva: dancefloor', 1, 1, 1, '2017-03-31 04:40:51', -1, NULL, '', 0, 0, 25, 'club', 'bar_b', '0', '0');

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
('floorlobby_b', 9, 21, 0, 2, 'XXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXX XXX0000000000000000XXXXXXX0 XXX000000000000000000XXXX00 X00000000000000000000000000 X00000000000000000000000000 XXX000000000000000000000000 XXXXXXX00000000000000000000 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX1XX100000011111111111111 XXX1XX100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX', '794 moneyplant 3 2 0 0\r\n795 roommatic 5 2 0 4\r\n796 roommatic 7 2 0 4\r\n797 roommatic 9 2 0 4\r\n798 roommatic 11 2 0 4\r\n799 roommatic 13 2 0 4\r\n800 watermatic 26 2 0 4\r\n801 standinglamp 3 8 1 0\r\n802 edgeb1 14 8 1 6\r\n803 edgeb1 15 8 1 0\r\n804 edgeb1 16 8 1 0\r\n805 edgeb1 17 8 1 0\r\n806 edgeb1 18 8 1 0\r\n807 edgeb1 19 8 1 0\r\n808 edgeb1 20 8 1 0\r\n809 edgeb1 21 8 1 0\r\n810 edgeb1 22 8 1 0\r\n811 edgeb1 23 8 1 0\r\n812 edgeb1 24 8 1 0\r\n813 edgeb1 25 8 1 0\r\n814 edgeb1 26 8 1 2\r\n815 chairf2b 3 9 1 4\r\n816 chairf2c 4 9 1 4\r\n817 chairf2 5 9 1 4\r\n818 moneyplant 15 9 1 0\r\n819 chairf2b 16 9 1 4\r\n820 chairf2c 17 9 1 4\r\n821 chairf2 18 9 1 4\r\n822 moneyplant 19 9 1 4\r\n823 moneyplant 21 9 1 0\r\n824 chairf2b 22 9 1 4\r\n825 chairf2c 23 9 1 4\r\n826 chairf2 24 9 1 4\r\n827 moneyplant 25 9 1 4\r\n828 chairf1 3 11 1 0\r\n829 newtable1 4 11 1 0\r\n830 chairf1 5 11 1 0\r\n831 chairf1 16 14 1 2\r\n832 chairf1 18 14 1 6\r\n833 chairf1 22 14 1 2\r\n834 chairf1 24 14 1 6\r\n835 newtable1 17 15 1 0\r\n836 newtable1 23 15 1 0\r\n837 newtable1 3 16 1 0\r\n838 chairf1 16 16 1 2\r\n839 chairf1 18 16 1 6\r\n840 chairf1 22 16 1 2\r\n841 chairf1 24 16 1 6\r\n842 chairf1 3 17 1 2\r\n843 chairf1 5 17 1 6\r\n844 standinglamp 3 18 1 0\r\n845 edgeb2 14 18 1 6\r\n846 edgeb2 15 18 1 0\r\n847 edgeb2 16 18 1 0\r\n848 edgeb2 17 18 1 0\r\n849 edgeb2 18 18 1 0\r\n850 edgeb2 19 18 1 0\r\n851 edgeb2 20 18 1 0\r\n852 edgeb2 21 18 1 0\r\n853 edgeb2 22 18 1 0\r\n854 edgeb2 23 18 1 0\r\n855 edgeb2 24 18 1 0\r\n856 edgeb2 25 18 1 0\r\n857 edgeb2 26 18 1 2\r\n', ''),
('floorlobby_a', 9, 21, 0, 2, 'XXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXX XXX0000000000000000XXXXXXX0 XXX000000000000000000XXXX00 X00000000000000000000000000 X00000000000000000000000000 XXX000000000000000000000000 XXXXXXX00000000000000000000 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX1XX100000011111111111111 XXX1XX100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX', '731 moneyplant 3 2 0 0\r\n732 roommatic 5 2 0 4\r\n733 roommatic 7 2 0 4\r\n734 roommatic 9 2 0 4\r\n735 roommatic 11 2 0 4\r\n736 roommatic 13 2 0 4\r\n737 watermatic 26 2 0 4\r\n738 standinglamp 3 8 1 0\r\n739 edge1 14 8 1 6\r\n740 edge1 15 8 1 0\r\n741 edge1 16 8 1 0\r\n742 edge1 17 8 1 0\r\n743 edge1 18 8 1 0\r\n744 edge1 19 8 1 0\r\n745 edge1 20 8 1 0\r\n746 edge1 21 8 1 0\r\n747 edge1 22 8 1 0\r\n748 edge1 23 8 1 0\r\n749 edge1 24 8 1 0\r\n750 edge1 25 8 1 0\r\n751 edge1 26 8 1 2\r\n752 chairf1 3 9 1 2\r\n753 chairf1 5 9 1 6\r\n754 moneyplant 15 9 1 0\r\n755 chairf2b 16 9 1 4\r\n756 chairf2c 17 9 1 4\r\n757 chairf2 18 9 1 4\r\n758 moneyplant 19 9 1 4\r\n759 moneyplant 21 9 1 0\r\n760 chairf2b 22 9 1 4\r\n761 chairf2c 23 9 1 4\r\n762 chairf2 24 9 1 4\r\n763 moneyplant 25 9 1 4\r\n764 chairf1 3 11 1 2\r\n765 chairf1 5 11 1 6\r\n766 newtable1 3 12 1 0\r\n767 chairf1 16 14 1 4\r\n768 chairf1 18 14 1 4\r\n769 chairf1 22 14 1 4\r\n770 chairf1 24 14 1 4\r\n771 newtable1 17 15 1 0\r\n772 newtable1 23 15 1 0\r\n773 newtable1 3 16 1 0\r\n774 chairf1 16 16 1 0\r\n775 chairf1 18 16 1 0\r\n776 chairf1 22 16 1 0\r\n777 chairf1 24 16 1 0\r\n778 chairf1 3 17 1 2\r\n779 chairf1 5 17 1 6\r\n780 standinglamp 3 18 1 0\r\n781 edge2 14 18 1 6\r\n782 edge2 15 18 1 0\r\n783 edge2 16 18 1 0\r\n784 edge2 17 18 1 0\r\n785 edge2 18 18 1 0\r\n786 edge2 19 18 1 0\r\n787 edge2 20 18 1 0\r\n788 edge2 21 18 1 0\r\n789 edge2 22 18 1 0\r\n790 edge2 23 18 1 0\r\n791 edge2 24 18 1 0\r\n792 edge2 25 18 1 0\r\n793 edge2 26 18 1 2\r\n', '0'),
('theater', 20, 27, 0, 2, 'XXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXX XXXXXXX111111111XXXXXXX XXXXXXX11111111100000XX XXXX00X11111111100000XX XXXX00x11111111100000XX 4XXX00X11111111100000XX 4440000XXXXXXXXX00000XX 444000000000000000000XX 4XX000000000000000000XX 4XX0000000000000000000X 44400000000000000000000 44400000000000000000000 44X0000000000000000O000 44X11111111111111111000 44X11111111111111111000 33X11111111111111111000 22X11111111111111111000 22X11111111111111111000 22X11111111111111111000 22X11111111111111111000 22X11111111111111111000 22211111111111111111000 22211111111111111111000 XXXXXXXXXXXXXXXXXXXX00X XXXXXXXXXXXXXXXXXXXX00X', '571 mic 11 10 1 0\r\n572 thchair2 2 11 4 2\r\n573 thchair2 2 12 4 2\r\n574 thchair2 2 15 4 2\r\n575 thchair1 6 15 0 0\r\n576 thchair1 7 15 0 0\r\n577 thchair1 8 15 0 0\r\n578 thchair1 9 15 0 0\r\n579 thchair1 10 15 0 0\r\n580 thchair1 12 15 0 0\r\n581 thchair1 13 15 0 0\r\n582 thchair1 14 15 0 0\r\n583 thchair1 15 15 0 0\r\n584 thchair1 16 15 0 0\r\n585 thchair2 2 16 4 2\r\n586 thchair1 6 20 1 0\r\n587 thchair1 7 20 1 0\r\n588 thchair1 8 20 1 0\r\n589 thchair1 9 20 1 0\r\n590 thchair1 10 20 1 0\r\n591 thchair1 12 20 1 0\r\n592 thchair1 13 20 1 0\r\n593 thchair1 14 20 1 0\r\n594 thchair1 15 20 1 0\r\n595 thchair1 16 20 1 0\r\n596 thchair1 6 23 1 0\r\n597 thchair1 7 23 1 0\r\n598 thchair1 8 23 1 0\r\n599 thchair1 9 23 1 0\r\n600 thchair1 10 23 1 0\r\n601 thchair1 12 23 1 0\r\n602 thchair1 13 23 1 0\r\n603 thchair1 14 23 1 0\r\n604 thchair1 15 23 1 0\r\n605 thchair1 16 23 1 0\r\n606 thchair1 6 26 1 0\r\n607 thchair1 7 26 1 0\r\n608 thchair1 8 26 1 0\r\n609 thchair1 9 26 1 0\r\n610 thchair1 10 26 1 0\r\n611 thchair1 12 26 1 0\r\n612 thchair1 13 26 1 0\r\n613 thchair1 14 26 1 0\r\n614 thchair1 15 26 1 0\r\n615 thchair1 16 26 1 0\r\n', '0'),
('pool_b', 9, 21, 7, 2, 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx7xxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx777xxxxxxxxxxx xxxxxxxxxxxxxxxxxx8888888x7xxx77777xxxxxxxxxx xxxxxxxxxxxxxxxxxx8888888x7xxx777777xxxxxxxxx xxxxxxxxxxxxxxxx88xxxxx77x7x777777777xxxxxxxx xxxxxxxxxxxxxxxx88x7777777777777777777xxxxxxx xxxxxxxxxxxxxxxx88x77777777777777777777xxxxxx xxxxxxxxxxxxxx9988x77777777777777777777xxxxxx xxxxxxxxxxxxxx9988x7777777777777777777x00xxxx xxxxxxxxxxxxxx9988x777777777777777777x0000xxx xxxxxxxxxxxxxx9988x7777777x0000000000000000xx xxxxxxxxxxxxxx9988x777777x000000000000000000x 7777777777xxxx9988777777x0x0000000000000000xx x7777777777xxx998877777x000x00000000000000xxx xx7777777777xx99887777x00000x000000000000xxxx xxx7777777777x9988777x0000000x0000000000xxxxx xxxx777777777x777777x00000000x000000000xxxxxx xxxxx777777777777777000000000x00000000xxxxxxx xxxxxx77777777777777000000000x0000000xxxxxxxx xxxxxxx777777x7777770000000000xxxx00xxxxxxxxx xxxxxxxx77777777777xx0000000000000xxxxxxxxxxx xxxxxxxxx777777110000x000000000000xxxxxxxxxxx xxxxxxxxxx7x77x1100000x0000000000xxxxxxxxxxxx xxxxxxxxxxx777x11000000x00000000xxxxxxxxxxxxx xxxxxxxxxxxx771110000000x000000xxxxxxxxxxxxxx xxxxxxxxxxxxx111100000000x0000xxxxxxxxxxxxxxx xxxxxxxxxxxxxx11100000000x000xxxxxxxxxxxxxxxx xxxxxxxxxxxxxxx1100000000x00xxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxx110000000x0xxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxx110000000xxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxx1100000xxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx11000xxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxx110xxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxx1xxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '405 poolEnter 17 21 0 0\r\n406 poolExit 17 22 0 0\r\n407 poolEnter 31 10 0 0\r\n408 poolExit 31 11 0 0\r\n409 poolExit 20 19 0 0\r\n410 poolLift 26 4 0 0\r\n694 umbrella2 33 2 7 0\r\n695 pool_2sofa2 18 3 8 4\r\n696 pool_2sofa1 19 3 8 4\r\n697 pool_2sofa2 20 3 8 4\r\n698 pool_2sofa1 21 3 8 4\r\n699 pool_2sofa2 22 3 8 4\r\n700 pool_2sofa1 23 3 8 4\r\n701 pool_chair2 33 3 7 4\r\n702 pool_chair2 32 4 7 2\r\n703 pool_table2 33 4 7 0\r\n704 pool_chair2 34 4 7 6\r\n705 pool_2sofa2 16 5 8 2\r\n706 pool_chair2 33 5 7 0\r\n707 pool_2sofa1 16 6 8 2\r\n708 pool_2sofa2 16 7 8 2\r\n709 pool_chair2 35 7 7 4\r\n710 flower1 14 8 9 0\r\n711 pool_2sofa1 16 8 8 2\r\n712 umbrella2 33 8 7 0\r\n713 pool_chair2 34 8 7 2\r\n714 pool_table2 35 8 7 0\r\n715 pool_chair2 36 8 7 6\r\n716 pool_2sofa2 14 9 9 2\r\n717 pool_2sofa2 16 9 8 2\r\n718 pool_chair2 35 9 7 0\r\n719 pool_2sofa1 14 10 9 2\r\n720 pool_2sofa1 16 10 8 2\r\n721 pool_2sofa2 14 11 9 2\r\n722 pool_2sofa2 16 11 8 2\r\n723 pool_2sofa1 14 12 9 2\r\n724 pool_2sofa1 16 12 8 2\r\n725 flower2b 3 13 7 2\r\n726 flower2a 4 13 7 2\r\n727 pool_2sofa2 14 13 9 2\r\n728 pool_2sofa2 16 13 8 2\r\n729 pool_2sofa1 14 14 9 2\r\n730 pool_2sofa1 16 14 8 2\r\n', '0'),
('cr_kitchen', 7, 21, 0, 2, 'X0XXXX000XXXX000X0X X000000000000000000 X000000000000000000 X000000000000000XXX X00XXXX00XXXX000XXX X00XXXX00XXXX00XXXX X00000000000000XXXX X00000000000000XXXX X00000000000000XXXX X00XXXXXXXXXX00XXXX X00XXXXXXXXXX00XXXX X00XXXXXXXXXX000XXX 0000000000000000XXX 000000000000000XXXX 000000000000000XXXX 000XXX0000XXX00XXXX 000XXX0000XXX00XXXX 000000000000000XXXX 000000000000000XXXX 000000000000000XXXX XXXXXXX00XXXXXXXXXX XXXXXXX00XXXXXXXXXX XXXXXXX00XXXXXXXXXX', '1360 plant 31 0 2111 6\r\n', '0'),
('taivas_cafe', 14, 29, 0, 2, 'XXXXXXXXXXXXX111111X XXXXXXXXXXXXX1111111 XXXXXXXXXXXXX1111111 XXXXXXXXXXXXX1111111 XXXXXXXXXXXXX1111111 XXX11111111111111111 XXX11111111111111111 XX111111111111111111 XX111111111111111111 XX111111111111111111 XXX11111111111111111 111111111XXXXXXX1111 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 111111111X0000000000 X11111111X0000000000 XX1111111X0000000000 XXX111111X0000000000 XXXX11111X0000000000 XXXXX111110000000000 XXXXXX11110000000000 XXXXXXX1110000000000 XXXXXXXX11000000000X XXXXXXXXXX00000000XX XXXXXXXXXXXXXX00XXXX XXXXXXXXXXXXXX00XXXX', '', '0'),
('bar_a', 5, 1, 7, 2, 'xxxx8888xxxxxxxxxxx xxxx7777xxxxxxxxxxx xxxx6666xxxxxxxxxxx xxx6666666555555555 xxx6666666555555555 xxx6666666555555555 xxx6666666555555555 xxx6666666555555555 xxx6666666555555555 xxx6666666555555555 xxx6666666555xxxxxx xxx6666666555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555xxxxxx xxx5555555555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555555555 xxx5555555555xxxxxx xxxx555555555555555 55xx555555555555555 55xx555555555555555 5555555555555555555 5555555555555555555 xxxxxxxx55555xxxxxx xxxxxxxxx5555xxxxxx xxxxxxxxx5555xxxxxx xxxxxxxxx5555xxxxxx xxxxxxxxx4444xxxxxx xxxxxxxxx3333xxxxxx', '919 lounge_chair_small 12 3 5 2\r\n920 lounge_table_one 13 3 5 0\r\n921 lounge_chair_small 14 3 5 6\r\n922 lounge_chair_small 13 4 5 0\r\n923 lounge_chair_small 18 5 5 4\r\n924 lounge_chair_small 17 6 5 2\r\n925 lounge_table_one 18 6 5 0\r\n926 lounge_chair_small 18 7 5 0\r\n927 lounge_private_bigback 13 11 5 3\r\n928 lounge_private_bigsofaback 14 11 5 4\r\n929 lounge_private_bigsofaback 15 11 5 4\r\n930 lounge_private_bigsofaback 16 11 5 4\r\n931 lounge_private_bigsofaback 17 11 5 4\r\n932 lounge_private_bigback 18 11 5 5\r\n933 lounge_private_bigsofa 18 12 5 6\r\n934 lounge_table_threea 14 13 5 0\r\n935 lounge_table_threeb 15 13 5 0\r\n936 lounge_table_threec 16 13 5 0\r\n937 lounge_private_bigsofa 18 13 5 6\r\n938 lounge_private_bigsofa 18 14 5 6\r\n939 lounge_private_bigcorner 13 15 5 1\r\n940 lounge_private_bigsofa 14 15 5 0\r\n941 lounge_private_bigsofa 15 15 5 0\r\n942 lounge_private_bigsofa 16 15 5 0\r\n943 lounge_private_bigsofa 17 15 5 0\r\n944 lounge_private_bigcorner 18 15 5 7\r\n945 lounge_chair_small 3 16 5 4\r\n946 lounge_chair_small 8 16 5 4\r\n947 lounge_table_one 3 17 5 0\r\n948 lounge_chair_small 4 17 5 6\r\n949 lounge_chair_small 7 17 5 2\r\n950 lounge_table_one 8 17 5 0\r\n951 lounge_chair_small 9 17 5 6\r\n952 lounge_private_bigcorner 13 17 5 3\r\n953 lounge_private_bigsofa 14 17 5 4\r\n954 lounge_private_bigsofa 15 17 5 4\r\n955 lounge_private_bigsofa 16 17 5 4\r\n956 lounge_private_bigsofa 17 17 5 4\r\n957 lounge_private_bigcorner 18 17 5 5\r\n958 lounge_chair_small 3 18 5 0\r\n959 lounge_chair_small 8 18 5 0\r\n960 lounge_private_bigsofa 18 18 5 6\r\n961 lounge_table_threea 14 19 5 0\r\n962 lounge_table_threeb 15 19 5 0\r\n963 lounge_table_threec 16 19 5 0\r\n964 lounge_private_bigsofa 18 19 5 6\r\n965 lounge_private_bigsofa 18 20 5 6\r\n966 lounge_private_bigcorner 13 21 5 1\r\n967 lounge_private_bigsofa 14 21 5 0\r\n968 lounge_private_bigsofa 15 21 5 0\r\n969 lounge_private_bigsofa 16 21 5 0\r\n970 lounge_private_bigsofa 17 21 5 0\r\n971 lounge_private_bigcorner 18 21 5 7\r\n972 lounge_bara 4 23 5 6\r\n973 lounge_bara 5 23 5 5\r\n974 lounge_bara 6 23 5 4\r\n975 lounge_bara 7 23 5 3\r\n976 lounge_private_bigcorner 13 23 5 3\r\n977 lounge_private_bigsofa 14 23 5 4\r\n978 lounge_private_bigsofa 15 23 5 4\r\n979 lounge_private_bigsofa 16 23 5 4\r\n980 lounge_private_bigsofa 17 23 5 4\r\n981 lounge_private_bigcorner 18 23 5 5\r\n982 lounge_bara 7 24 5 2\r\n983 lounge_private_bigsofa 18 24 5 6\r\n984 lounge_bara 7 25 5 2\r\n985 lounge_table_threea 14 25 5 0\r\n986 lounge_table_threeb 15 25 5 0\r\n987 lounge_table_threec 16 25 5 0\r\n988 lounge_private_bigsofa 18 25 5 6\r\n989 lounge_bara 7 26 5 2\r\n990 lounge_private_bigsofa 18 26 5 6\r\n991 lounge_bara 7 27 5 2\r\n992 lounge_private_bigcorner 13 27 5 1\r\n993 lounge_private_bigsofa 14 27 5 0\r\n994 lounge_private_bigsofa 15 27 5 0\r\n995 lounge_private_bigsofa 16 27 5 0\r\n996 lounge_private_bigsofa 17 27 5 0\r\n997 lounge_private_bigcorner 18 27 5 7\r\n', '0'),
('pub_a', 15, 25, 0, 2, 'xxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxx2222222211111xxx xxxxxxxxx2222222211111xxx xxxxxxxxx2222222211111xxx xxxxxxxxx2222222211111xxx xxxxxxxxx2222222222111xxx xxxxxxxxx2222222222111xxx xxxxxxxxx2222222222000xxx xxxxxxxxx2222222222000xxx xxxxxxxxx2222222222000xxx xxxxxxxxx2222222222000xxx x333333332222222222000xxx x333333332222222222000xxx x333333332222222222000xxx x333333332222222222000xxx x333333332222222222000xxx x333332222222222222000xxx x333332222222222222000xxx x333332222222222222000xxx x333332222222222222000xxx x333333332222222222000xxx xxxxx31111112222222000xxx xxxxx31111111000000000xxx xxxxx31111111000000000xxx xxxxx31111111000000000xxx xxxxx31111111000000000xxx xxxxxxxxxxxxxxx00xxxxxxxx xxxxxxxxxxxxxxx00xxxxxxxx xxxxxxxxxxxxxxx00xxxxxxxx xxxxxxxxxxxxxxx00xxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxx', '1214 pub_fence 8 12 3 0\r\n1213 pub_fence 18 11 2 0\r\n1212 pub_fence 8 11 3 1\r\n1211 pub_chair2 6 11 3 4\r\n1210 pub_chair2 5 11 3 4\r\n1209 pub_chair2 3 11 3 4\r\n1208 pub_chair2 2 11 3 4\r\n1207 pub_fence 18 10 2 0\r\n1206 pub_fence 18 9 2 0\r\n1205 bardesk3 11 9 2 0\r\n1204 bardesk4 10 9 2 0\r\n1203 pub_fence 18 8 2 0\r\n1202 pub_chair 12 8 2 6\r\n1201 bardesk1 11 8 2 0\r\n1200 pub_fence 18 7 2 0\r\n1199 bardesk2 11 7 2 0\r\n1198 pub_fence 18 6 2 0\r\n1197 pub_chair 12 6 2 6\r\n1196 bardesk1 11 6 2 0\r\n1195 pub_fence 18 5 2 1\r\n1194 bardesk2 11 5 2 0\r\n1193 pub_chair 12 4 2 6\r\n1192 bardesk1 11 4 2 0\r\n1191 bardesk2 11 3 2 0\r\n1190 pub_chair 12 2 2 6\r\n1189 bardesk1 11 2 2 0\r\n1188 pub_sofa 21 1 1 4\r\n1187 pub_sofa 20 1 1 4\r\n1186 pub_sofa2 19 1 1 4\r\n1215 pub_fence 18 12 2 0\r\n1216 pub_fence 8 13 3 0\r\n1217 pub_chair2 9 13 2 2\r\n1218 pub_chair3 14 13 2 2\r\n1219 pub_table2 15 13 2 1\r\n1220 pub_chair3 16 13 2 6\r\n1221 pub_fence 18 13 2 0\r\n1222 pub_fence 8 14 3 2\r\n1223 pub_chair2 9 14 2 2\r\n1224 pub_chair3 14 14 2 2\r\n1225 pub_table2 15 14 2 2\r\n1226 pub_chair3 16 14 2 6\r\n1227 pub_fence 18 14 2 0\r\n1228 pub_table 1 15 3 0\r\n1229 pub_fence 5 15 3 1\r\n1230 pub_fence 18 15 2 0\r\n1231 pub_sofa2 1 16 3 2\r\n1232 pub_fence 5 16 3 0\r\n1233 pub_fence 18 16 2 0\r\n1234 pub_sofa 1 17 3 2\r\n1235 pub_fence 5 17 3 0\r\n1236 pub_chair3 13 17 2 4\r\n1237 pub_chair3 14 17 2 4\r\n1238 pub_chair3 15 17 2 4\r\n1239 pub_chair3 16 17 2 4\r\n1240 pub_fence 18 17 2 0\r\n1241 pub_sofa 1 18 3 2\r\n1242 pub_fence 5 18 3 0\r\n1243 pub_table2 13 18 2 5\r\n1244 pub_table2 14 18 2 6\r\n1245 pub_table2 15 18 2 6\r\n1246 pub_table2 16 18 2 4\r\n1247 pub_fence 18 18 2 0\r\n1248 pub_sofa 2 19 3 0\r\n1249 pub_sofa2 3 19 3 0\r\n1250 pub_fence 5 19 3 0\r\n1251 pub_chair3 13 19 2 0\r\n1252 pub_chair3 14 19 2 0\r\n1253 pub_chair3 15 19 2 0\r\n1254 pub_chair3 16 19 2 0\r\n1255 pub_fence 18 19 2 0\r\n1256 pub_fence 1 20 3 5\r\n1257 pub_fence 2 20 3 6\r\n1258 pub_fence 3 20 3 6\r\n1259 pub_fence 4 20 3 6\r\n1260 pub_fence 5 20 3 3\r\n1261 pub_fence 18 20 2 0\r\n1262 pub_sofa2 7 21 1 4\r\n1263 pub_sofa 8 21 1 4\r\n1264 pub_fence 12 21 2 5\r\n1265 pub_fence 13 21 2 6\r\n1266 pub_fence 14 21 2 6\r\n1267 pub_fence 15 21 2 6\r\n1268 pub_fence 16 21 2 6\r\n1269 pub_fence 17 21 2 6\r\n1270 pub_fence 18 21 2 3\r\n1271 pub_sofa2 6 22 1 2\r\n1272 pub_table 15 22 0 0\r\n1273 pub_chair2 16 22 0 4\r\n1274 pub_chair2 17 22 0 4\r\n1275 pub_sofa 6 23 1 2\r\n1276 pub_table2 8 23 1 1\r\n1277 pub_sofa 6 24 1 2\r\n1278 pub_table2 8 24 1 0\r\n1279 pub_sofa 6 25 1 2\r\n1280 pub_table2 8 25 1 2\r\n', '0'),
('pool_a', 2, 25, 7, 2, '0000x0000000 0000xx000000 000000000000 00000000000x 000000000000 00x0000x0000', '401 poolBooth 17 11 0 0\r\n402 poolBooth 17 9 0 0\r\n403 poolEnter 20 28 0 0\r\n404 poolExit 21 28 0 0\r\n666 flower2b 2 28 7 0\r\n667 flower2a 2 29 7 0\r\n668 flower2b 19 30 7 0\r\n669 flower2a 19 31 7 0\r\n670 box 6 32 7 0\r\n671 flower1 13 33 7 0\r\n672 pool_chairy 10 34 7 4\r\n673 umbrellay 8 35 7 0\r\n674 pool_chairy 9 35 7 2\r\n675 pool_tabley 10 35 7 0\r\n676 pool_chairy 11 35 7 6\r\n677 umbrellap 15 35 7 0\r\n678 pool_chairy 10 36 7 0\r\n679 pool_chairp 15 36 7 4\r\n680 pool_chairo 22 36 7 4\r\n681 pool_chairp 14 37 7 2\r\n682 pool_tablep 15 37 7 0\r\n683 pool_chairp 16 37 7 6\r\n684 umbrellao 20 37 7 0\r\n685 pool_chairo 21 37 7 2\r\n686 pool_tabo 22 37 7 0\r\n687 pool_chairo 22 38 7 0\r\n688 pool_chairg 18 42 7 4\r\n689 umbrellag 16 43 7 0\r\n690 pool_chairg 17 43 7 2\r\n691 pool_tablg 18 43 7 0\r\n692 pool_chairg 19 43 7 6\r\n693 pool_chairg 18 44 7 0\r\n', '0'),
('lobby_a', 12, 27, 0, 2, 'XXXXXXXXX77777777777XXXXX XXXXXXXXX777777777777XXXX XXXXXXXXX777777777766XXXX XXXXXXXXX777777777755XXXX XX333333333333333334433XX XX333333333333333333333XX XX333333333333333333333XX 33333333333333333333333XX 333333XXXXXXX3333333333XX 333333XXXXXXX2222222222XX 333333XXXXXXX2222222222XX XX3333XXXXXXX2222222222XX XX3333XXXXXXX222222221111 XX3333XXXXXXX111111111111 333333XXXXXXX111111111111 3333333222211111111111111 3333333222211111111111111 3333333222211111111111111 XX33333222211111111111111 XX33333222211111111111111 XX3333322221111111XXXXXXX XXXXXXX22221111111XXXXXXX XXXXXXX22221111111XXXXXXX XXXXXXX22221111111XXXXXXX XXXXXXX22221111111XXXXXXX XXXXXXX222X1111111XXXXXXX XXXXXXX222X1111111XXXXXXX XXXXXXXXXXXX11XXXXXXXXXXX XXXXXXXXXXXX11XXXXXXXXXXX XXXXXXXXXXXX11XXXXXXXXXXX XXXXXXXXXXXX11XXXXXXXXXXX', 'f90 flower1 9 0 7 0\r\nS110 chairf2b 11 0 7 4\r\ns120 chairf2 12 0 7 4\r\nt130 table1 13 0 7 2\r\nS140 chairf2b 14 0 7 4\r\ns150 chairf2 15 0 7 4\r\nw160 watermatic 16 0 7 4\r\nT92 telkka 9 2 7 2\r\nf93 flower1 9 3 7 0\r\nZ113 chairf2d 11 3 7 0\r\ns123 chairf2 12 3 7 0\r\nt133 table1 13 3 7 2\r\nZ143 chairf2d 14 3 7 0\r\ns153 chairf2 15 3 7 0\r\nf124 flower1 12 4 3 0\r\nf164 flower1 16 4 3 0\r\nS07 chairf2b 0 7 3 4\r\ns17 chairf2 1 7 3 4\r\nZ010 chairf2d 0 10 3 0\r\ns110 chairf2 1 10 3 0\r\nr2112 roommatic 21 12 1 4\r\nr2212 roommatic 22 12 1 4\r\nr2312 roommatic 23 12 1 4\r\nr2412 roommatic 24 12 1 4\r\nS014 chairf2b 0 14 3 4\r\ns114 chairf2 1 14 3 4\r\nw1314 watermatic 13 14 1 2\r\nw1215 watermatic 12 15 1 4\r\nc1916 chairf1 19 16 1 4\r\nC2116 table2c 21 16 1 2\r\nc2316 chairf1 23 16 1 4\r\nZ017 chairf2d 0 17 3 0\r\ns117 chairf2 1 17 3 0\r\nD2117 table2b 21 17 1 2\r\nc1918 chairf1 19 18 1 0\r\nd2118 table2 21 18 1 2\r\nc2318 chairf1 23 18 1 0\r\nS721 chairf2b 7 21 2 2\r\nz722 chairf2c 7 22 2 2\r\nz723 chairf2c 7 23 2 2\r\nz724 chairf2c 7 24 2 2\r\ns725 chairf2 7 25 2 2\r\nt726 table1 7 26 2 2\r\ne1026 flower2 10 26 1 2\r\n', '0'),
('floorlobby_c', 9, 21, 0, 2, 'XXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXX XXX0000000000000000XXXXXXX0 XXX000000000000000000XXXX00 X00000000000000000000000000 X00000000000000000000000000 XXX000000000000000000000000 XXXXXXX00000000000000000000 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX1XX100000011111111111111 XXX1XX100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXX111100000011111111111111 XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX XXXXXXXX0000XXXXXXXXXXXXXXX', '858 moneyplant 3 2 0 0\r\n859 roommatic 5 2 0 4\r\n860 roommatic 7 2 0 4\r\n861 roommatic 9 2 0 4\r\n862 roommatic 11 2 0 4\r\n863 roommatic 13 2 0 4\r\n864 watermatic 26 2 0 4\r\n865 edgec1 14 8 1 6\r\n866 edgec1 15 8 1 0\r\n867 edgec1 16 8 1 0\r\n868 edgec1 17 8 1 0\r\n869 edgec1 18 8 1 0\r\n870 edgec1 19 8 1 0\r\n871 edgec1 20 8 1 0\r\n872 edgec1 21 8 1 0\r\n873 edgec1 22 8 1 0\r\n874 edgec1 23 8 1 0\r\n875 edgec1 24 8 1 0\r\n876 edgec1 25 8 1 0\r\n877 edgec1 26 8 1 2\r\n878 chairg1 3 9 1 2\r\n879 chairg1 5 9 1 6\r\n880 moneyplant 15 9 1 0\r\n881 chairg2b 16 9 1 4\r\n882 chairg2c 17 9 1 4\r\n883 chairg2 18 9 1 4\r\n884 moneyplant 19 9 1 4\r\n885 moneyplant 21 9 1 0\r\n886 chairg2b 22 9 1 4\r\n887 chairg2c 23 9 1 4\r\n888 chairg2 24 9 1 4\r\n889 moneyplant 25 9 1 4\r\n890 chairg1 3 11 1 2\r\n891 chairg1 5 11 1 6\r\n892 newtable2 3 12 1 0\r\n893 chairg1 16 14 1 4\r\n894 chairg1 18 14 1 4\r\n895 chairg1 22 14 1 4\r\n896 chairg1 24 14 1 4\r\n897 newtable2 17 15 1 0\r\n898 newtable2 23 15 1 0\r\n899 newtable2 3 16 1 0\r\n900 chairg1 16 16 1 0\r\n901 chairg1 18 16 1 0\r\n902 chairg1 22 16 1 0\r\n903 chairg1 24 16 1 0\r\n904 chairg1 3 17 1 2\r\n905 chairg1 5 17 1 6\r\n906 edgec2 14 18 1 6\r\n907 edgec2 15 18 1 0\r\n908 edgec2 16 18 1 0\r\n909 edgec2 17 18 1 0\r\n910 edgec2 18 18 1 0\r\n911 edgec2 19 18 1 0\r\n912 edgec2 20 18 1 0\r\n913 edgec2 21 18 1 0\r\n914 edgec2 22 18 1 0\r\n915 edgec2 23 18 1 0\r\n916 edgec2 24 18 1 0\r\n917 edgec2 25 18 1 0\r\n918 edgec2 26 18 1 2\r\n', ''),
('habburger', 22, 10, 0, 2, '22222222222222222222xxx 22222222222222222222xxx 22222222222222222222xxx 22222222222222222222xxx xxxxxxxxxxxxxxxx1111xxx xxxxxxxxxxxxxxxx0000xxx xxx00000000000000000xxx 00000000000000000000xxx 00000000000000000000xxx 00000000000000000000000 00000000000000000000000 00000000000000000000xxx 00000000000000000000xxx 00000000000000000000xxx xxx00000000000000000xxx', '616 sofa 0 0 2 2\r\n617 table 1 0 2 4\r\n618 sofa 2 0 2 6\r\n619 sofa 6 0 2 2\r\n620 tablesp 7 0 2 4\r\n621 sofa 8 0 2 6\r\n622 sofa 12 0 2 2\r\n623 table 13 0 2 4\r\n624 sofa 14 0 2 6\r\n625 trashcan 15 0 2 0\r\n626 sofa2 0 1 2 2\r\n627 table2 1 1 2 4\r\n628 sofa2 2 1 2 6\r\n629 sofa2 6 1 2 2\r\n630 table2 7 1 2 4\r\n631 sofa2 8 1 2 6\r\n632 sofa2 12 1 2 2\r\n633 table2 13 1 2 4\r\n634 sofa2 14 1 2 6\r\n635 bardesk3 2 7 0 0\r\n636 sofa 7 7 0 4\r\n637 sofa2 8 7 0 4\r\n638 sofa 12 7 0 4\r\n639 sofa2 13 7 0 4\r\n640 bardesk4 2 8 0 0\r\n641 tablesp 7 8 0 2\r\n642 table2 8 8 0 2\r\n643 table 12 8 0 2\r\n644 table2 13 8 0 2\r\n645 bardesk 2 9 0 0\r\n646 sofa 7 9 0 0\r\n647 sofa2 8 9 0 0\r\n648 sofa 12 9 0 0\r\n649 sofa2 13 9 0 0\r\n650 bardesk2 2 10 0 0\r\n651 bardesk 2 11 0 0\r\n652 bardesk 2 12 0 0\r\n653 sofa 7 12 0 4\r\n654 sofa2 8 12 0 4\r\n655 sofa 12 12 0 4\r\n656 sofa2 13 12 0 4\r\n657 bardesk 2 13 0 0\r\n658 table 7 13 0 2\r\n659 table2 8 13 0 2\r\n660 tablesp 12 13 0 2\r\n661 table2 13 13 0 2\r\n662 sofa 7 14 0 0\r\n663 sofa2 8 14 0 0\r\n664 sofa 12 14 0 0\r\n665 sofa2 13 14 0 0\r\n', '0'),
('bar_b', 2, 12, 4, 2, 'xxxxx4xxxxxxxxxxxx xxxx4444444xxxxxxx xxxx4444444xxxxxxx xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 654444444444444444 654444444444444444 654444444444444444 654444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxx444444444444444 xxxx44444444444444 xxxx33444444444444 xxxx22444444444444 xxxx2222222222xx44 xxxx2222222222xx44 xxxxx222222222xxxx xxxxxx22222222xxxx xxxxxx22222222xxxx xxxxxx22222222xxxx xxxxxx22222222xxxx xxxxxx22222222xxxx xxxxxx22222222xxxx', '1185 stair 5 21 2 0\r\n1184 stair 4 21 2 0\r\n1183 stair 5 20 3 0\r\n1182 stair 4 20 3 0\r\n1181 lounge_chair_small 14 19 4 6\r\n1180 lounge_table_one 13 19 4 0\r\n1179 lounge_chair_small 12 19 4 2\r\n1178 stair 5 19 4 0\r\n1177 stair 4 19 4 0\r\n1176 lounge_chair_small 8 18 4 0\r\n1175 lounge_table_one 8 17 4 0\r\n1174 lounge_chair_small 15 16 4 0\r\n1173 lounge_chair_small 8 16 4 4\r\n1172 lounge_table_one 15 15 4 0\r\n1171 lounge_chair_small 14 15 4 2\r\n1170 lounge_chair_small 15 14 4 4\r\n1169 lounge_chair_small 10 13 4 0\r\n1168 lounge_chair_small 11 12 4 6\r\n1167 lounge_table_one 10 12 4 0\r\n1166 lounge_chair_small 9 12 4 2\r\n1165 lounge_chair_small 10 11 4 4\r\n1164 lounge_chair_small 15 10 4 0\r\n1163 lounge_table_one 15 9 4 0\r\n1162 lounge_chair_small 14 9 4 2\r\n1161 fatsblox 3 9 4 6\r\n1160 lounge_chair_small 15 8 4 4\r\n1159 fatsofaa 3 8 4 2\r\n1158 lounge_chair_small 9 7 4 0\r\n1157 fatsofaa 3 7 4 2\r\n1156 lounge_chair_small 10 6 4 6\r\n1155 lounge_table_one 9 6 4 0\r\n1154 lounge_chair_small 8 6 4 2\r\n1153 fatsofaa 3 6 4 2\r\n1152 fatsofaa 3 5 4 2\r\n1151 fatsblox 3 4 4 2\r\n1150 fatsblox 17 3 4 0\r\n1149 fatsofaa 16 3 4 4\r\n1148 fatsofaa 15 3 4 4\r\n1147 fatsofaa 14 3 4 4\r\n1146 fatsofaa 13 3 4 4\r\n1145 fatsblox 12 3 4 4\r\n1144 pub_pineapple_small 3 3 4 0\r\n1143 stair 11 2 100000 2\r\n1142 stair 10 2 4 2\r\n1141 stair 9 2 4 2\r\n1140 stair 8 2 4 2\r\n1139 stair 7 2 4 2\r\n1138 stair 6 2 4 2\r\n1137 stair 5 2 4 2\r\n1136 stair 4 2 4 2\r\n', '0');

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
