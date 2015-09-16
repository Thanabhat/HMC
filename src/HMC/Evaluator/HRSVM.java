package HMC.Evaluator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import HMC.Container.HMCDataContainer;
import HMC.Container.Attribute.HierarchicalNode;
import HMC.Container.Data.DataEntry;

public class HRSVM {
	public static void exportToHRSVM(HMCDataContainer hmcDataContainer, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		exportHierarchicalFile(hmcDataContainer, filePath + ".hrsvm.model");
		exportTrueClassFile(hmcDataContainer, filePath + ".hrsvm.true");
		exportPredictionClassFile(hmcDataContainer, filePath + ".hrsvm.pred");
	}

	private static void exportHierarchicalFile(HMCDataContainer hmcDataContainer, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		for (HierarchicalNode node : hmcDataContainer.hierarchical.root.children) {
			if (node.children.size() == 0) {
				writer.println(node.getFullId() + " DUMMY");
			} else {
				printHierarchical(writer, node);
			}
		}
		writer.close();
	}

	private static void printHierarchical(PrintWriter writer, HierarchicalNode node) {
		for (HierarchicalNode child : node.children) {
			writer.println(node.getFullId() + " " + child.getFullId());
			printHierarchical(writer, child);
		}
	}

	private static void exportTrueClassFile(HMCDataContainer hmcDataContainer, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		for (DataEntry dataEntry : hmcDataContainer.dataEntries) {
			boolean firstNode = true;
			for (HierarchicalNode node : dataEntry.label) {
				if (!firstNode) {
					writer.print(" ");
				}
				writer.print(node.getFullId());
				firstNode = false;
			}
			writer.println();
		}
		writer.close();
	}

	private static void exportPredictionClassFile(HMCDataContainer hmcDataContainer, String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(filePath, "UTF-8");
		for (DataEntry dataEntry : hmcDataContainer.dataEntries) {
			boolean firstNode = true;
			for (HierarchicalNode node : dataEntry.predictedLabel) {
				if (!firstNode) {
					writer.print(" ");
				}
				writer.print(node.getFullId());
				firstNode = false;
			}
			writer.println();
		}
		writer.close();
	}
}
