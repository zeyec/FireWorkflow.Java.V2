//$Id: Dom4JServiceParserTest.java 501 2011-03-15 15:35:42Z westerly.lzh $
package org.fireflow.model.io;

import java.io.IOException;
import java.util.List;

import org.fireflow.model.servicedef.Service;
import org.junit.Test;
/**
 * @author westerly
 * @version 2.0
 */
public class Dom4JServiceParserTest extends BaseTestEnv {

	@Test
	public void testParse() {
		parser = new Dom4JServiceParser();
		try {
			List<Service> services = parser.parse(in);
			System.out.println(services.size());
		} catch (ParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
