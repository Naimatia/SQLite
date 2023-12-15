package com.example.sqlite

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sqlite.R

class MainActivity : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EtudiantAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var etudiants: MutableList<EtudiantBC> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = EtudiantDBHelper(applicationContext)
        val db = dbHelper.readableDatabase
        val etudiantsList = getAllEtudiants(db)

        recyclerView = findViewById(R.id.recyclerViewEtudiants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EtudiantAdapter(etudiantsList)
        recyclerView.adapter = adapter

        // Initialize swipeRefreshLayout before using it
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)


        val btnAjouter: TextView = findViewById(R.id.btnAjouter)
        btnAjouter.setOnClickListener {
            val intent = Intent(this, InscriptionActivity::class.java)
            startActivity(intent)
        }
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data here (e.g., fetch new data from the server)
            // After refreshing, call the updateEtudiantList function
            // to update the RecyclerView
            updateEtudiantList(getAllEtudiants(db))
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getAllEtudiants(db: SQLiteDatabase): List<EtudiantBC> {
        val etudiantsList = mutableListOf<EtudiantBC>()

        val projection = arrayOf(
            EtudiantBC.EtudiantEntry.COLUMN_NAME_NOM,
            EtudiantBC.EtudiantEntry.COLUMN_NAME_PRENOM,
            EtudiantBC.EtudiantEntry.COLUMN_NAME_PHONE,
            EtudiantBC.EtudiantEntry.COLUMN_NAME_GENDER,
            EtudiantBC.EtudiantEntry.COLUMN_NAME_EMAIL,
            EtudiantBC.EtudiantEntry.COLUMN_NAME_MDP
        )

        val cursor = db.query(
            EtudiantBC.EtudiantEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val nom = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_NOM))
                val prenom = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_PRENOM))
                val phone = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_PHONE))
                val gender = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_GENDER))
                val email = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_EMAIL))
                val mdp = getString(getColumnIndexOrThrow(EtudiantBC.EtudiantEntry.COLUMN_NAME_MDP))
                val etudiant = EtudiantBC(nom, prenom, phone, gender, email, mdp)
                etudiantsList.add(etudiant)
            }
        }

        cursor.close()
        return etudiantsList
    }


    fun updateEtudiantList(newEtudiants: List<EtudiantBC>) {
        etudiants.clear()
        etudiants.addAll(newEtudiants)
        adapter.notifyDataSetChanged()
    }


}
