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

import android.util.Log;

import org.fs.ghanaian.annotation.Accessibility;
import org.fs.ghanaian.annotation.Attribute;
import org.fs.ghanaian.annotation.Element;
import org.fs.ghanaian.annotation.ElementList;
import org.fs.ghanaian.annotation.Ignore;
import org.fs.ghanaian.annotation.Namespace;
import org.fs.ghanaian.core.CoreObject;
import org.fs.ghanaian.util.StringUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fatih on 17/10/14.
 */
public class Serializer extends CoreObject {

    private final static String KEY_NAMESPACE_TEMPALTE = "%s:%s";//use as prefix, tagName.
    private final static String KEY_ATTRIBUTE_NAMESPACE_TEMPALTE = "xmlns:%s";
    private final static String KEY_GETTER_TEMPLATE = "get%s";
    private final static String KEY_SETTER_TEMPLATE = "set%s";

    public Serializer() { /*Constructor*/ }

    /**
     * deserialize the xml to object
     *
     * @param value String of xml file or etc.
     * @param clazz clazz opf the deserialization.
     * @param <T>   type of the return class.
     * @return
     */
    public <T> T deserialize(String value, Class<?> clazz) {
        if (value == null) {
            log(Log.ERROR, "Yout should have set raw xml to be able to parse data into class... :(");
            return null;
        }
        if (clazz == null) {
            log(Log.ERROR, "You should have set class object to be able to parse data into that class... :(");
            return null;
        }
        org.w3c.dom.Document document = DocumentHelper.create(value);
        org.w3c.dom.Element root = document.getDocumentElement();
        return (T) convertClass(root, clazz, null); // downcast it to T type.
    }

    /**
     * deserialize the xml to object
     *
     * @param value    String of xml file or etc.
     * @param clazz    clazz opf the deserialization.
     * @param tagStart mapping to tag value should be valid else exception will be thrown
     * @param <T>      type of the return class.
     * @return
     */
    public <T> T deserialize(String value, Class<?> clazz, String tagStart) {
        if (value == null) {
            log(Log.ERROR, "Yout should have set raw xml to be able to parse data into class... :(");
            return null;
        }
        if (clazz == null) {
            log(Log.ERROR, "You should have set class object to be able to parse data into that class... :(");
            return null;
        }
        org.w3c.dom.Document document = DocumentHelper.create(value);
        org.w3c.dom.Element root = document.getDocumentElement();
        if (!StringUtility.isNullOrEmpty(tagStart)) {
            final int KEY_FIRST = 0;
            NodeList nodeList = root.getElementsByTagName(tagStart);
            if (nodeList != null && nodeList.getLength() > 0) {
                root = (org.w3c.dom.Element) nodeList.item(KEY_FIRST);
            } else {
                throw new IllegalArgumentException("you put map value but it won't access it by default... :(");
            }
        }
        return (T) convertClass(root, clazz, null); // downcast it to T type.
    }

    public <T> T deserialize(Document document, Class<?> clazz) {
        org.w3c.dom.Element root = document.getDocumentElement();
        return (T) convertClass(root, clazz, null); // downcast it to T type.
    }

    public <T> T deserialize(Document document, Class<?> clazz, String tagStart) {
        org.w3c.dom.Element root = document.getDocumentElement();
        if (!StringUtility.isNullOrEmpty(tagStart)) {
            final int KEY_FIRST = 0;
            NodeList nodeList = root.getElementsByTagName(tagStart);
            if (nodeList != null && nodeList.getLength() > 0) {
                root = (org.w3c.dom.Element) nodeList.item(KEY_FIRST);
            } else {
                throw new IllegalArgumentException("you put map value but it won't access it by default... :(");
            }
        }
        return (T) convertClass(root, clazz, null); // downcast it to T type.
    }

    /**
     * Serialize the T type to xml
     *
     * @param value T type class instance
     * @param <T>   Type of the class
     * @return String representation of xml
     */
    public <T> String serialize(T value) {
        if (value == null) {
            throw new IllegalArgumentException("You can not serialize null... :(");
        }
        org.w3c.dom.Document document = DocumentHelper.create();
        org.w3c.dom.Element root = parseClass(document, value);
        document.appendChild(root);
        String v = DocumentHelper.string(document);
        return v;
    }

    /**
     * @param f
     * @param instance
     * @return
     */
    Object getFieldInstance(Field f, Object instance) {
        String getMethod = String.format(Locale.ENGLISH, KEY_GETTER_TEMPLATE, f.getName());
        try {
            Accessibility accessibilityAnnotation = f.getAnnotation(Accessibility.class);
            if (accessibilityAnnotation != null) {
                getMethod = accessibilityAnnotation.get();
            }
            Method method = instance.getClass().getMethod(getMethod);
            method.setAccessible(true);
            return method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param f
     * @param value
     * @param instance
     */
    void setFieldInstance(Field f, Object value, Object instance) {
        String setMethod = String.format(Locale.ENGLISH, KEY_SETTER_TEMPLATE, f.getName());
        try {
            Accessibility accessibilityAnnotation = f.getAnnotation(Accessibility.class);
            if (accessibilityAnnotation != null) {
                setMethod = accessibilityAnnotation.set();
            }
            Method method = instance.getClass().getMethod(setMethod, f.getType());
            method.setAccessible(true);
            method.invoke(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * ,
     * @param value
     * @param instance
     */
    void addListFieldInstance(Object value, Object instance) {
        try {
            Method method = instance.getClass().getMethod("add", Object.class);
            method.setAccessible(true);
            method.invoke(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param instance
     * @param index
     * @return
     */
    Object getListFieldInstance(Object instance, int index) {
        try {
            Method method = instance.getClass().getMethod("get", int.class);
            method.setAccessible(true);
            return method.invoke(instance, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param ref
     * @return
     */
    Object newInstance(Class<?> ref) {
        try {
            return ref.newInstance();
        } catch (InstantiationException ie) { /*silent handle*/ } catch (IllegalAccessException iae) { /*silent handle*/ }
        return null;
    }

    /**
     * @param root
     * @param clazz
     * @return
     */
    Object convertClass(org.w3c.dom.Element root, Class<?> clazz, Field parent) {

        Object instance = newInstance(clazz);

        if (instance != null) {

            if(isCollection(instance)) {

                NodeList nodeList = root.getChildNodes();
                ElementList elementListA = parent.getAnnotation(ElementList.class);
                Class<?> c = null;

                if(elementListA != null) {
                    c = elementListA.clazz();
                }
                for(int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if(node instanceof org.w3c.dom.Element) {
                        org.w3c.dom.Element e = (org.w3c.dom.Element)node;
                        if(c != null) {
                            Object v = convertClass(e, c, parent);
                            if(v != null) {
                                addListFieldInstance(v, instance);
                            }
                        }
                    }
                }
            }

            else {

                List<Field> fields = Arrays.asList(clazz.getFields());

                NodeList nodeList = root.getChildNodes();
                int size = nodeList.getLength();

                for (int i = 0; i < size; i++) {

                    Node node = nodeList.item(i);

                    if (node instanceof org.w3c.dom.Element) {

                        org.w3c.dom.Element e = (org.w3c.dom.Element) node;

                        String eName = e.getTagName().toLowerCase(Locale.ENGLISH);
                        for (Field field : fields) {
                            String fName = field.getName().toLowerCase(Locale.ENGLISH);

                            Element eAnnotation = field.getAnnotation(Element.class);
                            String eNName = "";
                            if (eAnnotation != null) {
                                eNName = eAnnotation.name().toLowerCase(Locale.ENGLISH);
                            }

                            Ignore ignoreA = field.getAnnotation(Ignore.class);
                            if(ignoreA != null) {
                                continue;
                            }

                            if (eName.contains(fName) || eName.contains(eNName)) {
                                Class<?> type = field.getType();

                                if (SerializationHelper.isTypeKnown(type)) {
                                    if (type.equals(String.class)) {
                                        if (isCollection(instance)) {
                                            addListFieldInstance(e.getTextContent(), instance);
                                        } else {
                                            setFieldInstance(field, e.getTextContent(), instance);
                                        }
                                    } else if (type.equals(Integer.class) || type.equals(int.class)) {
                                        if (isCollection(instance)) {
                                            addListFieldInstance(Integer.parseInt(e.getTextContent()), instance);
                                        } else {
                                            setFieldInstance(field, Integer.parseInt(e.getTextContent()), instance);
                                        }
                                    } else if (type.equals(Double.class) || type.equals(double.class)) {
                                        if (isCollection(instance)) {
                                            addListFieldInstance(Double.parseDouble(e.getTextContent()), instance);
                                        } else {
                                            setFieldInstance(field, Double.parseDouble(e.getTextContent()), instance);
                                        }
                                    } else if (type.equals(Long.class) || type.equals(long.class)) {
                                        if (isCollection(instance)) {
                                            addListFieldInstance(Long.parseLong(e.getTextContent()), instance);
                                        } else {
                                            setFieldInstance(field, Long.parseLong(e.getTextContent()), instance);
                                        }
                                    } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                                        if (isCollection(instance)) {
                                            addListFieldInstance(Boolean.parseBoolean(e.getTextContent()), instance);
                                        } else {
                                            setFieldInstance(field, Boolean.parseBoolean(e.getTextContent()), instance);
                                        }
                                    } else if (type.equals(Date.class)) {
                                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
                                        try {
                                            if (isCollection(instance)) {
                                                addListFieldInstance(format.parse(e.getTextContent()), instance);
                                            } else {
                                                setFieldInstance(field, format.parse(e.getTextContent()), instance);
                                            }
                                        } catch (ParseException pe) { /*silent handle*/ }
                                    }
                                } else {
                                    if (e.hasChildNodes()) {
                                        if (e.getFirstChild() != null && e.getFirstChild() instanceof org.w3c.dom.Element) {
                                            if(isCollection(instance)) {
                                                addListFieldInstance(convertClass(e, type, field), instance);
                                            } else {
                                                setFieldInstance(field, convertClass(e, type, field), instance);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return instance;
    }

    boolean isCollection(Object instance) {
        if(instance instanceof List<?>||instance instanceof ArrayList<?>) {
            return true;
        }
        return false;
    }

    /***
     *
     * @param document
     * @param field
     * @param instance
     * @return
     */
    org.w3c.dom.Element parseAttribute(Document document, Field field, Object instance) {
        Element elementAnnotation = field.getAnnotation(Element.class);
        String tag = elementAnnotation == null ? field.getName() : elementAnnotation.name();
        String prefix = "";

        Class<?> owner = instance.getClass();

        //owner one namespace not the attribute one.
        Namespace namespaceAnnotation = owner.getAnnotation(Namespace.class);
        if(namespaceAnnotation != null) {
            prefix = namespaceAnnotation.prefix();
        }

        if(!StringUtility.isNullOrEmpty(prefix)) {
            tag = String.format(Locale.ENGLISH, KEY_NAMESPACE_TEMPALTE, prefix, tag);
        }

        org.w3c.dom.Element element = DocumentHelper.create(document, tag);

        Attribute attributeAnnotation = field.getAnnotation(Attribute.class);
        if(attributeAnnotation != null) {
            element.setAttribute(attributeAnnotation.name(), attributeAnnotation.value());
        }

        String getMethodName = String.format(Locale.ENGLISH, KEY_GETTER_TEMPLATE, field.getName());
        try {
            Method getMethod = owner.getMethod(getMethodName);
            getMethod.setAccessible(true);
            Object v = getMethod.invoke(instance);
            if(v != null) {
                element.setTextContent(String.valueOf(v));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return element;
    }

    /**
     *
     * @param document
     * @param instance
     * @return
     */
    org.w3c.dom.Element parseClass(Document document, Object instance) {
        if(instance == null) {
            return null;
        }
        Class<?> clazz = instance.getClass();
        String tag = clazz.getSimpleName();
        String prefix = null;
        String namespace = null;

        Namespace namespaceAnnotation = clazz.getAnnotation(Namespace.class);
        if(namespaceAnnotation != null) {
             namespace = namespaceAnnotation.referance();
             prefix = namespaceAnnotation.prefix();
        }

        if(!StringUtility.isNullOrEmpty(prefix)) {
            tag = String.format(Locale.ENGLISH, KEY_NAMESPACE_TEMPALTE, prefix, tag);
            prefix = String.format(Locale.ENGLISH, KEY_ATTRIBUTE_NAMESPACE_TEMPALTE, prefix);
        }

        org.w3c.dom.Element element = null;
        if(!StringUtility.isNullOrEmpty(tag)) {
            element = DocumentHelper.create(document, tag);
        }

        if(element != null) {
            if(!StringUtility.isNullOrEmpty(prefix)) {
                element.setAttribute(prefix, namespace);
            }
        }
        List<Field> fields = Arrays.asList(clazz.getFields());
        Collections.reverse(fields);
        for(Field field : fields) {

            Class<?> c = field.getType();
            if(SerializationHelper.isTypeKnown(c)) {
                org.w3c.dom.Element e = parseAttribute(document, field, instance);
                if(e != null) {
                    element.appendChild(e);
                }
            } else {
                Object attrInstance = getFieldInstance(field, instance);
                org.w3c.dom.Element e = parseClass(document, attrInstance);
                if(e != null) {
                    element.appendChild(e);
                } else {
                    String t = field.getType().getSimpleName();
                    String nv = null;
                    String n = null;

                    Namespace namespaceA = clazz.getAnnotation(Namespace.class);
                    if(namespaceA != null) {
                       t = String.format(Locale.ENGLISH, KEY_NAMESPACE_TEMPALTE, namespaceA.prefix(), t);
                       n = String.format(Locale.ENGLISH, KEY_ATTRIBUTE_NAMESPACE_TEMPALTE, namespaceA.prefix());
                       nv = namespaceA.referance();
                    }
                    e = DocumentHelper.create(document, t);
                    if(!StringUtility.isNullOrEmpty(n) && !StringUtility.isNullOrEmpty(nv)) {
                        e.setAttribute(n, nv);
                    }
                    if(e != null) {
                        element.appendChild(e);
                    }
                }
            }
        }
        return element;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isLogEnabled() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getClassTag() {
        return Serializer.class.getSimpleName();
    }
}
