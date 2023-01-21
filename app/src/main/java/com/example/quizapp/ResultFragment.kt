package com.example.quizapp

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.quizapp.databinding.FragmentResultBinding
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentResultBinding
    private lateinit var correctAns:String
    lateinit var bundle: Bundle

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
        // Inflate the layout for this fragment
        binding = FragmentResultBinding.inflate(layoutInflater,container,false)
        bundle = requireArguments()
        correctAns = bundle.getString("correctAns").toString()
        val mediaPlayer=MediaPlayer.create(binding.root.context,R.raw.celebration_sound)

        if(correctAns.toInt()>6)
        {
            binding.lottieAnim.visibility=View.VISIBLE
            mediaPlayer.start()
            binding.lottieAnim.playAnimation()
        }
        setupData()
        return binding.root
    }

    private fun setupData() {
        val ans=correctAns.toInt()
        val score=ans*10
        val complete=(ans*100)/10
        val wrong=10-ans
        binding.textScore.text= "$score"
        binding.textComplete.text="$complete%"
        binding.textCorrect.text=ans.toString()
        binding.textWrong.text=wrong.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHome.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_resultFragment_to_mainFragment)
        }
        binding.btnShare.setOnClickListener {
            val screenshot = viewShot(binding.resultLayout)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.type = "image/jpeg"
            val bytes = ByteArrayOutputStream()
            screenshot!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(requireActivity().contentResolver, screenshot, "Title", null)
            val imageUri = Uri.parse(path)
            intent.putExtra(Intent.EXTRA_STREAM, imageUri)
            startActivity(Intent.createChooser(intent, "Select"))
        }

        binding.btnReplay.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_resultFragment_to_testFragment,bundle)
        }

        binding.lottieAnim.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
            }
            override fun onAnimationEnd(animation: Animator) {
                binding.lottieAnim.visibility=View.INVISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        // Back Button Handle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_resultFragment_to_mainFragment)
            }
        })
    }
    private fun viewShot(v: View): Bitmap? {
        val height = v.height
        val width = v.width
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.draw(c)
        return b
    }
}