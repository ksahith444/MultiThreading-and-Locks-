package petersonNthread;

public class Tree {
	
	int flag = 0;
	Node root;
	
	public Node buildBinaryTree(int low, int high, int[] arr)
	{       
	    if(low > high)
	        return null;
	    else
	    {
	        int mid = (low + high)/2;
	        Node node = new Node(arr[mid]);
	        if(flag == 0)
	        {
	            root = node;
	            flag++;
	        }
	        node.leftChild = buildBinaryTree(low,(mid-1),arr);
	        node.rightChild = buildBinaryTree((mid+1),high,arr);
	        return node;
	    }
	}

	public int findParent(int key)
	{
	    if(root.key == key)
	        return  -1;
	    Node helpnode = root;

	    while(helpnode.leftChild.key != key && helpnode.rightChild.key != key)
	    {
	        if(key < helpnode.key)
	        {
	            helpnode = helpnode.leftChild;
	        }
	        else
	        {
	            helpnode = helpnode.rightChild;
	        }
	    }

	    return helpnode.key;
	}


}
