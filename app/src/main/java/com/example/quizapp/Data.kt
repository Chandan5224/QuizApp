package com.example.quizapp

data class Data (
    val question:String="",
    val correct_answer:String="",
    val incorrect_answers: ArrayList<String> = ArrayList()
)

