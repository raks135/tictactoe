package com.tictactoe.domain.games

import kotlin.random.Random

data class MathQuestion(
    val num1: Int,
    val num2: Int,
    val operator: String,
    val correctAnswer: Int,
    val options: List<Int>,
    val displayText: String
)

class MathGameEngine(
    private val maxNumber: Int = 10,
    private val isAddition: Boolean = true
) {
    fun generateQuestion(): MathQuestion {
        return if (isAddition) {
            generateAdditionQuestion()
        } else {
            generateSubtractionQuestion()
        }
    }
    
    private fun generateAdditionQuestion(): MathQuestion {
        val num1 = Random.nextInt(1, maxNumber + 1)
        val num2 = Random.nextInt(1, maxNumber + 1)
        val correctAnswer = num1 + num2
        val options = generateOptions(correctAnswer)
        
        return MathQuestion(
            num1 = num1,
            num2 = num2,
            operator = "+",
            correctAnswer = correctAnswer,
            options = options,
            displayText = "$num1 + $num2 = ?"
        )
    }
    
    private fun generateSubtractionQuestion(): MathQuestion {
        val num1 = Random.nextInt(5, maxNumber + 1)
        val num2 = Random.nextInt(1, num1)
        val correctAnswer = num1 - num2
        val options = generateOptions(correctAnswer)
        
        return MathQuestion(
            num1 = num1,
            num2 = num2,
            operator = "-",
            correctAnswer = correctAnswer,
            options = options,
            displayText = "$num1 - $num2 = ?"
        )
    }
    
    private fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)
        
        while (options.size < 4) {
            val offset = Random.nextInt(-5, 6)
            val option = (correctAnswer + offset).coerceAtLeast(0)
            if (option != correctAnswer) {
                options.add(option)
            }
        }
        
        return options.shuffled()
    }
    
    fun checkAnswer(question: MathQuestion, answer: Int): Boolean {
        return answer == question.correctAnswer
    }
}
