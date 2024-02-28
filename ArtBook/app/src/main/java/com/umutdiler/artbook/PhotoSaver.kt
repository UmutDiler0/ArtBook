package com.umutdiler.artboo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.umutdiler.artbook.ArtInfo
import com.umutdiler.artbook.MainActivity
import com.umutdiler.artbook.databinding.ActivityPhotoSaverBinding
import java.io.ByteArrayOutputStream

class PhotoSaver : AppCompatActivity() {

    lateinit var binding: ActivityPhotoSaverBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoSaverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()


    }

    fun save(view: View) {

        val artName = binding.photoName.text.toString()
        val artYear = binding.photoYear.text.toString()
        val artistName = binding.artistName.text.toString()

        if(selectedBitmap != null){
            val smallBitmap = smallerBitmap(selectedBitmap!!,300)

            val outPutStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outPutStream)
            val byteArray = outPutStream.toByteArray()
            try{
                val dataBase =this.openOrCreateDatabase("ArtInfo", Context.MODE_PRIVATE,null)
                dataBase.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artName VARCHAR,artYear String,artistName VARCHAR,image BLOB)")

                val sqlString = "INSERT INTO arts(artName,artYear,artistName,image) VALUES(?,?,?,?)"
                val statement = dataBase.compileStatement(sqlString)
                statement.bindString(1,artName)
                statement.bindString(2,artYear)
                statement.bindString(3,artistName)
                statement.bindBlob(4,byteArray)
                statement.execute()


            }catch(e : Exception){
                e.printStackTrace()
            }
            val intent = Intent(this@PhotoSaver,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }


    }

    fun select(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                            }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission",
                            View.OnClickListener {
                                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }).show()
                } else {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        }

    }
    fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    println("ok5")
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intent.data
                        //binding.imageView.setImageURI(imageData)
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val source = ImageDecoder.createSource(
                                        this@PhotoSaver.contentResolver,
                                        imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        contentResolver,
                                        imageData
                                    )
                                    binding.imageView.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

                if (result) {
                    //permission granted
                    val intentGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentGallery)
                } else {
                    //permission denied
                    Toast.makeText(this@PhotoSaver, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }


            }
    }
    fun smallerBitmap(image : Bitmap,maxSize : Int):Bitmap{
        var width = image.width
        var height = image.height
        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if(bitmapRatio > 1){
            width = maxSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()

        }else{
            height = maxSize
            val scaledWidth = height / bitmapRatio
            width = scaledWidth.toInt()

        }

        return Bitmap.createScaledBitmap(image,width,height,true)
    }
}