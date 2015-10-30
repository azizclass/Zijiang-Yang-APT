import java.io.*;

/**
 * My decorator of input stream to read lower case characters.
 */
public class ToLowerCase extends InputStream{
	
	private InputStream istream;
	
	public ToLowerCase(InputStream istream){
		if(istream == null)
			throw new IllegalArgumentException("Got null parameter!");
		this.istream = istream;
	}
	
	@Override
	public int available() throws IOException{
		return istream.available();
	}
	
	@Override
	public void close() throws IOException{
		istream.close();
	}
	
	@Override
	public void mark(int readlimit){
		istream.mark(readlimit);
	}
	
	@Override
	public boolean markSupported(){
		return istream.markSupported();
	}
	
	@Override
	public int read() throws IOException{
		int c = istream.read();
		if(c >= 'A'&& c <= 'Z')
			c -= 'A'-'a';
		return c;
	}
	
	@Override
	public int read(byte[] b) throws IOException{
		int ret = istream.read(b);
		for(int i=0; i<ret; i++)
			if(b[i] >= 'A' && b[i] <= 'Z')
				b[i] = (byte)(b[i] - ('A' - 'a'));
		return ret;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException{
		int ret = istream.read(b, off, len);
		for(int i=off; i<ret+off; i++)
			if(b[i] >= 'A' && b[i] <= 'Z')
				b[i] = (byte)(b[i] - ('A' - 'a'));
		return ret;
	}
	
	@Override
	public long skip(long n) throws IOException{
		return istream.skip(n);
	}
	
	/**
	 * Test the class, read from standard in and output to standard out
	 */
	public static void main(String[] args) throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ToLowerCase(System.in)));
		while(true)
			System.out.println(reader.readLine());
	}
	
}