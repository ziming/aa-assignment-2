Problem 1
=========

Problem 1 files are in aa.lb

LoadBalancerTimeDataCsvProducer is a class that parses timeData.txt (produced by LoadBalancerExercise when you pipe it to timeData.txt).
after parsing timeData.txt it produces timeData.csv

As it does normal System.out.print, you would need to pipe it to timeData.csv

If you happen to use IntelliJ go edit configuration > logs > check save console output to file.

Problem 2
=========

Problem 2 files are in aa.recursive

SearchFishAction parse the files serially, add every row into an arraylist, then pass that arraylist to the RecursiveAction to split up if necessary

SearchStingRayAction pass an arraylist of file names to the RecursiveAction which then split up to more RecursiveActions if necessary to
parse the files in parallel and check if sting ray and above 9000.

Problem 3
=========

Problem 3 files are in aa.callable

Problem 4
=========

Problem 4 files are in aa.food

Problem 4 uses RecursiveTask which is kind of like RecursiveAction except that it returns a result.

Misc
====
Store all your fish*.dat files and foods.txt in the project root.

This is an intelliJ project.

External libraries used are:
- univocity-parsers-1.5.6.jar in lib folder.