package com.example.weather.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentFavoriteBinding
import com.google.android.material.snackbar.Snackbar

class FavFragment : Fragment() {

    private lateinit var binding:FragmentFavoriteBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favoriteViewModel =
            ViewModelProvider(this).get(FavoriteViewModel::class.java)

        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FavoriteFragment_to_mapFragment)
            /*view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
        }

        val textView: TextView = binding.textGallery
        favoriteViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return binding.root
    }

}