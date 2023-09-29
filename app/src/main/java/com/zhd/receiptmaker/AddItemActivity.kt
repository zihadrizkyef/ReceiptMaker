package com.zhd.receiptmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhd.receiptmaker.databinding.ActivityAddItemBinding

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSave.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name", binding.inputName.text.toString())
            intent.putExtra("price", binding.inputPrice.text.toString())
            intent.putExtra("qty", binding.inputQty.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}