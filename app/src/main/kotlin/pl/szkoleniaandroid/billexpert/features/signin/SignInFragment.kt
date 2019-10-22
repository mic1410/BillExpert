package pl.szkoleniaandroid.billexpert.features.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.szkoleniaandroid.billexpert.databinding.ActivityLoginBinding

class SignInFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ActivityLoginBinding.inflate(inflater, container, false).apply {
            vm = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer {
            when (it) {
                is LoginInProgress -> {
                }
                is LoginError -> {
                    if (!it.error.consumed) {
                        Toast.makeText(requireContext(), it.error.consume(), Toast.LENGTH_LONG).show()
                    }
                }
                is LoginSuccessful -> goToBills()
            }
        })
    }

    private fun goToBills() {
        findNavController().navigate(SignInFragmentDirections.navSignedIn())
    }
}



