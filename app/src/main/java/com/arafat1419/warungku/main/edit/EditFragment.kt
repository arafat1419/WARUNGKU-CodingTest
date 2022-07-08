package com.arafat1419.warungku.main.edit

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arafat1419.warungku.assets.R
import com.arafat1419.warungku.core.domain.model.WarungDomain
import com.arafat1419.warungku.core.vo.Resource
import com.arafat1419.warungku.databinding.FragmentEditBinding
import com.arafat1419.warungku.main.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()

    private val args: EditFragmentArgs by navArgs()

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private lateinit var mLocationRequest: LocationRequest

    private var uriImage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.warungDomain != null) {
            setData(args.warungDomain)
            activity?.title = "Edit Warung"
        } else {
            activity?.title = "Tambah Warung"
        }

        with(binding) {
            btnCoordinate.setOnClickListener {
                getLocation()
            }
            imgPhoto.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                    .setTitle("Choose a profile image")
                    .setItems(arrayOf("Take photo", "Choose from gallery")) { _, index ->
                        when (index) {
                            0 -> cameraLauncher.launch(null)
                            1 -> galleryLauncher.launch("image/*")
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }
                builder.create()
                builder.show()
            }
            btnSubmit.setOnClickListener {
                if (checkEditText()) {
                    if (uriImage == args.warungDomain?.photoUrl?.toUri()) {
                        saveWarung(args.warungDomain?.photoUrl?.toUri())
                    } else {
                        uploadImage()
                    }
                }
            }
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            val uri = getImageUriFromBitmap(bitmap)
            uriImage = uri

            try {
                binding.imgPhoto.setImageURI(uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uriImage = uri

            try {
                binding.imgPhoto.setImageURI(uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var granted = true
        permissions.entries.forEach {
            if (!it.value) {
                granted = false
            }
        }
        if (granted) {
            createLocationRequest()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
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

            uriImage = warungDomain?.photoUrl?.toUri()
        }
    }

    private fun uploadImage() {
        viewModel.uploadWarungImage(uriImage!!, binding.edtName.text.toString().trim())
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        saveWarung(result.data)
                    }
                    is Resource.Failure -> Toast.makeText(
                        context,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveWarung(uri: Uri?) {
        with(binding) {
            viewModel.isNameAvailable(args.warungDomain?.id, edtName.text.toString().trim())
                .observe(viewLifecycleOwner) { checkResult ->
                    when (checkResult) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            if (checkResult.data) {
                                val splitGeoString = edtCoordinate.text.toString().trim().split(",")
                                val warungDomain = WarungDomain(
                                    id = args.warungDomain?.id,
                                    photoUrl = uri.toString(),
                                    name = edtName.text.toString().trim(),
                                    lat = splitGeoString[0].toDouble(),
                                    long = splitGeoString[1].toDouble(),
                                    address = edtAddress.text.toString().trim()
                                )

                                viewModel.saveWarung(warungDomain)
                                    .observe(viewLifecycleOwner) { result ->
                                        when (result) {
                                            is Resource.Loading -> binding.progressBar.visibility =
                                                View.VISIBLE
                                            is Resource.Success -> {
                                                binding.progressBar.visibility = View.GONE
                                                findNavController().navigate(
                                                    EditFragmentDirections.actionEditFragmentToHomeFragment()
                                                )
                                            }
                                            is Resource.Failure -> Toast.makeText(
                                                context,
                                                result.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                            } else {
                                Toast.makeText(context, "Name already exists", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        is Resource.Failure -> Toast.makeText(
                            context,
                            checkResult.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isGpsEnable()) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        if (it != null) {
                            val coordinateFormat = getString(R.string.coordinate_format)
                            binding.edtCoordinate.setText(
                                String.format(
                                    coordinateFormat,
                                    it.latitude,
                                    it.longitude
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Open google maps first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                gpsDialog()
            }
        } else {
            locationPermissionRequest.launch(LOCATION_PERMISSION_REQUEST)
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()

        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isGpsEnable(): Boolean {
        val manager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun gpsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle("GPS Configuration")
            .setMessage("Turn On")
            .setPositiveButton("Confirm") { _, _ ->
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also {
                    startActivity(it)
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { _, _ ->
                Toast.makeText(
                    context,
                    "For better functionality turn on your GPS",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap?): Uri? {
        var tempDir = context?.getExternalFilesDir(null)
        tempDir = File(tempDir?.absolutePath + "/.temp/")
        tempDir.mkdir()

        val tempFile = File.createTempFile("IMG_TEMP_", ".jpg", tempDir)
        val bytes = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        FileOutputStream(tempFile).apply {
            write(bitmapData)
            flush()
            close()
        }
        return Uri.fromFile(tempFile)

    }

    private fun checkEditText(): Boolean {
        var check: Boolean
        with(binding) {
            val isNullOrEmpty =
                edtName.text.isNullOrEmpty() || edtAddress.text.isNullOrEmpty() || edtCoordinate.text.isNullOrEmpty()
            val isImageEmpty = uriImage == null
            check = when {
                isNullOrEmpty -> {
                    Toast.makeText(context, "Field cannot be empty", Toast.LENGTH_SHORT).show()
                    false
                }
                isImageEmpty -> {
                    Toast.makeText(context, "Image cannot be empty", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> true
            }
        }
        return check
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2

        private val LOCATION_PERMISSION_REQUEST = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}