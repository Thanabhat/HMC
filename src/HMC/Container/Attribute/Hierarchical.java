package HMC.Container.Attribute;

import java.util.HashMap;

public class Hierarchical extends Attribute {

	HierarchicalNode root;
	public HashMap<String, HierarchicalNode> hierarchicalMapping;

	public Hierarchical() {
		// TODO Auto-generated constructor stub
		root = new HierarchicalNode(0);
		hierarchicalMapping = new HashMap<String, HierarchicalNode>();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "hierarchical";
	}

	@Override
	public Object getPossibleValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setPossibleValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addNode(String nodePath) {
		HierarchicalNode node = addNode(root, nodePath);
		node.setFullId(nodePath);
		hierarchicalMapping.put(nodePath, node);
	}

	public HierarchicalNode addNode(HierarchicalNode node, String nodePath) {
		String[] splitedNodePath = nodePath.split("/", 2);
		for (HierarchicalNode child : node.children) {
			if (child.id.equals(splitedNodePath[0])) {
				if (splitedNodePath.length > 1) {
					return addNode(child, splitedNodePath[1]);
				}
			}
		}
		HierarchicalNode newNode = new HierarchicalNode(node.getLevel()+1);
		newNode.setId(splitedNodePath[0]);
		newNode.addParent(node);
		node.addChildren(newNode);
		if (splitedNodePath.length > 1) {
			return addNode(newNode, splitedNodePath[1]);
		}
		return newNode;
	}

	public void rebuildMapping() {
		// root.fullId = root.id;
		// hierarchicalMapping.put(root.fullId, root);
		rebuildMapping(root, "");
	}

	private void rebuildMapping(HierarchicalNode node, String path) {
		for (HierarchicalNode child : node.children) {
			String fullId = path;
			fullId += path.equals("") ? "" : "/";
			fullId += child.id;
			child.fullId = fullId;
			hierarchicalMapping.put(fullId, child);
			rebuildMapping(child, fullId);
		}
	}

	public void printHierarchical() {
		printHierarchical(root, 0);
	}

	private void printHierarchical(HierarchicalNode node, int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("| ");
		}
		System.out.println("|-" + node.id);
		for (HierarchicalNode child : node.children) {
			printHierarchical(child, level + 1);
		}
	}
}
