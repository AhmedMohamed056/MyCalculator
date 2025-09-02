package com.thechance.mycalculator


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.thechance.mycalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var currentNumber = StringBuilder()
    private var firstOperand: Double? = null
    private var currentOperator: String? = null
    private var shouldClearDisplay = false
    private var fullExpression = StringBuilder()
    private var resultCalculated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setClickListeners()
        onClearClick()
    }

    private fun setClickListeners() {
        val buttons = listOf(
            binding.zeroButton, binding.oneButton, binding.towButton, binding.threeButton,
            binding.fourButton, binding.fiveButton, binding.sexButton, binding.sevenButton,
            binding.eightButton, binding.nineButton, binding.dotButton,
            binding.plusButton, binding.minsButton, binding.multiplicationButton,
            binding.dividerButton, binding.modulesButton,
            binding.equalButton, binding.acButton, binding.arrowButton, binding.plusMinsButton
        )
        buttons.forEach { it.setOnClickListener(this) }
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.zero_button, R.id.one_button, R.id.tow_button, R.id.three_button,
            R.id.four_button, R.id.five_button, R.id.sex_button, R.id.seven_button,
            R.id.eight_button, R.id.nine_button, R.id.dot_button -> {
                val buttonText = when (v.id) {
                    R.id.zero_button -> "0"
                    R.id.one_button -> "1"
                    R.id.tow_button -> "2"
                    R.id.three_button -> "3"
                    R.id.four_button -> "4"
                    R.id.five_button -> "5"
                    R.id.sex_button -> "6"
                    R.id.seven_button -> "7"
                    R.id.eight_button -> "8"
                    R.id.nine_button -> "9"
                    R.id.dot_button -> "."
                    else -> ""
                }
                onNumberClick(buttonText)
            }

            R.id.plus_button, R.id.mins_button, R.id.multiplication_button,
            R.id.divider_button, R.id.modules_button -> {
                val operatorText = when (v.id) {
                    R.id.plus_button -> "+"
                    R.id.mins_button -> "-"
                    R.id.multiplication_button -> "x"
                    R.id.divider_button -> "/"
                    R.id.modules_button -> "%"
                    else -> ""
                }
                onOperatorClick(operatorText)
            }
            R.id.equal_button -> onEqualsClick()
            R.id.ac_button -> onClearClick()
            R.id.arrow_button -> onBackspaceClick()
            R.id.plus_mins_button -> onPlusMinusClick()
        }
    }

    private fun onNumberClick(number: String) {
        if (resultCalculated) {
            resultCalculated = false
        }

        if (number == "." && currentNumber.contains(".")) return
        if (currentNumber.toString() == "0" && number != ".") {
            currentNumber.clear()
            if (fullExpression.toString() == "0") {
                fullExpression.clear()
            }
        }

        currentNumber.append(number)
        fullExpression.append(number)
        updateDisplay()
    }

    private fun onOperatorClick(operator: String) {
        if (fullExpression.isNotEmpty() && !fullExpression.last().isDigit()) {
            fullExpression.delete(fullExpression.length - 3, fullExpression.length)
            fullExpression.append(" $operator ")
            currentOperator = operator
            updateDisplay()
            return
        }

        if (currentNumber.isNotEmpty()) {
            val num = currentNumber.toString().toDouble()
            if (firstOperand != null && currentOperator != null) {
                firstOperand = performCalculation(firstOperand!!, num, currentOperator!!)
            } else {
                firstOperand = num
            }
        }

        currentOperator = operator
        currentNumber.clear()
        fullExpression.append(" $operator ")
        updateDisplay()
        resultCalculated = false
    }

    private fun onEqualsClick() {
        if (firstOperand == null || currentOperator == null || currentNumber.isEmpty()) return

        val secondOperand = currentNumber.toString().toDouble()
        val result = performCalculation(firstOperand!!, secondOperand, currentOperator!!)

        binding.previousCalculation.text = fullExpression.toString()
        val formattedResult = formatResult(result)
        binding.currentCalculation.text = formattedResult

        currentNumber.clear().append(formattedResult)
        fullExpression.clear().append(formattedResult)
        firstOperand = null
        currentOperator = null
        shouldClearDisplay = true
    }

    private fun onClearClick() {
        currentNumber.clear()
        firstOperand = null
        currentOperator = null
        fullExpression.clear()
        binding.previousCalculation.text = ""
        binding.currentCalculation.text = "0"
    }

    private fun onBackspaceClick() {
        if (currentNumber.isNotEmpty()) {
            currentNumber.deleteCharAt(currentNumber.length - 1)
        } else if (fullExpression.isNotEmpty() && fullExpression.last() == ' ') {
            fullExpression.delete(fullExpression.length - 3, fullExpression.length)
            val parts = fullExpression.split(" ")
            currentNumber.append(parts.last())
            currentOperator = null
            firstOperand = null
        }
        if (fullExpression.isNotEmpty()) {
            fullExpression.deleteCharAt(fullExpression.length - 1)
        }
        updateDisplay()
    }

    private fun onPlusMinusClick() {
        if (currentNumber.isNotEmpty()) {
            val value = currentNumber.toString().toDouble() * -1
            currentNumber.clear().append(value.toString())
            updateDisplay()
        }
    }

    private fun performCalculation(op1: Double, op2: Double, operator: String): Double {
        return when (operator) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "x" -> op1 * op2
            "/" -> if (op2 != 0.0) op1 / op2 else Double.NaN
            "%" -> op1 % op2
            else -> 0.0
        }
    }

    private fun updateDisplay() {
        if (fullExpression.isEmpty()) {
            binding.currentCalculation.text = "0"
        } else {
            binding.currentCalculation.text = fullExpression.toString()
        }
    }

    private fun formatResult(number: Double): String {
        return if (number.isNaN()) {
            "NaN"
        } else if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }
}