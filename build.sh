set -e
cd "$(dirname "$0")"
mkdir -p bin
find ./src -name *.java | javac -d bin -classpath "./lib/antlr-4.8-complete.jar" @/dev/stdin
