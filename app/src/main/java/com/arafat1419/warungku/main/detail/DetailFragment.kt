package com.arafat1419.warungku.main.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arafat1419.warungku.assets.R
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.vo.Resource
import com.arafat1419.warungku.databinding.FragmentDetailBinding
import com.arafat1419.warungku.main.MainViewModel
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Detail Warung"

        if (args.id != null) {
            viewModel.getWarung(args.id!!).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        setData(result.data)
                    }
                    is Resource.Failure -> Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            /*// --

            viewModel.addWarung(WarungDomain(
                args.id,
                "URL",
                "TestNEW2",
                1.0,
                1.0,
                "POSO"
            )).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> Log.d("LHT", "loading: Loading")
                    is Resource.Success -> {
                        Log.d("LHT", "success: ${result.data}")
                    }
                    is Resource.Failure -> Log.d("LHT", "failure: ${result.message}")
                }
            }

            // --*/
        }
    }

    private fun setData(warungDomain: WarungDomain?) {
        with(binding) {
            Glide.with(requireContext())
                .load(warungDomain?.photoUrl)
                .into(imgPhoto)

            edtName.setText(warungDomain?.name)

            val coordinateFormat = getString(R.string.coordinate_format)
            edtCoordinate.setText(
                String.format(
                    coordinateFormat,
                    warungDomain?.lat,
                    warungDomain?.long
                )
            )

            edtAddress.setText(warungDomain?.address)

            fabEdit.setOnClickListener {
                findNavController().navigate(
                    DetailFragmentDirections.actionDetailFragmentToEditFragment(warungDomain)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}