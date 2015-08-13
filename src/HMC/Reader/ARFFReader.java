package HMC.Reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import HMC.Container.*;
import HMC.Container.Attribute.*;
import HMC.Container.Data.DataEntry;
import HMC.Container.Data.HierarchicalParameter;
import HMC.Container.Data.NominalParameter;
import HMC.Container.Data.NumericParameter;
import HMC.Container.Data.StringParameter;

public class ARFFReader {
	public static HMCDataContainer readFile(String filePath) throws IOException {
		HMCDataContainer data = new HMCDataContainer();

		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		String line;
		while ((line = reader.readLine()) != null) {
			String[] splited = line.split("\\s+");
			// System.out.println(line);
			switch (splited[0].toLowerCase()) {
			case "@relation":
				data.setRelation(splited[1]);
				break;
			case "@attribute":
				Attribute attr = getAttribute(splited);
				data.addAttribute(attr);
				if (attr.getType().equals("hierarchical")) {
					// should be reach only once
					data.setHierarchical((Hierarchical) attr);
				}
				break;
			case "@data":
				if(null != data.hierarchical){
					data.hierarchical.rebuildMapping();
				}
				while ((line = reader.readLine()) != null) {
					data.addDataEntry(getDataEntry(line, data));
				}
				break;
			default:
				break;
			}
		}

		reader.close();
		
		return data;
	}

	private static Attribute getAttribute(String[] splited) {
		if (splited[2].contains("{")) {
			return getNominalAttribute(splited);
		} else if (splited[2].equalsIgnoreCase("numeric")) {
			return getNumericAttribute(splited);
		} else if (splited[2].equalsIgnoreCase("hierarchical")) {
			// should be reach only once
			return getHierarchical(splited);
		} else if (splited[2].equalsIgnoreCase("string")) {
			return getStringAttribute(splited);
		}
		return null;
	}

	private static NumericAttribute getNumericAttribute(String[] splited) {
		NumericAttribute attribute = new NumericAttribute();
		attribute.setName(splited[1]);
		return attribute;
	}

	private static NominalAttribute getNominalAttribute(String[] splited) {
		NominalAttribute attribute = new NominalAttribute();
		attribute.setName(splited[1]);
		attribute.addPossibleValue(splited[2].replace("{", "").replace("}", "")
				.split(","));
		return attribute;
	}

	private static Hierarchical getHierarchical(String[] splited) {
		Hierarchical h = new Hierarchical();
		h.setName(splited[1]);
		for (String str : splited[3].replace(" ", "").split(",")) {
			h.addNode(str);
		}
		return h;
	}

	private static StringAttribute getStringAttribute(String[] splited) {
		StringAttribute attribute = new StringAttribute();
		attribute.setName(splited[1]);
		return attribute;
	}

	private static DataEntry getDataEntry(String line, HMCDataContainer container) {
		String[] splited = line.replace(" ", "").split(",");
		DataEntry dataEntry = new DataEntry();
		for (int i = 0; i < container.attributes.size(); i++) {
			Attribute attr = container.attributes.get(i);
			if (attr instanceof NumericAttribute) {
				dataEntry.addParameter(new NumericParameter(splited[i], attr));
			} else if (attr instanceof NominalAttribute) {
				dataEntry.addParameter(new NominalParameter(splited[i], attr));
			} else if (container.attributes.get(i) instanceof Hierarchical) {
				HierarchicalParameter param = new HierarchicalParameter(splited[i], attr, dataEntry);
				dataEntry.addParameter(param);
				dataEntry.setLabel(param.getValue());
				dataEntry.setRawLabel(param.getRawValue());
			} else if (attr instanceof StringAttribute) {
				dataEntry.addParameter(new StringParameter(splited[i], attr));
			}
		}
		return dataEntry;
	}

}
