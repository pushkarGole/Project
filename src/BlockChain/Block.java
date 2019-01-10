/*
    This is the class which deals the various operation performed on a block of a blockchain such as mining a block and adding a transaction to a block
*/
package BlockChain;

import java.io.Serializable;    
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable  //Serializing the block to send over the network in the form of bytes
{
    private static final long serialVersionUID=2422789860422731812L;    
    public int index;
    public String hash;
    public String previousHash; 
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); 
    public long timeStamp; 
    public int nonce;
    /*
        Constructing a new block using specfied parameters
    */
    public Block(String previousHash,int index ) 
    {
	this.index=index;
	this.previousHash = previousHash;
	this.timeStamp = new Date().getTime();  //as number of milliseconds since 1/1/1970.
	this.hash=calculateHash();
    }
    
    /*
            Getter and setter functions for various fields of block
    */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
    /*
        Calculating hash of the block using SHA-256 algorithm
    */    
    public String calculateHash() 
    {
	String calculatedhash = StringUtil.applySha256( previousHash + Long.toString(timeStamp) + Integer.toString(nonce));
	return calculatedhash;
    }
    /*
        Return the content of block in the form of string
    */
    @Override
    public String toString() {
        return "Block{" + "index=" + index + ", hash=" + hash + ", previousHash=" + previousHash + ", merkleRoot=" + merkleRoot + ",\n transactions=" + transactions + ", timeStamp=" + timeStamp + ", nonce=" + nonce + '}';
    }
    /*
        Runs the Proof-of-work algorithm on the block
    */    
    public void mineBlock(int difficulty) 
    {
        String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty "0"s 
	while(!hash.substring( 0, difficulty).equals(target)) 
        {
            nonce ++;       //Include a proof of work according to bitcoin protocol
            hash = calculateHash();
	}
    }
    public boolean addTransaction(Transaction transaction) 
    {
	//Add transaction and check if null
	if(transaction == null) 
            return false;		
	transactions.add(transaction);
            return true;
    }
	
 
}
