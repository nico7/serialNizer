package RXTXdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JTextArea;

public class Reader implements Runnable 
{
    InputStream in;
    JTextArea mixArea;
    JTextArea rxArea;
    
    public Reader ( InputStream in , JTextArea mixArea, JTextArea rxArea)
    {
        this.in = in;
        this.mixArea = mixArea;
        this.rxArea = rxArea;
    }
    
    public void run ()
    {
        byte[] buffer = new byte[1024];
        int len = -1;
        try
        {
            while ( ( len = this.in.read(buffer)) > -1 )
            {
            	mixArea.append(new String(buffer,0,len));
            	rxArea.append(new String(buffer,0,len));
                System.out.print(new String(buffer,0,len));
            }
            
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }            
    }
}