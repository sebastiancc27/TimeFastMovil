package uv.tc.timefastmovil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.timefastmovil.databinding.ActivityEnvioBinding

class EnvioActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEnvioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEnvioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}