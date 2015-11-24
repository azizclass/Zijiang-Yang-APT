import junit.framework.*;
import com.mockobjects.servlet.*;
import java.text.DecimalFormat;

public class TestingLabConverterServletTest extends TestCase {
	
	public void testNullTemperatureGet() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    response.setExpectedContentType("text/html");
	    s.doGet(request,response);
	    response.verify();
	    assertEquals("<html><head><title>No Temperature</title>"
					+ "</head><body><h2>Need to enter a temperature!"
					+ "</h2></body></html>\n", response.getOutputStreamContents());
	}
	
	public void testNullTemperaturePost() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    response.setExpectedContentType("text/html");
	    s.doPost(request,response);
	    response.verify();
	    assertEquals("<html><head><title>No Temperature</title>"
					+ "</head><body><h2>Need to enter a temperature!"
					+ "</h2></body></html>\n", response.getOutputStreamContents());
	}

	public void testBadTemperatureGet() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    request.setupAddParameter("farenheitTemperature", "boo!");
	    response.setExpectedContentType("text/html");
	    s.doGet(request,response);
	    response.verify();
	    assertEquals("<html><head><title>Bad Temperature</title>"
				+ "</head><body><h2>Need to enter a valid temperature!"
			    + "Got a NumberFormatException on " 
				+ "boo!" 
				+ "</h2></body></html>\n", response.getOutputStreamContents());
	}
	
	public void testBadTemperaturePost() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    request.setupAddParameter("farenheitTemperature", "boo!");
	    response.setExpectedContentType("text/html");
	    s.doPost(request,response);
	    response.verify();
	    assertEquals("<html><head><title>Bad Temperature</title>"
				+ "</head><body><h2>Need to enter a valid temperature!"
			    + "Got a NumberFormatException on " 
				+ "boo!" 
				+ "</h2></body></html>\n", response.getOutputStreamContents());
	}
	
	public void testRightTemperatureGet() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    request.setupAddParameter("farenheitTemperature", "100.0");
	    response.setExpectedContentType("text/html");
	    s.doGet(request,response);
	    response.verify();
	    Double celTempDouble = 100.0*(100.0 - 32.0)/180.0;
		DecimalFormat df = new DecimalFormat("#.##");
		String celTemp = df.format(celTempDouble);
	    assertEquals("<html><head><title>Temperature Converter Result</title>"
				+ "</head><body><h2>100.0 Farenheit = " + celTemp + " Celsius "
				+ "</h2>\n"
				+ "<p><h3>The temperature in Austin is 451 degrees Farenheit</h3>\n"
				+ "</body></html>\n", response.getOutputStreamContents());
	}
	
	public void testRightTemperaturePost() throws Exception{
		TestingLabConverterServlet s = new TestingLabConverterServlet();
	    MockHttpServletRequest request = new MockHttpServletRequest();
	    MockHttpServletResponse response = new MockHttpServletResponse();
	    request.setupAddParameter("farenheitTemperature", "100.0");
	    response.setExpectedContentType("text/html");
	    s.doPost(request,response);
	    response.verify();
	    Double celTempDouble = 100.0*(100.0 - 32.0)/180.0;
		DecimalFormat df = new DecimalFormat("#.##");
		String celTemp = df.format(celTempDouble);
	    assertEquals("<html><head><title>Temperature Converter Result</title>"
				+ "</head><body><h2>100.0 Farenheit = " + celTemp + " Celsius "
				+ "</h2>\n"
				+ "<p><h3>The temperature in Austin is 451 degrees Farenheit</h3>\n"
				+ "</body></html>\n", response.getOutputStreamContents());
	}
	
	public static void main(String args[]) {
        String[] testCaseName = { TestingLabConverterServletTest.class.getName() };
        // junit.swingui.TestRunner.main(testCaseName);
        junit.textui.TestRunner.main(testCaseName);
    }
	
}