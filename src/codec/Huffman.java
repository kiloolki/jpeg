package codec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {

	private Queue<Node> coda;
	private Map<Object, String> prefixCodes;

	public Huffman() {
		this.coda = new PriorityQueue<Node>();
		this.prefixCodes = new HashMap<Object, String>();
	}

	public boolean addSymbol(Object symbol) {
		// un nodo per il simbolo
		Node node = null;
		// proviamo a vedere se c'è il nodo in base al simbolo
		Iterator<Node> iterator = this.getQueue().iterator();
		while (iterator.hasNext()) {
			Node n = iterator.next();
			if (symbol.equals(n.getSimbolo()))
				node = n;
		}
		// se non c'è lo dobbiamo aggiungere
		if (node == null) {
			node = new Node(symbol, 1);
			this.getQueue().add(node);
			return true;
		}
		// aggiorniamo la frequenza
		node.setFrequenza(node.getFrequenza() + 1);
		return false;
	}

	public Node buildTree() {
		int n = this.getQueue().size();
		for (int i = 0; i < n - 1; i++) {
			Node node = new Node();
			node.setLeftChild(this.extractMin());
			node.setRightChild(this.extractMin());
			node.setFrequenza(node.getLeftChild().getFrequenza()
					+ node.getRightChild().getFrequenza());
			this.getQueue().add(node);
		}
		return this.extractMin();
	}

	public Node extractMin() {
		return this.getQueue().poll();
	}

	public void buildCodeTable(Node node, String prefixCode) {
		if (node.isLeaf())
			this.getPrefixCodes().put(node.getSimbolo(), prefixCode);
		else {
			this.buildCodeTable(node.getLeftChild(), prefixCode + "0");
			this.buildCodeTable(node.getRightChild(), prefixCode + "1");
		}
	}

	public Queue<Node> getQueue() {
		return coda;
	}

	public void setCoda(Queue<Node> queue) {
		this.coda = queue;
	}

	public Map<Object, String> getPrefixCodes() {
		return prefixCodes;
	}

	public void setPrefixCodes(Map<Object, String> prefixCodes) {
		this.prefixCodes = prefixCodes;
	}

	
	/*
	public static void main(String[] args) {
		Node[] nodes = new Node[] { new Node("a", 45), new Node("b", 13),
				new Node("c", 12), new Node("d", 16), new Node("e", 9),
				new Node("f", 5) };
		Huffman h = new Huffman();
		for (Node node : nodes)
			h.getQueue().add(node);
		Node root = h.buildTree();
		System.out.println(root.getFrequency());
		h.buildCodeTable(root, "");
		for (Entry<Object, String> entry : h.getPrefixCodes().entrySet())
			System.out.println(String.format("%s: %s", entry.getKey()
					.toString(), entry.getValue()));
	}
*/
}
