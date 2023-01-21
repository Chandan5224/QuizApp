package com.example.quizapp

import android.animation.Animator
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.quizapp.databinding.FragmentMainBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        "Arts",
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
        // Inflate the layout for this fragment

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
            val bundle = Bundle()
            bundle.putString("cat", finalCat)
            bundle.putString("diff", finalDiff)
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_testFragment, bundle)
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

    private fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }
}


