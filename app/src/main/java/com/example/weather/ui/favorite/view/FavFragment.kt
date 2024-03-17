package com.example.weather.ui.favorite.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.model.response.SavedLocations
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.ui.favorite.viewmodel.FavoriteViewModel
import com.example.weather.ui.favorite.viewmodel.FavoritesViewModelFactory
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.NetworkManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavFragment : Fragment() {

    private val TAG = "FavFragment"
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = FavoritesViewModelFactory(MyApplication.getRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteAdapter = FavoriteAdapter{
            if(NetworkManager.isInternetConnected()){
                viewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLatitude,it.latitude.toString())
                viewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLongitude,it.longitude.toString())
                Log.i(TAG, "onViewCreated: ${it.currentTemp}")
                Log.i(TAG, "onViewCreated: ${it.address}")
                Log.i(TAG, "onViewCreated: ${it.latitude}---${it.longitude}")
                findNavController().navigate(FavFragmentDirections.actionNavFavToNavHome())
                val appBar = requireActivity().findViewById<NavigationView>(R.id.nav_view)
                appBar.setCheckedItem(0)
            }else{
                Toast.makeText(requireContext(),R.string.internet_connection,Toast.LENGTH_SHORT).show()
            }
        }
        activateFab()
        checkUpdates()
        setSwipeBehaviour(
            onSwipe = viewModel::deleteFavLocation,
            onUndo = viewModel::addLocation
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(R.id.nav_fav)
        }
    }

    private fun checkUpdates() {
        /*lifecycleScope.launch {
            viewModel.retrofitStateFavorites.collectLatest {
                when (it) {
                    is RetrofitStateFavorites.Loading -> {
                        binding.scrollViewFavorites.visibility = View.GONE
                        Toast.makeText(context, R.string.loading, Toast.LENGTH_SHORT).show()
                    }

                    is RetrofitStateFavorites.OnSuccess -> {
                        initFrag(it.listSavedLocations)
                        binding.scrollViewFavorites.visibility = View.VISIBLE
                    }

                    is RetrofitStateFavorites.OnFail -> {
                        Toast.makeText(context, R.string.problem, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "checkUpdates: ${it.errorMessage.message}")
                    }
                }
            }
        }*/
        lifecycleScope.launch {
            viewModel.weatherData.collectLatest {
                when(it){
                    is RetrofitStateWeather.OnSuccess->{
                        initFrag(it.weatherResponse)
                        binding.scrollViewFavorites.visibility=View.VISIBLE
                    }
                    is RetrofitStateWeather.Loading->{
                        binding.scrollViewFavorites.visibility=View.GONE
                        Toast.makeText(context,R.string.loading,Toast.LENGTH_SHORT).show()
                    }
                    is RetrofitStateWeather.OnFail->{
                        Toast.makeText(context,R.string.problem,Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "checkUpdates: ${it.errorMessage.message}")
                    }
                }
            }
        }
    }

    private fun activateFab() {
        binding.fab.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                findNavController().navigate(
                    FavFragmentDirections.actionFavoriteFragmentToMapFragment(
                        isFromFavorite = true
                    )
                )
            }else{
                Toast.makeText(context,R.string.internet_connection,Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initFrag(savedLocations: List<SavedLocations>){
        if(savedLocations.size==0){
            binding.scrollViewFavorites.visibility = View.GONE
            binding.noSavedLocations.visibility=View.VISIBLE
            binding.noSavedTxt.visibility=View.VISIBLE
//            startLottieAnimation(binding.noSavedLocations,R.raw.saved_location)
        }else{
            binding.scrollViewFavorites.visibility = View.VISIBLE
            binding.noSavedLocations.visibility=View.GONE
            binding.noSavedTxt.visibility=View.GONE
//            binding.noSavedLocations.pauseAnimation()

            binding.recyclerViewFavorite.apply {
                layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
                adapter=favoriteAdapter
            }
            favoriteAdapter.submitList(savedLocations)
        }
    }
    /*private fun startLottieAnimation(animationView: LottieAnimationView, animationName: Int) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }*/
    private fun setSwipeBehaviour(
        onSwipe: (SavedLocations) -> Unit,
        onUndo: (SavedLocations) -> Unit,
    ) {
        val itemTouchHelperCallback = SwipeHelper(
            requireContext(), favoriteAdapter
        ) { location ->
            onSwipe(location)
            Snackbar.make(requireView(),R.string.deleted,Snackbar.LENGTH_SHORT).setAction(R.string.undo){onUndo(location)}.show()
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerViewFavorite)
        }
    }

}
