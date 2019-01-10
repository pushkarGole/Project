/*
        This class deals with operation performed on Transaction output such as whether this output belongs to specified public address or not 
*/
package BlockChain;
import java.io.Serializable;


class TransactionOutput implements Serializable  //Serializing the Transaction output to send over the network in the form of bytes
{
    private static final long serialVersionUID=2422789860422731812L;
    public String id;
    public String reciepient; 
    public float value; 
    public String parentTransactionId; 
    /*
        Creating a new Transaction output with specified parameters
    */
    public TransactionOutput(String reciepient, float value, String parentTransactionId) 
    {
	this.reciepient = reciepient;   //also known as the new owner of these coins.
	this.value = value; //the amount of coins they own
	this.parentTransactionId = parentTransactionId; //the id of the transaction this output was created in
	this.id = StringUtil.applySha256(reciepient+Float.toString(value)+parentTransactionId);
    }
    /*
        Return the content of Transaction output in the form of string
    */
    @Override
    public String toString() {
        return "TransactionOutput{" + "id=" + id + ", reciepient=" + reciepient + ", value=" + value + ", parentTransactionId=" + parentTransactionId + '}';
    }
	
    /*
        Check if coin belongs to specified public address
    */
    public boolean isMine(String publicAddress) 
    {
        return (publicAddress.equals(reciepient));
    }
	
}

    

