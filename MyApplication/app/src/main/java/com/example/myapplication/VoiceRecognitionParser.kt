package com.example.myapplication

import java.util.ArrayList

class VoiceRecognitionParser {


    // method to loop through results trying to find a number
    fun getNumberFromResult(results: ArrayList<String>?): Int {
        if (results != null) {
            for (str in results) {
                if (getIntNumberFromText(str) != -1) {
                    return getIntNumberFromText(str)
                }
            }
        }
        return -1
    }

    // method to convert string number to integer
    private fun getIntNumberFromText(strNum: String): Int {
        when (strNum) {
            "zero" -> return 0
            "one" -> return 1
            "two" -> return 2
            "three" -> return 3
            "four" -> return 4
            "five" -> return 5
            "six" -> return 6
            "seven" -> return 7
            "eight" -> return 8
            "nine" -> return 9
        }
        return -1
    }
}