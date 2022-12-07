package com.example.team9

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.fragment_four.*
import org.w3c.dom.Text

class editActivity : AppCompatActivity() {


    private lateinit var editbutton: Button
    private lateinit var completebutton: Button
    private lateinit var editGallery: Button
    private lateinit var uploadGallery: Button
    private lateinit var uploadImage: ImageView
    private lateinit var uri: Uri
    private var storageReference = Firebase.storage
    var auth: FirebaseAuth? = null
    var Firestore: FirebaseFirestore? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        editGallery = findViewById(R.id.editGallery)
        uploadGallery= findViewById(R.id.uploadGallery)
        uploadImage= findViewById(R.id.uploadImage)

        storageReference = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        Firestore = FirebaseFirestore.getInstance()


        completebutton = findViewById(R.id.completeButton)

        completebutton.setOnClickListener {
            val intentgoedit = Intent(this,MainActivity::class.java)
            startActivity(intentgoedit)

        }
        val email = auth?.currentUser!!.email
        editbutton = findViewById(R.id.editButton)

        editbutton.setOnClickListener {
            var userInfomation = userInfo()
            userInfomation.name = editTextName.text.toString()
            userInfomation.age = editTextAge.text.toString()
            userInfomation.gender = editTextGender.text.toString()
            userInfomation.explanation = editExplanation.text.toString()
            Firestore?.collection("$email")?.document(auth?.currentUser!!.email.toString())?.set(userInfomation)
            Toast.makeText( context,"회원정보가 수정되었습니다.",Toast.LENGTH_SHORT).show()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                uploadImage.setImageURI(it)
                if (it != null) {
                    uri = it
                }
            })
        editGallery.setOnClickListener {
            galleryImage.launch("image/*")
        }
        uploadGallery.setOnClickListener {
            storageReference.getReference(FirebaseAuth.getInstance().currentUser!!.email.toString())
                .child(FirebaseAuth.getInstance().currentUser!!.email.toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val imageMap = mapOf("url" to uri.toString())
                            val databaseReference = FirebaseDatabase.getInstance().getReference("userImages")
                            databaseReference.child(uid).setValue(imageMap)
                            Toast.makeText( context,"회원사진이 수정되었습니다..",Toast.LENGTH_SHORT).show()
                        }
                }
        }
        }


    }
