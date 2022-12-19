package com.example.yz

import android.annotation.SuppressLint
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ListView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.cursoradapter.widget.SimpleCursorAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.Instant.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity","onCreate execute")
//        val vibrator = this.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
//        val vibrator = (vibrator).getSystemService(Service.VIBRATOR_MANAGER_SERVICE)
//        vibrator.vibrate(100)

        lateinit var jzzitem : String
        lateinit var jczitem : String
        lateinit var jzzseq : String
        lateinit var iteming : String
        lateinit var jczseq : String
        lateinit var inputjzz : String
        lateinit var inputjcz : String
        lateinit var optitemdisstat : String
        lateinit var optseqdisstat : String
        lateinit var resultdis : String
        lateinit var count : String
        lateinit var indate : String
        lateinit var counttotal : String

        editText.onFocusChangeListener = View.OnFocusChangeListener{ v, hasFocus ->
            if  (hasFocus) {
                editText.text = null
            }
        }

        editText2.onFocusChangeListener = View.OnFocusChangeListener{ v, hasFocus ->
            if  (hasFocus) {
                editText2.text = null
            }
        }

        val dbHelper = MyDatabaseHelper(this, "fx.db", 1)
        button.setOnClickListener {
            makeText(this, "You clicked Button", Toast.LENGTH_SHORT).show()
            editText.text = null
            editText.requestFocus()
            editText2.text = null
            tv_count.text = null
            tv_result.text = null
            tv_optitemdis.text = null
            tv_optseqdis.text = null
            listView.adapter = null
            }

        btn1.setOnClickListener {
            val intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }

//        val dbHelper = MyDatabaseHelper(this, "fx.db", 1)
        editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                //return@OnKeyListener true
                //makeText(this, editText.text.toString(), Toast.LENGTH_SHORT).show()
                inputjzz = editText.text.toString()
                if (inputjzz.length < 21) {
//                    val vibrator = this.getSystemService(Service.VIBRATOR_MANAGER_SERVICE)
                    vibrator.vibrate(10000)
                    alert("检测值不是21码，不符合编码要示，请重新输入！", "提示") {
                        okButton {
                            vibrator.cancel()
                            editText.requestFocus()
                        }
                    }.show()
                }
                else {
                    jzzitem = inputjzz.substring(2,12)
                    jzzseq = inputjzz.substring(12)
                    iteming = inputjzz.substring(2,9)
                }
//                makeText(this, inputjzz, Toast.LENGTH_SHORT).show()

                val db = dbHelper.writableDatabase
                //val sql = "select id,inputjcz,iteming,jczseq,resultdis,count FROM yz order by id DESC  limit 10 offset 0"
                //val sql = "select * FROM yz order by id limit 10 offset  (SELECT COUNT(*) - 10 FROM yz)"
                //得到一个Cursor，这个将要放入适配器中
                val cursor = db.rawQuery("select id as _id,inputjcz,iteming,jczseq,resultdis,count FROM yz WHERE iteming = ? order by id DESC  limit 15 offset 0", arrayOf(iteming))
                if(cursor != null && cursor.count >= 1) {
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
                    val listView: ListView = findViewById<View>(R.id.listView) as ListView
                    listView.adapter = adapter
                }
                else{
                    Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
                }

            }
            false
        })

//        checkBox.setOnClickListener {
//            if (checkBox.isChecked == false && checkBox2.isChecked == false ){
//                alert("检测项目需要选择一项！","提示"){
//                    okButton {
//                        checkBox.requestFocus()
//                    }
//                }
//            }
//        }

//        val dbHelper = MyDatabaseHelper(this, "fx.db", 1)
        editText2.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                dbHelper.writableDatabase

            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                inputjcz = editText2.text.toString()
                if (inputjcz.length < 21) {
                    vibrator.vibrate(10000)
                    alert("检测值不是21码，不符合编码要示，请重新输入！", "提示") {
                        okButton {
                            vibrator.cancel()
                            editText2.requestFocus()
                        }
                    }.show()
                }
                else {
                    jczitem = inputjcz.substring(2,12)
                    jczseq = inputjcz.substring(12)
                }
//                makeText(this, inputjcz, Toast.LENGTH_SHORT).show()
                editText2.text = null

                //--> Ematrix号版本号色号检测
                if (checkBox.isChecked){
                    if (jczitem == jzzitem){
                        tv_optitemdis.text = null
                        tv_optitemdis.textColor = Color.GREEN //green blue -0xffff01
                        tv_optitemdis.text = "通过"
                        optitemdisstat = "PASS"
                    }
                    else {
                        tv_optitemdis.text = null
                        tv_optitemdis.textColor = Color.RED //red black -0x1000000
                        tv_optitemdis.text = "未通过"
                        optitemdisstat = "NG"
                    }
                }
                else {
                    tv_optitemdis.text = null
                    optitemdisstat = ""
                }
                //<-- Ematrix号版本号色号检测

                //--> 日期模号流水号检测
                if (checkBox2.isChecked){

                    val db = dbHelper.writableDatabase
                    // 查询 yz 表中所有的数据
//                    val sql = "select COUNT(*) from yz  where jczitem = @jczitem and jczseq = @jczseq"
//                    val cursor = db.rawQuery("select COUNT(*) from yz  where inputjcz = @inputjcz",null) //'FX8001612020M62311193'
                    //val cursor = db.rawQuery("select COUNT(*) from yz  where inputjcz = ?",String[]{"FX8001612020M62311193"}) //'FX8001612020M62311193'
//                    val cursor = db.rawQuery("select COUNT(*) from yz where inputjcz = ?", arrayOf(inputjcz))
                    val pass = "PASS"
                    val cursor = db.rawQuery("select COUNT(*) from yz where jczitem = ? and jczseq = ? and resultdis = ?", arrayOf(jczitem,jczseq,pass))
                    if (cursor.moveToFirst()) {
                        do {
                            // 遍历Cursor对象，取出数据并打印
                            counttotal = cursor.getString(cursor.getColumnIndex("COUNT(*)"))
                            makeText(this, counttotal, Toast.LENGTH_SHORT).show()
                        } while (cursor.moveToNext())
                    }
                    cursor.close()

                    if (counttotal == "0"){
                        tv_optseqdis.text = null
                        tv_optseqdis.textColor = Color.GREEN //green blue -0xffff01
                        tv_optseqdis.text = "通过"
                        optseqdisstat = "PASS"
                    }
                    else {
                        tv_optseqdis.text = null
                        tv_optseqdis.textColor = Color.RED //red black -0x1000000
                        tv_optseqdis.text = "未通过"
                        optseqdisstat = "NG"
                    }
                }
                else {
                    tv_optseqdis.text = null
                    optseqdisstat = ""
                }
                //<-- 日期模号流水号检测

                //--> 检测结果
                if (optitemdisstat == "NG" || optseqdisstat == "NG"){
                    tv_result.text = null
                    tv_result.textColor = Color.RED//red black -0x1000000
                    tv_result.text = "检测失败"
                    resultdis = "NG"
                    vibrator.vibrate(10000)
                    alert("条码有问题，不可包装使用！！", "提示") {
                        okButton {
                            vibrator.cancel()
                        }
                    }.show()
                }
                else {
                    tv_result.text = null
                    tv_result.textColor = Color.GREEN //green blue -0xffff01
                    tv_result.text = "检测成功"
                    resultdis = "PASS"
                }
                //<-- 检测结果

                //--> 计数
                if (resultdis == "PASS"){
                    count = tv_count.text.toString()
                    var counttem : Int = count.toInt()
                    counttem++
                    count = counttem.toString()
                    tv_count.textColor = Color.GREEN
                    tv_count.text = counttem.toString()
                }
                else {
                    count = tv_count.text.toString()
                }
                //<-- 计数

                //--> 日期
//                val current = LocalDateTime.now()
//                val format = SimpleDateFormat("yyyyMMdd")
//                indate = format.format(current)

//                val current = LocalDateTime.now()
//                val formatter = DateTimeFormatter.BASIC_ISO_DATE
//                val formatted = current.format(formatter)
                //<-- 日期

                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    // 开始组装第一条数据
                    put("inputjcz", inputjcz)
                    put("jczitem", jczitem)
                    put("jczseq", jczseq)
                    put("iteming", iteming)
                    put("optitemdisstat", optitemdisstat)
                    put("optseqdisstat", optseqdisstat)
                    put("resultdis", resultdis)
                    put("count", count)
//                    put("indate", indate)
                    put("inputjzz", inputjzz)
                    put("jzzitem", jzzitem)
                    put("jzzseq", jzzseq)
                }
                db.insert("yz", null, values) // 插入第一条数据

                val sql = "select id,inputjcz,iteming,jczseq,resultdis,count FROM yz order by id DESC  limit 10 offset 0"
                //val sql = "select * FROM yz order by id limit 10 offset  (SELECT COUNT(*) - 10 FROM yz)"
                //得到一个Cursor，这个将要放入适配器中
                val cursor = db.rawQuery("select id as _id,inputjcz,iteming,jczseq,resultdis,count FROM yz WHERE iteming = ? order by id DESC  limit 15 offset 0", arrayOf(iteming))
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
                val listView: ListView = findViewById<View>(R.id.listView) as ListView
                listView.adapter = adapter

            }
            false
        })
        }
        }