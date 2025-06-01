# Makefile

JAVAC = javac
JAVA = java
SRC = src/
BIN = out
MAIN = Program

# Compile all .java files
compile:
	mkdir -p $(BIN)
	find $(SRC) -name "*.java" > sources.txt
	$(JAVAC) -d $(BIN) @sources.txt

run:
	$(JAVA) -cp $(BIN) $(MAIN) $(ARGS)

# Clean build files
clean:
	rm -rf $(BIN) sources.txt
