package cs10;

/**
 * Created by edreichua on 4/23/15.
 * CS 10 Spring 2015
 * Problem Set 3: Huffman Encoding
 * CharFreq class
 */

// CharFreq data type to store a character and a frequency

public class CharFreq{
    private Integer k; // k is the frequency and is the key for constructing the Huffman tree
    private Character v; // v is the character

    // Constructor for CharFreq given input integer and character
    public CharFreq(Integer k,Character v){
        this.k = k;
        this.v = v;
    }

    // Constructor for non-leaf node of binary tree of CharFreq type. Non-leaf node will have only frequency and not character
    public CharFreq(Integer k){
        this.k = k;
        this.v = null;
    }

    // Getter method for character
    public Character getChar(){
        return(v);
    }

    // Getter method for frequency
    public Integer getFreq(){
        return(k);
    }

    // Override the toString method
    public String toString(){

        if(v==null)
            return("Frequency: "+ k.toString()+"; Char: NULL");
        else
            return("Frequency: "+ k.toString()+"; Char: "+v);

    }
}