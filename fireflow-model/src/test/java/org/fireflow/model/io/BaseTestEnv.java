//$Id: BaseTestEnv.java 501 2011-03-15 15:35:42Z westerly.lzh $
package org.fireflow.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.BeforeClass;
/**
 * @author westerly
 * @version 2.0
 */
public abstract class BaseTestEnv {
	protected static final String FILE_NAME = "test_service-example-2.0.xml";
	protected static InputStream in;
	protected static Dom4JServiceParser parser;
	protected static Dom4JServiceSerializer serializer;
	@BeforeClass
	public static void setUpEnv(){
		URL file_url = Thread.currentThread().getContextClassLoader().getResource(FILE_NAME);
		try {
			in = new FileInputStream(new File(file_url.toURI()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
