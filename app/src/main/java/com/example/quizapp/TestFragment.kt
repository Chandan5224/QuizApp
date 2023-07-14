package com.example.quizapp

import android.animation.Animator
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.quizapp.databinding.FragmentTestBinding
import org.json.JSONException
import kotlin.collections.ArrayList
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var cat: String
    private lateinit var diff: String
    lateinit var binding: FragmentTestBinding
    private lateinit var mData: ArrayList<Data>
    private var quesNo: Int = 0
    private var correctAns = 0
    private var correct = ""
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
        binding = FragmentTestBinding.inflate(layoutInflater, container, false)
        bundle = requireArguments()
        cat = bundle.getString("cat").toString()
        diff = bundle.getString("diff").toString()
        mData = ArrayList(10)
        fetchData()
        /// Make view invisible
        binding.skyBack.visibility = View.INVISIBLE
        binding.quesCardView.visibility = View.INVISIBLE
        binding.radioLayout.visibility = View.INVISIBLE
        binding.btnNext.visibility = View.INVISIBLE
        binding.lottieAnim.visibility = View.VISIBLE

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        Next Button Handle
        binding.btnNext.setOnClickListener {
            if (correct == mData[quesNo - 1].correct_answer)
                correctAns++
            if (quesNo == 10) {
                bundle.putString("correctAns", correctAns.toString())
                Navigation.findNavController(view)
                    .navigate(R.id.action_testFragment_to_resultFragment, bundle)
            } else
                loadQues()
        }

//         Back Button Handle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(binding.root.context)

                builder.apply {
                    setTitle(R.string.dialogMessage)
                    setIcon(android.R.drawable.ic_dialog_alert)
                    setPositiveButton("Ok") { dialogInterface, which ->
                        Navigation.findNavController(view)
                            .navigate(R.id.action_testFragment_to_mainFragment)
                    }
                    setNeutralButton("Cancel") { dialogInterface, which ->
                        dialogInterface.cancel()
                    }
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        })

//        Radio Group Handle
        binding.radioGroupContainer.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.optionA -> {
                    if (binding.optionA.text == mData[quesNo - 1].correct_answer)
                        correct = binding.optionA.text.toString()
                }
                R.id.optionB -> {
                    if (binding.optionB.text == mData[quesNo - 1].correct_answer)
                        correct = binding.optionB.text.toString()
                }
                R.id.optionC -> {
                    if (binding.optionC.text == mData[quesNo - 1].correct_answer)
                        correct = binding.optionC.text.toString()
                }
                R.id.optionD -> {
                    if (binding.optionD.text == mData[quesNo - 1].correct_answer)
                        correct = binding.optionD.text.toString()
                }
            }
        }
    }


    private fun loadQues() {
        binding.radioGroupContainer.clearCheck()
        binding.textQues.text = mData[quesNo].question
        binding.quesLeft.text = "${quesNo + 1}"
        showToast(mData[quesNo].correct_answer)

        when (Random.nextInt(0, 4)) {
            0 -> {
                binding.optionA.text = mData[quesNo].correct_answer
                binding.optionB.text = mData[quesNo].incorrect_answers[0]
                binding.optionC.text = mData[quesNo].incorrect_answers[1]
                binding.optionD.text = mData[quesNo].incorrect_answers[2]
            }
            1 -> {
                binding.optionB.text = mData[quesNo].correct_answer
                binding.optionA.text = mData[quesNo].incorrect_answers[0]
                binding.optionC.text = mData[quesNo].incorrect_answers[1]
                binding.optionD.text = mData[quesNo].incorrect_answers[2]
            }
            2 -> {
                binding.optionC.text = mData[quesNo].correct_answer
                binding.optionA.text = mData[quesNo].incorrect_answers[0]
                binding.optionB.text = mData[quesNo].incorrect_answers[1]
                binding.optionD.text = mData[quesNo].incorrect_answers[2]
            }
            3 -> {
                binding.optionD.text = mData[quesNo].correct_answer
                binding.optionA.text = mData[quesNo].incorrect_answers[0]
                binding.optionC.text = mData[quesNo].incorrect_answers[1]
                binding.optionB.text = mData[quesNo].incorrect_answers[2]
            }
        }

        quesNo++
    }

    private fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private fun fetchData() {
        var finalCat = "&category=$cat"
        var finalDiff = "&difficulty=$diff"
        if (cat == "8")
            finalCat = ""
        if (diff == "any")
            finalDiff = ""

        val queue = Volley.newRequestQueue(binding.root.context)
        val url = "https://opentdb.com/api.php?amount=10$finalCat$finalDiff&type=multiple"

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            if (response.getInt("response_code") == 0) {
                val newJsonArray = response.getJSONArray("results")
                for (i in 0 until newJsonArray.length()) {
                    val newsJsonObject = newJsonArray.getJSONObject(i)
                    val incur = newsJsonObject.getJSONArray("incorrect_answers")
                    val insurString = ArrayList<String>(3)
                    for (i in 0 until incur.length()) {
                        insurString.add(incur.getString(i).trim())
                    }
                    val data = Data(
                        newsJsonObject.getString("question").trim(),
                        newsJsonObject.getString("correct_answer").trim(),
                        insurString
                    )
                    mData.add(data)
                }
            } else {
                showToast("Data can't fetch !")
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_testFragment_to_mainFragment)
            }
        }, { error ->
            showToast("No Response !")
        })
        queue.add(request)

        binding.lottieAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {

                if (mData.isNotEmpty()) {
                    binding.skyBack.visibility = View.VISIBLE
                    binding.quesCardView.visibility = View.VISIBLE
                    binding.radioLayout.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.VISIBLE
                    binding.lottieAnim.visibility = View.GONE
                    loadQues()
                } else {
                    showToast("Data can't fetch by mData!")
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_testFragment_to_mainFragment)
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}