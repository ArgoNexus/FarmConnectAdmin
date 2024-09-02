package com.example.farmconnectadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.farmconnectadmin.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Build

class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.farmconnectadmin.R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor()

        lifecycleScope.launch {
            delay(3000) // Delay for 3 seconds
            viewModel.isACurrentUser.collect { isCurrentUser ->
                if (isCurrentUser) {
                    startActivity(Intent(requireActivity(), com.example.farmconnectadmin.AdminMainActivity::class.java))
                    requireActivity().finish()
                } else {
                    findNavController().navigate(com.example.farmconnectadmin.R.id.action_splashFragment_to_signInFragment)
                }
            }
        }
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColor = ContextCompat.getColor(requireContext(),
                com.example.farmconnectadmin.R.color.yellow
            )
            this.statusBarColor = statusBarColor

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
