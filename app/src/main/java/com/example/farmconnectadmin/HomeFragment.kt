package com.example.farmconnectadmin

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.farmconnectadmin.adapter.AdapterProduct
import com.example.farmconnectadmin.adapter.CategoriesAdapter
import com.example.farmconnectadmin.databinding.EditProductLayoutBinding
import com.example.farmconnectadmin.databinding.FragmentHomeBinding
import com.example.farmconnectadmin.viewmodel.adminViewModel
import kotlinx.coroutines.launch



class HomeFragment : Fragment() {
    val viewModel: adminViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setStatusBarColor()
        setCategories()

        searchProduct()
        getAlltheProducts("All")
        return binding.root
    }

    private fun searchProduct() {
        binding.searchEt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query =s.toString().trim()
                adapterProduct.filter.filter(query)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun getAlltheProducts(category: String) {
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect { products ->
                if (products.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct(::onEditButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(products)
                adapterProduct.originalList= products as ArrayList<Product>
            }
        }
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

    private fun setCategories() {
        val categoryList = ArrayList<Categories>()
        for (i in com.example.farmconnectadmin.Constants.AllProductsCategory.indices) {
            categoryList.add(
                Categories(
                    com.example.farmconnectadmin.Constants.AllProductsTypes[i],
                    com.example.farmconnectadmin.Constants.AllProductCategoryIcon[i]
                )
            )
        }
        binding.rvCategories.adapter = CategoriesAdapter(categoryList, ::onCategoryClicked)
    }

    private fun onCategoryClicked(categories: Categories) {
        getAlltheProducts(categories.category)
    }

    private fun onEditButtonClicked(product: Product) {
        // Inflate the EditProductLayoutBinding
        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        // Set the current values to the EditText fields
        editProduct.apply {
            etProductTitle.setText(product.productTitle)
            etProductQuantity.setText(product.productQuantity.toString())
            etProductUnit.setText(product.productUnit)
            etProductPrice.setText(product.productPrice.toString())
            etNumberOfStocks.setText(product.numberOfStocks.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }


        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        setAutoCompleteTextViews(editProduct)
        editProduct.btnSave.setOnClickListener{

            lifecycleScope.launch{

            product.productTitle=editProduct.etProductTitle.text.toString()
            product.productQuantity=editProduct.etProductQuantity.text.toString().toInt()
            product.productUnit=editProduct.etProductUnit.text.toString()
            product.productPrice=editProduct.etProductPrice.text.toString().toInt()
            product.numberOfStocks=editProduct.etNumberOfStocks.text.toString().toInt()
            product.productCategory=editProduct.etProductCategory.text.toString()
            product.productType=editProduct.etProductType.text.toString()
            viewModel.savingUpdateProducts(product)}
            Utils.showToast(requireContext(),"Saved Changes")
            alertDialog.dismiss()
        }

        // Handle the Edit button click
        editProduct.btnEdit.setOnClickListener {
            editProduct.apply {
                // Enable all fields for editing
                etProductTitle.isEnabled = true
                etProductQuantity.isEnabled = true
                etProductUnit.isEnabled = true
                etProductPrice.isEnabled = true
                etNumberOfStocks.isEnabled = true
                etProductCategory.isEnabled = true
                etProductType.isEnabled = true
            }
        }
    }

    private fun setAutoCompleteTextViews(editProduct: EditProductLayoutBinding) {
        val unitAdapter = ArrayAdapter(
            requireContext(),
            R.layout.show_list,
            com.example.farmconnectadmin.Constants.unitCategory
        )
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            R.layout.show_list,
            com.example.farmconnectadmin.Constants.AllProductsCategory
        )
        val productTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.show_list,
            com.example.farmconnectadmin.Constants.AllProductsTypes
        )

        editProduct.apply {
            etProductUnit.setAdapter(unitAdapter)
            etProductCategory.setAdapter(categoryAdapter)
            etProductType.setAdapter(productTypeAdapter)
        }
    }
}





