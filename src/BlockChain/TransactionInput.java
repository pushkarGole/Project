/*
        A structure defined for transaction input
*/
package BlockChain;

import java.io.Serializable;


class TransactionInput implements Serializable //Serializing the Transaction input to send over the network in the form of bytes
{
    //private static final long serialVersionUID=2422789860422731812L;
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public TransactionOutput UTXO; //Contains the Unspent transaction output
    public TransactionInput(String transactionOutputId) 
    {
	this.transactionOutputId = transactionOutputId;
    }
    
}
