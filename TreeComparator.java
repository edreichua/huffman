package cs10;

import java.util.Comparator;

/**
 * Created by edreichua on 4/23/15.
 * CS 10 Spring 2015
 * Problem Set 3: Huffman Encoding
 * TreeComparator class
 */

// Tree comparator class for the priority queue. Compare based on frequency (of the CharFreq data type) stored in the node
//      of two binary trees.

public class TreeComparator implements Comparator<BinaryTree<CharFreq>> {

    // Compare binary tree t1 and t2 based on the frequency
    public int compare(BinaryTree<CharFreq> t1, BinaryTree<CharFreq> t2){
        int f1 = t1.data.getFreq();
        int f2 = t2.data.getFreq();
        if(f1<f2) return -1;
        else if (f1>f2) return 1;
        else return 0;
    }
}
