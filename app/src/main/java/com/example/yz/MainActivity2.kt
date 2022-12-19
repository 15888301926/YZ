package com.example.yz

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.cursoradapter.widget.SimpleCursorAdapter
import kotlinx.android.synthetic.main.activity_main2.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity2 : AppCompatActivity() {

//    lateinit var indate1 : String
//    lateinit var indate2 : String
    lateinit var inputtext : String

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        btn2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        save.setOnClickListener {
            var inputText = inputtext.toString()
            save(inputText)
        }
        
        val dbHelper = MyDatabaseHelper(this, "fx.db", 1)
        val db = dbHelper.writableDatabase
        query.setOnClickListener {
            val sql =
                "select id,inputjcz,iteming,jczseq,resultdis,count FROM yz order by id DESC  limit 10 offset 0"
            //val sql = "select * FROM yz order by id limit 10 offset  (SELECT COUNT(*) - 10 FROM yz)"
            //得到一个Cursor，这个将要放入适配器中

            val stdate1 = date_textT.text.toString()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val stdate2 = sdf.parse(stdate1)
            val stdate3 = sdf.format(stdate2).toString()

            val endate1 = time_textV.text.toString()
            val sdf2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val endate2 = sdf.parse(endate1)
            val endate3 = sdf.format(endate2)

            val cursor = db.rawQuery(
                "select id as _id,inputjcz,iteming,jczseq,resultdis,count FROM yz WHERE ((indate > ? and indate < ?) or indate = ?) or indate = ? order by id DESC", arrayOf(stdate3,endate3,stdate3,endate3))
            inputtext = cursor.toString()

            if (cursor.moveToFirst()){
                do {
                    val id = cursor.getString(cursor.getColumnIndex("_id"))
                    val inputjcz = cursor.getInt(cursor.getColumnIndex("inputjcz"))
                    val iteming = cursor.getString(cursor.getColumnIndex("iteming"))
                    val jczseq = cursor.getString(cursor.getColumnIndex("jczseq"))
                    val resultdis = cursor.getString(cursor.getColumnIndex("resultdis"))
                    val count = cursor.getString(cursor.getColumnIndex("count"))
                } while (cursor.moveToNext())
            }

//            val workBook = XSSFWorkbook()
//            val mSheet = mWorkbook.createSheet()
//            val row = mSheet.createRow(0)
//            val cell = row.createCell(0)
//            cell.setCellValue("test")
//            val fileOutputStream = FileOutputStream("test.xlsx")
//            mWorkbook.write(fileOutputStream)
//            fileOutputStream.close()


            val adapter = SimpleCursorAdapter(
                this,
                R.layout.layout_item,
                cursor,
                arrayOf("_id", "inputjcz", "iteming", "jczseq", "resultdis", "count"),
                intArrayOf(
                    R.id.tv_id,
                    R.id.tv_jcz,
                    R.id.tv_item,
                    R.id.tv_seq,
                    R.id.tv_result,
                    R.id.tv_count
                ),
                0
            )
            val listView2: ListView = findViewById<View>(R.id.listView2) as ListView
            listView2.adapter = adapter
        }

    }

    private fun save(inputText: String) {
        try {
            val output = openFileOutput("data", MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.write(inputText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buttonFunc(view: View) {
        when (view.id) {
            R.id.date_textT -> {
                // 日期选择器
                val ca = Calendar.getInstance()
                var mYear = ca[Calendar.YEAR]
                var mMonth = ca[Calendar.MONTH]
                var mDay = ca[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth
                        var mDate = "${year}-${month + 1}-${dayOfMonth}"
                        // 将选择的日期赋值给TextView
                        date_textT.text = mDate+" 00:00:00"
                    },
                    mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
            R.id.time_textV -> {
                // 日期选择器
                val ca = Calendar.getInstance()
                var mYear = ca[Calendar.YEAR]
                var mMonth = ca[Calendar.MONTH]
                var mDay = ca[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mYear = year
                        mMonth = month
                        mDay = dayOfMonth
                        var mTime = "${year}-${month + 1}-${dayOfMonth}"
                        time_textV.text = mTime+" 23:59:59"
//                        indate2 = time_textV.text.toString()
                    },
                    mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
        }
    }
}
