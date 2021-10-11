DROP TABLE IF EXISTS tbl_notification;
CREATE TABLE tbl_notification(
         tbl_id INTEGER PRIMARY KEY,
         title TEXT,
         content_text TEXT,
         seen_status INTEGER,
         n_time LONG,
         type_id INTEGER,
         additional TEXT
         );