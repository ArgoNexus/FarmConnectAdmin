package com.example.farmconnectadmin.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.farmconnectadmin.Product
import com.example.farmconnectadmin.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class adminViewModel: ViewModel() {

    private val _isImageUploaded = MutableStateFlow(false)
    var isImageUploaded: StateFlow<Boolean> = _isImageUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    val downloadedUrls: StateFlow<ArrayList<String?>> = _downloadedUrls

    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved

    fun saveImageInDB(imageUri: ArrayList<Uri>, onComplete: () -> Unit) {
        val downloadUrls = ArrayList<String?>()
        _isImageUploaded.value = false  // Reset state

        imageUri.forEach { uri ->
            val imageRef = FirebaseStorage.getInstance().reference.child(Utils.getCurrentUserId())
                .child("images/${uri.lastPathSegment ?: "image_${System.currentTimeMillis()}"}")
                .child(UUID.randomUUID().toString())

            imageRef.putFile(uri).continueWithTask { task ->
                if (task.isSuccessful) {
                    imageRef.downloadUrl
                } else {
                    throw task.exception ?: Exception("Unknown error")
                }
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result.toString()
                    downloadUrls.add(url)

                    if (downloadUrls.size == imageUri.size) {
                        _downloadedUrls.value = downloadUrls
                        _isImageUploaded.value = true
                        onComplete()  // Notify completion
                    }
                } else {
                    _isImageUploaded.value = false
                    onComplete()  // Notify completion, even if it fails
                }
            }
        }
    }

    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admin")
            .child("AllProducts/${product.productRandomId}")
            .setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admin").child("ProductCategory/${product.productCategory}//${product.productRandomId}").setValue(product)
                FirebaseDatabase.getInstance().getReference("Admin").child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)
                        _isProductSaved.value = true
                    }
                }







    fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()

                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category=="All"||prod?.productCategory==category)
                        products.add(prod!!)
                    }

                trySend(products).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately
                close(error.toException())
            }
        }

        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun savingUpdateProducts(product: Product){
        FirebaseDatabase.getInstance().getReference("Admin").child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductCategory/${product.productCategory}//${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admin").child("ProductType/${product.productType}/${product.productRandomId}").setValue(product)


    }
}

