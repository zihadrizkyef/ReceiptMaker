package com.zhd.receiptmaker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.zhd.receiptmaker.databinding.ActivityMainBinding
import com.zhd.receiptmaker.databinding.DesignResiBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val items = arrayListOf<Item>()
    private val adapter: ItemAdapter by lazy {
        ItemAdapter() {
            items.remove(it)
            adapter.submitList(items)
        }
    }

    private val addItemLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val name = it.data?.getStringExtra("name") ?: ""
                val price = it.data?.getStringExtra("price")
                val qty = it.data?.getStringExtra("qty")
                val item = Item(name, price?.toIntOrNull() ?: 0, qty?.toIntOrNull() ?: 0)
                items.add(item)
                adapter.submitList(items)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter

        binding.buttonAddItem.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            addItemLauncher.launch(intent)
        }

        binding.buttonGenerate.setOnClickListener {
            val bitmap = generateReceiptBitmap()
            val path = saveBitmapToFile(bitmap)
            if (path != null) {
                shareImage(path)
            }
        }
    }

    private fun generateReceiptBitmap(): Bitmap {
        val decimalFormat = DecimalFormat.getNumberInstance()
        val viewBinding = DesignResiBinding.inflate(layoutInflater)
        viewBinding.valueKelas.text = binding.inputClass.text.toString()
        viewBinding.valuePembeli.text = binding.inputName.text.toString()
        viewBinding.recyclerView.adapter = ItemAdapter(null).apply {
            isShowDeleteButton = false
            submitList(items)
        }
        viewBinding.valueTotalPrice.text = decimalFormat.format(items.sumOf { it.price * it.qty })

        val widthInDp = 1000
        val widthInPixels = (widthInDp * resources.displayMetrics.density).toInt()

        viewBinding.root.measure(
            View.MeasureSpec.makeMeasureSpec(widthInPixels, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        viewBinding.root.layout(0, 0, viewBinding.root.measuredWidth, viewBinding.root.measuredHeight)

        val bitmap = Bitmap.createBitmap(viewBinding.root.measuredWidth, viewBinding.root.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        viewBinding.root.draw(canvas)
        return bitmap
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        val filename = "${UUID.randomUUID()}.png"
        val directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val file = File(directory, filename)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun shareImage(imagePath: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/jpeg"
        val imageUri = FileProvider.getUriForFile(this, "$packageName.provider", File(imagePath))
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    private fun openImage(imagePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val imageUri = FileProvider.getUriForFile(this, "$packageName.provider", File(imagePath))
        intent.setDataAndType(imageUri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
}