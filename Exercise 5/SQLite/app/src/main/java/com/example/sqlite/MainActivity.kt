package com.example.sqlite

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var srollno: EditText
    private lateinit var sname: EditText
    private lateinit var scgpa: EditText
    private lateinit var insert: Button
    private lateinit var update: Button
    private lateinit var delete: Button
    private lateinit var view: Button
    private lateinit var viewAll: Button

    private lateinit var db: SQLiteDatabase
    private lateinit var c: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        srollno = findViewById(R.id.editRollno)
        sname = findViewById(R.id.editName)
        scgpa = findViewById(R.id.editCGPA)
        insert = findViewById(R.id.btnInsert)
        update = findViewById(R.id.btnUpdate)
        delete = findViewById(R.id.btnDelete)
        view = findViewById(R.id.btnView)
        viewAll = findViewById(R.id.btnViewAll)

        // Set OnClickListener for buttons
        insert.setOnClickListener(this)
        update.setOnClickListener(this)
        delete.setOnClickListener(this)
        view.setOnClickListener(this)
        viewAll.setOnClickListener(this)

        // Initialize database
        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS student(rollno VARCHAR, name VARCHAR, marks VARCHAR);")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnInsert -> {
                if (srollno.text.toString().isEmpty() || sname.text.toString().isEmpty()) {
                    showMessage("Error", "Please enter all values")
                    return
                }
                db.execSQL("INSERT INTO student VALUES('${srollno.text}', '${sname.text}', '${scgpa.text}')")
                showMessage("Success", "Record added")
                clearText()
            }
            R.id.btnUpdate -> {
                if (srollno.text.toString().isEmpty()) {
                    showMessage("Error", "Please enter roll no.")
                    return
                }
                c = db.rawQuery("SELECT * FROM student WHERE rollno='${srollno.text}'", null)
                if (c.moveToFirst()) {
                    db.execSQL("UPDATE student SET name='${sname.text}', marks='${scgpa.text}' WHERE rollno='${srollno.text}'")
                    showMessage("Success", "Record updated")
                } else {
                    showMessage("Error", "Invalid roll no.")
                }
                clearText()
                c.close()
            }
            R.id.btnDelete -> {
                if (srollno.text.toString().isEmpty()) {
                    showMessage("Error", "Please enter roll no.")
                    return
                }
                c = db.rawQuery("SELECT * FROM student WHERE rollno='${srollno.text}'", null)
                if (c.moveToFirst()) {
                    db.execSQL("DELETE FROM student WHERE rollno='${srollno.text}'")
                    showMessage("Success", "Record deleted")
                } else {
                    showMessage("Error", "Invalid roll no.")
                }
                clearText()
                c.close()
            }
            R.id.btnView -> {
                if (srollno.text.toString().isEmpty()) {
                    showMessage("Error", "Please enter roll no.")
                    return
                }
                c = db.rawQuery("SELECT * FROM student WHERE rollno='${srollno.text}'", null)
                if (c.moveToFirst()) {
                    sname.setText(c.getString(1))
                    scgpa.setText(c.getString(2))
                } else {
                    showMessage("Error", "Invalid roll no.")
                }
                c.close()
            }
            R.id.btnViewAll -> {
                c = db.rawQuery("SELECT * FROM student", null)
                if (c.count == 0) {
                    showMessage("Error", "No records found")
                    return
                }
                val buffer = StringBuffer()
                while (c.moveToNext()) {
                    buffer.append("Roll No: ${c.getString(0)}\n")
                    buffer.append("Name: ${c.getString(1)}\n")
                    buffer.append("CGPA: ${c.getString(2)}\n\n")
                }
                showMessage("Student Details", buffer.toString())
                c.close()
            }
        }
    }

    private fun showMessage(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    private fun clearText() {
        srollno.setText("")
        sname.setText("")
        scgpa.setText("")
    }
}