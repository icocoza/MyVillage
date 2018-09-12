package com.ccz.myvillage.repository.db;

import android.provider.BaseColumns;

public final class Databases {

    public static final class BoardDB implements BaseColumns {
        public static final String TABLENAME = "board";
        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXIST "+TABLENAME+"(id integer primary key autoincrement, "
                        +"search text not null, "
                        +"created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                        +"user text not null, INDEX idx_search(search) );";
    }

}
