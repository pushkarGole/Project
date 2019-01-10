/*
        This class performs defined a structure of tranaction and perform required operations of transactions  
*/
package BlockChain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


public class Transaction implements Serializable //Serializing the Transaction to send over the network in the form of bytes
{	
    private static final long serialVersionUID=2422789860422731812L;
    public String transactionId; // this is the hash of the transaction.
    public String sender; // senders Publicaddress.
    public String reciepient; // Recipients Public address
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>(); 
    /*
            Constructing a transaction using specfied sender, receiver, value, and a list of inputs that are required to complete this transaction 
    */
    public Transaction(String from, String to, float value,  ArrayList<TransactionInput> inputs) 
    {
	this.sender = from;
	this.reciepient = to;
	this.value = value;
	this.inputs = inputs;
    }
    /*
        Return the content of Transaction output in the form of string
    */
    @Override
    public String toString() {
        return "Transaction{" + "transactionId=" + transactionId + ", sender=" + sender + ", reciepient=" + reciepient + ", value=" + value + ", signature=" + signature.toString()+'}';
    }
    /*
            Getter and setter functions for various fields of transactions
    */
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciepient() {
        return reciepient;
    }

    public void setReciepient(String reciepient) {
        this.reciepient = reciepient;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<TransactionOutput> outputs) {
        this.outputs = outputs;
    }
    /*
        This Calculates the transaction hash (which will be used as its Id) using SHA-256 hashing algorithm
    */
    public String calulateHash() 
    {
	return StringUtil.applySha256(Float.toString(value) + sender+reciepient );
    }
    /*
        This function generate the signature of the transaction using ECDSA algorithm with the help of Private key of the sender
    */
    public void generateSignature(PrivateKey privateKey) 
    {
	String data =sender + reciepient + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);
    }
    /*
        Verifies the signature of the transaction using Public key of sender with the help of ECDSA signature verification algorithm
    */
    public boolean verifiySignature(PublicKey key) 
    {
        String data = sender + reciepient + Float.toString(value);
        return StringUtil.verifyECDSASig(key, data, signature);     
    }   
    /*
        This functions process the transaction and creates two outputs (if necessary, in case of partial payment against a UTXO)
        one for receiver with specified value of transaction and second is for sender with leftover amount
        and finally updates the global UTXOs
    */
    public boolean processTransaction() 
    {
	//Gather transaction inputs (Make sure they are unspent
	for(TransactionInput i : inputs) 
        {
            i.UTXO = Run.UTXOs.get(i.transactionOutputId);
        }
	if(getInputsValue() < Run.minimumTransaction) 
        {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
	}
        //generate transaction outputs:
	float leftOver = getInputsValue() - value; //get value of inputs then the left over change
	transactionId = calulateHash();
	outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //send value to recipient
	outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender				
	//add outputs to Unspent list
	for(TransactionOutput o : outputs) 
        {
            Run.UTXOs.put(o.id , o);
	}	
	//remove transaction inputs from UTXO lists as spent
	for(TransactionInput i : inputs) 
        {
            Run.UTXOs.remove(i.UTXO.id);
	}
	return true;
    }
	
    /*
        This function returns sum of inputs(UTXOs) values
    */
    public float getInputsValue() 
    {
	float total = 0;
	for(TransactionInput i : inputs) 
        {
		total += i.UTXO.value;
	}
	return total;
    }

    /*
        This function returns sum of outputs
    */
    public float getOutputsValue() 
    {
	float total = 0;
	for(TransactionOutput o : outputs) 
        {
            total += o.value;
	}
	return total;
    }

}

