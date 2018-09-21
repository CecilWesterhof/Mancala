#!/usr/bin/env bash

# Exit on error and an unset variable is also an error
set -o errexit
set -o nounset


declare -r _msgTotal='Executed %d tests\n'
declare -r _msgWrong='%d test(s) with did not give the expected error\n'

declare -a testFiles=(
    wrongTestFile01.txt
    wrongTestFile02.txt
    wrongTestFile03.txt
    wrongTestFile04.txt
    wrongTestFile05.txt
    wrongTestFile06.txt
    wrongTestFile07.txt
    wrongTestFile08.txt
    wrongTestFile09.txt
)

declare    error
declare -i totalTests=0
declare -i totalErrors=0

cd $(dirname "$(readlink -f "$0")")
for testFile in "${testFiles[@]}" ; do
    totalTests+=1
    # We need the error code so temporaly disable exit on error
    set +o errexit
    scala ../Mancala.scala --test ${testFile} &>/dev/null
    error=${?}
    set -o errexit
    if [[ ${error} -ne 201 ]] ; then
        echo "${testFile} did not return 201 (${error})"
        totalErrors+=1
    fi
done
printf "${_msgTotal}" ${totalTests}
printf "${_msgWrong}" ${totalErrors}
