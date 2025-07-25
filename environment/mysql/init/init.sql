-- Bật enforce GTID consistency
SET GLOBAL enforce_gtid_consistency = ON;

-- Bật GTID theo từng bước
SET GLOBAL gtid_mode = OFF_PERMISSIVE;
SET GLOBAL gtid_mode = ON_PERMISSIVE;
SET GLOBAL gtid_mode = ON;

-- Kiểm tra lại cấu hình
SHOW VARIABLES LIKE 'gtid_mode';
SHOW VARIABLES LIKE 'enforce_gtid_consistency';
