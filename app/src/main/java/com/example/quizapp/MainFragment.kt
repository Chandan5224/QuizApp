package com.example.quizapp

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.quizapp.databinding.FragmentMainBinding
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class MainFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: FragmentMainBinding
    var finalCat: String = ""
    var finalDiff: String = ""
    private val diff = arrayOf("Any Difficulty", "Easy", "Medium", "Hard")
    private val cat = arrayOf(
        "Any Category",
        "General Knowledge",
        "Books",
        "Film",
        "Music",
        "Musicals & Theatres",
        "Television",
        "Video Games",
        "Board Games",
        "Science & Nature",
        "Computers",
        "Mathematics",
        "Methodology",
        "Sports",
        "Geography",
        "History",
        "Politics",
        "Art",
        "Celebrities",
        "Animals",
        "Vehicles",
        "Comics",
        "Gadgets",
        "Japanese Anime & Manga",
        "Cartoon & Animations"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        val logIn = sharedPreferences.getBoolean("logIn", false)

        if (!logIn) {
            onCreateDialog()
        } else {
            setData()
        }

//         Inflate the layout for this fragment
//                check Internet
        if (!checkForInternet(binding.root.context)) {
            showToast("Connect To The Internet")
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            if (checkForInternet(binding.root.context)) {
                // Getting data
                val bundle = Bundle()
                bundle.putString("cat", finalCat)
                bundle.putString("diff", finalDiff)
                Navigation.findNavController(view)
                    .navigate(R.id.action_mainFragment_to_testFragment, bundle)
            } else
                showToast("Connect To The Internet !")
        }


        binding.lottieAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                binding.lottieAnim2.visibility = View.VISIBLE
                binding.lottieAnim2.playAnimation()
                binding.lottieAnim2.loop(true)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })

        /// Finish Main Activity
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        val adapterCat: ArrayAdapter<String> =
            ArrayAdapter(binding.root.context, android.R.layout.simple_dropdown_item_1line, cat)
        with(binding.spinnerCategory) {
            adapter = adapterCat
//            setSelection(0, false)
            onItemSelectedListener = this@MainFragment
            gravity = Gravity.CENTER
        }

        val adapterDiff: ArrayAdapter<String> =
            ArrayAdapter(binding.root.context, android.R.layout.simple_dropdown_item_1line, diff)
        with(binding.spinnerDifficulty) {
            adapter = adapterDiff
//            setSelection(0, false)
            onItemSelectedListener = this@MainFragment
            gravity = Gravity.CENTER
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.id == binding.spinnerCategory.id) {
            finalCat = (position + 8).toString()
        } else {
            when (position) {
                0 -> finalDiff = "any"
                1 -> finalDiff = "easy"
                2 -> finalDiff = "medium"
                3 -> finalDiff = "hard"
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        showToast("Nothing is selected")
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            return true
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun onCreateDialog() {
        val dialog = Dialog(binding.root.context, com.airbnb.lottie.R.style.Theme_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dialog);
        dialog.setCanceledOnTouchOutside(false);

        val userN = dialog.findViewById<TextView>(R.id.popupUserName)
        val image = dialog.findViewById<ImageButton>(R.id.imageUpload)
        var uri: Uri = Uri.parse("")
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), ActivityResultCallback {
                uri = it!!
                image.setImageURI(it)
            }
        )
        image.setOnClickListener {
            getImage.launch("image/*")
        }
        dialog.findViewById<MaterialButton>(R.id.saveBtn).setOnClickListener {
            if (userN.text.isNotEmpty()) {
                var name = userN.text.toString()[0].toString().uppercase()
                for (i in 1 until userN.text.length) {
                    if (userN.text.toString()[i] != ' ')
                        name += userN.text.toString()[i].toString()
                    else
                        break
                }
                writeTextData("$name $uri")
                binding.userName.text = "Hello, $name"
//                binding.userImage.setImageURI(uri)
            }
            sharedPreferences.edit().putBoolean("logIn", true).apply()
            dialog.cancel()
        }
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.show();
    }



    private fun writeTextData(data: String) {
        // Creating folder with name GeeksForGeeks
        val folder: File? = requireActivity().getExternalFilesDir("QuizApp")

        // Creating file with name gfg.txt
        val file = File(folder, "userData.txt")
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray())
//            Toast.makeText(context, "Done" + file.absolutePath, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setData() {

//         GeeksForGeeks represent the folder name to access privately saved data
        val folder: File? = requireActivity().getExternalFilesDir("QuizApp")

        // gft.txt is the file that is saved privately
        val file = File(folder, "userData.txt")
        val data = getData(file)
        for (i in 0 until data!!.length) {
            if (data[i] == ' ') {
//                binding.userImage.setImageURI(Uri.parse(data.subSequence(i+1, data.length).toString()))
                binding.userName.text= "Hello, ${data.subSequence(0,i)}"
            }
        }
    }

    // getData() is the method which reads the data
    // the data that is saved in byte format in the file
    private fun getData(myFile: File): String? {
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(myFile)
            var i = -1
            val buffer = StringBuffer()
            while (fileInputStream.read().also { i = it } != -1) {
                buffer.append(i.toChar())
            }
            return buffer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }


    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}

