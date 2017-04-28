package app.com.xmlparser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView1);

        parseBySAX(); // uses SAX-Parser
        //or
        parseByDOM(); // uses DOM-Parser
        //or
        parseViaXmlPullParser(); // uses XmlPullParser
    }

    void parseBySAX(){
        try {
            //create new instance
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {
                boolean name = false;
                boolean salary = false;
                public void startElement(String uri, String localName,String qName,
                                         Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("name")) {
                        name = true;
                    }
                    if (qName.equalsIgnoreCase("salary")) {
                        salary = true;
                    }
                }//end of startElement method
                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (name) {
                        tv.setText(tv.getText()+"\n\nName : " + new String(ch, start, length));
                        name = false;
                    }
                    if (salary) {
                        tv.setText(tv.getText()+"\nSalary : " + new String(ch, start, length));
                        salary = false;
                    }
                }//end of characters method
            };//end of DefaultHandler object

            InputStream is = getAssets().open("file.xml");
            saxParser.parse(is, handler);
        } catch (Exception e) {e.printStackTrace();}
    }

    void parseByDOM(){
        try {
            InputStream is = getAssets().open("file.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("employee");
            for (int i=0; i<nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    tv.append("\n\n-----------------------"+"\nName : "
                            + getValue("name", element2)+"\n"+"Salary : "
                            + getValue("salary", element2)+"\n"
                            + "-----------------------");
                }
            }//end of for loop
        } catch (Exception e) {e.printStackTrace();}
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    void parseViaXmlPullParser(){

        // Init parser
        XmlPullParser parser = Xml.newPullParser();
        /*
         * or the alternate way :
         * XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
         * XmlPullParser parser = pullParserFactory.newPullParser();
         */

        try {
            //Get xml data from file
            InputStream inputStream = getAssets().open("pizza.xml");
            //Set as input to parser
            parser.setInput(inputStream,null);

            // Parsing data
            List<Item> items = null;
            Item item = new Item();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String field;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        // before <menu>
                        items = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        field = parser.getName();
                        // when <id>
                        if (field.equalsIgnoreCase("id"))
                            item.setId(Integer.parseInt(parser.nextText()));
                        // when <name>
                        else if (field.equalsIgnoreCase("name"))
                            item.setName(parser.nextText());
                        // when <cost>
                        else if (field.equalsIgnoreCase("cost"))
                            item.setCost(Integer.parseInt(parser.nextText()));
                        // when <name>
                        else if (field.equalsIgnoreCase("description"))
                            item.setDescription(parser.nextText());
                        break;
                    case XmlPullParser.END_TAG:
                        field = parser.getName();
                        // when </item>
                        if (field.equalsIgnoreCase("item")) {
                            if (items != null) {
                                items.add(item);
                            }
                            item = new Item(); // resetting pojo
                        }
                        break;
                }
                eventType = parser.next();//moves to next value (text or field)
            }
            if (items != null && items.size()>0) {
                // setting data to textView
                for (int i = 0; i < items.size(); i++) {
                    tv.append("\n\nId : " + items.get(i).getId() + ", Name : " + items.get(i).getName()
                            + ", Cost : " + items.get(i).getCost() + "\nDescription : " + items.get(i).getDescription());
                }
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}