/*
        This class defines a structure of blockchain as a list of blocks in which first block is genesis block which contains only coinbase transaction
*/
package BlockChain;

import java.util.ArrayList;


public class blockChain
{
    public ArrayList<Block> chain = new ArrayList<>();
    public Transaction coinBaseTransaction;
    
    /*
        Creating a new blockchain by generating genesis block 
    */
    public blockChain() 
    {
        Block genesis=createGenesisBlock(); //creating genesis block
        genesis.mineBlock(Run.difficulty);  //Mine this using bitcoin protocols
        addtochain(genesis);                //add this block to chain
    }
    /*
            This function creates genesis block as bitcoin genesis block created in Jan 2009 by Satoshi Nakamoto
    */
    public final Block createGenesisBlock()
    {
        Block blk=new Block("0",0);     //Create a new block with previous hash "0" and 0 index 
        /*
                Create a new transaction using the input as the headline of newspaper
                this transaction releases 50 BTC to Pushkar
        */
        ArrayList<TransactionInput> inputs=new ArrayList<>();
        inputs.add(new TransactionInput("The Times 03/Jan/2009 Chancellor on brink of second bailout for banks["));
        coinBaseTransaction=new Transaction(Run.Pushkar.PublicAddress,Run.Pushkar.PublicAddress,50,inputs);
        coinBaseTransaction.generateSignature(Run.Pushkar.privateKey);
        coinBaseTransaction.transactionId="0";
        coinBaseTransaction.outputs.add(new TransactionOutput(coinBaseTransaction.reciepient,coinBaseTransaction.value,coinBaseTransaction.transactionId));
        for(TransactionOutput o:coinBaseTransaction.outputs)
        {
            Run.UTXOs.put(o.id, o); //Update the gloabal UTXOs
        }
        blk.addTransaction(coinBaseTransaction);        //Add this transaction to the block
        blk.setMerkleRoot(StringUtil.getMerkleRoot(blk.getTransactions())); //Set merkle root
        return blk; 
    }
    //Adding a block to blockchain
    public final void addtochain(Block b)
    {
        chain.add(b);
    }
    /*
        This function checks whether this block is valid or not
    */
    public boolean isBlockValid(Block b,int difficulty) 
    {
	Block currentBlock=b; 
	Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
	
	previousBlock =chain.get(chain.size()-1);
       	//compare previous hash and registered previous hash
	if(!previousBlock.hash.equals(currentBlock.previousHash) ) 
        {
            System.out.println("Previous Hashes not equal");
            return false;
	}
        //Check whether this block is mined or not
        if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) 
        {
            System.out.println("This block hasn't been mined");
            return false;
	} 
        return true;	
    }    
    
    /*
        This function displays the whole blockchain
    */
    @Override
    public String toString() {
        for(Block b:chain)
        {
            System.out.println("\n\n"+b.toString());
        }
        return "";
    }
    
}
