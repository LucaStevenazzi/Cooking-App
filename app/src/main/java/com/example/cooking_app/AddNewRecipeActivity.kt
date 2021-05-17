package com.example.cooking_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.view.*
import kotlinx.android.synthetic.main.view_ricetta_activity.*
import kotlin.random.Random


// Activity per l'aggiunta delle ricette

class AddNewRecipeActivity : AppCompatActivity() {

    private val TAG = "AddNewRecipeActivity"
    private lateinit var nameUp : String

    //dati
    private var ricetta: Ricetta = Ricetta()
    private var lista_ingredienti = ArrayList<Ingredienti>()
    private val DBricette: DatabaseReference = FirebaseDatabase.getInstance().getReference("ricette")
    private lateinit var imageUri: Uri

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        setComponent()

        checkUpdateMode()
    }

    // settaggio di tutti i componenti dell'activity
    private fun setComponent() {
        setSpinner() //settaggio degli spinner per la visualizzazione delle PORTATE / DIFFICOLTà / MISURE
        setRecyclerView()//Codice per la lista degli ingredianti
        add_Ingrediente_to_List()
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
    private fun setRecyclerView() {     //settiamo la RecyclerView per la lista degli ingredienti
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = Lista_Ingredienti_Adapter(lista_ingredienti)

    }
    private fun add_Ingrediente_to_List() {        //funzione che aggiunge gli ingredienti alla lista sottostante
        add_ing.setOnClickListener(View.OnClickListener { v ->
            val ingnome = ing_nome.text.toString()
            val ingquanti = ing_quantità.text.toString()
            val ingmisura = ing_misura.selectedItem.toString()
            if (ingnome.isEmpty() || ingquanti.isEmpty() || !ingquanti.isDigitsOnly() || ingnome.isDigitsOnly()) {
                return@OnClickListener
            }
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)

            lista_ingredienti.add(0, ing)
            ing_nome.text.clear()
            ing_quantità.text.clear()
            ing_misura.setSelection(0)
            recyclerview.adapter?.notifyItemInserted(0)
            return@OnClickListener
        }
        )
    }

    // Update della ricetta
    private fun checkUpdateMode() {
        if (intent.extras != null) { //se esite l'intent con degli extra allora carica la ricetta scelta per essere modificafata
            setComponentToUpdate()//dove vengono settatti tutti componeti per l'update
            getRicettaExtra()//inizializzo la variabile ricetta locale
            setDatiRicetta()//assegno alle View i dati della variabile ricetta
        }
    }
    private fun setComponentToUpdate(){
        title = "Update Ricetta"
        val testo_update = "Aggiorna ricetta"
        ButtonOK.text = testo_update
        ETnome.isEnabled = false
    }
    private fun getRicettaExtra() { //ottenere la ricetta dall'intent di creazione
        ricetta.immagine = intent.getStringExtra("Immagine").toString()
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficoltà").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        getIngredientiExtra()
        ricetta.note = intent.getStringExtra("Note").toString()
    }
    private fun getIngredientiExtra() {
        val count = intent.getIntExtra("Count" , 0)
        if(count == 0) return
        for(i in 0 until count){
            val ingnome = intent.getStringExtra("Ingrediente $i nome").toString()
            val ingquanti = intent.getStringExtra("Ingrediente $i quantità").toString()
            val ingmisura = intent.getStringExtra("Ingrediente $i misura").toString()
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)
            lista_ingredienti.add(ing)
        }
    }
    private fun setDatiRicetta() { //settagio dei dati per l'intent
        Picasso.with(this).load(ricetta.immagine).into(IVimmagine)
        ETnome.setText(ricetta.nome)
        when (ricetta.diff) {
            "BASSA" -> spinner_diff.setSelection(0)
            "MEDIA" -> spinner_diff.setSelection(1)
            "ALTA" -> spinner_diff.setSelection(2)
        }
        ETtempo.setText(ricetta.tempo)
        ETtipologia.setText(ricetta.tipologia)
        when (ricetta.portata) {
            "Antipasto" -> spinner_portata.setSelection(0)
            "Primo" -> spinner_portata.setSelection(1)
            "Secondo" -> spinner_portata.setSelection(2)
            "Contorno" -> spinner_portata.setSelection(3)
            "Dolce" -> spinner_portata.setSelection(4)
        }
        ETpersone.setText(ricetta.persone.toString())
        //gli ingredienti sono gia stati settati tramite l'adapter collegato alla ricetta_ingredienti
        ETnote.setText(ricetta.note)
    }

    //onClick sul salvataggio della nuova ricetta o l'update della ricetta selezionata
    fun saveRecipe(v: View) {//onClick del button che salva i dati della ricetta nel DB

        if (intent.extras != null) { //se l'intent esiste allora UPDATE ricetta al DB

            saveRicettaDB()
            DBricette.child(ricetta.nome).setValue(ricetta)

        } else { //altrimenti aggiungo ricetta al DB

            //salvataggio nuova ricetta
            uploadFile()
            saveRicettaDB()
        }
        //chiusura activity dell'aggiunta di una ricetta e apertura activity principale
        finish()
    }
    private fun getFileExtension(uri: Uri): String {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))!!
    }//funzione che ritorna l'estensione del file passato come parametro (.png -> png)
    private fun saveRicettaDB() {
        ricetta.immagine
        ricetta.nome = ETnome.text.toString()
        ricetta.diff = spinner_diff.selectedItem.toString()
        ricetta.tempo = ETtempo.text.toString()
        ricetta.tipologia = ETtipologia.text.toString()
        ricetta.portata = spinner_portata.selectedItem.toString()
        ricetta.persone = ETpersone.text.toString().toInt()
        ricetta.listaIngredienti = lista_ingredienti
        ricetta.note = ETnote.text.toString()
    }
    private fun uploadFile() {

        val DBStorage: StorageReference = FirebaseStorage.getInstance().getReference("Immagini")
        nameUp = Random.nextInt(1000000000).toString() + "." + getFileExtension(imageUri)
        val fileReference = DBStorage.child(nameUp)
        lateinit var url : String

        //funzioni che permettono di svolgere azioni quando l'upload è avvenuto con successo, quando fallisce e quando sta caricando
        fileReference.putFile(imageUri).addOnSuccessListener {
            taskSnapshot ->
            fileReference.downloadUrl.addOnCompleteListener {
                taskSnapshot ->
                url = taskSnapshot.result.toString()
            }
        }.addOnFailureListener {
            e ->
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }//mostra l'errore

        val immagine = url
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

        val ricetta = Ricetta(immagine, nome, diff, tempo, tipologia, portata, numPersone, lista_ingredienti, note) // creazione della ricetta


        //salvataggio della ricetta sul DB
        DBricette.child(ricetta.nome).setValue(ricetta)
        Toast.makeText(this, "Aggiunta: $nome", Toast.LENGTH_LONG).show()
    }//funzione che aggiunge allo Storage l'immagine
    fun addImage(v: View) {        //funzione che permette di inserire l'immagine della ricetta

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission()
            //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            accesso = true
        }*/
        //if (accesso == true) {
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, 1000)
        //}
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            IVimmagine.setImageURI(imageUri)
        }
    }//funzione che recupera l'immagine scelta dall'utente nella galleria e la inserisce nella variabile imageUri


/*

    //funzione che richiede l'accesso alla galleria e in caso contrario mostra un dialog che informa del perchè della richiesta
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle("Permesso richiesto")
                    .setMessage("Permesso per accedere alla galleria e caricare un'immagine")
                    .setPositiveButton("ok") { dialog, which -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE) }
                    .setNegativeButton("cancella") { dialog, which -> dialog.dismiss() }
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    //funzione che controlla se l'accesso è stato garantito o meno
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Accesso garantito", Toast.LENGTH_SHORT).show()
                //accesso = false
            } else {
                Toast.makeText(this, "Accesso NON garantito", Toast.LENGTH_SHORT).show()
                //accesso = true
            }
        }
    }


     */
}