package org.devocative.samples.xml;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.beanutils.PropertyUtils;
import org.devocative.samples.xml.vo.XDoc;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;

public class XStreamInc {
	public static void main(String[] args) throws Exception {
		XStream xStream = new XStream();
		xStream.processAnnotations(XDoc.class);

		XDoc xDoc1 = (XDoc) xStream.fromXML(XStreamInc.class.getResourceAsStream("/doc1.xml"));
		XDoc xDoc2 = (XDoc) xStream.fromXML(XStreamInc.class.getResourceAsStream("/doc2.xml"));

		System.out.println("xDoc1 = " + xDoc1);
		System.out.println("xDoc2 = " + xDoc2);

//		XDoc xDoc3 = (XDoc) xStream.fromXML(XStreamInc.class.getResourceAsStream("/doc2.xml"), xDoc1);
//		System.out.println("xDoc3 = " + xDoc3);

		copy(xDoc1, xDoc2);

		System.out.println("xDoc1 (c) = " + xDoc1);
	}

	public static void copy(Object dest, Object src) throws Exception {
		copy(dest, src, null);
	}

	public static void copy(Object dest, Object src, String filterByPackage) throws Exception {
		PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(src);

		for (PropertyDescriptor pd : propertyDescriptors) {
			String prop = pd.getName();

			if ("class".equals(prop)) {
				continue;
			}

			Object destValue = PropertyUtils.getProperty(dest, prop);
			Object srcValue = PropertyUtils.getProperty(src, prop);

			if (srcValue != null) {
				if (destValue == null) {
					PropertyUtils.setProperty(dest, prop, srcValue);
				} else {
					if (srcValue instanceof Collection) {
						Collection srcCol = (Collection) srcValue;
						Collection destCol = (Collection) destValue;
						for (Object srcObjOfCol : srcCol) {
							Object destObjOfCol = findIn(srcObjOfCol, destCol);
							if (destObjOfCol == null) {
								destCol.add(srcObjOfCol);
							} else {
								copy(destObjOfCol, srcObjOfCol);
							}
						}
					} else if (pd.getPropertyType().isArray()) {
						Object[] srcArr = (Object[]) srcValue;
						Object[] destArr = (Object[]) destValue;
						for (Object srcObjOfArr : srcArr) {
							Object destObjOfArr = findIn(srcObjOfArr, destArr);
							if (destObjOfArr == null) {
								append(srcObjOfArr, destArr);
							} else {
								copy(destObjOfArr, srcObjOfArr);
							}
						}
					} else if (srcValue instanceof Map) {
						Map srcMap = (Map) srcValue;
						Map destMap = (Map) destValue;
						for (Object srcKey : srcMap.keySet()) {
							if (destMap.containsKey(srcKey)) {
								copy(destMap.get(srcKey), srcMap.get(srcKey));
							} else {
								destMap.put(srcKey, srcMap.get(srcKey));
							}
						}
					} else if (!pd.getPropertyType().getName().startsWith("java.lang.") ||
						filterByPackage == null ||
						pd.getPropertyType().getName().startsWith(filterByPackage)) {
						copy(destValue, srcValue);
					}
				}
			}
		}

	}

	public static Object findIn(Object obj, Collection col) {
		for (Object o : col) {
			if (o.equals(obj)) {
				return o;
			}
		}
		return null;
	}

	public static Object findIn(Object obj, Object[] arr) {
		for (Object o : arr) {
			if (o.equals(obj)) {
				return o;
			}
		}
		return null;
	}

	public static Object[] append(Object obj, Object[] arr) {
		Object[] result = new Object[arr.length + 1];
		int i;
		for (i = 0; i < arr.length; i++) {
			result[i] = arr[i];
		}
		result[i] = obj;

		return result;
	}
}
