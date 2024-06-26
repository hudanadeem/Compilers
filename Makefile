JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-cp /usr/share/java/cup.jar:.
CUP=cup
# JFLEX=~/Projects/jflex/bin/jflex
# CLASSPATH=-cp ~/Projects/java-cup-11b.jar:.
# CUP=$(JAVA) $(CLASSPATH) java_cup.Main

all: CM.class

CM.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java SemanticAnalyzer.java NodeType.java CodeGenerator.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cm.flex
	$(JFLEX) cm.flex

parser.java: cm.cup
	#$(CUP) -dump -expect 3 cm.cup
	$(CUP) -expect 3 cm.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class tests/*.sym tests/*.abs tests/12345_C1/*.sym tests/12345_C1/*.abs tests/given_tests_C1/*.sym tests/given_tests_C1/*.abs tests/given_tests_C2/*.sym tests/given_tests_C2/*.abs *~