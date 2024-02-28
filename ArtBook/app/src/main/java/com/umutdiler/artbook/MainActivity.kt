package com.umutdiler.artbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import com.umutdiler.artboo.PhotoSaver
import com.umutdiler.artbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var arrayArt : ArrayList<ArtInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        arrayArt = ArrayList<ArtInfo>()

        try{
            val dataBase = this.openOrCreateDatabase("ArtInfo", MODE_PRIVATE,null)
            val cursor = dataBase.rawQuery("SELECT * FROM arts",null)
            val artNameIx = cursor.getColumnIndex("artNme")
            val idIx = cursor.getColumnIndex("id")

            while(cursor.moveToNext()){
                val name = cursor.getString(artNameIx)
                val id = cursor.getInt(idIx)
                val art = ArtInfo(name,id)

                arrayArt.add(art)
            }
            cursor.close()
        }catch(e : Exception){

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //inflate
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_art_item){
            val intent = Intent(this@MainActivity, PhotoSaver::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}