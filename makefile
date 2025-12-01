// Zubair Elliot
// MakeFile

JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
PACKAGE=medleySimulation
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/$(PACKAGE)/%.class: $(SRCDIR)/$(PACKAGE)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=GridBlock.class FinishCounter.class CounterDisplay.class\
		PeopleLocation.class StadiumGrid.class\
		StadiumView.class Helper.class Swimmer.class SwimTeam.class\
		MedleySimulation.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/$(PACKAGE)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/$(PACKAGE)/*.class

run: default
	$(JAVA) -cp $(BINDIR) $(PACKAGE).MedleySimulation $(ARGS)
