package fr.lepatou76.marilou.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository.Singleton.buttonList
import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R
import fr.lepatou76.marilou.adapter.ButtonAdapter

class HomeFragment(private val context: MainActivity, private val nbButtons: Int): Fragment() {


    var nbButtons1 = 0
    var nbButtons2 = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        var buttonList1 = arrayListOf<ButtonModel>()
        var buttonList2 = arrayListOf<ButtonModel>()


        // adapter position et images des boutons suivant leur nombre
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
            recyclerViewButtonsOne.adapter = ButtonAdapter(context, nbButtons1, buttonList1)
        }

        // recupérer le recyclerview2
        val recyclerViewButtonsTwo = view.findViewById<RecyclerView>(R.id.recycler_view_buttons_two)
        recyclerViewButtonsTwo.adapter = ButtonAdapter(context, nbButtons2, buttonList2)
        // rendre inaccessible la ligne 2 si besoin
        if(nbButtons <=3) {
            recyclerViewButtonsTwo.visibility = View.GONE
        }



        return view
    }
}