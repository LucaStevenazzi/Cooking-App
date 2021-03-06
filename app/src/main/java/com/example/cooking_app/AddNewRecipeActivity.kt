package com.example.cooking_app


import android.Manifest.permission.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Classi.*
import com.google.firebase.database.*
import com.google.firebase.storage.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.view.*
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*
import kotlinx.android.synthetic.main.choice_image.view.*
import kotlinx.android.synthetic.main.view_ricetta_activity.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random


//Activity per l'aggiunta delle ricette
class AddNewRecipeActivity : AppCompatActivity() {

    //dati
    private val TAG = "AddNewRecipeActivity"
    private lateinit var nameUp : String
    private var existDBonline: HashMap<String,Boolean> = HashMap()
    private var ricetta: Ricetta = Ricetta()
    private var lista_ingredienti = ArrayList<Ingredienti>()
    private val DBricette: DatabaseReference = FirebaseDatabase.getInstance().getReference("ricette")
    private val db : DataBaseHelper = DataBaseHelper(this)
    private lateinit var imageUri: Uri
    private var flag_img : Boolean = true
    private var checkPassati = false

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        setComponent()

        checkUpdateMode()
    }

    // settaggio di tutti i componenti dell'activity
    private fun setComponent() {
        setSpinner() //settaggio degli spinner per la visualizzazione delle PORTATE / DIFFICOLT?? / MISURE
        setRecyclerView()//Codice per la lista degli ingredianti
        add_Ingrediente_to_List()
        setListenerDBonline()
    }

    //aggiunge un listener che permette di far partire l'upload della ricetta su firebase solo se non ?? ancora presente online
    private fun setListenerDBonline() {
        DBricette.orderByKey().equalTo(ricetta.nome + ricetta.immagine).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    existDBonline[ricetta.nome+ricetta.immagine] = dataSnapshot.exists()
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    //settaggio di tutti gli spinner dell'activity
    private fun setSpinner() {

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la difficolt??
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

    //settiamo la RecyclerView per la lista degli ingredienti
    private fun setRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = Lista_Ingredienti_Adapter(lista_ingredienti)

    }

    //funzione che aggiunge gli ingredienti alla lista sottostante
    private fun add_Ingrediente_to_List() {
        add_ing.setOnClickListener(View.OnClickListener { v ->
            var check_Ing = false
            val ingnome = ing_nome.text.toString().trim()
            val ingquanti = ing_quantit??.text.toString().trim()
            val ingmisura = ing_misura.selectedItem.toString().trim()
            check_Ing = checkIng()
            if (!check_Ing) {
                return@OnClickListener
            }
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)

            lista_ingredienti.add(0, ing)
            ing_nome.text.clear()
            ing_quantit??.text.clear()
            ing_misura.setSelection(0)
            recyclerview.adapter?.notifyItemInserted(0)
            return@OnClickListener
        }
        )
    }

    //funzione che controlla che esista almeno un ingrediente nella lista_ing prima di salvare la ricetta
    private fun checkIng() : Boolean{
        if (ing_nome.text.toString().trim().isEmpty()){
            ing_nome.error = "Inserisci un nome"
            ing_nome.requestFocus()
            return false
        }
        if (ing_quantit??.text.toString().trim().isEmpty()){
            ing_quantit??.error = "Inserisci la quantit??"
            ing_quantit??.requestFocus()
            return false
        }
        if(ing_quantit??.text.toString().trim()[0] == '.'){
            ing_quantit??.error = "Inserisci un numero valido"
            ing_quantit??.requestFocus()
            return false
        }
        return true
    }

    // Update della ricetta
    private fun checkUpdateMode() {
        if (intent.extras != null) { //se esite l'intent con degli extra allora carica la ricetta scelta per essere modificafata
            setComponentToUpdate()//dove vengono settatti tutti componeti per l'update
            getRicettaExtra()//inizializzo la variabile ricetta locale
            setDatiRicetta()//assegno alle View i dati della variabile ricetta
        }
    }

    //settaggio dei componenti dell'activity quando si modifica una ricetta
    private fun setComponentToUpdate(){
        title = "Modifica Ricetta"
        val testo_update = "Aggiorna ricetta"
        ButtonOK.text = testo_update
        IVimmagine.isEnabled = false
        ETnome.isEnabled = false
        ETdesc.isEnabled = false
    }

    //ottenere la ricetta dall'intent di creazione
    private fun getRicettaExtra() {
        val byteToBitmap = intent.getByteArrayExtra("Bitmap")
        if(byteToBitmap != null){
            ricetta.bit = BitmapFactory.decodeByteArray(byteToBitmap, 0, byteToBitmap.size)
        }
        ricetta.immagine = intent.getStringExtra("Immagine").toString()
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficolt??").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        getIngredientiExtra()
        ricetta.descrizione = intent.getStringExtra("Descrizione").toString()
        ricetta.note = intent.getStringExtra("Note").toString()
    }
    private fun getIngredientiExtra() {
        val count = intent.getIntExtra("Count" , 0)
        if(count == 0) return
        for(i in 0 until count){
            val ingnome = intent.getStringExtra("Ingrediente $i nome").toString()
            val ingquanti = intent.getStringExtra("Ingrediente $i quantit??").toString()
            val ingmisura = intent.getStringExtra("Ingrediente $i misura").toString()
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)
            lista_ingredienti.add(ing)
        }
    }

    //settagio dei dati per l'intent
    private fun setDatiRicetta() {
        if(ricetta.bit != null){
            IVimmagine.setImageBitmap(ricetta.bit)
        }else{
            Picasso.with(this).load(ricetta.immagine).into(IVimmagine)
        }

        ETnome.setText(ricetta.nome)
        when (ricetta.diff) {
            "Facile" -> spinner_diff.setSelection(0)
            "Media" -> spinner_diff.setSelection(1)
            "Difficile" -> spinner_diff.setSelection(2)
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
        ETdesc.setText(ricetta.descrizione)
        ETnote.setText(ricetta.note)
    }

    //funzione che da un nome randomico ad ogni immagine
    private fun randomName(): String {
        if(flag_img){
            nameUp = Random.nextInt(1000000000).toString() + getFileExtension(imageUri)
        }else{
            nameUp = Random.nextInt(1000000000).toString() + "jpg"
        }
        return nameUp
    }

    //funzione che ritorna l'estensione del file passato come parametro (.png -> png)
    private fun getFileExtension(uri: Uri): String?{
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))!!
    }

    val REQUEST_IMAGE_CAPTURE = 1001
    val REQUEST_GALLERY_CAPTURE = 1002

    //funzione che permette di inserire l'immagine della ricetta
    fun addImage(v: View) {
        permessi()
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.choice_image, null)//Inflate del DialogView con la CustomView
        val mBuilder = AlertDialog.Builder(this)//AlertDialogBuilder
        mBuilder.setView(mDialogView)//set del Dialog con la  View
        val  mAlertDialog = mBuilder.show()//mostra il DialogView
        mDialogView.btn_photo.setOnClickListener {//predi foto dalla fotocamer
            flag_img = false
            takePhotoByCamera()
            mAlertDialog.dismiss()
        }
        mDialogView.btn_gallery.setOnClickListener {//prendi foto dalla gallery
            flag_img = true
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CAPTURE)
            mAlertDialog.dismiss()
        }
        mDialogView.btn_annulla_img.setOnClickListener{//annulla
            mAlertDialog.dismiss()
        }
    }
    private fun permessi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(CAMERA , WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 1)

                //tutto ok se accettatti entrami ma da gestire meglio in caso di non accettazione dei permessi
            }
        }
    }
    private fun takePhotoByCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }
    //funzione che recupera l'immagine scelta dall'utente nella galleria o dalla fotocamera e la inserisce nella variabile imageUri
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY_CAPTURE && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            IVimmagine.setImageURI(imageUri)
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitMap = data?.extras?.get("data") as Bitmap
            IVimmagine.setImageBitmap(bitMap)
            convertBitMapToUri(this, bitMap)
        }
    }

    //funzione che converte una bitmap in uri prima di salvarla online
    private fun convertBitMapToUri(context: Context, inImage: Bitmap) {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver,inImage, "Title",null)
        imageUri = Uri.parse(path)
    }

    //funzione che salva in locale la ricetta
    fun salvataggioRicettaDBLocale(v : View){
        //check
        checkPassati = false
        checkRicetta()
        if (!checkPassati)
            return
        if(intent.extras != null){//modifica

            val contenuto = ContentValues()
            contenuto.put(COL_DIFF, spinner_diff.selectedItem.toString())
            contenuto.put(COL_TEMPO, ETtempo.text.toString().trim())
            contenuto.put(COL_TIPO, ETtipologia.text.toString().trim())
            contenuto.put(COL_PORT, spinner_portata.selectedItem.toString())
            contenuto.put(COL_PERS, ETpersone.text.toString().trim().toInt())
            contenuto.put(COL_NOTE, ETnote.text.toString().trim())
            db.modificaRicetta(ricetta.descrizione, ricetta.nome, contenuto)
            db.eliminaIngredienti(ricetta.descrizione,ricetta.nome)
            lista_ingredienti.forEach {
                contenuto.clear()
                contenuto.put(COL_NOME, ETnome.text.toString())
                contenuto.put(COL_DESC, ETdesc.text.toString())
                contenuto.put(COL_NOME_ING, it.Name)
                contenuto.put(COL_QUANT, it.quantit)
                contenuto.put(COL_MIS, it.misura)
                db.salvaDati(TABELLA_ING, contenuto)
            }
            //se ?? presente online allora modifica anche online
            val ric = db.readData(ricetta.nome,ricetta.descrizione)
            updateRicettaDBonline(ric)
                //saveRicettaDB()//modifica ricetta online
        }
        else{//creazione nuova ricetta
            if (db.controllaRicetta(ETdesc.text.toString().trim(), ETnome.text.toString().trim())) {        //funzione che controlla che nel DB locale non sia gi?? presente la ricetta che sta per essere inserita
                Toast.makeText(this, "La ricetta ?? gi?? presente in locale", Toast.LENGTH_LONG).show()
                return
            }
            val valoriRicetta = ContentValues()
            val array = convertImage(IVimmagine.drawable.toBitmap())

            checkRicetta()
            valoriRicetta.put(COL_IMM, array)
            valoriRicetta.put(COL_NOME_IMM, randomName())
            valoriRicetta.put(COL_NOME, ETnome.text.toString().trim())
            valoriRicetta.put(COL_DIFF, spinner_diff.selectedItem.toString())
            valoriRicetta.put(COL_TEMPO, ETtempo.text.toString().trim())
            valoriRicetta.put(COL_TIPO, ETtipologia.text.toString().trim())
            valoriRicetta.put(COL_PORT, spinner_portata.selectedItem.toString())
            valoriRicetta.put(COL_PERS, ETpersone.text.toString().trim().toInt())
            valoriRicetta.put(COL_DESC, ETdesc.text.toString().trim())
            valoriRicetta.put(COL_NOTE, ETnote.text.toString().trim())

            db.salvaDati(TABELLA_RICETTE, valoriRicetta)

            lista_ingredienti.forEach {
                val valoriIngredienti = ContentValues()
                valoriIngredienti.put(COL_NOME, ETnome.text.toString())
                valoriIngredienti.put(COL_DESC, ETdesc.text.toString())
                valoriIngredienti.put(COL_NOME_ING, it.Name)
                valoriIngredienti.put(COL_QUANT, it.quantit)
                valoriIngredienti.put(COL_MIS, it.misura)

                db.salvaDati(TABELLA_ING, valoriIngredienti)
            }
        }
        db.close()
        finish()
    }

    //funzione che aggionra la ricetta online
    private fun updateRicettaDBonline(ricetta: Ricetta){
        val DBStorage: StorageReference = FirebaseStorage.getInstance().getReference("Immagini")
        val fileReference = DBStorage.child(ricetta.immagine)
        convertBitMapToUri(constraintLayoutAdd.context,ricetta.bit!!)
        //funzioni che permettono di svolgere azioni quando l'upload ?? avvenuto con successo, quando fallisce e quando sta caricando
        fileReference.putFile(imageUri).addOnSuccessListener {
                taskSnapshot ->
            fileReference.downloadUrl.addOnCompleteListener{
                    taskSnapshot ->
                //salvataggio degli riccetta sul DB onl??ne
                val temp = ricetta.bit
                val name = ricetta.immagine
                ricetta.bit = null
                ricetta.immagine = taskSnapshot.result.toString() //passaggio del link
                DBricette.child(ricetta.nome + name).setValue(ricetta)
                Toast.makeText(constraintLayoutAdd.context, "Aggiunta Ricetta online: ${ricetta.nome}", Toast.LENGTH_SHORT).show()
                ricetta.bit = temp
                ricetta.immagine = name
                existDBonline[ricetta.nome+ricetta.immagine] = true
            }
        }.addOnFailureListener {
            //mostra l'errore
                e-> Log.v("Lista_Ricette_locali", e.toString())
        }
    }

    //funzione che restituisce l'array di byte relativo all'immagine passata per argomento
    private fun convertImage(image : Bitmap) : ByteArray{
        val objByteArrayOutputStream = ByteArrayOutputStream()      //nel DB non si po?? salvare una bitmap, va quindi prima convertita in un ByteArrayOutputStream
        image.compress(Bitmap.CompressFormat.JPEG, 100, objByteArrayOutputStream)       //si converte la bitmap in un ByteArrayOutputStream
        return objByteArrayOutputStream.toByteArray()       //si inseriscono i byte in un array
    }

    //funzione che controlla i campi prima di salvare la ricetta
    fun checkRicetta(){
        if (IVimmagine.drawable.constantState == resources.getDrawable(android.R.drawable.ic_menu_report_image).constantState){
            ETnome.error = "Inserisci un'immagine"
            ETnome.requestFocus()
            return
        }
        if(ETnome.text.toString().trim().isEmpty()){
            ETnome.error = "Inserisci un nome"
            ETnome.requestFocus()
            return
        }
        if (ETtempo.text.toString().trim().isEmpty()){
            ETtempo.error = "Inserisci il tempo"
            ETtempo.requestFocus()
            return
        }
        if (ETtipologia.text.toString().trim().isEmpty()){
            ETtipologia.error = "Inserisci una tipologia"
            ETtipologia.requestFocus()
            return
        }
        if (ETpersone.text.toString().trim().isEmpty()){
            ETpersone.error = "Inserisci un numero"
            ETpersone.requestFocus()
            return
        }
        if (ETpersone.text.toString().toInt() > 10){
            ETpersone.error = "Inserisci un numero minore di 10"
            ETpersone.requestFocus()
            return
        }
        if (lista_ingredienti.isEmpty()){
            ing_nome.error = "Inserisci almeno un ingrediente"
            ing_nome.requestFocus()
            return
        }
        if (ETdesc.text.toString().trim().isEmpty()){
            ETdesc.error = "Inserisci il procedimento"
            ETdesc.requestFocus()
            return
        }
        checkPassati = true
    }
}