package com.example.farmconnectadmin

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.farmconnectadmin.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()

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
    private fun getUserNumber() {
        binding.UserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                number: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                val len = number?.length
                if (len == 10) {
                    binding.ContinueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    binding.ContinueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Gray_blue))
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Optional: Implement if needed
            }

            override fun afterTextChanged(s: Editable?) {
                // Optional: Implement if needed
            }
        })
    }
    private fun onContinueButtonClick() {
        binding.ContinueBtn.setOnClickListener {
            val number = binding.UserNumber.text.toString()
            if (number.isEmpty() || number.length != 10) {
                Utils.showToast(requireContext(),"Please enter a valid number")  // Changed method to standard Toast
            } else {
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(
                    R.id.action_signInFragment_to_OTPFragment,
                    bundle
                )
            }
        }
    }
}