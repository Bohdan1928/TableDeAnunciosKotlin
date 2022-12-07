package com.runner.tabledeanuncioskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRegistrar
import com.google.firebase.auth.FirebaseUser
import com.runner.tabledeanuncioskotlin.databinding.ActivityMainBinding
import com.runner.tabledeanuncioskotlin.dialoghelper.DialogConst
import com.runner.tabledeanuncioskotlin.dialoghelper.DialogHelper
import com.runner.tabledeanuncioskotlin.dialoghelper.GoogleAccConst

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var tvAccount: TextView
    private lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val myAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            }catch (e: ApiException){
                Log.d("MyLog", "API error: ${e.message}")
            }
/*
            Log.d("MyLog", "Sign in result")
*/
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        val toggle =
            ActionBarDrawerToggle(
                this,
                rootElement.drawerLayout,
                rootElement.mainContent.toolbar,
                R.string.ac_open,
                R.string.ac_close
            )
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)

        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ad_my_ads -> {
                Toast.makeText(this, "Presed ad_my_ads", Toast.LENGTH_LONG).show()
            }
            R.id.ad_car -> {
                Toast.makeText(this, "Presed ad_car", Toast.LENGTH_LONG).show()
            }
            R.id.ad_pc -> {
                Toast.makeText(this, "Presed ad_pc", Toast.LENGTH_LONG).show()
            }
            R.id.ad_dm -> {
                Toast.makeText(this, "Presed ad_dm", Toast.LENGTH_LONG).show()
            }
            R.id.ad_smart -> {
                Toast.makeText(this, "Presed ad_smart", Toast.LENGTH_LONG).show()
            }
            R.id.sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.sign_out -> {
                uiUpdate(null)
                myAuth.signOut()
            }
            R.id.sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?){

        tvAccount.text = if (user == null){
            resources.getString(R.string.not_reg)
        }else{
            user.email
        }
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }
}