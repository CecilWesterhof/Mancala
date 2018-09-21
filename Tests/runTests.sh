#!/usr/bin/env bash

# Exit on error and an unset variable is also an error
set -o errexit
set -o nounset


declare -r _msgFailed='%d test(s) failed\n'
declare -r _msgTotal='Executed %d tests\n'

declare -a testFiles=(
    currentPlayerEmpty01.txt
    currentPlayerEmpty02.txt
    emptyPit01.txt
    gameFinished01.txt
    otherPlayerEmpty01.txt
    otherPlayerEmpty02.txt
    samePlayer01.txt
    wrongPit01.txt
    wrongPlayer01.txt
    wrongPlayer02.txt
)

declare    output
declare -i totalTests=0
declare -i totalErrors=0

cd $(dirname "$(readlink -f "$0")")
for testFile in "${testFiles[@]}" ; do
    totalTests+=1
    output=$(scala ../Mancala.scala --test ${testFile})
    if [[ ${output} != "" ]] ; then
        echo "${testFile}: ${output}"
        totalErrors+=1
    fi
done
printf "${_msgTotal}"  ${totalTests}
printf "${_msgFailed}" ${totalErrors}
