package fr.lepatou76.marilou.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.lepatou76.marilou.ButtonModel
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved
import fr.lepatou76.marilou.MainActivity
import fr.lepatou76.marilou.R


class ButtonAdapter(
    private val context: MainActivity,
    //private val nbButtons: Int,
    private val buttonList: List<ButtonModel>
    ): RecyclerView.Adapter<ButtonAdapter.ViewHolder>() {

    // boite pour ranger les composants à controler
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // image du bouton
        val buttonImage = view.findViewById<ImageView>(R.id.image_item)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_horizontal_button, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // recuperer les infos du bouton
        val currentButton = buttonList[position]

        // utiliser glide pour mettre à jour l'image
        Glide.with(context).load(Uri.parse(currentButton.imageUrl)).into(holder.buttonImage)

        // interaction lors du clic sur un bouton
        holder.itemView.setOnClickListener {
            // Lancer le son
            val sound = MediaPlayer.create(context, Uri.parse(currentButton.sonUrl))
            sound.start()
            // désactiver le bouton pendant le délai paramétré pour éviter le spam
            var delay = (infosSaved[0].toInt() * 1000).toLong()
            holder.itemView.isEnabled = (false)
            Handler().postDelayed({
                holder.itemView.isEnabled = (true)
            }, delay)
        }
    }

    override fun getItemCount(): Int = buttonList.size

}