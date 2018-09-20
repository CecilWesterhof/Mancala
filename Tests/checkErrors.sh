#!/usr/bin/env bash

# Exit on error and an unset variable is also an error
set -o errexit
set -o nounset


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

declare -i totalTests=0
declare -i totalErrors=0

cd $(dirname "$(readlink -f "$0")")
# All the test should give an error, that is why we do not exit on error anymore
set +o errexit
for testFile in "${testFiles[@]}" ; do
    totalTests+=1
    scala ../Mancala.scala --test ${testFile} >/dev/null
    if [[ ${?} -ne 1 ]] ; then
        echo "${testFile} did not return 1"
        totalErrors+=1
    fi
done
printf "Executed %d tests\n"                 ${totalTests}
printf "There were %d tests with an error\n" ${totalErrors}
