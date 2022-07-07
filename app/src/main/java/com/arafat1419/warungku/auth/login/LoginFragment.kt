package com.arafat1419.warungku.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arafat1419.warungku.auth.AuthViewModel
import com.arafat1419.warungku.core.vo.Resource
import com.arafat1419.warungku.databinding.FragmentLoginBinding
import com.arafat1419.warungku.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnRegister.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }

            btnLogin.setOnClickListener {
                if (checkEditText()) {
                    viewModel.signInUser(edtUsername.text.toString(), edtPassword.text.toString())
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is Resource.Loading -> btnLogin.isEnabled = false
                                is Resource.Success -> {
                                    Intent(context, MainActivity::class.java).also {
                                        startActivity(it)
                                        activity?.finish()
                                    }
                                }
                                is Resource.Failure -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                    btnLogin.isEnabled = true
                                }
                            }
                        }
                }
            }
        }

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