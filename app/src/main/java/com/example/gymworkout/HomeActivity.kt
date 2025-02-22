package com.example.gymworkout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        var email = intent.extras?.getString("email")

        binding.buttonCreateGroup.setOnClickListener { joinGroup(email, it) }
        binding.buttonJoinGroup.setOnClickListener { createGroup(email, it) }

        binding.menuButtonHome.setOnClickListener {
            showPopup(binding.menuButtonHome)
        }

    }

    fun joinGroup(email : Any?, view: View){
        val intencion = Intent(this, MainActivity::class.java)
        db.collection("users").document(email as String).get().addOnSuccessListener {
            if (it.get("onGroup") == true){
                saveGroupID(it.get("group") as String)
                intencion.putExtra("email", "$email")
                startActivity(intencion)
            } else {
                Snackbar.make(
                    view,
                    getString(R.string.useroffGroup),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun saveGroupID(groupId : String){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("group", groupId)
        prefs.apply()
    }

    fun createGroup(email : Any?, view: View){
        val intencion = Intent(this, CreateGroupActivity::class.java)
        db.collection("users").document(email as String).get().addOnSuccessListener {
            if (it.get("onGroup") == false){
                intencion.putExtra("email", "$email")
                startActivity(intencion)
            } else {
                Snackbar.make(
                    view,
                    getString(R.string.useronGroup),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPopup (view: View){
        val popup = PopupMenu(this,view)
        val inflater: MenuInflater = popup.menuInflater
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)?.edit()
        val intencion = Intent(this, LoginActivity::class.java)
        inflater.inflate(R.menu.options_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.log_out -> {
                    prefs?.clear()
                    prefs?.apply()
                    startActivity(intencion)

                    auth.signOut()
                    finish()
                }
            }
            true
        }
        popup.show()
    }
}