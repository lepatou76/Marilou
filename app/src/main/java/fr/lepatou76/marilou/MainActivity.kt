package fr.lepatou76.marilou



import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef2
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.fragments.HomeFragment
import fr.lepatou76.marilou.fragments.ParametersFragment
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    // initialisation
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
        // recuperer le bouton
        val parametersButton = findViewById<Button>(R.id.parameters_button)
        val passwordEditText = findViewById<EditText>(R.id.password_text_edit)
        // interaction
        parametersButton.setOnClickListener {
            findViewById<LinearLayout>(R.id.password_layout).visibility = View.VISIBLE
            passwordEditText.text = null
            passwordEditText.requestFocus()
            parametersButton.isClickable = (false)
            passwordEditText.isEnabled = (true)
            findViewById<Button>(R.id.exit_Button).visibility = View.GONE
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
            // récupération des changement
            // injecter le fragment dans notre boite (fragment_container)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, HomeFragment(this, newNbButtons))
            transaction.addToBackStack(null)
            transaction.commit()
            // cacher le bouton valider
            validButton.visibility = View.GONE
            findViewById<Button>(R.id.parameters_button).isClickable = (true)
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
            if(enteredPassword == password){
                // injecter le fragment dans notre boite (fragment_container)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ParametersFragment(this, newNbButtons))
                transaction.addToBackStack(null)
                transaction.commit()
                // remettre le bouton valider
                findViewById<Button>(R.id.valid_button).visibility = View.VISIBLE
                // enlever le layout password
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
            else{
                passwordEditText.text = null
                findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                findViewById<Button>(R.id.parameters_button).isClickable = (true)
                passwordEditText.isEnabled = (false)
            }
        }
    }

    private fun exit(){
        val exitButton = findViewById<Button>(R.id.exit_Button)
        exitButton.setOnClickListener {
            exitProcess(0)
        }
    }




}