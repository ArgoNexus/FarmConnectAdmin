package com.example.farmconnectadmin

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.farmconnectadmin.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {
    private var dialog: AlertDialog? = null

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showDialog(context: Context, message: String) {
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.Message.text = message
        dialog = AlertDialog.Builder(context)
            .setView(progress.root)
            .setCancelable(false)
            .create()
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    private var auth: FirebaseAuth? = null
    fun getFirebaseAuthInstance(): FirebaseAuth {
        if (auth == null) {
            auth = FirebaseAuth.getInstance()
        }
        return auth!!

    }
    fun getCurrentUserId(): String{
        return getFirebaseAuthInstance().currentUser!!.uid

    }
    fun getRandomId():String{
        return (1..25)
            .map { ('A'..'Z').random() }  // Generates a random uppercase letter from A to Z
            .joinToString("")

    }
}