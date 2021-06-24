package com.example.cooking_app.Classi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

val DB_NAME = "CookingApp.db"
val DB_OLD_VERSION = 18
val DB_NEW_VERSION = 19
val TABELLA_RICETTE = "ricette"
val COL_IMM = "immagine"
val COL_NOME_IMM = "nomeImmagine"
val COL_NOME = "nome"
val COL_DIFF = "difficoltà"
val COL_TEMPO = "tempo"
val COL_TIPO = "tipologia"
val COL_PORT = "portata"
val COL_PERS = "persone"
val COL_DESC = "descrizione"
val TABELLA_ING = "ingredienti"
val COL_NOME_ING = "nomeIng"
val COL_QUANT = "quantità"
val COL_MIS = "misura"
val TABLENAME3 = "note"
val COL_NOTE = "note"

val createTableRicette =
        "CREATE TABLE $TABELLA_RICETTE (" +
        "$COL_IMM BLOB, " +
        "$COL_NOME_IMM VARCHAR(256), " +
        "$COL_NOME VARCHAR(256), " +
        "$COL_DIFF VARCHAR(256), " +
        "$COL_TEMPO VARCHAR(256), " +
        "$COL_TIPO VARCHAR(256), " +
        "$COL_PORT VARCHAR(256), " +
        "$COL_PERS INTEGER, " +
        "$COL_DESC VARCHAR(2048), " +
        " PRIMARY KEY ($COL_NOME, $COL_DESC))"

val createTableIng =
        "CREATE TABLE $TABELLA_ING (" +
        "$COL_NOME VARCHAR(256)," +
        "$COL_DESC VARCHAR(1024), " +
        "$COL_NOME_ING VARCHAR(256), " +
        "$COL_QUANT VARCHAR(256), " +
        "$COL_MIS VARCHAR(256), " +
        "PRIMARY KEY ($COL_NOME_ING, $COL_NOME, $COL_DESC), " +
        "FOREIGN KEY ($COL_NOME, $COL_DESC) REFERENCES $TABELLA_RICETTE ON DELETE CASCADE)"

//classe che gestisce il DB: creazione, aggiornamento e lettura dati

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_OLD_VERSION) {

    //funzinoe che crea il DB
    override fun onCreate(db: SQLiteDatabase?) {

        onUpgrade(db, DB_OLD_VERSION, DB_NEW_VERSION)      //versione 17 da usare in caso di aggiornamento

        //db?.execSQL(createTableRicette)

        //db?.execSQL(createTableIng)

        /*val createTable3 = "CREATE TABLE" + TABLENAME3 + "(" +
                COL_IMM + " BLOB REFERENCES $TABLENAME1($COL_IMM), " +
                COL_NOTE + "VARCHAR(256) PRIMARY KEY)"

        db?.execSQL(createTable3)*/
    }

    //funzione che serve per aggiornare il DB
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 20) {
            db?.execSQL("DROP TABLE IF EXISTS $TABELLA_ING")
            db?.execSQL(createTableIng)
            db?.execSQL("DROP TABLE IF EXISTS $TABELLA_RICETTE")
            db?.execSQL(createTableRicette)
            Log.v("creazione tabella", "riuscita")
        }

    }

    /*funzione per la lettura: per ogni ricetta trovata dalla query si usa un ciclo per prendere tutti
      i suoi elementi e per recuperare i suoi ingredienti associati si usa un nuovo ciclo. Si ritorna,
      infine, la lista di ricette creata
    */
    fun readData(): ArrayList<Ricetta> {

        val listaRic = ArrayList<Ricetta>()
        val listaIng = ArrayList<Ingredienti>()
        val db = this.readableDatabase  //uso readable e non writeable perché in questa funzione devo leggere da db, non mi serve scrivere
        var OFFSET = 0

        val queryNumTotale = "SELECT COUNT(*) FROM $TABELLA_RICETTE"
        val numero = db.rawQuery(queryNumTotale, null)
        numero.moveToFirst()
        val count = numero.getInt(0)
        Log.v("numero elementi", count.toString())
        numero.close()

        for (i in 0 until count) {
            val queryRicette = "SELECT * FROM $TABELLA_RICETTE ORDER BY $COL_NOME LIMIT 1 OFFSET $OFFSET"
            OFFSET += 1
            val cursoreRicetta = db.rawQuery(queryRicette, null)   //crea un cursore che permette di 'navigare' nel risultato della query precedente
            if (cursoreRicetta.moveToFirst()) {
                do {
                    val ricetta = Ricetta()

                    val array = cursoreRicetta.getBlob(cursoreRicetta.getColumnIndex(COL_IMM))
                    Log.v("nome array", array.toString())
                    val bit: Bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                    ricetta.bit = bit
                    ricetta.immagine = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_NOME_IMM))
                    ricetta.nome = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_NOME))
                    ricetta.diff = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DIFF))
                    ricetta.tempo = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TEMPO))
                    ricetta.tipologia = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TIPO))
                    ricetta.portata = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_PORT))
                    ricetta.persone = cursoreRicetta.getInt(cursoreRicetta.getColumnIndex(COL_PERS))
                    ricetta.note = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DESC))

                    val queryIngredienti = "SELECT $COL_NOME, $COL_NOME_ING, $COL_MIS, $COL_QUANT FROM $TABELLA_ING WHERE $COL_NOME = " + "\"" + ricetta.nome + "\"" + " AND $COL_DESC = " + "\"" + ricetta.note + "\""
                    val cursoreIngredienti = db.rawQuery(queryIngredienti, null)
                    Log.v("elementi trovati?", cursoreIngredienti.moveToFirst().toString())
                    if (cursoreIngredienti.moveToFirst()) {
                        do {
                            Log.v("sono dentro il do", "vediamo")
                            val ingrediente = Ingredienti()

                            ingrediente.Name = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_NOME_ING))
                            ingrediente.misura = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_MIS))
                            ingrediente.quantit = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_QUANT))
                            val nome = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_NOME))
                            Log.v("nome ricetta", nome)
                            Log.v("offset iniziale ing", ingrediente.toString())
                            listaIng.add(ingrediente)
                        } while (cursoreIngredienti.moveToNext())
                    }

                    ricetta.listaIngredienti = listaIng
                    listaRic.add(ricetta)

                } while (cursoreRicetta.moveToNext())
            }
            cursoreRicetta.close()
        }
        db.close()
        return listaRic
    }

    //funzione che permette di salvare i dati nel DB
    fun salvaDati(name: String, cv: ContentValues) {
        val dbW = this.writableDatabase
        dbW.insert(name, null, cv);
    }

    //funzione che permette di eliminare i dati di una ricetta nel DB
    fun eliminaRicetta(des : String, nome: String) {
        val dbW = this.writableDatabase
        val queryEliminazione = "$COL_DESC = " + "\"" + des + "\" AND $COL_NOME = " + "\"" + nome + "\""
        dbW.delete(TABELLA_RICETTE, queryEliminazione, null)
    }
    //funzione che permette di eliminare gli ingredienti nel DB
    fun eliminaIngredienti(desc: String, nome: String) {
        val dbW = this.writableDatabase
        val queryEliminazione = "$COL_DESC = " + "\"" + desc + "\" AND $COL_NOME = " + "\"" + nome + "\""
        dbW.delete(TABELLA_ING,queryEliminazione,null)
    }
    //funzione che permette di modificare i dati di una ricetta nel DB
    fun modificaRicetta(des: String, nome: String, contenuto: ContentValues) {
        val dbW = this.writableDatabase
        val queryModifica = "$COL_DESC = " + "\"" + des + "\" AND $COL_NOME = " + "\"" + nome + "\""
        dbW.update(TABELLA_RICETTE, contenuto, queryModifica, null)
    }

    fun controllaRicetta(des : String, nome: String) : Boolean{
        val dbR = this.readableDatabase
        var check = false
        val cercaRicetta = "SELECT $COL_NOME FROM $TABELLA_RICETTE WHERE $COL_NOME = " + "\"" + nome + "\" AND $COL_DESC = " + "\"" + des + "\""
        val cursoreRicetta = dbR.rawQuery(cercaRicetta, null)
        if (cursoreRicetta.moveToFirst() != false) {
            check = true
            return check
        }
        return check
    }


    fun readData(nome: String, desc: String): Ricetta {
        val dbR = this.readableDatabase
        val listaIng = ArrayList<Ingredienti>()
        val cercaRicetta = "SELECT * FROM $TABELLA_RICETTE WHERE $COL_NOME = " + "\"" + nome + "\" AND $COL_DESC = " + "\"" + desc + "\""
        val cursoreRicetta = dbR.rawQuery(cercaRicetta, null)
        cursoreRicetta.moveToFirst()
        val ricetta = Ricetta()
        val array = cursoreRicetta.getBlob(cursoreRicetta.getColumnIndex(COL_IMM))
        val bit: Bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
        ricetta.bit = bit
        ricetta.immagine = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_NOME_IMM))
        ricetta.nome = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_NOME))
        ricetta.diff = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DIFF))
        ricetta.tempo = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TEMPO))
        ricetta.tipologia = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TIPO))
        ricetta.portata = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_PORT))
        ricetta.persone = cursoreRicetta.getInt(cursoreRicetta.getColumnIndex(COL_PERS))
        ricetta.note = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DESC))

        val queryIngredienti = "SELECT $COL_NOME, $COL_NOME_ING, $COL_MIS, $COL_QUANT FROM $TABELLA_ING WHERE $COL_NOME = " + "\"" + ricetta.nome + "\"" + " AND $COL_DESC = " + "\"" + ricetta.note + "\""
        val cursoreIngredienti = dbR.rawQuery(queryIngredienti, null)
        if (cursoreIngredienti.moveToFirst()) {
            do {
                val ingrediente = Ingredienti()
                ingrediente.Name = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_NOME_ING))
                ingrediente.misura = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_MIS))
                ingrediente.quantit = cursoreIngredienti.getString(cursoreIngredienti.getColumnIndex(COL_QUANT))
                listaIng.add(ingrediente)
            } while (cursoreIngredienti.moveToNext())
        }
        ricetta.listaIngredienti = listaIng
        return ricetta
    }
}