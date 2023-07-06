package fr.lepatou76.marilou



import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef2
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.fragments.HomeFragment
import fr.lepatou76.marilou.fragments.ParametersFragment
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private val masterPassword = "2309"
    private var password = "0000"
    private var newNbButtons = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // cacher le bouton valider
        findViewById<Button>(R.id.valid_button).visibility = View.GONE
        // cacher le layout password
        findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE

        parametersAccess()
        validParameters()
        validPassword()
        exit()

        // charger notre ButtonRepository
        val repo = ButtonsRepository()

        // mettre à jour la liste de boutons et les infos sauvegardées
        repo.updateData {
            val nbButtons = infosSaved[0].toInt()
            newNbButtons = infosSaved[0].toInt()
            password = infosSaved[1]
            // injecter le fragment dans notre boite (fragment_container)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(this, nbButtons))
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    // click sur le bouton parametres demande la saisie du mot de passe
    private fun parametersAccess(){
        // recuperer le bouton et l'Edittext
        val parametersButton = findViewById<Button>(R.id.parameters_button)
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)
        // interaction
        parametersButton.setOnClickListener {
            // afficher le layout pour entrer le mot de passe
            findViewById<LinearLayout>(R.id.password_layout).visibility = View.VISIBLE
            // rendre l'Edittext actif, le vider avant la saisie et y mettre le focus
            passwordEditText.isEnabled = (true)
            passwordEditText.text = null
            passwordEditText.requestFocus()
            // bloquer le click sur le bouton parametre
            parametersButton.isClickable = (false)
        }
    }

    private fun validParameters(){
        // recuperer le bouton
        val validButton = findViewById<Button>(R.id.valid_button)
        // interaction
        validButton.setOnClickListener {
            // recupération et sauvegarde du nombre de boutons choisis
            newNbButtons =
                findViewById<Spinner>(R.id.button_spinner_input).selectedItem.toString().toInt()
            databaseRef2.child("nbbuttons").setValue(newNbButtons.toString())
            // recupération et sauvegarde du mot de passe si besoin
            val newPassword = findViewById<EditText>(R.id.password_change_text_edit).text.toString()
            databaseRef2.child("password").setValue(newPassword)
            // recuperation et sauvegarde de l'action (name) du bouton au cas ou changement
            val newAction = findViewById<EditText>(R.id.editText_action).text.toString()
            val position = findViewById<EditText>(R.id.editText_position).text.toString()
            databaseRef.child("button" + position).child("name").setValue(newAction)
            // injecter le fragment modifié dans notre boite (fragment_container)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(this, newNbButtons))
            transaction.addToBackStack(null)
            transaction.commit()
            // cacher le bouton valider
            validButton.visibility = View.GONE
            // redonner accès au bouton parametres
            findViewById<Button>(R.id.parameters_button).isClickable = (true)
            // rendre le bouton quitter visible
            findViewById<Button>(R.id.exit_Button).visibility = View.VISIBLE
        }
    }

    private fun validPassword(){
        // recuperer le bouton
        val passwordValidButton = findViewById<Button>(R.id.password_valid_button)
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)

        // interaction
        passwordValidButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            // test si le mot de passe est correct
            if(enteredPassword == password || enteredPassword == masterPassword){
                // injecter le fragment dans notre boite (fragment_container)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ParametersFragment(this, newNbButtons))
                transaction.addToBackStack(null)
                transaction.commit()
                // cacher le bouton quitter
                findViewById<Button>(R.id.exit_Button).visibility = View.GONE
                // remettre le bouton valider
                findViewById<Button>(R.id.valid_button).visibility = View.VISIBLE
                // enlever le layout password
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
            else{
                // vider l'Edittext de saisie
                passwordEditText.text = null
                // cacher le layout de saisie
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // reactiver le bouton parametre
                findViewById<Button>(R.id.parameters_button).isClickable = (true)
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
        }
    }

    private fun exit(){
        // recuperer le bouton
        val exitButton = findViewById<Button>(R.id.exit_Button)
        // click pour quitter
        exitButton.setOnClickListener {
            exitProcess(0)
        }
    }
}