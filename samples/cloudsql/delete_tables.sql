-- 外部キー制約を無効化
SET FOREIGN_KEY_CHECKS = 0;

-- テーブルを削除
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- 外部キー制約を有効化
SET FOREIGN_KEY_CHECKS = 1;

-- インデックスを削除する（通常、DROP TABLEでインデックスも削除されます）
-- ※ここは特定のインデックスを削除する場合に追加します。
-- DROP INDEX index_name ON table_name;


-- インデックスを個別に削除する必要がある場合、以下のスクリプトを使用してDDLを生成します。
-- SELECT CONCAT('DROP INDEX `', index_name, '` ON `', table_name, '`;')
-- FROM information_schema.statistics
-- WHERE table_schema = 'your_database_name'
--   AND index_name != 'PRIMARY';
