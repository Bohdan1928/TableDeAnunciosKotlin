package com.runner.tabledeanuncioskotlin.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.runner.tabledeanuncioskotlin.MainActivity
import com.runner.tabledeanuncioskotlin.R
import com.runner.tabledeanuncioskotlin.accounthelper.AccountHelper
import com.runner.tabledeanuncioskotlin.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity) {
    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)

        builder.setView(rootDialogElement.root)
        setDialogState(index, rootDialogElement)

        val dialog = builder.create()

        rootDialogElement.btnSignUpIn.setOnClickListener {
            setOnClickSingUpIn(index, rootDialogElement, dialog)
        }
        rootDialogElement.btnForgotPassword.setOnClickListener {
            setOnClickResetPassword(rootDialogElement, dialog)
        }
        rootDialogElement.btnGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
        }

        dialog.show()
    }

    private fun setOnClickResetPassword(
        rootDialogElement: SignDialogBinding,
        dialog: AlertDialog?
    ) {

        if (rootDialogElement.edSignEmail.text.isNotEmpty()) {
            act.myAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, R.string.email_reset_password_send, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            dialog?.dismiss()
        } else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSingUpIn(
        index: Int,
        rootDialogElement: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        dialog?.dismiss()
        if (index == DialogConst.SIGN_UP_STATE) {
            accHelper.signUpWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        } else {
            accHelper.signInWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        }
    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if (index == DialogConst.SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.sign_up)
            rootDialogElement.btnSignUpIn.text = act.resources.getString(R.string.sign_up_action)

        } else {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.sign_in)
            rootDialogElement.btnSignUpIn.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.btnForgotPassword.visibility = View.VISIBLE
        }
    }
}