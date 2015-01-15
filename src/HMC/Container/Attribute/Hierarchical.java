package HMC.Container.Attribute;

import java.util.HashMap;

public class Hierarchical extends Attribute {

	HierarchicalNode root;
	public HashMap<String, HierarchicalNode> hierarchicalMapping;

	public Hierarchical() {
		// TODO Auto-generated constructor stub
		root = new HierarchicalNode();
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
		// String[] splitedNodePath = nodePath.split("/", 2);
		//
		// if (root.id == null) {
		// root.id = splitedNodePath[0];
		// } else if (!root.id.equals(splitedNodePath[0])) {
		// // error, shouldn't reach this condition
		// return;
		// }
		// HierarchicalNode node = root;
		// if (splitedNodePath.length > 1) {
		// node = addNode(root, splitedNodePath[1]);
		// }
		// node.setFullId(nodePath);
		// hierarchicalMapping.put(nodePath, node);

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
		HierarchicalNode newNode = new HierarchicalNode();
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
