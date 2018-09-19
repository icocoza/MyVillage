package com.ccz.myvillage.repository.db;

import android.provider.BaseColumns;

public final class Databases {

    public static final class BoardDB implements BaseColumns {
        public static final String TABLENAME = "board";
        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXISTS "+TABLENAME+"(id integer primary key autoincrement, "
                        +"search text not null, "
                        +"created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        +"user text);";
        public static final String CREATE_INDEX_QUERY =
                "CREATE UNIQUE INDEX IF NOT EXISTS idx_search ON "+TABLENAME+"(search)";
    }

}
