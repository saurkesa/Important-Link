package com.cisco.sdaf.controller.test;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * @author jatgarg
 * @since       1.0
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class AccessGettersAndSettersThroughReflection {

	private static final Logger logger = LoggerFactory.getLogger(AccessGettersAndSettersThroughReflection.class);

	@Test
	public void testGettersAndSettersCalled()
			throws IllegalAccessException, InstantiationException, ClassNotFoundException {


		Double aDouble = 10.0;
		int anInt = 1;
		String str = "abc";
		Long aLong = 2L;
		boolean decision = true;
		float floatValue = 7.7f;


		List<String> packageList = new ArrayList<>();
		List<String> classes = new ArrayList<>();
		packageList.add("com.cisco.creando.batch.model");
		packageList.add("com.cisco.sdaf.model.app");
		packageList.add("com.cisco.sdaf.model.wf");
		packageList.add("com.cisco.sdaf.model.wf.oozie");
		packageList.add("com.cisco.jwfm.stub.ws.model.app");
		packageList.add("com.cisco.jwfm.stub.ws.model.wf");
		packageList.add("com.cisco.jwfm.stub.ws.model");
		packageList.add("com.cisco.sdaf.wfo.jaxb");
		packageList.add("com.cisco.sdaf.dcp");
		packageList.add("com.cisco.sdaf.jms");
		for (String pack : packageList) {
			Reflections reflections = new Reflections(pack, new SubTypesScanner(false));

			Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);

			for (Class c : allClasses) {
				classes.add(c.getName());
			}
		}

		for (String className : classes) {
			logger.info("$$$$$$$Calling getters and setters for class....$$$$$$$" + className);
			Class<?> cls = Class.forName(className);
			if (Modifier.isAbstract(cls.getModifiers())) {
				logger.info("Class ..." + className + "  is abstract hence skipping...");
				continue;
			}
			Object ob = cls.newInstance();
//        while (rs.next()) {
			for (Field field : cls.getDeclaredFields()) {
				for (Method method : cls.getMethods()) {
					if ((method.getName().startsWith("set"))
							&& (method.getName().length() == (field.getName().length() + 3))) {
						if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
							// MZ: Method found, run it
							try {
//                            System.out.println("Field name called is "+field);
//                            System.out.println("method name called is "+method);
								method.setAccessible(true);
								if (field.getType().getSimpleName().toLowerCase().endsWith("integer"))
									method.invoke(ob, anInt);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("long"))
									method.invoke(ob, aLong);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("int"))
									method.invoke(ob, anInt);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("string"))
									method.invoke(ob, str);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("boolean"))
									method.invoke(ob, decision);
//								else if (field.getType().getSimpleName().toLowerCase().endsWith("timestamp"))
//									method.invoke(ob, null);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("date"))
									method.invoke(ob, new Date());
								else if (field.getType().getSimpleName().toLowerCase().endsWith("double"))
									method.invoke(ob, aDouble);
								else if (field.getType().getSimpleName().toLowerCase().endsWith("float"))
									method.invoke(ob, floatValue);
//								else if (field.getType().getSimpleName().toLowerCase().endsWith("time"))
//									method.invoke(ob, null);

								else if (field.getType() instanceof Class && ((Class<?>) field.getType()).isEnum()) {
									logger.info("Skipping Setting enum type for field " + field);

								} else if (field.getType().equals(List.class)) {
									System.out.println(" Setting list type for list field " + field);
									System.out.println("  Setting list type for list method " + method);
									method.invoke(ob, new ArrayList<>());

								} else if (field.getType().equals(Map.class)) {
									logger.info(" Setting list type for list field " + field);
									method.invoke(ob, new HashMap<>());

								} else {
									Class<?> ref = field.getType();
									logger.info("Setting reference type for field " + field);
									logger.info("Field type is " + ref);
									if (Modifier.isAbstract(ref.getModifiers())) {
										logger.info("Reference ..." + ref + "  is abstract hence skipping...");
//                                    continue;
									} else {
										method.invoke(ob, field.getType().newInstance());
									}
								}
							} catch (IllegalAccessException | InvocationTargetException e) {
								logger.error("Error is " + e.getMessage());
							}
						}
					}
				}
			}

			for (Field field : cls.getDeclaredFields()) {
				for (Method method : cls.getMethods()) {
					if ((method.getName().startsWith("get"))
							&& (method.getName().length() == (field.getName().length() + 3))) {
						if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
							// MZ: Method found, run it
							try {
								logger.info(" called getter for field name " + field);
								logger.info(" called getter method for field name " + method);
								method.setAccessible(true);
								method.invoke(ob);
							} catch (IllegalAccessException | InvocationTargetException e) {
								logger.error(e.getMessage());
							}
						}
					}
				}
			}
			System.out.println("Object after setters is " + ob);
			System.out.println("@@@@@@@Finished Calling getters and setters for class....@@@@@@@@@@" + className);
		}
	}
}
