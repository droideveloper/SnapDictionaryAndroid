/*
Copyright 2014 GhanaianFramework.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.fs.ghanaian.xml;

import android.net.Uri;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Fatih on 19/10/14.
 */
public class DocumentHelper {

    private final static String PATTERN_IP_HOST = "\\d{1,3}.\\d{1,2}.\\d{1,3}.\\d{1,2}";

    /**
     * Create new @link{org.w3c.dom.Document.class} instance
     * @return empty Document object instance.
     */
    public static Document create() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.newDocument();
        } catch (ParserConfigurationException pce) {
            /*silent exception*/
        }
        return null;
    }

    /**
     * parse raw xml string into Document
     * @param xml
     * @return
     */
    public static Document create(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException e) {
            /*Silent handle*/
        } catch (IOException ioe) {
            /*silent handle*/
        } catch (SAXException saxe) {
            /*silent handle*/
        }
        return null;
    }

    /**
     * Create Element inside the document
     *
     * @param document
     * @param tag
     * @return
     */
    public static org.w3c.dom.Element create(Document document, String tag) {
        return document.createElement(tag);
    }

    /***
     * Creates new @link{String.class} instance from provided Document object.
     * @param document convert to String
     * @return String representation of Document
     */
    public static String string(Document document) {
        DOMSource dom = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = factory.newTransformer();
            transformer.transform(dom, result);
            return writer.toString();
        } catch (TransformerConfigurationException tce) {
            /*silent exception*/
        } catch (TransformerException te) {
            /*silent exception*/
        }
        return null;
    }

    /**
     * Gets namesapce uri and gets first reliable data to tag prefix for child items.
     * @param uri
     * @return
     */
    public static String getNamespace(String uri) {
        Uri URL = Uri.parse(uri);
        String host = URL.getHost();
        if(isHostIP(host)) {
            String path = URL.getPath();
            return getChar(path);
        }
        return getChar(host);
    }

    /**
     *
     * @param data
     * @return
     */
    static String getChar(String data) {
        if(data.contains("/")) {
            data = data.replace("/", "");
        }
        String value = data.substring(0, data.length() > 4 ? 4 : data.length());
        return value.toLowerCase(Locale.ENGLISH);
    }

    /**
     *
     * @param host
     * @return
     */
    static boolean isHostIP(String host) {
        Pattern pattern = Pattern.compile(PATTERN_IP_HOST);
        Matcher matcher = pattern.matcher(host);
        if(matcher.find()) {
            matcher.reset();
            return true;
        }
        return false;
    }
}
