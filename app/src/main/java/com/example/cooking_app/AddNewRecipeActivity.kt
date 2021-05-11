package com.example.cooking_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.Classi.Upload
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.view.*

// Activity per l'aggiunta delle ricette

class AddNewRecipeActivity : AppCompatActivity() {

    private val TAG = "AddNewRecipeActivity"

    //dati
    var ricetta: Ricetta = Ricetta()
    var lista_ingredienti = arrayListOf<Ingredienti>()
    val DBricette: DatabaseReference = FirebaseDatabase.getInstance().getReference("ricette")
    private lateinit var imageUri : Uri
    private lateinit var mUploadTask: StorageTask<*>  //evita il salvataggio multiplo delle immagini

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        setSpinner() //settaggio degli spinner per la visualizzazione delle PORTATE / DIFFICOLTà / MISURE


        //Codice per la lista degli ingredianti
        setRecyclerView()
        add_Ingrediente_to_List()

        if(intent.extras !=null){ //se esite l'intent con degli extra allora carica la ricetta scelta per la modifica
            title = "Update Ricetta "
            ButtonOK.text = "Aggiorna ricetta"
            setDatiRicetta()
            getRicettaExtra()
        }

    }

    private fun getRicettaExtra() { //ottenere la ricetta dall'intent di creazione
        ricetta.immagine = intent.getIntExtra("Immagine", 0)
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficoltà").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        ricetta.listaIngredienti = intent.getStringArrayListExtra("ListaIngredienti") as ArrayList<Ingredienti>
        ricetta.note = intent.getStringExtra("Note").toString()
    }
    private fun setDatiRicetta() { //settagio dei dati per l'intent
        IVimmagine.setImageResource(intent.getIntExtra("Immagine", 0))
        ETnome.setText(intent.getStringExtra("Nome").toString())
        when(intent.getStringExtra("Difficoltà").toString()){
            "BASSA" -> spinner_diff.setSelection(0)
            "MEDIA" -> spinner_diff.setSelection(1)
            "ALTA" -> spinner_diff.setSelection(2)
        }
        ETtempo.setText(intent.getStringExtra("Tempo").toString())
        ETtipologia.setText(intent.getStringExtra("Tipologia").toString())
        when(intent.getStringExtra("Portata").toString()){
            "Antipasto" -> spinner_portata.setSelection(0)
            "Primo" -> spinner_portata.setSelection(1)
            "Secondo" -> spinner_portata.setSelection(2)
            "Contorno" -> spinner_portata.setSelection(3)
            "Dolce" -> spinner_portata.setSelection(4)
        }
        ETpersone.setText(intent.getIntExtra("Persone", 0).toString())
        setIngredienti()
        ETnote.setText(intent.getStringExtra("Note").toString() )
    }
    private fun setIngredienti() {
        val count = intent.getIntExtra("Count", 0)
        if(count == 0) return
        for(i in 0 until count){
            val nome_ing = intent.getStringExtra("Ingrediente $i nome").toString()
            val quantità_ing = intent.getStringExtra("Ingrediente $i quantità").toString()
            val misura_ing = intent.getStringExtra("Ingrediente $i misura").toString()
            val ing = Ingredienti(nome_ing , quantità_ing , misura_ing)
            lista_ingredienti.add(ing)
        }
    }

    private fun setSpinner() {

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la difficoltà
            this,
            R.array.array_diff,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_diff.adapter = adapter
        }

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la misura
            this,
            R.array.array_misure,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            ing_misura.adapter = adapter
        }

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la portata
            this,
            R.array.array_portata,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_portata.adapter = adapter
        }
    }

    private fun add_Ingrediente_to_List(){        //funzione che aggiunge gli ingredienti alla lista sottostante
        add_ing.setOnClickListener(View.OnClickListener { v ->
            val ingnome = ing_nome.text.toString()
            val ingquanti = ing_quantità.text.toString()
            val ingmisura = ing_misura.selectedItem.toString()
            if (ingnome.isEmpty() || ingquanti.isEmpty() || !ingquanti.isDigitsOnly() || ingnome.isDigitsOnly()) {
                return@OnClickListener
            }
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)

            //salvataggio degli ingredienti sul DB
            val cn = "c2" //modificare il codice univoco
            val DBricette: DatabaseReference = FirebaseDatabase.getInstance().getReference("liste_ingredienti")       //implementare l'eliminazione da DB dell'ing quando si elimina dalla lista delle ricette
            DBricette.child(cn).child(ingnome).setValue(ing)

            lista_ingredienti.add(0, ing)
            ing_nome.text.clear()
            ing_quantità.text.clear()
            ing_misura.setSelection(0)
            recyclerview.adapter?.notifyItemInserted(0)
            return@OnClickListener
        }
        )
    }

    private fun setRecyclerView() {     //settiamo la RecyclerView per la lista degli ingredienti
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = Lista_Ingredienti_Adapter(lista_ingredienti)

    }

    fun saveRecipe(v: View) {      //onClick del button che salva i dati della ricetta nel DB

        if(intent.extras != null){ //se l'intent esiste allora UPDATE ricetta al DB

            val update_ricetta = saveRicettoDB()

            Log.v(TAG , "${DBricette.child(ricetta.nome).key}")
            DBricette.child(ricetta.nome).setValue(update_ricetta)
            Toast.makeText(this, "Aggiornata: ${update_ricetta.nome}", Toast.LENGTH_LONG).show()

        }else{ //altrimenti aggiungo ricetta al DB

            //salvataggio immagine
            val nome = ETnome.text.toString()
            val diff = spinner_diff.selectedItem.toString()
            val tempo = ETtempo.text.toString()
            val tipologia = ETtipologia.text.toString()
            val portata = spinner_portata.selectedItem.toString()
            val numPersone = ETpersone.text.toString().toInt()
            val note = ETnote.text.toString()

            /*fare i check prima di salvare la ricetta
                1- nessun campo vuoto
                2- nome diverso dalle altre ricette nel DB
                3- ...
             */

            val ricetta = Ricetta(0 ,nome, diff, tempo, tipologia, portata, numPersone, lista_ingredienti, note)

            //salvataggio degli ingredienti sul DB

            DBricette.child(ricetta.nome).setValue(ricetta)
            Toast.makeText(this, "Aggiunta: $nome", Toast.LENGTH_LONG).show()

        }
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show()
        } else {
            uploadFile()
        }

        //Log.v("oggetto", ricetta.toString())
        Toast.makeText(this, "Aggiunta la ricetta: $nome", Toast.LENGTH_LONG).show()

        //chiusura activity dell'aggiunta di una ricetta e apertura activity principale
        finish()
    }
        //salvataggio delle immagini

        val DBimmagini = FirebaseDatabase.getInstance().getReference("immagini")
        val DBStorage : StorageReference = FirebaseStorage.getInstance().getReference("Immagini")

        //funzione che ritorna l'estensione del file passato come parametro (.png -> png)
        fun getFileExtension(uri: Uri) : String {
            val cr = contentResolver
            val mime = MimeTypeMap.getSingleton()
            return mime.getExtensionFromMimeType(cr.getType(uri))!!
        }

        //funzione che aggiunge allo Storage l'immagine

        fun uploadFile(){
            if (imageUri != null){

                val nameUp = System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
                val fileReference = DBStorage.child(nameUp)

                //funzioni che permettono di svolgere azioni quando l'upload è avvenuto con successo, quando fallisce e quando sta caricando
                mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener {
                    //settiamo di nuovo la barra a 0%
                    taskSnapshot -> val handler = Handler()
                    handler.postDelayed(Runnable { progress_bar  .setProgress(0) }, 500)
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show()
                    val upload = Upload(nameUp, taskSnapshot.metadata.toString())               //ci vorrebbe il metodo getDownloadUrl al posto di metadata ma non me lo trova
                    //si entra nel DB e si crea un id unico per ogni immagine e la si salva
                    val uploadId: String? = DBimmagini.push().key
                    DBimmagini.child(uploadId!!).setValue(upload)
                }.addOnFailureListener {
                    //mostra l'errore
                    e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }.addOnProgressListener {
                    //percentuale dei byte trasferiti in contronto a quelli totali
                    taskSnapshot -> val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                    progress_bar.setProgress(progress.toInt())
                }
            }
        }


    private fun saveRicettoDB(): Ricetta {
        var ricetta: Ricetta = Ricetta()
        ricetta.nome = ETnome.text.toString()
        ricetta.diff = spinner_diff.selectedItem.toString()
        ricetta.tempo = ETtempo.text.toString()
        ricetta.tipologia = ETtipologia.text.toString()
        ricetta.portata = spinner_portata.selectedItem.toString()
        ricetta.persone = ETpersone.text.toString().toInt()
        ricetta.listaIngredienti = lista_ingredienti
        ricetta.note = ETnote.text.toString()
        return ricetta
    }

    fun addImage(v: View) {        //funzione che permette di inserire l'immagine della ricetta

        //apertura della galleria
        val openGalleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(openGalleryIntent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {       //funzione che recupera l'immagine scelta dall'utente e la inserisce
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            IVimmagine.setImageURI(imageUri)
        }
    }

}