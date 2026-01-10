package com.tictactoe.domain.games

import kotlin.random.Random

enum class QuestionType {
    WHAT_COMES_NEXT,
    WHAT_COMES_BEFORE,
    FILL_THE_GAP
}

data class NumberQuestion(
    val questionType: QuestionType,
    val number: Int,
    val correctAnswer: Int,
    val options: List<Int>,
    val displayText: String
)

class NumberGameEngine(
    private val maxNumber: Int = 20
) {
    fun generateQuestion(): NumberQuestion {
        val questionType = QuestionType.values().random()
        
        return when (questionType) {
            QuestionType.WHAT_COMES_NEXT -> generateNextQuestion()
            QuestionType.WHAT_COMES_BEFORE -> generateBeforeQuestion()
            QuestionType.FILL_THE_GAP -> generateGapQuestion()
        }
    }
    
    private fun generateNextQuestion(): NumberQuestion {
        val number = Random.nextInt(1, maxNumber)
        val correctAnswer = number + 1
        val options = generateOptions(correctAnswer)
        
        return NumberQuestion(
            questionType = QuestionType.WHAT_COMES_NEXT,
            number = number,
            correctAnswer = correctAnswer,
            options = options,
            displayText = "What comes after $number?"
        )
    }
    
    private fun generateBeforeQuestion(): NumberQuestion {
        val number = Random.nextInt(2, maxNumber + 1)
        val correctAnswer = number - 1
        val options = generateOptions(correctAnswer)
        
        return NumberQuestion(
            questionType = QuestionType.WHAT_COMES_BEFORE,
            number = number,
            correctAnswer = correctAnswer,
            options = options,
            displayText = "What comes before $number?"
        )
    }
    
    private fun generateGapQuestion(): NumberQuestion {
        val middle = Random.nextInt(2, maxNumber)
        val correctAnswer = middle
        val options = generateOptions(correctAnswer)
        
        return NumberQuestion(
            questionType = QuestionType.FILL_THE_GAP,
            number = middle,
            correctAnswer = correctAnswer,
            options = options,
            displayText = "${middle - 1}, ?, ${middle + 1}"
        )
    }
    
    private fun generateOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)
        
        while (options.size < 4) {
            val offset = Random.nextInt(-3, 4)
            val option = (correctAnswer + offset).coerceIn(0, maxNumber + 5)
            if (option != correctAnswer) {
                options.add(option)
            }
        }
        
        return options.shuffled()
    }
    
    fun checkAnswer(question: NumberQuestion, answer: Int): Boolean {
        return answer == question.correctAnswer
    }
}
