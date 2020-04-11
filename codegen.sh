set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.8-complete.jar:./bin main"
# cat > program.txt   # save everything in stdin to program.txt
$CCHK
cd "test"
clang test.ll builtin.ll
./a.out <test.in >test.out
