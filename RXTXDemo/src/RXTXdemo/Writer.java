package RXTXdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JTextArea;

import gnu.io.CommPort;

public class Writer //implements Runnable 
{
  
    JTextArea txArea;
    JTextArea mixArea;
    
    public Writer ( JTextArea txArea, JTextArea mixArea)
    {
        this.txArea = txArea;
        this.mixArea = mixArea;
    }
    
    public void send(CommPort myCOM, OutputStream out, byte[] message, int option) throws IOException
    {
    
    	if(option != 0)
    	{
    		for(byte a:message)
    		{
    			if(a == '\n')
    			{
    				out.write('\r');
    			}
    			out.write(a);
    			
    		}
    	}
    	else
    	{
    		out.write(message);
    	}
    	
    	
    	
    }
    public void send(CommPort myCOM,   OutputStream out, String message, int option) throws IOException
    {
    	byte b;
    	boolean caps = false;
    	if(option>3)
    	{
    		option-=4;
    		message = message.toUpperCase();
    	}
    	
    	switch(option)
    	{
    		case 1:			// append \r to \n
    			for(int i =0; i < message.length(); i++)
    			{	
    				b = (byte) message.charAt(i);
    				if(b == '\n')
    				{
    					out.write('\r');
    					this.mixArea.append("[\\r\\n]");
    				}
    				out.write(b);
    			}
    			break;
    		case 2:			// send \n at the end of message
    			for(int i =0; i < message.length(); i++)
    			{	
    				b = (byte)message.charAt(i);
    				out.write(b);
    			}
    			out.write('\n');
    			this.mixArea.append("[\\n]");
    			break;
    		case 3:			// append \r\n to end of message
    			for(int i =0; i < message.length(); i++)
    			{	
    				b = (byte)message.charAt(i);
    				if(b == '\n')
    				{
    					out.write('\r');
    					this.mixArea.append("[\\r\\n]");
    				}
    				out.write(b);
    			}
    			out.write('\r');
    			out.write('\n');
    			this.mixArea.append("[\\r\\n]");
    			break; 	
    		 default:
    			for(int i =0; i < message.length(); i++)
    			{	
    				b = (byte)message.charAt(i);
    				out.write(b);
    			}
    			
    	}	
        	
    }
    public void send(CommPort myCOM, OutputStream out, byte[] message) throws IOException
    {
    	
    	out.write(message);
    }
    

    
    public void run ()
    {
      /*  try
        {                
            int c = 0;
            while ( ( c = System.in.read()) > -1 )
            {
                this.out.write(c);
            }                
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    */}
}