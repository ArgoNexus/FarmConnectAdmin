package com.example.farmconnectadmin

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.farmconnectadmin.adapter.AdapterSelectedImage
import com.example.farmconnectadmin.databinding.FragmentAddProductBinding
import com.example.farmconnectadmin.viewmodel.adminViewModel
import kotlinx.coroutines.launch


class addProductFragment : Fragment() {
    private val viewModel: adminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val selectedImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
        val fiveImages = listOfUri.take(5)
        imageUris.clear()
        imageUris.addAll(fiveImages)
        binding.rvProductImages.adapter = AdapterSelectedImage(ArrayList(imageUris))
    }
    private val imageUris: ArrayList<Uri> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        setStatusBarColor()
        setAutoCompeteTextViews()
        onImageSelectClicked()
        onAddButtonClicked()

        // Collecting the state flow here
        return binding.root
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)
            this.statusBarColor = statusBarColor

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun setAutoCompeteTextViews() {
        val unit = ArrayAdapter(requireContext(), R.layout.show_list, com.example.farmconnectadmin.Constants.unitCategory)
        val category=ArrayAdapter(requireContext(),R.layout.show_list,
            com.example.farmconnectadmin.Constants.AllProductsCategory)
        val productType=ArrayAdapter(requireContext(),R.layout.show_list,
            com.example.farmconnectadmin.Constants.AllProductsTypes)
        binding.apply {
            ProductUnit.setAdapter(unit)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }

    }

    private fun onImageSelectClicked() {
        binding.ProductImage.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun onAddButtonClicked() {
        binding.addProductButton.setOnClickListener {
            Utils.showDialog(requireContext(), "Adding Product")
            val productName = binding.ProductTitle.text.toString()
            val productQuantity = binding.ProductQuantity.text.toString()
            val productUnit = binding.ProductUnit.text.toString()
            val productPrice = binding.ProductPrice.text.toString()
            val numberOfStocks = binding.NumberOfStocks.text.toString()
            val productCategory = binding.etProductCategory.text.toString()

            if (productName.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty() || productPrice.isEmpty() || numberOfStocks.isEmpty()) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Please fill all the fields")
            } else if (imageUris.isEmpty()) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Please select at least one image")
            } else {
                val product = Product(
                    productTitle = productName,
                    productQuantity = productQuantity.toInt(),
                    productCategory = productCategory,
                    productUnit = productUnit,
                    productPrice = productPrice.toInt(),
                    numberOfStocks = numberOfStocks.toInt(),
                    itemCount = 0,
                    adminUid = Utils.getCurrentUserId(),
                    productRandomId = Utils.getRandomId()

                )
                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        viewModel.saveImageInDB(imageUris) {
        lifecycleScope.launch {
            viewModel.isImageUploaded.collect {
                if (it) {
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext(), "Product Added Successfully")
                    }
                    getUrls(product)
                }}}}
        }

    private fun getUrls(product: Product) {
        Utils.showDialog(requireContext(),"Publishing Product...")
        lifecycleScope.launch{
            viewModel.downloadedUrls.collect {
                val urls = it
                product.productImageUris=urls
                saveProduct(product)
            }
        }
    }
    private fun saveProduct(product: Product){
        viewModel.saveProduct(product)
        lifecycleScope.launch{
        viewModel.isProductSaved.collect{
            if(it) {
                Utils.hideDialog()
                startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                Utils.showToast(requireContext(), "Your Product is Live")
            }
            }
        }


    }
}

