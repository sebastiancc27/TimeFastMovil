package uv.tc.timefastmovil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uv.tc.timefastmovil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun onStart() {
        super.onStart()

    }
}