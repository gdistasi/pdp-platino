
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.juniper.netconf.Device;
import net.juniper.netconf.NetconfException;
import net.juniper.netconf.XML;
import org.xml.sax.SAXException;
import java.util.ArrayList;
import java.util.List;


public class TestNetconf {
		
	public static void main(String args[]) throws NetconfException, 
    ParserConfigurationException, SAXException, IOException, InterruptedException {

		
		ArrayList<String> capabilities = new ArrayList<String>();
		capabilities.add("urn:ietf:params:netconf:base:1.0");
		capabilities.add("urn:ietf:params:netconf:capability:writable-running:1.0");
		capabilities.add("urn:ietf:params:netconf:capability:validate:1.0");
		capabilities.add("urn:ietf:params:netconf:capability:with-defaults:1.0?basic-mode=explicit&amp;also-supported=report-all,report-all-tagged,trim,explicit");
		capabilities.add("urn:ietf:params:netconf:capability:interleave:1.0");
		capabilities.add("urn:ietf:params:netconf:capability:url:1.0?scheme=scp,file");
		capabilities.add("urn:ietf:params:xml:ns:netmod:notification?module=nc-notifications&amp;revision=2008-07-14");
		capabilities.add("urn:ietf:params:netconf:capability:startup:1.0");
		capabilities.add("http://www.comics.unina.it/nc/shaper?module=shaper&amp;revision=2014-01-01");
		
		
		//Create the device object and establish a NETCONF session
		Device device = new Device("127.0.0.1", "giovanni", "rexregis123", null, 22, capabilities);
		device.connect();
		
		String configuration = "<shaper xmlns=\"http://www.comics.unina.it/nc/shaper\">"+
							   "<qdisc><interface>eth0</interface>"+
							   "<bandwidth>4000</bandwidth><class><id>11</id><rate>3000</rate><ceil>4000</ceil><filter><id>16</id><protocol>0</protocol><sourcePort>1040</sourcePort><destinationPort>2000</destinationPort></filter></class>"+
							   "</qdisc></shaper>";
		
		device.loadXMLConfiguration(configuration, "replace");

		System.out.println("END.");
		device.close();
	}
}

