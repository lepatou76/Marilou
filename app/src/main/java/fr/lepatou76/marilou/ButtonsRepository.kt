package fr.lepatou76.marilou

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import fr.lepatou76.marilou.ButtonsRepository.Singleton.buttonList
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef
import fr.lepatou76.marilou.ButtonsRepository.Singleton.storageReference
import java.util.UUID
import com.google.android.gms.tasks.Continuation
import fr.lepatou76.marilou.ButtonsRepository.Singleton.databaseRef2
import fr.lepatou76.marilou.ButtonsRepository.Singleton.downloadImageUri
import fr.lepatou76.marilou.ButtonsRepository.Singleton.downloadSoundUri
import fr.lepatou76.marilou.ButtonsRepository.Singleton.infosSaved


class ButtonsRepository {

    object Singleton {
        //donner le lien pour accéder au bucket
        private const val BUCKET_URL: String = "gs://marilou-30309.appspot.com"

        // se connecter à notre espace de stockage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URL)

        // se connecter à la référence "buttons"
        val databaseRef = FirebaseDatabase.getInstance().getReference("buttons")
        val databaseRef2 = FirebaseDatabase.getInstance().getReference("infosaved")
        // créer une liste qui va contenir les boutons
        val buttonList = arrayListOf<ButtonModel>()
        val infosSaved = arrayListOf<String>()

        // pour contenir le lien de la nouvelle image
        var downloadImageUri: Uri? = null
        // pour contenir le lien du nouveau son
        var downloadSoundUri: Uri? = null

    }

    fun updateData(callback: () -> Unit) {
        // absorber les données depuis la databaseRef -> liste de boutons
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // retirer les anciennes
                buttonList.clear()
                // recolter la liste
                for (ds in snapshot.children) {
                    // construire un objet bouton
                    val button = ds.getValue(ButtonModel::class.java)

                    // verifier que la plante n'est pas null
                    if(button != null){
                        // ajouter le bouton à notre liste
                        buttonList.add(button)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}

        })
        databaseRef2.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                infosSaved.clear()
                for (ds in snapshot.children) {
                    // construire un objet bouton
                    val info = ds.getValue(String::class.java)

                    // verifier que la plante n'est pas null
                    if (info != null) {
                        // ajouter le bouton à notre liste
                        infosSaved.add(info)
                    }
                }
                // actionner le callback
                callback()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    // fonction pour envoyer des images sur le storage
    fun uploadImage(file: Uri, callback: () -> Unit) {
        // verifier que ce fichier n'est pas null
        if(file != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = storageReference.child(fileName)
            val uploadTask = ref.putFile(file)
            // demarrer la tache d'envoi
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                // si il y a eu un probleme lors de l'envoi du fichier
                if(!task.isSuccessful) {
                    task.exception?.let {throw it}
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                // verifier si tout a bien fonctionné
                if(task.isSuccessful){
                    // recuperer l'image
                    downloadImageUri = task.result
                    callback()
                }
            }
        }
    }
    // fonction pour envoyer des sons sur le storage
    fun uploadSound(file: Uri, callback: () -> Unit) {
        // verifier que ce fichier n'est pas null
        if(file != null) {
            val fileName = UUID.randomUUID().toString() + ".mp3"
            val ref = storageReference.child(fileName)
            val uploadTask = ref.putFile(file)
            // demarrer la tache d'envoi
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                // si il y a eu un probleme lors de l'envoi du fichier
                if(!task.isSuccessful) {
                    task.exception?.let {throw it}
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                // verifier si tout a bien fonctionné
                if(task.isSuccessful){
                    // recuperer l'image
                    downloadSoundUri = task.result
                    callback()
                }
            }
        }
    }
    // mettre à jour un objet bouton en bdd
    fun updateButton(button: ButtonModel) {
        databaseRef.child("button" + button.position.toString()).setValue(button)
    }

}