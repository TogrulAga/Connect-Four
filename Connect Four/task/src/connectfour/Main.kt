package connectfour

import kotlin.system.exitProcess

/**
 * Board class for holding board dimensions
 */
data class Board(val rows: Int, val columns: Int) {
    private var matrix = MutableList(size = columns) { MutableList(size = rows) { ' ' } }

    /**
     * Prints the board
     */
    fun print() {
        printHeader()

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                print("║${matrix[column][row]}")
            }
            println("║")
        }

        printFooter()
    }

    /**
     * Prints the board header - column numbers
     */
    private fun printHeader() {
        for (column in 1 .. columns) {
            print(" $column")
        }
        println()
    }

    /**
     * Prints the board footer - only the bottom row
     */
    private fun printFooter() {
        println("╚═${"╩═".repeat(columns - 1)}╝")
    }

    /**
     * Mark the board with a player's symbol if column is not full
     * @param player the player to mark the column with
     * @param column the column to mark
     * @return true if marking was successful, false otherwise
     */
    fun mark(player: Player, column: Int): Boolean {
        if (columnIsFull(column)) {
            println("Column ${column + 1} is full")
            return false
        }

        val index = getFirstEmptyIndex(column)

        matrix[column][index] = player.marker

        return true
    }

    /**
     * Checks if the column is full
     * @param column the column to check
     * @return true if the column is full, false otherwise
     */
    private fun columnIsFull(column: Int): Boolean {
        return matrix[column].all { it != ' ' }
    }

    /**
     * Gets the first empty index in the column
     * @param column the column to check
     * @return the first empty index in the column
     */
    private fun getFirstEmptyIndex(column: Int): Int {
        for (index in rows - 1 downTo 0) {
            if (matrix[column][index] == ' ') {
                return index
            }
        }

        return -1
    }

    /**
     * Checks if the player won the game
     * @return true if player won, false otherwise
     */
    fun checkForWin(player: Player): Boolean {
        val marker = player.marker

        return checkForHorizontalWin(marker) || checkForVerticalWin(marker) || checkForMainDiagonalWin(marker) || checkForSecondaryDiagonalWin(marker)
    }

    /**
     * Checks if the player won horizontally
     * @param marker the player's marker
     * @return true if player won, false otherwise
     */
    private fun checkForHorizontalWin(marker: Char): Boolean {
        for (row in 0 until rows) {
            for (column in 0 until columns - 3) {
                if (matrix[column][row] == marker &&
                    matrix[column + 1][row] == marker &&
                    matrix[column + 2][row] == marker &&
                    matrix[column + 3][row] == marker
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if the player won vertically
     * @param marker the player's marker
     * @return true if player won, false otherwise
     */
    private fun checkForVerticalWin(marker: Char): Boolean {
        for (column in 0 until columns) {
            for (row in 0 until rows - 3) {
                if (matrix[column][row] == marker &&
                    matrix[column][row + 1] == marker &&
                    matrix[column][row + 2] == marker &&
                    matrix[column][row + 3] == marker
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if the player won in main diagonal
     * @param marker the player's marker
     * @return true if player won, false otherwise
     */
    private fun checkForMainDiagonalWin(marker: Char): Boolean {
        for (column in 0 until columns - 3) {
            for (row in 0 until rows - 3) {
                if (matrix[column][row] == marker &&
                    matrix[column + 1][row + 1] == marker &&
                    matrix[column + 2][row + 2] == marker &&
                    matrix[column + 3][row + 3] == marker
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if the player won in secondary diagonal
     * @param marker the player's marker
     * @return true if player won, false otherwise
     */
    private fun checkForSecondaryDiagonalWin(marker: Char): Boolean {
        for (column in 3 until columns) {
            for (row in 0 until rows - 3) {
                if (matrix[column][row] == marker &&
                    matrix[column - 1][row + 1] == marker &&
                    matrix[column - 2][row + 2] == marker &&
                    matrix[column - 3][row + 3] == marker
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if the board is full
     * @return true if game is over, false otherwise
     */
    fun isFull(): Boolean {
        return matrix.all { it.all { it != ' ' } }
    }

    /**
     * Reset the board to initial state
     */
    fun reset() {
        matrix = MutableList(size = columns) { MutableList(size = rows) { ' ' } }
    }
}

/**
 * Player class for holding player names
 */
data class Player(val name: String, val marker: Char) {
    private var score: Int = 0

    /**
     * Get column number from user input
     */
    fun getColumnNumber(lastColumn: Int): Int {
        while (true) {
            try {
                println("$name's turn")
                val column = readln()
                if (column == "end") {
                    println("Game over!")
                    exitProcess(0)
                }

                if (column.toInt() !in 1 .. lastColumn) {
                    println("The column number is out of range (1 - $lastColumn)")
                    continue
                }
                return column.toInt()
            } catch (e: Exception){
                println("Incorrect column number")
                continue
            }
        }
    }

    /**
     * @return the player's score
     */
    fun printScore(): String {
        return "$name: $score"
    }

    /**
     * Increment the player's score by 2
     */
    fun addWinScore() {
        score += 2
    }

    /**
     * Increment the player's score by 1
     */
    fun addDrawScore() {
        score++
    }
}

/**
 * Connect Four game
 */
object ConnectFour {
    private lateinit var player1: Player
    private lateinit var player2: Player

    private lateinit var board: Board

    private var numberOfGames: Int = 0

    /**
     * Entry point of the game
     */
    fun startGame() {
        getPlayerNames()

        getBoardSize()

        getNumberOfGames()

        printGameInfo()

        loop()

        println("Game over!")
    }

    /**
     * Gets player names and saves them in player1 and player2 properties
     */
    private fun getPlayerNames() {
        println("Connect Four")
        println("First player's name:")
        player1 = Player(readln(), 'o')
        println("Second player's name:")
        player2 = Player(readln(), '*')
    }

    /**
     * Gets board size from user, validates and saves the Board in board property
     */
    private fun getBoardSize() {
        while (true) {
            val pattern = """^\s*(\d+)\s*x\s*(\d+)\s*$""".toRegex(RegexOption.IGNORE_CASE)

            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val input = readln()

            if (input.isEmpty()) {
                board = Board(6, 7)
                break
            }

            val match = pattern.find(input)

            if (match?.groupValues?.size != 3) {
                println("Invalid input")
                continue
            }

            val rows = match.groupValues[1].toInt()
            val columns = match.groupValues[2].toInt()

            if (rows !in 5 .. 9) {
                println("Board rows should be from 5 to 9")
                continue
            } else if (columns !in 5 .. 9) {
                println("Board columns should be from 5 to 9")
                continue
            }

            board = Board(rows, columns)
            break
        }
    }

    /**
     * Gets number of games from user, validates and saves the number in numberOfGames property
     */
    private fun getNumberOfGames() {
        var number: String
        while (true) {
            println("""
                |Do you want to play single or multiple games?
                |For a single game, input 1 or press Enter
                |Input a number of games:
                """.trimMargin())
            number = readln()

            try {
                if (number.isNotEmpty() && number.toInt() < 1) {
                    println("Invalid input")
                    continue
                }
            } catch (e: Exception) {
                println("Invalid input")
                continue
            }

            break
        }

        if (number.isEmpty()) {
            numberOfGames = 1
            return
        }

        numberOfGames = number.toInt()
    }

    /**
     * Print player names and board size
     */
    private fun printGameInfo() {
        println("${player1.name} VS ${player2.name}")
        println("${board.rows} x ${board.columns} board")

        if (numberOfGames > 1) {
            println("Total $numberOfGames games")
            println("Game #1")
        } else {
            println("Single game")
        }

        board.print()
    }

    /**
     * Main game loop
     */
    private fun loop() {
        for (i in 1..numberOfGames) {
            if (numberOfGames > 1 && i > 1) {
                println("Game #$i")
                board.reset()
                board.print()
            }

            val p1 = if (i % 2 == 0) player2 else player1
            val p2 = if (i % 2 == 0) player1 else player2

            while (true) {
                playerMoves(p1)

                if (board.checkForWin(p1)) {
                    println("Player ${p1.name} won")
                    p1.addWinScore()
                    break
                }

                if (board.isFull()) {
                    println("It is a draw")
                    p1.addDrawScore()
                    p2.addDrawScore()
                    break
                }

                playerMoves(p2)

                if (board.checkForWin(p2)) {
                    println("Player ${p2.name} won")
                    p2.addWinScore()
                    break
                }

                if (board.isFull()) {
                    p1.addDrawScore()
                    p2.addDrawScore()
                    println("It is a draw")
                    break
                }
            }

            if (numberOfGames > 1) {
                printScore()
            }
        }
    }

    /**
     * Gets player move and validates it
     * @param player Player to get move from
     */
    private fun playerMoves(player: Player) {
        var column: Int

        while (true) {
            column = player.getColumnNumber(board.columns)

            if (!board.mark(player, column - 1)) {
                continue
            }

            break
        }

        board.print()
    }

    /**
     * Prints score
     */
    private fun printScore() {
        println("Score")
        println("${player1.printScore()} ${player2.printScore()}")
    }
}


fun main() {
    ConnectFour.startGame()
}