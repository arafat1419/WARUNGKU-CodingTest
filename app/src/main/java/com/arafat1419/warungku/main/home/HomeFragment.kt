package com.arafat1419.warungku.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arafat1419.warungku.auth.AuthActivity
import com.arafat1419.warungku.core.ui.WarungAdapter
import com.arafat1419.warungku.core.vo.Resource
import com.arafat1419.warungku.databinding.FragmentHomeBinding
import com.arafat1419.warungku.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private val warungAdapter: WarungAdapter by lazy { WarungAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Daftar Warung"

        setRecyclerView()

        viewModel.getAllWarung().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    warungAdapter.setWarung(result.data)
                }
                is Resource.Failure -> Toast.makeText(context, result.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        clickHandler()

    }

    private fun clickHandler() {
        warungAdapter.onItemClicked = {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.name)
            )
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToEditFragment(null)
            )
        }

        binding.fabLogOut.setOnClickListener {
            viewModel.signOut()
            viewModel.isUserAuthenticated().observe(viewLifecycleOwner) { isAuthenticated ->
                if (!isAuthenticated) {
                    Intent(context, AuthActivity::class.java).also {
                        startActivity(it)
                        activity?.finish()
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        with(binding) {
            rvWarung.layoutManager = LinearLayoutManager(context)
            rvWarung.adapter = warungAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}