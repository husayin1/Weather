package com.example.weather.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val textView: TextView = binding.textSlideshow
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return binding.root
    }

}