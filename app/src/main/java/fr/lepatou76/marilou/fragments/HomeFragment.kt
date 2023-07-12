package fr.lepatou76.marilou.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository
import fr.lepatou76.marilou.ButtonsRepository.Singleton.buttonList
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R
import fr.lepatou76.marilou.adapter.ButtonAdapter
import kotlin.system.exitProcess

class HomeFragment(private val context: MainActivity, private val nbButtons: Int): Fragment() {

    var nbButtons1 = 0
    var nbButtons2 = 0
    var buttonList1 = arrayListOf<ButtonModel>()
    var buttonList2 = arrayListOf<ButtonModel>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // adapter position et images des boutons suivant le nombre choisi
        if(nbButtons <= 3){
            nbButtons1 = nbButtons
            buttonList1.add(buttonList[0])
            if(nbButtons1 == 2){
                buttonList1.add(buttonList[1])
            }
            if(nbButtons1 == 3){
                buttonList1.add(buttonList[1])
                buttonList1.add(buttonList[2])
            }
        }
        else{
            if(nbButtons == 4){
                nbButtons1 = 2
                nbButtons2 = 2
                buttonList1.add(buttonList[0])
                buttonList1.add(buttonList[1])
                buttonList2.add(buttonList[2])
                buttonList2.add(buttonList[3])
            }
           else{
                nbButtons1 = 3
                nbButtons2 = nbButtons - 3
                buttonList1.add(buttonList[0])
                buttonList1.add(buttonList[1])
                buttonList1.add(buttonList[2])
                buttonList2.add(buttonList[3])
                buttonList2.add(buttonList[4])

                if(nbButtons == 6){
                    buttonList2.add(buttonList[5])
                }
            }
        }

        // recuperer le recyclerview1
        val recyclerViewButtonsOne = view.findViewById<RecyclerView>(R.id.recycler_view_buttons_one)
        if (recyclerViewButtonsOne != null) {
            recyclerViewButtonsOne.adapter = ButtonAdapter(context, buttonList1)
        }

        // recupérer le recyclerview2
        val recyclerViewButtonsTwo = view.findViewById<RecyclerView>(R.id.recycler_view_buttons_two)
        recyclerViewButtonsTwo.adapter = ButtonAdapter(context, buttonList2)
        // rendre inaccessible la ligne 2 si besoin
        if(nbButtons <=3) {
            recyclerViewButtonsTwo.visibility = View.GONE
        }

        // cacher le layout password
        view.findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE

        exit(view)
        parametersAccess(view)
        validPassword(view)

        return view

    }

    private fun exit(view: View){
        // recuperer le bouton
        val exitButton = view.findViewById<Button>(R.id.exit_Button)
        // click pour quitter
        exitButton.setOnClickListener {
            exitProcess(0)
        }
    }
    // click sur le bouton parametres demande la saisie du mot de passe
    private fun parametersAccess(view: View){
        // recuperer le bouton et l'Edittext
        val parametersButton = view.findViewById<Button>(R.id.parameters_button)
        val passwordEditText = view.findViewById<EditText>(R.id.password_text_edit)
        // interaction
        parametersButton.setOnClickListener {
            // afficher le layout pour entrer le mot de passe
            view.findViewById<LinearLayout>(R.id.password_layout).visibility = View.VISIBLE
            // rendre l'Edittext actif, le vider avant la saisie et y mettre le focus
            passwordEditText.isEnabled = (true)
            passwordEditText.text = null
            passwordEditText.requestFocus()
            // bloquer le click sur le bouton parametre
            parametersButton.isClickable = (false)
        }
    }

    private fun validPassword(view: View){
        // recuperer le bouton
        val passwordValidButton = view.findViewById<Button>(R.id.password_valid_button)
        val passwordEditText = view.findViewById<EditText>(R.id.password_text_edit)

        // interaction
        passwordValidButton.setOnClickListener {
            val enteredPassword = passwordEditText.text.toString()
            // test si le mot de passe est correct
            if(enteredPassword == infosSaved[2] || enteredPassword == "2309"){
                // injecter le fragment dans notre boite (fragment_container)
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, ParametersFragment(context))
                transaction.addToBackStack(null)
                transaction.commit()
                // enlever le layout password
                view.findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
            else{
                // vider l'Edittext de saisie
                passwordEditText.text = null
                // cacher le layout de saisie
                view.findViewById<LinearLayout>(R.id.password_layout).visibility = View.GONE
                // reactiver le bouton parametre
                view.findViewById<Button>(R.id.parameters_button).isClickable = (true)
                // forcer fermeture clavier
                passwordEditText.isEnabled = (false)
            }
        }
    }
}