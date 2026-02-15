# 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_name` VARCHAR(255) NOT NULL,
    `hashed_password` VARCHAR(255) NOT NULL,
    `is_active` TINYINT(1) NOT NULL,
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 资产表
CREATE TABLE IF NOT EXISTS `asset` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `type` ENUM ('资金', '信用卡', '充值', '投资理财') NOT NULL,
    `balance` DECIMAL(10, 2) NOT NULL,
    `asset_name` VARCHAR(255) NOT NULL,
    `bill_date` INT NULL,
    `repay_date` INT NULL,
    `quota` DECIMAL(10, 2) NULL,
    `in_total` TINYINT(1) NOT NULL,
    `svg` VARCHAR(255) NULL,
    KEY `idx_asset_user_id` (`user_id`),
    CONSTRAINT `fk_asset_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 账本表
CREATE TABLE IF NOT EXISTS `book` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `begin_date` INT NOT NULL DEFAULT 1,
    `total_budget` DECIMAL(10, 2) NULL,
    `used_budget` DECIMAL(10, 2) NULL DEFAULT 0.00,
    KEY `idx_book_user_id` (`user_id`),
    CONSTRAINT `fk_book_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 账单分类表
CREATE TABLE IF NOT EXISTS `bill_category` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NULL,
    `bill_category_name` VARCHAR(255) NOT NULL,
    `svg` TEXT NOT NULL,
    `type` ENUM ('收入', '支出', '转账') NOT NULL,
    KEY `idx_bill_category_book_id` (`book_id`),
    CONSTRAINT `fk_bill_category_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 预算表
CREATE TABLE IF NOT EXISTS `budget` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NOT NULL,
    `bill_category_id` INT NOT NULL,
    `used` DECIMAL(10, 2) NULL DEFAULT 0.00,
    `times` INT NULL DEFAULT 0,
    `limit_amount` DECIMAL(10, 2) NULL,
    KEY `idx_budget_book_id` (`book_id`),
    KEY `idx_budget_bill_category_id` (`bill_category_id`),
    CONSTRAINT `fk_budget_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_budget_bill_category_id` FOREIGN KEY (`bill_category_id`) REFERENCES `bill_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 收入账单表
CREATE TABLE IF NOT EXISTS `income_bill` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NOT NULL,
    `income_asset_id` INT NOT NULL,
    `bill_category_id` INT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `bill_time` TIMESTAMP NULL,
    `remark` VARCHAR(255) NULL,
    `image` MEDIUMBLOB NULL,
    KEY `idx_income_bill_book_id` (`book_id`),
    KEY `idx_income_bill_income_asset_id` (`income_asset_id`),
    KEY `idx_income_bill_bill_category_id` (`bill_category_id`),
    CONSTRAINT `fk_income_bill_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_income_bill_income_asset_id` FOREIGN KEY (`income_asset_id`) REFERENCES `asset` (`id`),
    CONSTRAINT `fk_income_bill_bill_category_id` FOREIGN KEY (`bill_category_id`) REFERENCES `bill_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 支出账单表
CREATE TABLE IF NOT EXISTS `pay_bill` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NOT NULL,
    `pay_asset_id` INT NOT NULL,
    `bill_category_id` INT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `bill_time` TIMESTAMP NULL,
    `remark` VARCHAR(255) NULL,
    `refunded` TINYINT(1) NOT NULL DEFAULT 0,
    `image` MEDIUMBLOB NULL,
    KEY `idx_pay_bill_book_id` (`book_id`),
    KEY `idx_pay_bill_pay_asset_id` (`pay_asset_id`),
    KEY `idx_pay_bill_bill_category_id` (`bill_category_id`),
    CONSTRAINT `fk_pay_bill_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_pay_bill_pay_asset_id` FOREIGN KEY (`pay_asset_id`) REFERENCES `asset` (`id`),
    CONSTRAINT `fk_pay_bill_bill_category_id` FOREIGN KEY (`bill_category_id`) REFERENCES `bill_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支出账单';

# 退款账单表
CREATE TABLE IF NOT EXISTS `refund_bill` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NOT NULL,
    `pay_bill_id` INT NOT NULL,
    `refund_asset_id` INT NOT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `bill_time` TIMESTAMP NULL,
    `remark` VARCHAR(255) NULL,
    `image` MEDIUMBLOB NULL,
    KEY `idx_refund_bill_book_id` (`book_id`),
    KEY `idx_refund_bill_pay_bill_id` (`pay_bill_id`),
    KEY `idx_refund_bill_refund_asset_id` (`refund_asset_id`),
    CONSTRAINT `fk_refund_bill_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_refund_bill_pay_bill_id` FOREIGN KEY (`pay_bill_id`) REFERENCES `pay_bill` (`id`),
    CONSTRAINT `fk_refund_bill_refund_asset_id` FOREIGN KEY (`refund_asset_id`) REFERENCES `asset` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 转账账单表
CREATE TABLE IF NOT EXISTS `transfer_bill` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `book_id` INT NOT NULL,
    `in_asset_id` INT NULL,
    `out_asset_id` INT NULL,
    `amount` DECIMAL(10, 2) NOT NULL,
    `transfer_fee` DECIMAL(10, 2) NULL,
    `bill_time` TIMESTAMP NULL,
    `remark` VARCHAR(255) NULL,
    `image` MEDIUMBLOB NULL,
    KEY `idx_transfer_bill_book_id` (`book_id`),
    KEY `idx_transfer_bill_in_asset_id` (`in_asset_id`),
    KEY `idx_transfer_bill_out_asset_id` (`out_asset_id`),
    CONSTRAINT `fk_transfer_bill_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
    CONSTRAINT `fk_transfer_bill_in_asset_id` FOREIGN KEY (`in_asset_id`) REFERENCES `asset` (`id`),
    CONSTRAINT `fk_transfer_bill_out_asset_id` FOREIGN KEY (`out_asset_id`) REFERENCES `asset` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

