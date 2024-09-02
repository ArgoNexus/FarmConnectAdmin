package com.example.farmconnectadmin

import android.widget.Filter
import com.example.farmconnectadmin.adapter.AdapterProduct
import java.util.Locale
import com.example.farmconnectadmin.Product

class FilteringProducts(
    private val adapter: AdapterProduct,
    val filter: ArrayList<Product>
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val result = FilterResults()

        if (!constraint.isNullOrEmpty()) {
            val filteredLists = ArrayList<Product>()
            val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")

            for (products in filter) {
                if (query.any {
                        products.productTitle?.uppercase(Locale.getDefault())
                            ?.contains(it) == true ||
                                products.productCategory?.uppercase(Locale.getDefault())
                                    ?.contains(it) == true ||
                                products.productPrice?.toString()?.uppercase(Locale.getDefault())
                                    ?.contains(it) == true ||
                                products.productType?.uppercase(Locale.getDefault())
                                    ?.contains(it) == true


                    }) {
                    filteredLists.add(products)
                }
                }

                result.values = filteredLists
                result.count = filteredLists.size
            }
        else{result.values=filter
            result.count=filter.size}

            return result
        }

        override fun publishResults(p0: CharSequence?, result: FilterResults?) {
            adapter.differ.submitList(result?.values as ArrayList<Product>)
        }
    }




