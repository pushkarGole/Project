/*
            This class deals with all opertaions of database such as storing a block or transaction and retrieve these blocks and transactions as well. 
*/

package BlockChain;

import com.google.gson.Gson;
import java.util.ArrayList;
import com.mongodb.MongoCredential; 
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.*;
import com.mongodb.util.JSON;


public class Dbconnect 
{
    
    MongoClient mongo;
    MongoCredential credential; 
    MongoDatabase database;
    
    /*
            This function add a transaction to the Transaction collection created in database 
    */
    public void AddTransactionToDB(Transaction t)
    {
        mongo = new MongoClient("localhost",27017 );     //Connecting to Database server
        credential = MongoCredential.createCredential("sampleUser", "myDb","password".toCharArray()); //Creating credentials to connect database
        DB db = mongo.getDB( "myDb" );
        DBCollection coll = db.getCollection("Transaction");  //Selecting Transaction collection   
        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject)JSON.parse(gson.toJson(t));  //Framing a transaction object into a Gson string
        coll.insert(obj);   //Storing the transaction in collection
    }
    /*
        This function retrieves all unconfirmed transactions from the database in the form arraylist of transactions
    */
    public ArrayList<Transaction>  RetrieveTransactions()
    {
        ArrayList<Transaction> array=new ArrayList<>();
        mongo = new MongoClient( "localhost" , 27017 );    //Connecting to Database server 
        credential = MongoCredential.createCredential("sampleUser", "myDb","password".toCharArray());  //Creating credentials to connect database
        DB db = mongo.getDB( "myDb" );  
        DBCollection coll = db.getCollection("Transaction");    //Selecting Transaction collection    
        Transaction t =null;    
        DBCursor cursor = coll.find();      //Getting the list of all unconfirmed transaction in database
        try 
        {
            while(cursor.hasNext()) 
            {
                DBObject dbobj = cursor.next();
                t = (new Gson()).fromJson(dbobj.toString(), Transaction.class);     //Converting BasicDBObject to a custom Class Transaction
                array.add(t); //Adding each unconfirmed transaction to array list of transactions
            }
        } 
        finally 
        {
            cursor.close();
        }
       return array;
    }
   /*
            This function add a Block to the Blockchain collection created in database 
    */
    public void AddBlockToDB(Block t)
    {
        mongo = new MongoClient( "localhost" , 27017 );     //Connecting to Database server
        credential = MongoCredential.createCredential("sampleUser", "myDb","password".toCharArray());  //Creating credentials to connect database
        DB db = mongo.getDB( "myDb" );
        DBCollection coll = db.getCollection("Blockchain");     //Selecting Blockchain collection    
        Gson gson = new Gson();
        BasicDBObject obj = (BasicDBObject)JSON.parse(gson.toJson(t));  //Framing a Block object into a Gson string
        coll.insert(obj);           //Storing the transaction in collection
    }
    /*
        This function retrieves all blocks from the database in the form arraylist of blocks
    */
   public ArrayList<Block>  ReteriveBlocks()
   {
       ArrayList<Block> array=new ArrayList<Block>();
       mongo = new MongoClient( "localhost" , 27017 );     //Connecting to Database server
       credential = MongoCredential.createCredential("sampleUser", "myDb","password".toCharArray());  //Creating credentials to connect database
       DB db = mongo.getDB( "myDb" );
       DBCollection coll = db.getCollection("Blockchain");     //Selecting Blockchain collection  
       Block b =null;    
       DBCursor cursor = coll.find();           //Getting the list of all unconfirmed transaction in database
       try 
       {
           while(cursor.hasNext()) 
           {
                DBObject dbobj = cursor.next();
                b = (new Gson()).fromJson(dbobj.toString(), Block.class);       //Converting BasicDBObject to a custom Class Block
                array.add(b);       //Adding each block to array list of blocks
           }
        }
        finally 
        {
            cursor.close();
        }
        return array;
    }     
}