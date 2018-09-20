# Mancala
An engine in Scala for playing mancala

I was asked to write a mancala engine. Because it was some time ago that I worked with Scala I decide to implement it in Scala. This repository is the result from this assignment.

It is just a proof of concept. There are a lot of things to be improved. For example:
- Use an user defined exception
- The layout of the code
- More and better tests
- Splitting the code in logical elements

You can play mancala on the command-line with:
    scala Mancala.scala --play

Tests can be done with:
    scala Mancala.scala --test FILENAME

Test files contain exactly four lines:
- First line:  description. (Not used, is for the user.)
- Second line: nr of stones for all boardStores and the currentPlayer.
- Third line:  move (player and pit)
- Fourth line: as second line, but after the move or the exception string.

In the directory Test I have put some test files and two Bash scripts to test the application.

With runTests.sh the functionality is tested.
With checkErrors.sh I test if errors in the test files are correctly determined.

The script runTests.sh is terminated when there is a test file that is not correctly constructed. In my opinion you should first make sure that your tests are correct, before running them.


It was fun to do and I think that I will make a full blown (GUI) application.
Do not hesitate to contact me about this application with questions, tips for improvements or requests.
