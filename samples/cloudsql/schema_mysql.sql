-- テーブルが存在する場合に削除
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- users テーブルを作成
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY, -- ユーザーID (主キー)
    username VARCHAR(50) NOT NULL UNIQUE, -- ユーザー名 (ユニーク制約)
    email VARCHAR(255) NOT NULL UNIQUE, -- メールアドレス (ユニーク制約)
    password_hash VARCHAR(255) NOT NULL, -- パスワード (ハッシュ化されたものを格納)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新日時
);

-- posts テーブルを作成
CREATE TABLE posts (
    id INT AUTO_INCREMENT PRIMARY KEY, -- 投稿ID (主キー)
    user_id INT NOT NULL, -- 投稿したユーザーID (外部キー)
    title VARCHAR(255) NOT NULL, -- 投稿タイトル
    content TEXT NOT NULL, -- 投稿内容
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 作成日時
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新日時
    FOREIGN KEY (user_id) REFERENCES users(id) -- 外部キー制約 (users テーブルの id を参照)
);
