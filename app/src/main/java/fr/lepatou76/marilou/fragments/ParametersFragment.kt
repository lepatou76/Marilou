package fr.lepatou76.marilou.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository
import fr.lepatou76.marilou.ButtonsRepository.Singleton.buttonList
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef2
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R


class ParametersFragment(private val context: MainActivity): Fragment(){

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parameters, container, false)

        // selection du spinner sur le nombre de boutons actuel
        view.findViewById<Spinner>(R.id.button_spinner_input).setSelection(infosSaved[1].toInt()-1)
        // recuperation du mot de passe actuel
        view.findViewById<EditText>(R.id.password_change_text_edit).setText(infosSaved[2])
        // cacher le layout de modification du mot de passe
        view.findViewById<LinearLayout>(R.id.password_change_layout).visibility = View.GONE
        // cacher le layout de confirmation réinitialisation
        view.findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE

        selectButton(view)
        changePassword(view)
        validParameters(view)

        // recuperer le bouton de réinitialisation
        val resetButton = view.findViewById<Button>(R.id.reset_app)
        // clique pour lancer la procédure
        resetButton.setOnClickListener { restApp(view) }
        // recuperer bouton mode d'emploi
        val noticeButton = view.findViewById<Button>(R.id.read_notice)
        // clique pour accéder à la notice
        noticeButton.setOnClickListener {
            // adresse du lien de la notice
            val url = "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/Doc%20appli%20Marilou.pdf?alt=media&token=d88e16d5-e416-4be6-a651-9a944b38986a"
            // activité pour la récupérer et l'afficher
            val intent = Intent(Intent.ACTION_QUICK_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            startActivity(intent)
        }

        return view
    }

    // pour ouvrir le fragment Button avec le bouton selectionné
    private fun selectButton(view: View){
        val uploadSelectedButton = view.findViewById<Button>(R.id.upload_button_selected)
        uploadSelectedButton.setOnClickListener {
            val position =
                view.findViewById<Spinner>(R.id.button_spinner_modif).selectedItem.toString().toInt() - 1
            val button: ButtonModel = buttonList[position]
            val transaction = parentFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, ButtonFragment(context, button))
            transaction?.addToBackStack(null)
            transaction?.commit()
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
    private fun restApp(view: View) {
        // faire apparaitre le layout demandant de confirmer la réinitialisation avec avertissement
        view.findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.VISIBLE
        // récupérer les boutons OUI et NON
        val yesButton = view.findViewById<Button>(R.id.yes_reset_button)
        val noButton = view.findViewById<Button>(R.id.no_reset_button)
        // choix de ne pas réinitialiser
        noButton.setOnClickListener {
            // disparition de la fenêtre
            view.findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE
        }
        // choix de réinitialiser
        yesButton.setOnClickListener {resetButtons(view) {
            // disparition de la fenêtre
            view.findViewById<LinearLayout>(R.id.confirm_reset_layout).visibility = View.GONE
            }
        }
    }

    private fun resetButtons(view: View, callback: () -> Unit) {
        val repo = ButtonsRepository()

        // recréer les six boutons d'origine grace au stockage de FireBase et le mettre à jour dans la bdd
        val button1 = ButtonModel(1, "Manger", "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/faim.jpg?alt=media&token=677a503f-f096-4e23-a741-4a920183e74c",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/faim.mp3?alt=media&token=4d9e4d43-f196-4b83-935d-848e279f101a")
        repo.updateButton(button1)
        val button2 = ButtonModel(2,"Boire","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/soif.jpg?alt=media&token=6f035b45-bd08-4b21-bc41-895f1865bb15",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/soif.mp3?alt=media&token=23fcaa3b-17ac-4402-b2ff-d6fccadc8244")
        repo.updateButton(button2)
        val button3 = ButtonModel(3, "Toilettes","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/toilettes.jpg?alt=media&token=7d0dd5d3-3d31-4f7b-b415-6b49149511e3",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/toilettes.mp3?alt=media&token=6e82ff50-cf13-4b4a-9e37-6e893e971ba5")
        repo.updateButton(button3)
        val button4 = ButtonModel(4,"Dormir","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/sommeil.jpg?alt=media&token=4b3f2158-19b7-46f7-be97-950df361386c",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/sommeil.mp3?alt=media&token=4944cce5-1305-4ab3-a849-8295c48b145f")
        repo.updateButton(button4)
        val button5 = ButtonModel(5,"Jouer","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/jouer.jpg?alt=media&token=e24e73e3-e1c2-42bd-86ed-100b24189fa8",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/jouer.mp3?alt=media&token=2d0b571c-eaa2-4cd8-bbe4-e9928d854feb")
        repo.updateButton(button5)
        val button6 = ButtonModel(6,"Se promener","https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/se_promener.jpg?alt=media&token=babc7c2b-7fcf-4b5d-9723-ce955b770e67",
            "https://firebasestorage.googleapis.com/v0/b/marilou-30309.appspot.com/o/se_promener.mp3?alt=media&token=a6136713-8a4f-4716-a1fb-435328764468")
        repo.updateButton(button6)
        // remettre le nombre de boutons, le mot de passe et le délai anti spam bouton d'origine
        databaseRef2.child("nbbuttons").setValue("6")
        databaseRef2.child("password").setValue("1111")
        databaseRef2.child("delay").setValue("3")

        callback()
    }
    private fun validParameters(view: View){
        // recuperer le bouton
        val validButton = view.findViewById<Button>(R.id.valid_button1)
        // interaction
        validButton?.setOnClickListener {
            // recupération et sauvegarde du nombre de boutons choisis
            val newNbButtons =
                view.findViewById<Spinner>(R.id.button_spinner_input)?.selectedItem.toString().toInt()
            databaseRef2.child("nbbuttons").setValue(newNbButtons.toString())
            // recupération et sauvegarde du mot de passe si besoin
            val newPassword = view.findViewById<EditText>(R.id.password_change_text_edit)?.text.toString()
            databaseRef2.child("password").setValue(newPassword)
            // injecter le fragment modifié dans notre boite (fragment_container)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(context, newNbButtons))
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}