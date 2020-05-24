set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.8-complete.jar:./bin main 1"
#cat > code.txt   # save everything in stdin to program.txt
$CCHK