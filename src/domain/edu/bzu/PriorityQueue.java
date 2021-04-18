package domain.edu.bzu;

public class PriorityQueue {
	
	final int DEFAULTMAX = 256;

	HuffmanNode [] nodes;
	int capacity,total;

	public PriorityQueue(){
		capacity = DEFAULTMAX;
		total = 0;
		nodes = new HuffmanNode[capacity];
	}
	public PriorityQueue(int max){
		capacity = max;
		total = 0;
		nodes = new HuffmanNode[capacity];

	}

	public boolean Enqueue(HuffmanNode hNode){

		if(isFull()) return false;

		if(isEmpty()){ //first element?
			nodes[total++] = hNode;
			return true;
		}
		int i = total-1,pos;
		while(i >= 0){
			if(nodes[i].freq < hNode.freq){
				break;
				}
			i--;
			}
		pos = total-1;
		while(pos >= i+1){
			nodes[pos+1] = nodes[pos];
			pos--;
			}
		nodes[i+1] = hNode;
		total++;
		return true;
	}

	public HuffmanNode Dequeue(){

		if(isEmpty()) return null;
		HuffmanNode ret = nodes[0];
		total--;
		for(int i = 0;i<total;i++)
		nodes[i] = nodes[i+1];
		return ret;
		}

	public boolean isEmpty(){
		return (total == 0);
		}

	public boolean isFull(){
		return (total == capacity);
		}

	public int totalNodes(){
		return total;
		}

	//debug
	public void displayQ(){
	for(int i=0;i<total;i++){
		System.out.println("Q" + i + ") " + nodes[i].ch + " : " + nodes[i].freq);
		}	

		}

}
