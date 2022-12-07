package com.runner.tabledeanuncioskotlin.accounthelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.runner.tabledeanuncioskotlin.MainActivity
import com.runner.tabledeanuncioskotlin.R
import com.runner.tabledeanuncioskotlin.constants.FirebaseAuthConstants
import com.runner.tabledeanuncioskotlin.dialoghelper.GoogleAccConst

class AccountHelper(private val act: MainActivity) {
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    performanceCheck(task, email, password)
                }
        }
    }

    private fun performanceCheck(
        task: Task<AuthResult>,
        email: String,
        password: String
    ) {
        if (task.isSuccessful) {
            sendEmailVerify(task.result?.user!!)
            act.uiUpdate(task.result?.user)
        } else {

            var exception: Exception
            Log.d("MyLog", "Exception: + ${task.exception}")
            when (task.exception) {
                is FirebaseAuthUserCollisionException -> {
                    exception = task.exception as FirebaseAuthUserCollisionException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                        Toast.makeText(
                            act,
                            "Акаун вже існує!",
                            Toast.LENGTH_SHORT
                        ).show()
                        linkEmailToGoogle(email, password)
                    }
                }
                is FirebaseAuthWeakPasswordException -> {
                    exception = task.exception as FirebaseAuthWeakPasswordException
                    if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                        Toast.makeText(
                            act,
                            "Слабкй пароль! Використовуйте пароль з мінімум 6 символами",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    exception =
                        task.exception as FirebaseAuthInvalidCredentialsException

                    if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                        Toast.makeText(
                            act,
                            "Не правильний формат Email",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }

    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        act.uiUpdate(task.result?.user)
                    } else {

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Log.d("MyLog", "Exception: + ${task.exception}")
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException

                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    act,
                                    "Не правильний Email або формат Email",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            } else {
                                if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                                    Toast.makeText(
                                        act,
                                        "Не віриний пароль або Email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }

                }
        }
    }

    private fun sendEmailVerify(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verify_done),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d("MyLog", "Send Email Exception: + ${task.exception}")
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verify_error),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.myAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign In Done", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            } else {
                Log.d("MyLog", "Google Sign In Exception: + ${task.exception}")
            }
        }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.myAuth.currentUser != null) {

            act.myAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            act,
                            act.resources.getString(R.string.link_email_to_google),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                act,
                "Увійдіть в акаунт або викорастейте вхід за допомогою Google",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}