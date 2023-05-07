/*
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

public class KeyValueExtractor implements Serializable {
    
    HashMap<String, ArrayList<String>> aa = new HashMap<>();
    ArrayList<String> omittedKeys = new ArrayList<>();
    ArrayList<String> keyWords = new ArrayList<>();


    String filename;
    
    public KeyValueExtractor(String filename) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.filename = filename;

        omittedKeys.add("osak:identifier");
        omittedKeys.add("website");
        omittedKeys.add("email");
        omittedKeys.add("network:wikipedia");
        omittedKeys.add("phone");
        omittedKeys.add("brand");
        omittedKeys.add("website:municipality");
        omittedKeys.add("name");
        omittedKeys.add("opening_hours");
        omittedKeys.add("description");
        omittedKeys.add("fvst:navnelbnr");
        omittedKeys.add("wikidata");
        omittedKeys.add("note");
        omittedKeys.add("cuisine");
        omittedKeys.add("clothes");
        omittedKeys.add("capacity");
        omittedKeys.add("wikimedia_commons");
        omittedKeys.add("voltage");
        omittedKeys.add("to");
        omittedKeys.add("artist:wikidata");
        omittedKeys.add("mapillary");
        omittedKeys.add("fixme");
        omittedKeys.add("model");
        omittedKeys.add("width");
        omittedKeys.add("opening_hours:kitchen");
        omittedKeys.add("operator");
        omittedKeys.add("description:en");
        omittedKeys.add("BBR:Husnummer");
        omittedKeys.add("defibrillator:location");
        omittedKeys.add("manufacturer");
        omittedKeys.add("ref");
        omittedKeys.add("gst:feat_id");
        omittedKeys.add("wikipedia");
        omittedKeys.add("branch");
        omittedKeys.add("facebook");
        omittedKeys.add("architect");
        omittedKeys.add("comment");
        omittedKeys.add("height");
        omittedKeys.add("url");
        omittedKeys.add("wheelchair:description");
        omittedKeys.add("population");
        omittedKeys.add("inscription");
        omittedKeys.add("source");
        omittedKeys.add("street_cabinet:lock");
        omittedKeys.add("flickr");
        omittedKeys.add("bbr:husnummer");
        omittedKeys.add("direction");
        omittedKeys.add("department");
        omittedKeys.add("product");
        omittedKeys.add("koes_id");
        omittedKeys.add("email");
        omittedKeys.add("email");
        omittedKeys.add("email");

        keyWords.add("addr");
        keyWords.add("name:");
        keyWords.add(":name");
        keyWords.add("_name");
        keyWords.add("contact:");
        keyWords.add("ref:");
        keyWords.add(":ref");
        keyWords.add("turn:");
        keyWords.add("source:");
        keyWords.add("_date");
        keyWords.add("brand:");
        keyWords.add(":date");
        keyWords.add("destination");
        
        if (filename.endsWith(".osm.zip")) {
            parseZIP(filename);
        } else if (filename.endsWith(".osm")) {
            parseOSM(filename);
        }
    }

    
    private void parseZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var input = new ZipInputStream(new FileInputStream(filename));
        input.getNextEntry();
        parseOSM(input);
    }

    private void parseOSM(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError, IOException {
        parseOSM(new FileInputStream(filename));
    }

    private void parseOSM(InputStream inputStream) throws XMLStreamException, FactoryConfigurationError, IOException {
        var input = XMLInputFactory.newInstance().createXMLStreamReader(new InputStreamReader(inputStream));
        

        while (input.hasNext()) {
            var tagKind = input.next();

            if (tagKind == XMLStreamConstants.START_ELEMENT) {
                var name = input.getLocalName();
                
                if (name == "tag") {
                    var v = input.getAttributeValue(null, "v");
                    var k = input.getAttributeValue(null, "k");

                
                
                    if(aa.containsKey(k)) {
                        ArrayList<String> temp = aa.get(k);
                        if(!temp.contains(v)) {
                            temp.add(v);
                        }
                            
                    } else if(!omittedKeys.contains(k)) {
                        boolean t = false;
                        for (int i = 0; i < keyWords.size(); i++) {
                            if(k.contains(keyWords.get(i))) t = true;
                        }
                        if(!t) {
                            ArrayList<String> bb = new ArrayList<>();
                            bb.add(v);
                            aa.put(k, bb);
                            t = false;
                        }
                        
                    }
                    
                    
                }
            }
        }

        FileWriter m = new FileWriter(new File(filename + ".txt"));
        aa.forEach((key, value) -> {
            try {
                m.write("\n\n'" + key + "':\n   ");
                for (String v : value) {
                    m.write(" '" + v + "'',");
                }
            } catch (IOException e) {}
        });
        m.close();
    }
}
*/