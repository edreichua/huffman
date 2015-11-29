package cs10;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Created by edreichua on 4/23/15.
 * CS 10 Spring 2015
 * Problem Set 3: Huffman Encoding
 * Huffman class
 */

// Implements Huffman Encoding and Decoding

public class Huffman {

    private static Map<Character,Integer> freqmap; //The frequency map: character -> frequency
    private static BinaryTree<CharFreq> hufftree; //The Huffman tree to be used in decoding
    private static Map<Character,ArrayList<Integer>> finalmap; //The final map to be used in encoding: character
    /**
     * getFilePath
     *
     * Allows the user to choose a file and return with the pathname
     *
     * @return String with the pathname
     */

    public static String getFilePath() {
        JFileChooser fc = new JFileChooser("."); // start at current directory

        int returnVal = fc.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String pathName = file.getAbsolutePath();
            return pathName;
        }
        else
            return "";
    }

    /**
     * CreateMap
     *
     * Static method to generate a frequency map based on the frequency of appearance of the character in the file
     *
     * @param filename
     */

    private static void CreateMap(String filename){
        BufferedReader input = null;
        Map<Character, Integer> freq = new TreeMap<Character, Integer>();

        try{
            input = new BufferedReader(new FileReader(filename)); // Open file
            char curr;
            int uni;

            // Read the file character by character and store in the map
            while ((uni = input.read()) != -1) {
                curr = (char) uni;
                if (freq.containsKey(curr))
                    freq.put(curr, freq.get(curr) + 1);
                else
                    freq.put(curr, 1);
            }
            freqmap = freq;

        }catch(Exception e){ // Catch possible FileNotFound exception
            e.printStackTrace();

        }finally{

            // Close file if file exist. If not, catch the exception
            try{
                input.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * CreateTree
     *
     * Static method to build the Huffman binary tree using a priority queue
     *
     * @return hufftree of BinaryTree<CharFreq> type: the Huffman tree constructed
     */

    private static BinaryTree<CharFreq> CreateTree(){

        // Create a priority queue of nodes with TreeComparator as the comparator
        Comparator<BinaryTree<CharFreq>> comparator = new TreeComparator();
        PriorityQueue<BinaryTree<CharFreq>> pq = new PriorityQueue<>(freqmap.size(),comparator);

        // Create tree node for every character
        for(Map.Entry<Character,Integer> entry: freqmap.entrySet()){
            CharFreq data = new CharFreq(entry.getValue(),entry.getKey());
            BinaryTree<CharFreq> charTree = new BinaryTree<>(data);
            pq.add(charTree);
        }

        // Combine trees in the priority queue if there are more than 2 trees to construct hufftree
        while(pq.size()>=2){
            BinaryTree<CharFreq> T1 = pq.remove();
            BinaryTree<CharFreq> T2 = pq.remove();
            pq.add(new BinaryTree(new CharFreq(T1.data.getFreq()+T2.data.getFreq()),T1,T2));
        }

        return(hufftree = pq.remove());
    }

    /**
     * CodeMap
     *
     * Static method to build the map Character -> code
     *
     */

    private static void CodeMap(){

        BinaryTree<CharFreq> tree = hufftree;
        finalmap = new TreeMap<Character, ArrayList<Integer>>();
        ArrayList<Integer> code = new ArrayList<Integer>();
        CodeMap(tree, code);
    }

    /**
     * CodeMap helper method
     *
     * Static method that uses recursion to build the code map character -> code
     *
     * @param tree
     * @param code
     */

    private static void CodeMap(BinaryTree<CharFreq> tree, ArrayList<Integer> code){

        // Input the character and the code into finalmap if the node is a leaf
        if(tree.isLeaf()){
            finalmap.put(tree.data.getChar(), code);
            return;
        }

        // If the tree has a left node, recurse with left node and add 0 to the new code
        if(tree.hasLeft()){
            ArrayList<Integer> newcode = new ArrayList<Integer>(code);
            newcode.add(0);
            CodeMap(tree.getLeft(), newcode);
        }

        // If the tree has a right node, recurse with right node and add 1 to the new code
        if(tree.hasRight()){
            ArrayList<Integer> newcode = new ArrayList<Integer>(code);
            newcode.add(1);
            CodeMap(tree.getRight(), newcode);
        }
    }

    /**
     * Compress
     *
     * Static method that compresses the file
     *
     * @param filename
     * @return the compressed filename
     */
    private static String Compress(String filename){

        BufferedReader input = null;
        BufferedBitWriter bitOutput = null;
        String compressedpathname = null;

        try{
            input = new BufferedReader(new FileReader(filename));
            compressedpathname = filename.substring(0,filename.length()-4) + "_compressed.txt";
            bitOutput = new BufferedBitWriter(compressedpathname);

            char curr;
            int uni;
            while((uni = input.read()) != -1) { // Read the file
                curr = (char) uni;
                ArrayList<Integer> wbit =  finalmap.get(curr); // Get the code for each character

                for(int i = 0; i < wbit.size();i++){ // Write the bit
                    int bit = wbit.get(i);
                    bitOutput.writeBit(bit);
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }finally{
            // Close file if file exist. If not, catch the exception
            try{
                input.close();
                bitOutput.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return(compressedpathname);
        }
    }

    /**
     * Decompress
     *
     * Static method that decompresses the file
     *
     * @param filename
     * @return the decompressed filename
     */

    private static String Decompress(String filename){

        BufferedBitReader bitInput = null;
        String decompressedpathname = null;
        BufferedWriter output = null;

        try{
            bitInput = new BufferedBitReader(filename);
            decompressedpathname = filename.substring(0,filename.length()-15) + "_decompressed.txt";
            output = new BufferedWriter(new FileWriter(decompressedpathname));
            int curr;
            BinaryTree<CharFreq> tree = hufftree;

            while((curr = bitInput.readBit())!=-1){ // Read the compressed file
                if(curr==0){                        // Get left node if code is 0
                    tree = tree.getLeft();
                }else{                              // Get right node if code is 1
                    tree = tree.getRight();
                }
                if(tree.isLeaf()) {                 // if node is a leaf, write character to output
                    output.write(tree.getData().getChar());
                    tree = hufftree;                // return to the top of the tree
                }
            }
        }catch(Exception e){
            e.printStackTrace();

        }finally{
            // Close file if file exist. If not, catch the exception
            try{
                bitInput.close();
                output.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return(decompressedpathname);
        }

    }

    public static void main(String[] args){

        // Get the file
        String filename = getFilePath();

        // Check if it is a txt file
        if(filename.length()<4 && filename.substring(filename.length()-5,filename.length()-1)!=".txt"){
            System.err.println("Only valid for file of type .txt");
            return;
        }

        //Create the frequency map
        CreateMap(filename);

        //Exit out of main function if the file contains less than 2 unique character
        if(freqmap.size()<2){
            System.err.println("File contains less than 2 unique characters. Unable to perform Huffman compression");
            return;
        }

        // Create the Huffman tree
        CreateTree();

        // Create the code map
        CodeMap();

        //Print the frequency map, the huffman tree and the code map
        System.out.println("------------Frequency Map-------------");
        System.out.println(freqmap.toString());
        System.out.println("------------Huffman tree-------------");
        System.out.println(hufftree.toString());
        System.out.println("------------Code Map-------------");
        System.out.println(finalmap.toString());

        // Compress file
        String compressname = Compress(filename);
        System.out.println(compressname);

        // Decompress file
        String decompressname = Decompress(compressname);
        System.out.println(decompressname);

    }
}

