package com.example.cooking_app.Classi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DB_NAME = "CookingApp.db"
val DB_VERSION = 1
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
val COL_NOME_ING = "nome"
val COL_QUANT = "quantità"
val COL_MIS = "misura"
val TABLENAME3 = "note"
val COL_NOTE = "note"

//classe che gestisce il DB: creazione, aggiornamento e lettura dati

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    //funzinoe che crea il DB
    override fun onCreate(db: SQLiteDatabase?) {

        val createTable1 = "CREATE TABLE " + TABLENAME1 + " (" +
                COL_IMM + " VARCHAR(256) PRIMARY KEY, " +
                COL_NOME + " VARCHAR(256), " +
                COL_DIFF + " VARCHAR(256), " +
                COL_TEMPO + " VARCHAR(256), " +
                COL_TIPO + " VARCHAR(256), " +
                COL_PORT + " VARCHAR(256), " +
                COL_PERS + " INTEGER, " +
                COL_DESC + " VARCHAR(1024))"

        db?.execSQL(createTable1)

        val createTable2 = "CREATE TABLE " + TABLENAME2 + " (" +
                COL_IMM + " VARCHAR(256) REFERENCES $TABLENAME1($COL_IMM), " +
                COL_NOME_ING + " VARCHAR(256), " +
                COL_QUANT + " VARCHAR(256), " +
                COL_MIS + " VARCHAR(256), " +
                " PRIMARY KEY " + "(" + COL_IMM + ", " + COL_NOME_ING + "))"

        db?.execSQL(createTable2)

        /*val createTable3 = "CREATE TABLE" + TABLENAME3 + "(" +
                COL_IMM + "VARCHAR(256) FOREIGN KEY" +
                COL_NOTE + "VARCHAR(256) PRIMARY KEY)"

        db?.execSQL(createTable3)*/
    }

    //funzione che serve per aggiornare il DB
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //eventuali modifiche
    }

    /*funzione per la lettura: per ogni ricetta trovata dalla query si usa un ciclo per prendere tutti
      i suoi elementi e per recuperare i suoi ingredienti associati si usa un nuovo ciclo. Si ritorna,
      infine, la lista di ricette creata
    */
    fun readData(): ArrayList<Ricetta> {

        val listaRic = ArrayList<Ricetta>()
        val listaIng = ArrayList<Ingredienti>()
        val db = this.readableDatabase  //uso readable e non writeable perché in questa funzione devo leggere da db, non mi serve scrivere
        val query = "SELECT * FROM $TABLENAME1"
        val risultato = db.rawQuery(query,null)   //crea un cursore che permette di 'navigare' nel risultato della query precedente
        if (risultato.moveToFirst()) {
            do {
                val ricetta = Ricetta()

                ricetta.immagine = risultato.getString(risultato.getColumnIndex(COL_IMM))
                ricetta.nome = risultato.getString(risultato.getColumnIndex(COL_NOME))
                ricetta.diff = risultato.getString(risultato.getColumnIndex(COL_DIFF))
                ricetta.tempo = risultato.getString(risultato.getColumnIndex(COL_TEMPO))
                ricetta.tipologia = risultato.getString(risultato.getColumnIndex(COL_TIPO))
                ricetta.portata = risultato.getString(risultato.getColumnIndex(COL_PORT))
                ricetta.persone = risultato.getInt(risultato.getColumnIndex(COL_PERS))
                ricetta.note = risultato.getString(risultato.getColumnIndex(COL_DESC))

                val chiaveEsterna = risultato.getString(risultato.getColumnIndex(COL_IMM))

                val query2 = "SELECT * FROM $TABLENAME2 WHERE $COL_IMM = $chiaveEsterna"
                val risultato2 = db.rawQuery(query2, null)
                if (risultato2.moveToFirst()) {
                    do {
                        val ingrediente = Ingredienti()

                        ingrediente.Name = risultato2.getString(risultato2.getColumnIndex(COL_NOME_ING))
                        ingrediente.misura = risultato2.getString(risultato2.getColumnIndex(COL_MIS))
                        ingrediente.quantit = risultato2.getString(risultato2.getColumnIndex(COL_QUANT))
                        listaIng.add(ingrediente)
                    } while (risultato2.moveToNext())
                }

                ricetta.listaIngredienti = listaIng
                listaRic.add(ricetta)

            } while (risultato.moveToNext())
        }

        return listaRic
    }

    //funzione temporanea per popolare il DB
    fun insertData(r : Ricetta){
        val db = this.readableDatabase
        val contentValue = ContentValues()
        contentValue.put(COL_IMM, r.immagine)
        contentValue.put(COL_NOME, r.nome)
        contentValue.put(COL_DIFF, r.diff)
        contentValue.put(COL_TEMPO, r.tempo)
        contentValue.put(COL_TIPO, r.tipologia)
        contentValue.put(COL_PORT, r.portata)
        contentValue.put(COL_PERS, r.persone)
        contentValue.put(COL_DESC, r.note)
        val result = db.insert(TABLENAME1, null, contentValue)
    }
}