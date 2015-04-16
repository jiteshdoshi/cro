package cro;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JOptionPane;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
public class Algorithm{

	//constructor that receives user values
	public Algorithm(String c2, String g2, String sub2, String ipop2,
			String ebuff2, String ike2, String crate2, String enloss2,
			String decomp2, String synth2, String step2, String maxg2) {
		this.c=Double.parseDouble(c2);
		this.g=Double.parseDouble(g2);
		this.ebuff=Double.parseDouble(ebuff2);
		this.ike=Double.parseDouble(ike2);
		this.crate=Double.parseDouble(crate2);
		this.decomp=Double.parseDouble(decomp2);
		this.synth=Double.parseDouble(synth2);
		this.step=Double.parseDouble(step2);
		this.sub=Integer.parseInt(sub2);
		this.maxg=Integer.parseInt(maxg2);
		this.ipop=Integer.parseInt(ipop2);
		
	}
	
	//User Input Parameters
	double c, g;							//SVM parameters: cost(c) and gamma(g)
	double ebuff, ike;						//CRO parameters: energy buffer and initial kinetic energy
	double crate, enloss;					//CRO parameters: rate of collision and rate of energy loss 
	double decomp, synth;					//CRO parameters: threshold of decomposition and synthesis
	double step;							//CRO parameters: step size
	int sub, ipop, maxg;					//CRO parameters: Subset size, initial population size and maximum number of generations
	static String filename;
	int lowerBound;
	int upperBound;
	
	//program parameters
	double GlobalMin=1000;						//Global minimum; value will continue to update throughout the run

	int AttrNo, RecordNo; 						//specifies number of attributes and records in a given file
	double[][] InfoGain=null; 					//stores feature indices and respective infogain values
	int[] SelectedAttributes=null; 				//indices of the features selected in infogain
	ArrayList<Molecule> mol=new ArrayList<>(); 	//ArrayList to hold the population of molecules
	Random rand=new Random(); 					//random number generaotor

	//File Handling Parameters: Different FileLoaders from weka.core.converters
	File fname=new File(filename);
	LibSVMLoader libl=new LibSVMLoader();
	CSVLoader csl=new CSVLoader();
	ArffLoader arffl=new ArffLoader();

	Instances data=null;
	Instances filteredData=null;

	//method that reads input file and stores data in weka format in Instances object
	public int readFile() throws IOException{
		String extension = "";
		int i = fname.toString().lastIndexOf('.');
		extension = fname.toString().substring(i+1);

		if(extension.contains("libsvm") || extension.contains("LIBSVM")){
			libl.setFile(fname);
			data=libl.getDataSet();
		}
		else if(extension.contains("csv") || extension.contains("CSV")){
			csl.setFile(fname);
			data=csl.getDataSet();
		}
		else if(extension.contains("arff") || extension.contains("CSV")){
			arffl.setFile(fname);
			data=arffl.getDataSet();
		} else {
			JOptionPane .showMessageDialog(null, "Please select files with extention .libsvm or .arff or .csv", "File Extention Error", JOptionPane.ERROR_MESSAGE);
			data=null;
			return 0;
		}
		System.out.println("*** Input Information ***\nNo of Attributes:	"+data.numAttributes()
				+"\nNo of instances:	"+data.numInstances()+"\nSubset size:	"+sub);
		JOptionPane.showMessageDialog(null, "*** Input Information ***\nNo of Attributes:	"+data.numAttributes()
				+"\nNo of instances:	"+data.numInstances()+"\nSubset size:	"+sub, "InputInfo", JOptionPane.INFORMATION_MESSAGE);
		return 1;
	}

	//Method InfoGain utilizes various methods from weka library and gives InfoGainAttributEval
	public void InfoGain() throws Exception{
		NumericToNominal nm=new NumericToNominal(); 	//Converting last index attribute to type nominal from numeric
		nm.setAttributeIndices("last");					//as the last index would be class label for the data
		nm.setInputFormat(data);

		filteredData= Filter.useFilter(data, nm); 		//filtered data stored in new Instances object

		AttrNo=filteredData.numAttributes(); 			//number of attributes in given file
		RecordNo=filteredData.numInstances(); 			//Number of recoreds in given file

		lowerBound=0;
		upperBound=AttrNo-1;
		AttributeSelection atsl=new AttributeSelection();
		Ranker search=new Ranker();
		InfoGainAttributeEval infog=new InfoGainAttributeEval(); //Applying Attribute Selection using InfoGain evaluator with Ranker search
		atsl.setEvaluator(infog);
		atsl.setSearch(search);
		atsl.SelectAttributes(filteredData);
		InfoGain=atsl.rankedAttributes();
		SelectedAttributes=atsl.selectedAttributes();
	}

	
	
	//runs main CRO algorithm
	double run() throws Exception{
		
		int statusFile=readFile();
		if(statusFile==1){
			InfoGain();
			MainGUI.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			IntitializeIniPopSize();
			MainGUI.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			double tempMin=GlobalMin;
			int iter=0;
			FileWriter fw=new FileWriter("graph.dat");
			File f=new File("graph.dat");
			f.deleteOnExit();
			
			MainGUI.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			while(iter<maxg){
				fw.write((iter)+"\t"+(100-GlobalMin)+"\n");
				if(tempMin>GlobalMin || iter==0){
					System.out.printf("Best value = %.2f", 100-GlobalMin);
					System.out.println("%, iteration = "+iter+", number of molecules = "+mol.size()+", subset size = "+sub);
					tempMin=GlobalMin;
					if(GlobalMin==0.0){
						break;
					}
				}
				if(rand.nextDouble()>crate || mol.size()==1){
					//Decomposition
					int pos=rand.nextInt(mol.size());
					Molecule p=new Molecule();
					p=mol.get(pos);
					if(DecompCheck(p)){
						Molecule q=new Molecule();
						q=initialize(q);
						if(Decomposition(p, q)){
							GlobalUpdate(p);
							GlobalUpdate(q);
							mol.add(q);
						}
						iter+=2;
					} // On wall
					else{
						ebuff+=OnWallIneffective(p);
						GlobalUpdate(p);
						iter++;
					}
				}
				else{
					//Synthesis
					int pos1=rand.nextInt(mol.size());
					int pos2=pos1;
					while(pos2==pos1){
						pos2=rand.nextInt(mol.size());
					}
					Molecule p=mol.get(pos1);
					Molecule q=mol.get(pos2);
					if(SynthCheck(p) && SynthCheck(q)){
						if(Synthesis(p, q)){
							GlobalUpdate(q);
							mol.remove(pos2);
						}
						iter+=2;
						//Inter-molecular 
					}
					else{
						InterMolecular(p, q);
						GlobalUpdate(p);
						GlobalUpdate(q);
						iter++;
					}
				}
			}
			MainGUI.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			System.out.println("Maximum accuracy acheived = "+(100-GlobalMin));
			System.out.println("\nMost informative gene indices are:");
			FileReader special=new FileReader("special20.dat");
			BufferedReader br=new BufferedReader(special);
			String line=br.readLine();
			while(line!=null){
				System.out.print(line+"  ");
				line=br.readLine();
			}
			special.close();
			System.out.println("\n==================END====================");
			MainGUI.graph.setEnabled(true);
			MainGUI.save.setEnabled(true);
			fw.close();
			return GlobalMin;
		}
	
		else{
			System.out.println("Please chose Appropriate file..");
			}
		return GlobalMin;
		
	}
	


	//initialization of initial population size
	void IntitializeIniPopSize() throws Exception {
		Molecule temp=new Molecule();
		for(int j=0; j<ipop;j++){
			temp=initialize(temp);
			mol.add(temp);
		}
	}

	//initialize individial molecule
	Molecule initialize(Molecule temp) throws Exception {
		int num=0; int k=0;
		int index[]=new int[AttrNo];
		temp.molS=new int[sub];
		while(k!=sub){
			for(int j=0; j<sub; j++){
				num=rand.nextInt(AttrNo-1);
				if(index[num]==0 && num!=0){ //random indices of subset size make up the molecular structure
					temp.molS[k]=num;
					index[num]=1;
					k++;
					if(k==sub){
						break;
					}
				}
			}
		}
		temp.PE=fitness(temp.molS);
		temp.numHit=0;
		temp.minHit=0;
		temp.KE=ike;
		GlobalUpdate(temp);
		return temp;
	}

	//calculates fitness value of a member (libsvm cross-validation accuracy)
	double fitness(int[] molS) throws Exception {
	
		Arrays.sort(molS);									//sorting incoming array (molecule structure)
	
		FileWriter fw=new FileWriter("shortsvm.libsvm");	//generating temperory input file for svm_train
		File flib=new File("shortsvm.libsvm");
		flib.deleteOnExit();
		for(int i=0; i< RecordNo; i++){
			fw.write((int)filteredData.instance(i).value(filteredData.classIndex())+" ");
			for(int j=0; j<molS.length;j++){
				fw.write(molS[j]+":"+filteredData.instance(i).value(molS[j])+" ");
			}
			fw.write("\n");
		}
		fw.close();
		//storing SVM command as a String[] as required by svm_train
		String[] svm_command={"-q","-c",Double.toString(c),"-g",Double.toString(g),"-t","2","-v","10","shortsvm.libsvm"};
		double cva=Double.parseDouble(svmtrain.main(svm_command));		//svm_train returns cross-validation accuracy		
		if((100.0-cva) < GlobalMin){
		FileWriter f=new FileWriter("special20.dat");
			for(int i=0; i< sub; i++){
				f.write(molS[i]+"\n");									//writing subset size number of features to file
			}
			f.close();
		}
		return 100-cva;
	}

	void GlobalUpdate(Molecule temp) {
		if(temp.PE<GlobalMin){
			GlobalMin=temp.PE;
		}
	}

	Molecule LocalUpdate(Molecule p) {
	
		if(p.PE<p.localMin){
			p.localMin=p.PE;
			p.minHit=p.numHit;
		}
		return p;
	}

	boolean Synthesis(Molecule p, Molecule q) throws Exception {
	
		p.numHit++;
		p.t1=p.molS;
		p.t2=q.molS;
	
		//perform synthesis
		for(int i=0; i< sub; i++){
			if(rand.nextDouble()>0.5){
				p.t1[i]=p.t2[i];
			}
		}
		p.t1=CheckDuplicate(p.t1);
		double tempPE=fitness(p.t1);
	
		//Energy Check
		double tempBuff=p.PE+p.KE+q.PE+q.KE-tempPE;
		if(tempBuff>=0){
			p.PE=tempPE;
			p.KE=tempBuff;
			p.molS=p.t1;
			p=LocalUpdate(p);
			p.minHit=0;
			p.numHit=0;
			return true;
		}
		return false;
	}

	boolean Decomposition(Molecule p, Molecule q) throws Exception {
	
		p.numHit++;
	
		//copy original molecular structure to temp structures
		p.t1=p.molS;
		p.t2=p.molS;
	
		//modify the temp structures
		for(int i=0; i< sub/2 ;i++){
			Neighbour(p.t1);
			Neighbour(p.t2);
		}
	
		//Evaluate the temp structures
		p.t1=CheckDuplicate(p.t1);
		double tempPE1=fitness(p.t1);
		p.t2=CheckDuplicate(p.t2);
		double tempPE2=fitness(p.t2);
	
		//Energy check
		double tempBuff=p.PE+p.KE-tempPE1-tempPE2;
		if(tempBuff>=0 || (tempBuff+ebuff)>=0){
			if(tempBuff>=0){
				p.KE=tempBuff*rand.nextDouble();
				q.KE=tempBuff-p.KE;
			}
			else{
				ebuff=ebuff+tempBuff;
				p.KE=ebuff*rand.nextDouble()*rand.nextDouble();
				ebuff=ebuff-p.KE;
				q.KE=ebuff*rand.nextDouble()*rand.nextDouble();
				ebuff=ebuff-q.KE;
			}
			p.minHit=0;
			p.numHit=0;
	
			//copy temp structures to the two generated real molecules
			p.PE=tempPE1;
			p.molS=p.t1;
			p=LocalUpdate(p);
			q.PE=tempPE2;
			q.molS=p.t2;
			q=LocalUpdate(q);
			return true;
		}
		return false;
	}

	double OnWallIneffective(Molecule p) throws Exception {
	
		double result=0;
		double tempPE;
		p.numHit++;
		//perform neighbourhood search
		p.t1=p.molS;
		Neighbour(p.t1);
		p.t1=CheckDuplicate(p.t1);
		tempPE=fitness(p.t1);
	
		//Energy check
		double tempBuff=p.PE+p.KE-tempPE;
		if(tempBuff>=0){
			p.PE=tempPE;
			p.KE=tempBuff*(rand.nextDouble()*(1.0-enloss)+enloss);
			p.molS=p.t1;
			p=LocalUpdate(p);
			result+=tempBuff-p.KE;
		}
	
		return result;
	}

	void InterMolecular(Molecule p, Molecule q) throws Exception {

		p.numHit++;
		q.numHit++;
		//perform inter-molecular operator
		p.t1=p.molS;
		p.t2=q.molS;
		Neighbour(p.t1);
		Neighbour(p.t2);
		p.t1=CheckDuplicate(p.t1);
		double tempPE1=fitness(p.t1);
		p.t2=CheckDuplicate(p.t2);
		double tempPE2=fitness(p.t2);

		//Energy check
		double tempBuff=p.PE+p.KE+q.PE+q.KE-tempPE1-tempPE2;

		if(tempBuff>=0){
			p.PE=tempPE1;
			q.PE=tempPE2;
			p.KE=tempBuff*rand.nextDouble();
			q.KE=tempBuff-p.KE;
			p.molS=p.t1;
			q.molS=p.t2;
			p=LocalUpdate(p);
			q=LocalUpdate(q);
		}
	}

	boolean SynthCheck(Molecule p) {
		return (p.KE<synth);
	}

	boolean DecompCheck(Molecule p) {
		return ((p.numHit-p.minHit)>decomp);
	}

	
	void Neighbour(int[] old) {

		int pos=rand.nextInt(sub-1);
		double temp=old[pos]+rand.nextGaussian()*step;
		boolean loop=true;

		while(loop){
			if(temp<lowerBound){
				temp=2*lowerBound-temp;
			}
			else if(temp>upperBound){
				temp=2*upperBound-temp;
			}
			else{
				loop=false;
			}
		}
		if(temp>upperBound){
			temp=upperBound;
		}
		else if(temp<lowerBound){
			temp=lowerBound;
		}
		old[pos]=(int) temp;
	}


	int[] CheckDuplicate(int[] t) {

		for(int i=0; i<sub;i++){
			if(t[i]<lowerBound){
				t[i]=lowerBound;
			}
			else if(t[i]>=upperBound){
				t[i]=upperBound;
			}
		}
		t=RemoveDuplicate(t);
		Arrays.sort(t);
		return t;
	}

	int[] RemoveDuplicate(int[] t) {

		int[] index=new int[AttrNo];
		for(int i=0; i<sub;i++){
			if(index[t[i]]==0){
				index[t[i]]=1;
			}
			else{
				t[i]=RandomPut(index);
				index[t[i]]=1;
			}
		}
		return t;
	}

	int RandomPut(int[] index) {

		int num=rand.nextInt(AttrNo-1);
		while(true){
			if(index[num]==0 && InfoGain[num][1]>0){
				return num;
			}
			else{
				num=rand.nextInt(AttrNo-1);
			}
		}
	}


	
	
	
	
	
}