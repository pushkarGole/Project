/*
       This class contains various funnction which are used in our project
       we clubbed these function into a single class to enhance error detecting a readiablity
*/
package BlockChain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;


class StringUtil 
{
    /*
        Applies SHA-256 to specified input
    */
    public static String applySha256(String input)
    {		
        try 
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");	       
            byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) 
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                   hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
    /*
        This function applies the ECDSA signature alogrithm on the input specified using the Private key
    */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) 
    {
        Signature dsa;
        byte[] output = new byte[0];
        try 
        {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        }       
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
        return output;
    }
    /*
        This function applies the ECDSA signature verification alogrithm on the input specified using the Public key
    */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) 
    {   
        try 
        {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }
        catch(Exception e) 
        {
            throw new RuntimeException(e);
        }
    }  
    /*
            This function creates a string of 0s of specified length
    */
    public static String getDificultyString(int difficulty) 
    {
	return new String(new char[difficulty]).replace('\0', '0');
    }
    /*
            This function creates a merkle tree of transaction specified and returs its root
    */
    public static String getMerkleRoot(ArrayList<Transaction> transactions) 
    {
	int count = transactions.size();  
	ArrayList<String> previousTreeLayer = new ArrayList<String>();
	for(Transaction transaction : transactions) 
	{
		previousTreeLayer.add(transaction.transactionId);
	}
	ArrayList<String> treeLayer = previousTreeLayer;
	while(count > 1) 
	{
		treeLayer = new ArrayList<>();
		for(int i=1; i < previousTreeLayer.size(); i++) 
		{
			treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
		}
		count = treeLayer.size();
		previousTreeLayer = treeLayer;
	}
	String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
	return applySha256(merkleRoot);
    }
    
}
