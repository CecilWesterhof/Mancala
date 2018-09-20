/*
 * Proof of concept only
 * Playable on command-line (scala Mancala.scala --play)
 * File based test          (scala Mancala.scala --test FILENAME)
 *
 * With the tests an exit 1 is done when there is a problem with the test file
 * In this way it is easy to terminate the tests when there is a problem with a test file
 * In my opinion the best option: you should not run tests when they do not have a correct syntax
 *
 * To be improved upon:
 * - Use an user defined exception
 * - The layout of the code
 */
object Mancala {
  // Numer of pits pro player excluding mancala
  val pitsNr        = 6
  val totalStores   = (pitsNr + 1) * 2

  /*
   * boardStores: total stores, including the mancalas
   * - 0 - (pitsNr - 1)                      -> pits player A
   * - pitsNr                                -> mancala player A
   * - (pitsNr + 1) - ((pitsNr + 1) * 2 - 2) -> pits player B
   * - ((pitsNr + 1) * 2 - 1)                -> mancala player B
   */
  val boardStores            = new Array[Int](totalStores)
  val mancalaA               = pitsNr
  val mancalaB               = totalStores - 1
  val players : List[String] = List("A", "B")
  val stonesInPit            = 4
  val totalStones            = 2 * pitsNr * stonesInPit

  // 0 -> player A, 1 -> player B, -1 -> game is finished, -2 -> not set
  var currentPlayer = -2


  def main(args : Array[String]) {
    if (args.length == 0) {
      wrongCall()
    }
    args(0) match {
      case "--help" => if (args.length == 1) usage()       else wrongCall()
      case "--play" => if (args.length == 1) play()        else wrongCall()
      case "--test" => if (args.length == 2) test(args(1)) else wrongCall()
      case _        => wrongCall()
    }
  }


  // Only returns when thisBoard is a correct board
  def checkInputBoard(thisBoard : List[Int]) {
    // Should contain all the stores and currentPlayer
    if (thisBoard.length != (totalStores + 1)) {
      errorExit("Nr of elements has to be %d. (%d)".format(totalStores + 1, thisBoard.length))
    }
    val thisPlayer = thisBoard.last
    // sum also contains currentPlayer, so this has to be subtracted before the compare
    if ((thisBoard.sum - thisPlayer) != totalStones) {
      errorExit("There are %d stones instead of %d".format(thisBoard.sum - thisPlayer, totalStones))
    }
    // Number of stones should not be negative (mancalaB is last store)
    for (i <- 0 to mancalaB) {
      if (thisBoard(i) < 0) {
        errorExit("Number of stones cannot be negative: %d".format(thisBoard(i)))
      }
    }
    // Player needs to be between -1 and 1
    val player = thisBoard.last
    if (!(-1 to 1).toList.contains(player)) {
      errorExit("Player needs to be between -1 and 1: %d".format(player))
    }
    // When game is finished all stones should be in the mancala
    if ((player == -1) && ((thisBoard(mancalaA) + thisBoard(mancalaB)) != totalStones)) {
      errorExit("With a finished game all stones should be in the mancalas")
    }
  }

  // -1 for quiting, otherwise check it is legal
  def enterNextMove() : Int = {
    var found   = false
    var takePit = -2
    while (!found) {
      try {
        print("Player %s use which pit (-1 to quit): ".format(players(currentPlayer)))
        takePit = scala.io.StdIn.readInt()
        if ((takePit == -1) || isOwnPitAndNotEmpty(takePit)) {
          found = true
        }
      } catch {
        case ex: NumberFormatException => // Try it again
      }
    }
    takePit
  }

  def errorExit(message : String) {
    println(message)
    System.exit(1)
  }

  // Create board for starting a game
  def initBoard() {
    for (i <- 0 to mancalaA - 1) {
      boardStores(i)              = stonesInPit
      boardStores(i + pitsNr + 1) = stonesInPit
    }
    boardStores(mancalaA) = 0
    boardStores(mancalaB) = 0
    currentPlayer         = 0
  }

  // Checks if move is legal, throws if not
  def isMoveLegal(player : String, selectedPit : Int) {
    // Check game is not already finished
    if (currentPlayer == -1) {
      throw new IllegalArgumentException("Game is finished.")
    }
    // Check correct player
    if (!(
      ((currentPlayer == 0) && (player == "A")) ||
      ((currentPlayer == 1) && (player == "B"))
    )) {
      throw new IllegalArgumentException("Wrong player: " + player);
    }
    // Check selected pit is from current player
    if (!isOwnPit(selectedPit)) {
      val message = "Wrong pit: %d".format(selectedPit)
      throw new IllegalArgumentException(message);
    }
    // An empty pit cannot be selected
    if (boardStores(selectedPit) < 1) {
      val message = "You cannot select an empty pit: %d".format(selectedPit)
      throw new IllegalArgumentException(message);
    }
  }

  def isOwnPit(pit : Int) : Boolean = {
    val low  = currentPlayer * (pitsNr + 1)
    val high = low + pitsNr - 1
    (low <= pit) && (pit <= high)
  }

  def isOwnPitAndNotEmpty(pit : Int) : Boolean = {
    isOwnPit(pit) && (boardStores(pit) > 0)
  }

  // Get next pit to distribute a stone to
  def getNextPit(currentPit : Int) : Int = {
    var nextPit = (currentPit + 1) % boardStores.length
    // Mancala of other player is skipped
    if (((nextPit == mancalaA) && (currentPlayer == 1)) ||
      ((nextPit == mancalaB) && (currentPlayer == 0))) {
      nextPit = (nextPit + 1) % boardStores.length
    }
    nextPit
  }

  // Execute move if it is legal
  def move(player : String, selectedPit : Int) {
    isMoveLegal(player, selectedPit)
    // Distribute stones
    var lastPit              = selectedPit
    val stones               = boardStores(selectedPit)
    boardStores(selectedPit) = 0
    for (i <- 1 to stones) {
      lastPit = getNextPit(lastPit)
      boardStores(lastPit) += 1
    }
    // If ended in an empty pit from yourself,
    // take stones from opposite site and own stone
    if (isOwnPit(lastPit) && (boardStores(lastPit) == 1)) {
      val otherPit = (2 * pitsNr) - lastPit
      val mancala  = if (currentPlayer == 0) mancalaA else mancalaB
      boardStores(mancala) += boardStores(otherPit) + 1
      boardStores(otherPit) = 0
      boardStores(lastPit)  = 0
    }
    // If last stone not in players mancala switch user
    if ((lastPit != mancalaA) && (lastPit != mancalaB)) {
      currentPlayer = 1 - currentPlayer
    }
    // Check if current player has a legal move
    val startPit = if (currentPlayer == 0) 0 else pitsNr + 1
    var count    = 0
    for (i <- 0 to pitsNr - 1) {
      count += boardStores(startPit + i)
    }
    // No, collect all stones and set game as finished
    if (count == 0) {
      val startPit = if (currentPlayer == 1) 0 else pitsNr + 1
      val mancala  = startPit + pitsNr
      for (i <- 0 to pitsNr - 1) {
        boardStores(mancala)     += boardStores(startPit + i)
        boardStores(startPit + i) = 0
      }
      currentPlayer = -1
    }
  }

  def moveAndPrint(player : String, selectedPit : Int) {
    move(player, selectedPit)
    printMancala()
  }

  def play() {
    var takePit = -2

    // Setup board and display it
    initBoard()
    printMancala()
    while (currentPlayer != -1) {
      takePit = enterNextMove()
      if (takePit == -1) {
        println("Quiting the game")
        return
      }
      moveAndPrint(players(currentPlayer), takePit)
    }
    println("Game finished")
  }

  def printMancala() {
    val formatMancala = "  %2d                   %2d\n"
    val formatPit     = " %2d"
    val startPit      = "    "

    printf(startPit)
    var currentPit = 2 * pitsNr
    for (i <- 1 to mancalaA) {
      printf(formatPit, boardStores(currentPit))
      currentPit -= 1
    }
    println("")
    printf(formatMancala, boardStores(mancalaB), boardStores(mancalaA))
    printf(startPit)
    currentPit = 0
    for (i <- 1 to mancalaA) {
      printf(formatPit, boardStores(currentPit))
      currentPit += 1
    }
    println("")
    println("\n")
  }

  /*
   * Test files have four lines:
   * First line:  description. (Not used)
   * Second line: nr of stones for all boardStores and the currentPlayer.
   * Third line:  move (player and pit)
   * Fourth line: as second line, but after move or the exception string.
   */
  def test(fileName : String) {
    var useException = false
    val lines        = scala.io.Source.fromFile(fileName).getLines.toList
    if (lines.length != 4) {
      errorExit("A test file should be four lines long. (%d)".format(lines.length))
    }
    // Should contain totalStores + 1 (currentPlayer is added) integers
    val beforeBoard  = lines(1).split(" +").map(_.toInt).toList
    // If this call returns beforeBoard is a correct representation
    checkInputBoard(beforeBoard)
    // Initialise the board (mancalaB is last element of board)
    for (i <- 0 to mancalaB) {
      boardStores(i) = beforeBoard(i)
    }
    currentPlayer = beforeBoard.last
    val testMove  = lines(2).split(" +").toList
    if (testMove.length != 2) {
      errorExit("A move has two elements: player and pit (%s)".format(lines(2)))
    }
    var takePit = 0
    try {
      takePit = testMove(1).toInt
    } catch {
      case ex: NumberFormatException => errorExit("Pit to take from needs to be an integer %s".format(testMove(1)))
    }
    var afterBoard = List(0)
    try {
      afterBoard = lines(3).split(" +").map(_.toInt).toList
    } catch {
      // We should get an exception when executing the move
      case ex: NumberFormatException => useException = true
    }
    if (useException) {
      try {
        move(testMove(0), takePit)
        errorExit("Expected an exception, but did not get one")
      } catch {
        case ex: IllegalArgumentException => {
          if ("%s".format(ex) != lines(3)) {
            testExit("Got wrong exception '%s' instead of '%s'".format(ex, lines(3)))
          }
        }
      }
    } else {
      // If this call returns afterBoard is a correct representation
      checkInputBoard(afterBoard)
      move(testMove(0), takePit)
      for (i <- 0 to mancalaB) {
        if (boardStores(i) != afterBoard(i)) {
          testExit("Pit %d contains %d stones instead of %d".format(i, boardStores(i), afterBoard(i)))
        }
      }
      if (currentPlayer != afterBoard.last) {
        testExit("Current player is %d instead of %d".format(currentPlayer, afterBoard.last))
      }
    }
  }

  def testExit(message : String) {
    println(message)
    System.exit(0)
  }

  def usage() {
    println("Usage:")
    println("  Mancale --help")
    println("  Mancala --play")
    println("  Mancala --test FILENAME")
  }

  def wrongCall() {
    println("ERROR: Called Wrongly")
    usage()
    System.exit(1)
  }
}
