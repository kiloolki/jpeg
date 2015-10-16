package codec;

public class Node implements Comparable<Node> {

	private int frequenza;
	private Object simbolo;
	private Node leftChild, rightChild;

	public Node() {
		this.frequenza = 0;
		this.simbolo = null;
	}

	public Node(Object symbol, int frequency) {
		this.simbolo = symbol;
		this.frequenza = frequency;
	}

	public boolean isLeaf() {
		return this.getLeftChild() == null && this.getRightChild() == null;
	}

	@Override
	public int compareTo(Node o) {
		return new Integer(this.getFrequenza()).compareTo(o.getFrequenza());
	}

	public int getFrequenza() {
		return frequenza;
	}

	public void setFrequenza(int frequency) {
		this.frequenza = frequency;
	}

	public Object getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(Object symbol) {
		this.simbolo = symbol;
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

}
