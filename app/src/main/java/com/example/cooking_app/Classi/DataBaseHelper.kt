package com.example.cooking_app.Classi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast

val DB_NAME = "CookingApp.db"
val DB_VERSION = 9
val TABLENAME1 = "ricette"
val COL_IMM = "immagine"
val COL_NOME = "nome"
val COL_DIFF = "difficoltà"
val COL_TEMPO = "tempo"
val COL_TIPO = "tipologia"
val COL_PORT = "portata"
val COL_PERS = "persone"
val COL_DESC = "descrizione"
val TABLENAME2 = "ingredienti"
val COL_NOME_ING = "nomeIng"
val COL_QUANT = "quantità"
val COL_MIS = "misura"
val TABLENAME3 = "note"
val COL_NOTE = "note"

val createTable1 =
        "CREATE TABLE $TABLENAME1 (" +
        "$COL_IMM BLOB, " +
        "$COL_NOME VARCHAR(256), " +
        "$COL_DIFF VARCHAR(256), " +
        "$COL_TEMPO VARCHAR(256), " +
        "$COL_TIPO VARCHAR(256), " +
        "$COL_PORT VARCHAR(256), " +
        "$COL_PERS INTEGER, " +
        "$COL_DESC VARCHAR(1024), " +
        " PRIMARY KEY ($COL_IMM, $COL_NOME))"

val createTable2 =
        "CREATE TABLE $TABLENAME2 (" +
        "$COL_IMM BLOB, " +
        "$COL_NOME VARCHAR(256)," +
        "$COL_NOME_ING VARCHAR(256), " +
        "$COL_QUANT VARCHAR(256), " +
        "$COL_MIS VARCHAR(256), " +
        "PRIMARY KEY ($COL_IMM, $COL_NOME_ING), " +
        "FOREIGN KEY ($COL_IMM, $COL_NOME) REFERENCES $TABLENAME1)"

//classe che gestisce il DB: creazione, aggiornamento e lettura dati

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    //funzinoe che crea il DB
    override fun onCreate(db: SQLiteDatabase?) {

        onUpgrade(db, DB_VERSION, 10)      //versione 11 da usare in caso di aggiornamento

        db?.execSQL(createTable1)

        db?.execSQL(createTable2)

        /*val createTable3 = "CREATE TABLE" + TABLENAME3 + "(" +
                COL_IMM + " BLOB REFERENCES $TABLENAME1($COL_IMM), " +
                COL_NOTE + "VARCHAR(256) PRIMARY KEY)"

        db?.execSQL(createTable3)*/
    }

    //funzione che serve per aggiornare il DB
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 20) {
            db?.execSQL(createTable1)
            db?.execSQL(createTable2)
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
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
        val queryNumTotale = "SELECT COUNT(*) FROM $TABLENAME1"
        val numero = db.rawQuery(queryNumTotale, null)
        numero.moveToFirst()
        val count = numero.getInt(0)
        Log.v("numero elementi", count.toString())
        numero.close()
        /*val cicli : Int
        if (count%2 == 0)
            cicli = count/2
        else
            cicli = count/2 + 1
        */
        for (i in 0 until count) {
            Log.v("valore offset iniziale", OFFSET.toString())
            val queryRicette = "SELECT * FROM $TABLENAME1 ORDER BY $COL_NOME LIMIT 1 OFFSET $OFFSET"
            OFFSET += 1
            Log.v("valore offset finale", OFFSET.toString())
            val cursoreRicetta = db.rawQuery(queryRicette, null)   //crea un cursore che permette di 'navigare' nel risultato della query precedente
            if (cursoreRicetta.moveToFirst()) {
                do {
                    val ricetta = Ricetta()

                    val array = cursoreRicetta.getBlob(cursoreRicetta.getColumnIndex(COL_IMM))
                    val bit: Bitmap = BitmapFactory.decodeByteArray(array, 0, array.size)
                    ricetta.bit = bit
                    ricetta.nome = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_NOME))
                    ricetta.diff = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DIFF))
                    ricetta.tempo = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TEMPO))
                    ricetta.tipologia = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_TIPO))
                    ricetta.portata = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_PORT))
                    ricetta.persone = cursoreRicetta.getInt(cursoreRicetta.getColumnIndex(COL_PERS))
                    ricetta.note = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_DESC))

                    val chiaveEsterna = cursoreRicetta.getString(cursoreRicetta.getColumnIndex(COL_IMM))

                    val queryIngredienti = "SELECT * FROM $TABLENAME2 WHERE $COL_IMM = $chiaveEsterna"
                    val cursoreIngredienti = db.rawQuery(queryIngredienti, null)
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
                    listaRic.add(ricetta)

                } while (cursoreRicetta.moveToNext())
            }
            cursoreRicetta.close()
        }
        db.close()
        return listaRic
    }

    //funzione temporanea per popolare il DB
    fun insertData(r: Ricetta) {
        val db = this.readableDatabase
        val valoriRicetta = ContentValues()
        valoriRicetta.put(COL_IMM, r.immagine)
        valoriRicetta.put(COL_NOME, r.nome)
        valoriRicetta.put(COL_DIFF, r.diff)
        valoriRicetta.put(COL_TEMPO, r.tempo)
        valoriRicetta.put(COL_TIPO, r.tipologia)
        valoriRicetta.put(COL_PORT, r.portata)
        valoriRicetta.put(COL_PERS, r.persone)
        valoriRicetta.put(COL_DESC, r.note)
        db.insert(TABLENAME1, null, valoriRicetta)
        r.listaIngredienti.forEach {
            val valoriIngredienti = ContentValues()
            valoriIngredienti.put(COL_IMM, r.immagine)
            valoriIngredienti.put(COL_NOME_ING, it.Name)
            valoriIngredienti.put(COL_QUANT, it.quantit)
            valoriIngredienti.put(COL_MIS, it.misura)
            db.insert(TABLENAME2, null, valoriIngredienti)
        }
        db.close()
    }

    fun inserisciDati(name: String, cv: ContentValues) {
        val db = this.writableDatabase
        db.insert(name, null, cv);
    }
}