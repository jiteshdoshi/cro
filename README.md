# CRO based Gene Selection
Java based UI to use modified CRO (Chemical Reaction Optimization) Algorithm for important gene selection from microarray data

This is a initial phase, first version of CRO-GS, a chemical reaction optimization (CRO) algorithm adapted to solve gene selection problem from a large gene pool present in microarray data. Programme essentially filters a large microarray data of a particular cancer type and outputs best subset of gene significant in that particular type of cancer. Next version is expected with a robust cancer classification system.

Related Publication:
  Simultaneous Gene Selection and Cancer Classification using Chemical Reaction Optimization
  Jitesh Doshi, Mahesh Chindhe, Yogesh Kharche, Shameek Ghosh, Jayaraman Valadi Proceedings of the World Congress on
  Engineering 2014 ([PDF](http://www.iaeng.org/publication/WCE2014/WCE2014_pp219-223.pdf))

Requirements: Java Runtime Environment (1.6 or higher)
              gnuplot (for visualizing graph)

Input:
Data file containing gene expression data of diseased and healthy person in one of the following formats: csv libsvm arff Input file must have extension .csv, .libsvm or .arff (or .CSV, .LIBSVM, .ARFF)

Output:
Currently program outputs list of indexes of significant genes and a graph to visualize progress in accuracy of the algorithm

Usage:

  1. Use executable .jar file (double click or use on commandline "java -jar CROGenSel.jar")
  2. Import the sources into Java project and run cro/MainGUI.java (Will require weka and libsvm libraries to be added)

  duke.libsvm is an example file of breast cancer dataset from [libsvm dataset library](http://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/binary.html)
