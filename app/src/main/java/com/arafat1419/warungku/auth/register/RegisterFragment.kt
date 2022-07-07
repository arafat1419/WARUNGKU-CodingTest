package com.arafat1419.warungku.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arafat1419.warungku.auth.AuthViewModel
import com.arafat1419.warungku.core.vo.Resource
import com.arafat1419.warungku.databinding.FragmentRegisiterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisiterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisiterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLogin.setOnClickListener {
                navigateLogin()
            }

            btnRegister.setOnClickListener {
                if (checkEditText()) {
                    viewModel.signUpUser(edtUsername.text.toString(), edtPassword.text.toString())
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Resource.Loading -> btnRegister.isEnabled = false
                                is Resource.Success -> {
                                    navigateLogin()
                                }
                                is Resource.Failure -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                    btnRegister.isEnabled = true
                                }
                            }
                        }
                }
            }
        }
    }

    private fun navigateLogin() {
        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        )
    }

    private fun checkEditText(): Boolean {
        var check: Boolean
        with(binding) {
            check = when {
                edtUsername.text?.isEmpty() == true -> {
                    Toast.makeText(context, "Email/Phone cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
                edtPassword.text?.isEmpty() == true -> {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                    false
                }
                edtPassword.text?.toString()?.length!! < 6 -> {
                    Toast.makeText(context, "Password minimum 6", Toast.LENGTH_SHORT).show()
                    false
                }
                edtRePassword.text?.toString() != edtPassword.text?.toString() -> {
                    Toast.makeText(context, "Password is not match", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> {
                    true
                }
            }
        }
        return check
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}