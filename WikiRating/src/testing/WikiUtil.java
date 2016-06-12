package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.wikidata.wdtk.wikibaseapi.ApiConnection;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class WikiUtil {

	public static String streamToString(InputStream in){
		
		// Converting InputStream object to String
		  String result="";
		  BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			
			e1.printStackTrace();
		}
			
	        StringBuilder builder = new StringBuilder();
	        String line;
	        try {
				while ((line = reader.readLine()) != null) {
				    builder.append(line);
				}
				result=builder.toString();
				in.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	        return result;
	}

	public static Map<String,String> getPageParam(String ns){
		 Map<String,String> mm=new HashMap<String, String>();
		  mm.put("action", "query");
		  mm.put("list","allpages");
		  mm.put("apfrom","a");
		  mm.put("aplimit","max");
		  mm.put("apnamespace",ns);
		  mm.put("format","json");
		  return mm;
	}
	
	public static InputStream reqSend(ApiConnection con,Map<String,String> mm){
		InputStream in=null;
		try {
			in=con.sendRequest("POST", mm);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return in;
	}
	
public static boolean rCheck(int pid){
		
		OrientGraph graph=Connections.getInstance().getDbGraph();
		Iterable<Vertex> vv=graph.getVertices("pid", pid);
		Iterator it=vv.iterator();

		if(it.hasNext()){
			return false;
		}
		else
			return true;
	
	}
}
