package fr.lepatou76.marilou.fragments

import android.annotation.SuppressLint
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
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository
import fr.lepatou76.marilou.ButtonsRepository.Singleton.buttonList
import fr.lepatou76.marilou.ButtonsRepository.Singleton.downloadImageUri
import fr.lepatou76.marilou.ButtonsRepository.Singleton.downloadSoundUri
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R


class ParametersFragment(private val context: MainActivity, nbButtons: Int): Fragment(){

    var nbButton = nbButtons
    private var fileImage:Uri? = null
    private var fileSound:Uri? = null
    private var uploadedImage:ImageView? = null
    private var sound: MediaPlayer? = null
    private var imageChange = 0
    private var soundChange = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parameters, container, false)

        // selection du spinner sur le nombre de boutons actuel
        view.findViewById<Spinner>(R.id.button_spinner_input).setSelection(nbButton-1)
        // recuperation du mot de passe actuel
        view.findViewById<EditText>(R.id.password_change_text_edit).setText(infosSaved[2])
        // cacher le layout de modification du mot de passe
        view.findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.GONE
        // rendre impossible le changement de position du bouton
        view.findViewById<EditText>(R.id.editText_position).isEnabled = (false)
        // charger le délai d'inactivité bouton sauvegardé
        view.findViewById<EditText>(R.id.editTextDelay).text = SpannableStringBuilder(infosSaved[0].toString())
        selectButton(view)
        listenButton(view)
        changeAction(view)
        changePassword(view)
        // initialisation de l'image dans les parametres
        uploadedImage = view.findViewById(R.id.imageView_button)

        // selectionner par défaut le bouton 1 pour la modification et afficher ses infos
        val position =
            view.findViewById<Spinner>(R.id.button_spinner_modif)?.selectedItem.toString().toInt() - 1
        val button: ButtonModel = buttonList[position]
        view.findViewById<EditText>(R.id.editText_position).text = SpannableStringBuilder((button.position).toString())
        view.findViewById<EditText>(R.id.editText_action)?.text = SpannableStringBuilder(button.name)
        Glide.with(context).load(Uri.parse(button.imageUrl)).into(view.findViewById(R.id.imageView_button))
        sound = (MediaPlayer.create(context, Uri.parse(button.sonUrl)))


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
        saveNewImage.setOnClickListener {saveImage(view)}
        // recuperer le bouton pour sauvegarder la nouvelle image
        val saveNewSound = view.findViewById<Button>(R.id.save_new_sound)
        // clique pour sauver les changements
        saveNewSound.setOnClickListener {saveSound(view)}

        return view
    }

    // Pour sauvegarder l'image si on est allé en selectionner une nouvelle
    private fun saveSound(view: View) {
        val repo = ButtonsRepository()

        if(soundChange > 0){
            repo.uploadSound(fileSound!!){
                val position = view.findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = view.findViewById<EditText>(R.id.editText_action).text.toString()
                val imageUrl = buttonList[position - 1].imageUrl
                val downloadSoundUrl = downloadSoundUri.toString()
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

    // Pour sauvegarder le son si on est allé en selectionner un nouveau
    private fun saveImage(view: View) {
        val repo = ButtonsRepository()

        if(imageChange > 0){
            repo.uploadImage(fileImage!!){
                val position = view.findViewById<EditText>(R.id.editText_position).text.toString().toInt()
                val action = view.findViewById<EditText>(R.id.editText_action).text.toString()
                val soundUrl = buttonList[position - 1].sonUrl
                val downloadImageUrl = downloadImageUri.toString()
                // créer le nouveau bouton
                val newButton = ButtonModel(
                    position,
                    action,
                    downloadImageUrl,
                    soundUrl)
                // mettre à jour dans la bdd
                repo.updateButton(newButton)
                soundChange = 0
            }
        }
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

    // pour charger les informations du bouton selectionné
    private fun selectButton(view: View){
        val uploadSelectedButton = view.findViewById<Button>(R.id.upload_button_selected)
        uploadSelectedButton.setOnClickListener {
            val position =
                view.findViewById<Spinner>(R.id.button_spinner_modif).selectedItem.toString().toInt() - 1
            val button: ButtonModel = buttonList[position]

            view.findViewById<EditText>(R.id.editText_action).text = SpannableStringBuilder(button.name)
            view.findViewById<EditText>(R.id.editText_position).text = SpannableStringBuilder((button.position).toString())
            Glide.with(context).load(Uri.parse(button.imageUrl)).into(view.findViewById(R.id.imageView_button))
            sound = (MediaPlayer.create(context, Uri.parse(button.sonUrl)))
            sound?.start()
        }
    }

    // pour ecouter le son actuel
    private fun listenButton(view: View){
        val listenButton = view.findViewById<Button>(R.id.listen_button)
        listenButton.setOnClickListener {
            sound?.start()
        }
    }

    // place le curseur a la fin du text
    private fun changeAction(view: View){
        val changeAction = view.findViewById<EditText>(R.id.editText_action)
        changeAction.setOnClickListener {
            changeAction.setSelection(changeAction.text.length)
        }
    }

    // pour modifier le mot de passe
    private fun changePassword(view: View){
        val changePassword = view.findViewById<Button>(R.id.password_change_button)
        val textPassword = view.findViewById<EditText>(R.id.password_change_text_edit)
        changePassword.setOnClickListener {
            textPassword.isEnabled = (true)
            view.findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.VISIBLE
            view.findViewById<EditText>(R.id.password_change_text_edit).requestFocus()
            textPassword.setSelection(textPassword.text.length)
        }
        val validChange = view.findViewById<Button>(R.id.password_change_valid_button)
        validChange.setOnClickListener {
            view.findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.GONE
            textPassword.isEnabled = (false)
        }
    }
}