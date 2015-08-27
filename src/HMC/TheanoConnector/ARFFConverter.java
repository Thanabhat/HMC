package HMC.TheanoConnector;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import HMC.Utility;
import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.Attribute;
import HMC.Container.Attribute.Hierarchical;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Attribute.NominalAttribute;
import HMC.Container.Attribute.NumericAttribute;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.HierarchicalParameter;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;
import HMC.Container.Data.Parameter;
import HMC.Reader.ARFFReader;

public class ARFFConverter {

	private static int classCount = 0;
	private static int attrCount = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final String dataset = "eisen_FUN";
		final boolean useMLNP = true;

		HMCDataContainer dataTrain = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".train.arff");
		HMCDataContainer dataValid = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".valid.arff");
		HMCDataContainer dataTest = ARFFReader.readFile("datasets/datasets_FUN/"+dataset+"/"+dataset+".test.arff");
		 
		if (useMLNP) {
			Utility.createMandatoryLeafNode(dataTrain);
			Utility.createMandatoryLeafNode(dataValid);
			Utility.createMandatoryLeafNode(dataTest);
		}
		
		dataTrain.hierarchical.printHierarchical();
		
		 //normalize numeric data
		Utility.numericalNormalizer(new HMCDataContainer[]{dataTrain,dataValid,dataTest}, false);

		countClass(dataTrain.hierarchical.root, useMLNP);
		countAttr(dataTrain);
		
		printJOHMCFF(dataTrain, "datasets/datasets_FUN/"+dataset+"/"+dataset+".train.johmcff", useMLNP);
		printJOHMCFF(dataValid, "datasets/datasets_FUN/"+dataset+"/"+dataset+".valid.johmcff", useMLNP);
		printJOHMCFF(dataTest, "datasets/datasets_FUN/"+dataset+"/"+dataset+".test.johmcff", useMLNP);
	}
	
	private static void countClass(HierarchicalNode node, boolean useMLNP) {
		for (HierarchicalNode child : node.children) {
			if (!useMLNP || child.isLeaf()) {
				classCount++;
			}
			countClass(child, useMLNP);
		}
	}
	
	private static void countAttr(HMCDataContainer dataContainer){
		for(Attribute attr: dataContainer.attributes){
			if(attr instanceof NumericAttribute){
				attrCount++;
			}else if(attr instanceof NominalAttribute){
				attrCount+=((NominalAttribute)attr).getPossibleValue().size();
			}
		}
	}
	
	private static void printClass(HierarchicalNode node, PrintWriter writer, boolean useMLNP) {
		for (HierarchicalNode child : node.children) {
			if (!useMLNP || child.isLeaf()) {
				writer.println(child.getFullId());
			}
			printClass(child, writer, useMLNP);
		}
	}
	
	private static void printJOHMCFF(HMCDataContainer dataContainer, String filepath, boolean useMLNP) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(filepath, "UTF-8");
		
		writer.println(classCount);
		printClass(dataContainer.hierarchical.root, writer, useMLNP);
		
		writer.println(dataContainer.dataEntries.size()+" "+attrCount);
		for(DataEntry dataEntry:dataContainer.dataEntries){
			for(Parameter param:dataEntry.parameters){
				if(param instanceof NumericParameter){
					if(param.hasValue()){
						writer.print(doubleFormat((Double)param.getValue()));
						writer.print(" ");
					}else{
						writer.print(doubleFormat(0.5));
						writer.print(" ");
					}
				}else if(param instanceof NominalParameter){
					ArrayList<String> possibleValue = ((NominalAttribute)(((NominalParameter)param).getAttribute())).getPossibleValue();
					for(String value:possibleValue){
						if(param.getValue()==null){
							writer.print(doubleFormat(0.5));
							writer.print(" ");
						}else if(value.equalsIgnoreCase((String)param.getValue())){
							writer.print(doubleFormat(1.0));
							writer.print(" ");
						}else{
							writer.print(doubleFormat(0.0));
							writer.print(" ");
						}
					}
				}else if(param instanceof HierarchicalParameter){
					ArrayList<HierarchicalNode> nodeList = ((HierarchicalParameter) param).getValue();
					if (useMLNP) {
						for (int i = nodeList.size() - 1; i >= 0; i--) {
							if (!nodeList.get(i).isLeaf()) {
								nodeList.remove(i);
							}
						}
					}
					String[] buf = new String[nodeList.size()];
					for(int i=0;i<nodeList.size();i++){
						buf[i]=nodeList.get(i).getFullId();
					}
					Arrays.sort(buf);
					writer.print(joinString(buf,"@"));
				}
			}
			writer.println();
		}
		
		writer.close();
	}
	
	private static String doubleFormat(double value)
	{
	    return String.format("%.10f", value) ;
	}
	
	private static String joinString(String[] strs, String delimeter){
	    StringBuilder sbStr = new StringBuilder();
	    for (int i = 0, il = strs.length; i < il; i++) {
	        if (i > 0)
	            sbStr.append(delimeter);
	        sbStr.append(strs[i]);
	    }
	    return sbStr.toString();
	}

}
