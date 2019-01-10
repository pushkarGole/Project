/*
        This class defines the network structure of a node and includes the functions required to communicate over the newtork
*/
package BlockChain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Node implements Runnable
{
    public int port;
    public InetAddress address;
    public MulticastSocket socket;
    public Thread runThread;
    
    /*
            Whenever a node starts it call the constructor of this class which eventually creates a UDP socket 
            on this machine to communicate over the network
    */
    public Node(int port) throws UnknownHostException 
    {
        this.port = port;
        this.address=InetAddress.getByName("224.0.0.3");
        try
        {
            socket=new MulticastSocket(port);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        runThread=new Thread(this,"Server");
        runThread.start();
    }
    @Override
    public void run()
    {   
    }
    /*
            This function convert the object of block class into the bytes which is required for sending the block over the network in the form of packets
            This is done by a process called Serialization
    */
    public byte[] ObjectToByteArray(Block b)
    {
        ObjectOutputStream objectOutputStream=null;
        byte[] data=null;
        try
        {
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(b);
            data=byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try 
            {
                objectOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }
    /*
            This function convert the object of transaction class into the bytes which is required for sending the transaction over the network 
            in the form of packets, this is done by a process called Serialization
    */
    public byte[] ObjectToByteArray(Transaction t)
    {
        
        ObjectOutputStream objectOutputStream=null;
              byte [] data=null;
              try {
                  ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                  objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                  objectOutputStream.writeObject(t);
                  data=byteArrayOutputStream.toByteArray();
              } catch (IOException ex) {
                  Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
              } finally {
                  try {
                      objectOutputStream.close();
                  } catch (IOException ex) {
                      Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                  }
              }
              return data;
    }
    /*
        This function sends a block over the network using socket API provided by JAVA 
    */
    public void send(Block b,InetAddress address,int port)
    {
        try
        {
            byte[] data=ObjectToByteArray(b);
            DatagramPacket packet=new DatagramPacket(data,data.length, address, port);
            socket.send(packet);
            
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
        This function sends a transaction over the network using socket API provided by JAVA 
    */
    public void send(Transaction t,InetAddress address,int port)
    {
        
              try 
              {
                byte [] data=ObjectToByteArray(t);
                DatagramPacket packet=new DatagramPacket(data,data.length,address, port);
                socket.send(packet);
                 
              } 
              catch (IOException ex) 
              {
                  Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
              }
          
    }
    /*
            This function convert the bytes received into the object of transaction class which is required to perform various operations 
            This is done by a process called De-Serialization
    */
    public Transaction byteArrayToTransaction(byte [] arr)
    {
        ObjectInputStream objectInputStream=null;
        Transaction t=null;
        try {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(arr);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            t=(Transaction) objectInputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return t;
    }
    /*
        This function convert the bytes received into the object of block class which is required to perform various operations 
        This is done by a process called De-Serialization
    */
    public Block byteArrayToBlock(byte []arr)
    {
        ObjectInputStream objectInputStream=null;
        Block b=null;
        try
        {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(arr);
            objectInputStream=new ObjectInputStream(byteArrayInputStream);
            b=(Block) objectInputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try {
                objectInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return b;
    }
     /*
        This function receives a block from the network using socket API provided by JAVA 
    */
    public Block receiveBlock()
    {
        Block b=null;
        try
        {
            byte[] data=new byte[4096];
            DatagramPacket packet=new DatagramPacket(data,data.length);
            socket.receive(packet);
            b=byteArrayToBlock(packet.getData());
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }
     /*
        This function receives a transaction over the network using socket API provided by JAVA 
    */
    public Transaction receiveTransaction()
    {
                Transaction t=null;
                try {
                    byte [] data=new byte[4096];
                    DatagramPacket packet;
                    packet = new DatagramPacket(data,data.length);
                    socket.receive(packet);
                    t=byteArrayToTransaction(packet.getData());
                } catch (IOException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return t;  
    }
    
    
}
