/*
        This class deals with operations on wallet such as generating initial key pairs, sending funds, check balance, perform miner's functioning 
*/
package BlockChain;

import java.util.*;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet 
{
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public String PublicAddress;
    public String password;
    public String name;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<>();           //only UTXOs owned by this wallet.
    
    /*
        Wallet constructor that initialize the fields of the wallet to the required information such as Userid,password and also creates a pair of 
        keys that are used by wallet to send funds.
    */
    public Wallet(String s,String password)
    {
        generateKeyPair();
        PublicAddress=s;
        this.password=password;
        this.name=s.toUpperCase();
    }
    /*
        This function creates a pair of keys, using the Elliptic curve cryptography, that are used by wallet to send funds.
    */
    public void generateKeyPair() 
    {
        try 
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC"); //Getting an instance of ECDSA key generator provided by BouncyCastle 
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");           //Generating secure random number using SHA1PRNG
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");     // Generating Elliptic curve of prive192v1 standard
            keyGen.initialize(ecSpec, random);                                    //Intializing key generator algorithm with elliptic curve specification and randomness
	    KeyPair keyPair = keyGen.generateKeyPair();                           // Set the public and private keys from the keyPair
	    privateKey = keyPair.getPrivate();                                   // Set the public and private keys from the keyPair
	    publicKey = keyPair.getPublic();
        }
        catch(Exception e) 
        {
            throw new RuntimeException(e);
	}
    }
	/*
           This function gives the balance and stores the UTXO's owned by this wallet
        */
	public float getBalance() 
        {
            float total = 0;	
            for (Map.Entry<String, TransactionOutput> item: Run.UTXOs.entrySet())       //Collecting all UTXOs which are mapped to this wallet by iterating over all global UTXOs
            {
        	TransactionOutput UTXO = item.getValue();   //Fetching the value of UTXO
                if(UTXO.isMine(PublicAddress))              //Checking that UTXO is mapped to this wallet's public address
                { 
                    UTXOs.put(UTXO.id,UTXO);            //Put this UTXO in wallet's local UTXO list
                    total += UTXO.value ; 
                } 
            
            }  
            return total;
	}
	/*
                    Generates a transaction of specified amount from this wallet to recipient
        */ 
	public Transaction sendFunds(String _recipient,float value ) 
        {
            if(getBalance() < value)    //gather balance and check funds.
            { 
		System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
		return null;
            }
            ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
            float total = 0;
            for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()) //Fetching the required inputs to complete this transaction by itertaing over wallet's UTXO 
            {
		TransactionOutput UTXO = item.getValue();
		total += UTXO.value;
		inputs.add(new TransactionInput(UTXO.id));      //adding the these UTXOs in the inputs of this transaction till the totl sum is just greater or equal to value specfied in transaction 
		if(total > value)
                    break;
            }	
            Transaction newTransaction = new Transaction(PublicAddress, _recipient , value, inputs);    //Creating a new transaction to recipient ith specified value and required inputs 
            newTransaction.generateSignature(privateKey);       //Sigining the transaction for authorization
            for(TransactionInput input: inputs)
            {
		UTXOs.remove(input.transactionOutputId);        //Remove these UTXO which are used in this transaction as transaction input to avoid double spending 
            }
		return newTransaction;
	}
        /*
            This function allows miners to create a new block and mine this block according to Bitcoin protocols
        */
        public Block startMining()
        {
            Block b=new Block(Run.bchain.chain.get(Run.bchain.chain.size()-1).hash,Run.bchain.chain.size());    //Create a new block with previous hash as last block's hash and index is one greater than last block
            ArrayList<Transaction> transactions=new ArrayList<>();
            transactions=Run.db.RetrieveTransactions();     //Retrieve all unconfirmed transactions from transction pool
            
            /*
                Create a coinbase transaction that gives a reward of 25 BTC to miner for creating a mined block
            */ 
            ArrayList<TransactionInput> inputs=new ArrayList<>();
            inputs.add(new TransactionInput(Integer.toString(Run.bchain.chain.size())));
            Transaction coinBaseTransaction=new Transaction(PublicAddress, PublicAddress,25,inputs);
            coinBaseTransaction.transactionId=coinBaseTransaction.calulateHash();
            coinBaseTransaction.outputs.add(new TransactionOutput(coinBaseTransaction.reciepient,coinBaseTransaction.value,coinBaseTransaction.transactionId));
            coinBaseTransaction.generateSignature(privateKey);
            for(TransactionOutput o : coinBaseTransaction.outputs) 
            {
		Run.UTXOs.put(o.id , o);        //Adding outputs of this transaction to global UTXOs
            }
            b.addTransaction(coinBaseTransaction);  //Add this transaction as first transaction of the newly created block
            for(Transaction t:transactions)
            {
                b.addTransaction(t);        //Add all unconfirmed transactions to this block
            }
            b.merkleRoot=StringUtil.getMerkleRoot(transactions);    //Setting merkle root of the block
            b.mineBlock(Run.difficulty);    //Mining the block using Proof-of-work algorithm
            return b;
        }
        
}


