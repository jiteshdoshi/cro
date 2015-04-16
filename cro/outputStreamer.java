package cro;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class outputStreamer extends OutputStream{

	
	
	JTextArea jtext;

	public outputStreamer(JTextArea jtext) {
		super();
		this.jtext=jtext;
	}

	@Override
	public void write(final int b) throws IOException {
		//jtext.append((char)b+"");
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	jtext.append((char)b+"");
		    }
		  });
		
	}
	

	
}