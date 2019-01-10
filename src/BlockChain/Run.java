/*
        The execution of our project begins from this class
*/
package BlockChain;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.HashMap;
import java.util.Scanner;


public class Run 
{
    public static Node n;               //Creating an instance of Node class
    public static blockChain bchain;    //Creating an instance of Node class
    public static int difficulty = 3;   //Setting the difficulty target for the Proof-of work-algorithm
    public static float minimumTransaction = 0.1f;  //Setting a thershold for minimum transaction amount
    public static Wallet Shivani;     //Creating two wallets Pushkar and Shivani   
    public static Wallet Pushkar;
    final public static int size_of_block=2;      //Setting the threshold for the number of transactions in a block
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();    //A hashmap of gloabl UTXOs
    public static HashMap<String,Wallet> walletsHash=new HashMap<String,Wallet>();      //A hashmap for all wallets
    public static Dbconnect db; //Creating an instance of Dbconnect class

public static void main(String[] args) throws UnknownHostException, IOException 
{	
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Adds Bouncycastle as a security provider of various cryptographic algorithm used later
    Shivani=new Wallet("Shivani","1234");       //Initialzing the wallets with their userID and passwords
    Pushkar=new Wallet("Pushkar","1234");
    walletsHash.put(Shivani.PublicAddress,Shivani); //Put these wallets into wallet hash for fast access
    walletsHash.put(Pushkar.PublicAddress, Pushkar);
    n=new Node(8332);                               //Starting the network for comunnication on port 8332 
    n.socket.joinGroup(n.address);                  //Joining the network
    bchain=new blockChain();                        //Creating the blockchain with genesis block
    n.send(bchain.chain.get(0),n.address,n.port);   //Transamit this block over the network
    db=new Dbconnect();
    n.receiveBlock();
    db.AddBlockToDB(bchain.chain.get(0));       //Add this block to database
    
    /*
            Getting login to your wallet
    */
    Scanner in=new Scanner(System.in);
    System.out.println("Welcome to Wallet\nPlease Enter Your UserID ");
    String userId=in.next();
    in.nextLine();
    boolean flag=walletsHash.containsKey(userId);
    if(flag)
    {
        System.out.println("Enter Password");
        String password=in.next();
        Wallet w=walletsHash.get(userId);
        if(w.password.equalsIgnoreCase(password))
        {
            
            
            System.out.println("Welcome "+w.name+" to Wallet");
            int ch;
            //Displays the full menu for the user
            do
            {
                System.out.println("Select from following operation\n1.Check Balance\n2.Send Funds\n3.Receive Transaction \n4.Receive Block \n5.Start mining \n6.View Blockchain\n7.Logout");
                Scanner sc=new Scanner(System.in);
                ch=sc.nextInt();
                sc.nextLine();
                switch(ch)
                {
                    //Check wallet balance
                    case 1:System.out.println(w.name+" you have "+w.getBalance()+"BTC in your Wallet");
                            break;
                    // Send funds to a user on Bitcoin network
                    case 2:System.out.println("Enter Public address of Receiver");
                            String receiver=sc.next();
                            sc.nextLine();
                            if(walletsHash.containsKey(receiver))
                            {
                                System.out.println("Enter Amount to be send:");
                                float amount=sc.nextFloat();
                                Transaction t=w.sendFunds(walletsHash.get(receiver).PublicAddress,amount);
                                if(t.verifiySignature(w.publicKey))
                                {
                                    t.processTransaction();
                                    System.out.println("Tranaction created with transaction id = "+t.getTransactionId()+" and transmitted to the Bitcoin network");
                                    n.send(t,n.address,n.port);
                                    n.receiveTransaction();
                                }
                                else
                                {
                                    System.err.println("Signature verification fails!!!!");
                                }
                            }
                            else
                            {
                                System.out.println("No Such Receiver exist In Network");
                            }
                            break;
                    case 3: System.out.println("Receiving transaction from network.........");
                            Transaction rt=n.receiveTransaction();
                            System.out.println(rt.toString());
                            for(TransactionOutput o:rt.outputs)
                            {
                                UTXOs.put(o.id, o);
                            }
                            for(TransactionInput i:rt.inputs)
                            {
                                if(i.UTXO==null)
                                    continue;
                                UTXOs.remove(i);      
                            }
                            
                            db.AddTransactionToDB(rt);
                            System.out.println("Transaction received sucessfully");
                            break;
                    case 4: System.out.println("Receiving block from network.........");
                            Block b=n.receiveBlock();
                            if(bchain.isBlockValid(b, difficulty))
                            {
                                bchain.addtochain(b);
                                db.AddBlockToDB(b);
                            }
                            else
                            {
                                System.err.println("Block is not valid");
                            }
                            System.out.println("Block received sucessfully");
                            break;
                    case 5: 
                            Block mblock=w.startMining();
                            System.out.println("New mined block created successfully and broadcasted over the network");
                            db.AddBlockToDB(mblock);
                            bchain.addtochain(mblock);
                            n.send(mblock,n.address,n.port);
                            n.receiveBlock();
                            break;
                    case 6:
                            bchain.toString();
                            break;
                    case 7:
                            System.out.println("Thanks for using this application\n Have a nice day ^_^");
                            break;
                    default:
                            System.out.println("This option is not valid\nPlease try again...");
                }
            }while(ch!=7);    
        }
        else
        {
            System.out.println("Incorrect Password!!!\nTry again");
        }
        
    }
    else
    {
        System.out.println("No Such Wallet Exists!!!!!\nTry Again");
    }
   
}

}
