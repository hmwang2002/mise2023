CREATE DATABASE `usercenter` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */;
use usercenter;

-- usercenter.`user` definition

-- usercenter.`user` definition

CREATE TABLE `user` (
                        `user_id` bigint NOT NULL AUTO_INCREMENT,
                        `user_name` varchar(100) NOT NULL,
                        `password` varchar(100) NOT NULL,
                        `phone` varchar(100) NOT NULL,
                        `create_time` date NOT NULL,
                        `last_login_time` date DEFAULT NULL,
                        `photo` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- usercenter.aliconfig definition

CREATE TABLE `aliconfig` (
                             `accesskey` varchar(100) DEFAULT NULL,
                             `accesssecret` varchar(100) DEFAULT NULL,
                             `id` int NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- usercenter.`user-community` definition

CREATE TABLE `user_community` (
                                  `userId` bigint NOT NULL,
                                  `community_list` text,
                                  PRIMARY KEY (`userId`),
                                  CONSTRAINT `user_community_FK` FOREIGN KEY (`userId`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;