package uv.tc.timefastmovil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.timefastmovil.databinding.ActivityMisEnviosBinding

class MisEnviosActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMisEnviosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMisEnviosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}