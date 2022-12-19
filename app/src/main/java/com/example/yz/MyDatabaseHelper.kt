package com.example.yz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast


class MyDatabaseHelper(private val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    private val createyz = "create table yz (" +
            " id integer primary key autoincrement," +
            "inputjcz text," +
            "jczitem text," +
            "jczseq text," +
            "iteming text," +
            "optitemdisstat text," +
            "optseqdisstat text," +
            "resultdis text," +
            "count text," +
            "inputjzz text," +
            "jzzitem text," +
            "jzzseq text," +
            "[indate] TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))"
//            [CreatedTime] TimeStamp NOT NULL DEFAULT (datetime('now','localtime')
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createyz)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}