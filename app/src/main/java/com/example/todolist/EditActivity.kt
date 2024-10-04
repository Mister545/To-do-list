package com.example.todolist

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.EditActivityBinding
import com.example.todolist.db.MyDbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EditActivity : AppCompatActivity() {
    val imageRequestCode = 10
    var tempImageUri = "empty"
    var id = 0
    var isEditState = false
    val myDbManager = MyDbManager(this)
    private lateinit var binding: EditActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        this.contentResolver.takePersistableUriPermission(
        data?.data!!,
        Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode){
            binding.imMain.setImageURI(data.data)
            tempImageUri = data.data.toString()
        }
    }

    fun onClickAddImage(view: View) {

        binding.imButtonEditImage.visibility = View.VISIBLE
        binding.imButtonDeleteImage.visibility = View.VISIBLE
        binding.mainImageLayout.visibility = View.VISIBLE
        binding.fbAddImage.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {

        binding.mainImageLayout.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        tempImageUri = "empty"
    }
    fun onClickChooseImage(view: View) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, imageRequestCode)
    }

    fun onClickSave(view: View) {



        val myTitle = binding.edTitle.text.toString()
        val myDesk = binding.edDesc.text.toString()

        if (myDesk != "" && myTitle != ""){

            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState){
                    myDbManager.updateItem(myTitle, myDesk, tempImageUri, id, getCurrentTime())
                }else {
                    myDbManager.insertToDb(myTitle, myDesk, tempImageUri, getCurrentTime())
                }
                finish()
            }
        }
    }

    fun getMyIntents() {
        binding.fbEdit.visibility = View.GONE
        val i = intent
        if (i != null) {
            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {

                binding.fbAddImage.visibility = View.GONE

                binding.edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                binding.edDesc.setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))
                isEditState = true
                binding.fbEdit.visibility = View.VISIBLE
                binding.edTitle.isEnabled = false
                binding.edDesc.isEnabled = false
                id = (i.getIntExtra(MyIntentConstants.I_ID, 0))

                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {
                    binding.mainImageLayout.visibility = View.VISIBLE
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!
                    binding.imMain.setImageURI(Uri.parse(tempImageUri))
                    binding.imButtonDeleteImage.visibility = View.GONE
                    binding.imButtonEditImage.visibility = View.GONE
                }
            }
        }
    }

    fun onEditEnable(view: View) {
        binding.fbEdit.visibility = View.GONE
        binding.fbAddImage.visibility = View.VISIBLE
        binding.edTitle.isEnabled = true
        binding.edDesc.isEnabled = true
        if (tempImageUri == "empty"){
            binding.imButtonEditImage.visibility = View.VISIBLE
            binding.imButtonDeleteImage.visibility = View.VISIBLE
        }
    }

    private fun getCurrentTime(): String{
        val time = Calendar.getInstance().time
        val format = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        val fTime = format.format(time)
        return fTime.toString()
    }
}