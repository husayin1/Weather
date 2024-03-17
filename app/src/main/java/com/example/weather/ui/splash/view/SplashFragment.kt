package com.example.weather.ui.splash.view

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentSplashBinding
import com.example.weather.ui.splash.viewmodel.SplashViewModel
import com.example.weather.ui.splash.viewmodel.SplashViewModelFactory
import com.example.weather.utilites.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    lateinit var splashViewModel: SplashViewModel
    private lateinit var binding: FragmentSplashBinding
    private val TAG: String = "SPLASH_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        val factory = SplashViewModelFactory(MyApplication.getRepository())
        splashViewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPref()
    }

    private fun checkPref() {
        if (splashViewModel.isNotFirstTime()) {
            Log.i(TAG, "checkPref: if its first time")
            if (splashViewModel.isDark()) {
                Log.i(TAG, "checkPref: check if there is dark mode")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                Log.i(TAG, "checkPref: its light mode")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        startSplash()
    }

    private fun startSplash() {
        val animationView = binding.splashLottie
        animationView.setAnimation(R.raw.splash)
        animationView.playAnimation()

        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.i(TAG, "onAnimationStart: Starting Splash")
            }

            override fun onAnimationEnd(animation: Animator) {
                if (splashViewModel.isNotFirstTime()) {
                    Log.i(TAG, "onAnimationEnd: if not first time go to home")
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToNavHome()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.mobile_navigation, true)
                        .build()

                    if (isAdded)
                        findNavController().navigate(action, navOptions)

                } else {
                    Log.i(TAG, "onAnimationEnd: its first time go to init_app")
                    val action =
                        SplashFragmentDirections.actionSplashFragmentToInitPrefFragment()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.mobile_navigation, true)
                        .build()

                    val toast = Toast.makeText(
                        requireContext(),
                        R.string.internet_connection,
                        Toast.LENGTH_LONG
                    )

                    lifecycleScope.launch(Dispatchers.Main) {
                        while (true) {
                            if (NetworkManager.isInternetConnected()) {
                                toast.cancel()

                                if (isAdded)
                                    findNavController().navigate(action, navOptions)
                                break
                            } else {
                                toast.show()
                            }
                            delay(2000)
                        }
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}