package fr.lepatou76.marilou.fragments

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved

import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R

class ButtonFragment (private val context: MainActivity, private val button: ButtonModel): Fragment() {

    private var sound: MediaPlayer? = null
    private var fileImage:Uri? = null
    private var fileSound:Uri? = null
    private var uploadedImage: ImageView? = null
    private var imageChange = 0
    private var soundChange = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_button, container, false)

        // rendre impossible le changement de position du bouton
        view.findViewById<EditText>(R.id.editText_position).isEnabled = (false)
        // charger le délai d'inactivité bouton sauvegardé
        view.findViewById<EditText>(R.id.editTextDelay).text = SpannableStringBuilder(infosSaved[0])
        // cacher l'attente enregistrement
        view.findViewById<Button>(R.id.waiting_upload).visibility = View.GONE

        // recupérer et afficher les infos du boutons choisi
        view.findViewById<EditText>(R.id.editText_action).text = SpannableStringBuilder(button.name)
        view.findViewById<EditText>(R.id.editText_position).text = SpannableStringBuilder((button.position).toString())
        Glide.with(context).load(Uri.parse(button.imageUrl)).into(view.findViewById(R.id.imageView_button))
        sound = (MediaPlayer.create(context, Uri.parse(button.sonUrl)))
        sound?.start()

        listenButton(view)
        changeAction(view)
        validButton(view)

        // initialisation de l'image dans les parametres
        uploadedImage = view.findViewById(R.id.imageView_button)
        // recuperer le bouton pour charger l'image
        val pickupImageButton = view.findViewById<Button>(R.id.upload_image_button)
        // clique dessus ouvre les images du telephone
        pickupImageButton.setOnClickListener { pickupImage() }
        // recuperer le bouton pour charger le son
        val pickupSoundButton = view.findViewById<Button>(R.id.upload_sound_button)
        // clique pour ouvrir les fichiers du telephone
        pickupSoundButton.setOnClickListener { pickupSound() }
        // récuperer le bouton pour aller sur le site internet
        val internetLinkButton = view.findViewById<Button>(R.id.internet_link_button)
        // clique pour aller sur le site de creation de son
        internetLinkButton.setOnClickListener {
            // adresse du site
            val url = "https://magicrec.com/fr/text-to-speech"
            // activité pour y aller
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        // recuperer le bouton pour sauvegarder la nouvelle image
        val saveNewImage = view.findViewById<Button>(R.id.save_new_image)
        // clique pour sauver les changements
        saveNewImage.setOnClickListener {
            // message attente visible 3s
            view.findViewById<Button>(R.id.waiting_upload).visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
            view.findViewById<Button>(R.id.waiting_upload).visibility = View.GONE
        }, 2000)
        saveImage(view)}
        // recuperer le bouton pour sauvegarder le nouveau son
        val saveNewSound = view.findViewById<Button>(R.id.save_new_sound)
        // clique pour sauver les changements
        saveNewSound.setOnClickListener {
            // message attente visible 3s
            view.findViewById<Button>(R.id.waiting_upload).visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
            view.findViewById<Button>(R.id.waiting_upload).visibility = View.GONE
            }, 2000)
        saveSound(view)}

        return view
    }

    // pour aller chercher une image dans l'appareil
    private fun pickupImage() {
        imageChange += 1
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 33)
    }

    // pour aller chercher un son dans l'appareil
    private fun pickupSound() {
        soundChange += 1
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Sound"), 44)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 33 && resultCode == Activity.RESULT_OK){
            // verifier si les donnees sont nulles
            if(data == null || data.data == null) return
            // recuperer l'image
            fileImage = data.data
            // mettre à jour l'aperçu de l'image
            uploadedImage?.setImageURI(fileImage)

        }
        if(requestCode == 44 && resultCode == Activity.RESULT_OK){
            // verifier si les donnees sont nulles
            if(data == null || data.data == null) return
            // recuperer le son
            fileSound = data.data
            // mettre à jour le son
            sound = (MediaPlayer.create(context, fileSound))
        }
    }

    // Pour sauvegarder l'image dans Firebase si on est allé en selectionner un nouveau
    private fun saveImage(view: View) {
        val repo = ButtonsRepository()

        if(imageChange > 0){
            repo.uploadImage(fileImage!!){
                val position = view.findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = view.findViewById<EditText>(R.id.editText_action).text.toString()
                val soundUrl = ButtonsRepository.Singleton.buttonList[position - 1].sonUrl
                val downloadImageUrl = ButtonsRepository.Singleton.downloadImageUri.toString()
                // créer le nouveau bouton
                val newButton = ButtonModel(
                    position,
                    action,
                    downloadImageUrl,
                    soundUrl)
                // mettre à jour dans la bdd
                repo.updateButton(newButton)
                imageChange = 0
            }
        }
    }

    // Pour sauvegarder le son dans Firebase si on est allé en selectionner une nouvelle
    private fun saveSound(view: View) {
        val repo = ButtonsRepository()

        if(soundChange > 0){
            repo.uploadSound(fileSound!!){
                val position = view.findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = view.findViewById<EditText>(R.id.editText_action).text.toString()
                val imageUrl = ButtonsRepository.Singleton.buttonList[position - 1].imageUrl
                val downloadSoundUrl = ButtonsRepository.Singleton.downloadSoundUri.toString()
                // créer le nouveau bouton
                val newButton = ButtonModel(
                    position,
                    action,
                    imageUrl,
                    downloadSoundUrl)
                // mettre à jour dans la bdd
                repo.updateButton(newButton)
                soundChange = 0
            }
        }
    }

    // pour ecouter le son actuel
    private fun listenButton(view: View){
        val listenButton = view.findViewById<Button>(R.id.listen_button)
        listenButton.setOnClickListener {
            sound?.start()
        }
    }

    // place le curseur a la fin du texte quand on le selectionne
    private fun changeAction(view: View){
        val changeAction = view.findViewById<EditText>(R.id.editText_action)
        changeAction.setOnClickListener {
            changeAction.setSelection(changeAction.text.length)
        }
    }
    // sauvegarde les changement et retourne à l'écran principal
    private fun validButton(view: View) {
        // recuperer le bouton
        val validButton = view.findViewById<Button>(R.id.valid_button2)
        // interaction
        validButton?.setOnClickListener {
            // recuperation et sauvegarde de l'action (name) du bouton au cas ou changement
            val newAction = view.findViewById<EditText>(R.id.editText_action).text.toString()
            val position = view.findViewById<EditText>(R.id.editText_position).text.toString()
            ButtonsRepository.Singleton.databaseRef.child("button" + position).child("name").setValue(newAction)
            // recupération et sauvegarde du délai anti spam des boutons
            val newDelay = view.findViewById<EditText>(R.id.editTextDelay).text.toString()
            ButtonsRepository.Singleton.databaseRef2.child("delay").setValue(newDelay)
            // injecter le fragment modifié dans notre boite (fragment_container)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(context, infosSaved[1].toInt()))
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}