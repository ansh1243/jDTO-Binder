/*
 *    Copyright 2011 Juan Alberto López Cavallotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.juancavallotti.jdto.impl;

import com.juancavallotti.jdto.MultiPropertyValueMerger;
import com.juancavallotti.jdto.SinglePropertyValueMerger;
import com.juancavallotti.jdto.mergers.FirstObjectPropertyValueMerger;
import com.juancavallotti.jdto.mergers.IdentityPropertyValueMerger;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for bean analysis, by implementing the template method pattern
 * this class is responsible for the default bean analysis cycle, this means
 * reading all of the fields on a given bean class and delegating to the concrete
 * implementation what to do for each field but providing the funcionality to
 * get default values as needed.
 * @author juancavallotti
 */
abstract class AbstractBeanInspector {

    static final Logger logger = LoggerFactory.getLogger(AbstractBeanInspector.class);

     <T> BeanMetadata inspectBean(Class<T> beanClass) {
        logger.debug("Strarting analysis of " + beanClass.toString());
        try {

            //the returned metadata.
            BeanMetadata ret = new BeanMetadata();

            HashMap<String, Method> beanGetters = BeanPropertyUtils.discoverPropertiesByGetters(beanClass);

            //the standard cycle for analyzing a bean.
            for (String propertyName : beanGetters.keySet()) {

                FieldMetadata metadata = buildFieldMetadata(propertyName, beanGetters.get(propertyName), beanClass);

                //if the field is transient, then do not add it.
                if (metadata.isFieldTransient()) {
                    //nothing to bind, this could happen if the field is tansient
                    //or not found.
                    continue;
                }

                logger.debug("\tBound " + propertyName + " to " + metadata.getSourceFields().toString());


                ret.putFieldMetadata(propertyName, metadata);
            }


            String[] sourceBeanNames = readSourceBeanNames(beanClass);

            ret.setDefaultBeanNames(sourceBeanNames);

            return ret;

        } catch (Exception ex) {
            logger.error("Error while inspecting bean" + beanClass.toString(), ex);
            throw new RuntimeException("Could not inspect bean", ex);
        }
    }

    /**
     * Build the field metadata for a given field.
     * @param propertyName
     * @param readAccessor
     * @param beanClass 
     * @return 
     */
    abstract FieldMetadata buildFieldMetadata(String propertyName, Method readAccessor, Class beanClass);

    /**
     * Read the source names out of a class or whatever :)
     * @param beanClass
     * @return 
     */
    abstract String[] readSourceBeanNames(Class beanClass);

    /**
     * Build the default field metadata for a single field. This can be used either
     * to base your configuration on the standard and override as needed or
     * to just return it when no configuration is found.
     * @param propertyName
     * @return 
     */
    FieldMetadata buildDefaultFieldMetadata(String propertyName) {
        FieldMetadata ret = new FieldMetadata();

        //set the cascade
        ret.setCascadePresent(defaultCascadePresent());
        ret.setCascadeTargetClass(defaultCascadeTargetClass());
        ret.setCascadeType(defaultCascadeType());
        ret.setFieldTransient(defaultFieldTransient());
        ret.setMergerParameter(defaultMergerParameter());
        ret.setPropertyValueMerger(defaultMultiPropertyMerger());
        ret.setSourceBeanNames(defaultFieldSourceBeanNames());
        
        //the default source fields is the same property name.
        ret.setSourceFields(defaultSourceFields(propertyName));
        //set the data associated with the single property value merger
        ret.setSinglePropertyValueMerger(propertyName, defaultSinglePropertyMerger(), defaultMergerParameter(), defaultSourceBeanName());
        
        //and that is the default field metadata.
        //later on it can be used to customise it on other ways.

        return ret;
    }

    /**
     * Encapuslate the default value for cascade present.
     * @return 
     */
    static boolean defaultCascadePresent() {
        return false;
    }

    /**
     * as the default cascade is false, then it is safe to set null
     * as the default cascade target class.
     * @return 
     */
    static Class defaultCascadeTargetClass() {
        return Object.class;
    }

    /**
     * The default cascade type is safe to be null also.
     * @return 
     */
    static CascadeType defaultCascadeType() {
        return null;
    }

    /**
     * the default is all fields are not transient.
     * @return 
     */
    static boolean defaultFieldTransient() {
        return false;
    }

    /**
     * An empty string is the default merger parameter.
     * @return 
     */
    static String defaultMergerParameter() {
        return "";
    }
    
    /**
     * The defailt source bean name is ""
     * @return 
     */
    static String defaultSourceBeanName() {
        return "";
    }

    /**
     * The default multi property merger is the first value.
     * @return 
     */
    static MultiPropertyValueMerger defaultMultiPropertyMerger() {
        return InstancePool.getOrCreate(FirstObjectPropertyValueMerger.class);
    }
    //to save energy
    private static final String[] defaultSrouceBeanNames = {""};
    private static final String[] defaultFieldSrouceBeanNames = {};
    
    
    /**
     * The default source bean names. DO NOT CHANGE!!
     * @return 
     */
    static String[] defaultSourceBeanNames() {
        return defaultSrouceBeanNames;
    }
    
    /**
     * The default source bean fields should be an empty array.
     * @return 
     */
    static String[] defaultFieldSourceBeanNames() {
        return defaultFieldSrouceBeanNames;
    }

    /**
     * The default single property merger map.
     * @param propertyName
     * @return 
     */
    static HashMap<String, SinglePropertyValueMerger> defaultSinglePropertyMerger(String propertyName) {
        HashMap<String, SinglePropertyValueMerger> ret = new HashMap<String, SinglePropertyValueMerger>();
        ret.put(propertyName, defaultSinglePropertyMerger());
        return ret;
    }

    /**
     * The default single property merger params.
     * @param propertyName
     * @return 
     */
    static HashMap<String, String> defaultSinglePropertyMergerParams(String propertyName) {
        HashMap<String, String> ret = new HashMap<String, String>();
        ret.put(propertyName, "");
        return ret;
    }

    /**
     * The default single property merger instance.
     * @return 
     */
    static SinglePropertyValueMerger defaultSinglePropertyMerger() {
        return InstancePool.getOrCreate(IdentityPropertyValueMerger.class);
    }

    /**
     * Build the default source fields list.
     * @param propertyName
     * @return 
     */
    static List<String> defaultSourceFields(String propertyName) {
        LinkedList<String> ret = new LinkedList<String>();
        ret.add(propertyName);
        return ret;
    }

    /**
     * Applies the cascade logic in a generic way.
     * @param cascade
     * @param accesorType 
     * @param target 
     */
    static void applyCascadeLogic(Class cascadeTargetType, Method readAccesor, FieldMetadata target) {

        CascadeType cascadeType = null;
        
        Class accessorType = readAccesor.getReturnType();
        target.setCascadePresent(true);
        
        if (List.class.isAssignableFrom(accessorType)) {
            cascadeType = CascadeType.COLLECTION;
        } else if (accessorType.isArray()) {
            cascadeType = CascadeType.ARRAY;
        } else {
            cascadeType = CascadeType.SINGLE;
        }

        //if the target type is pressent on the annotation, then all is quite simple
        if (cascadeTargetType != null && cascadeTargetType != Object.class) {
            target.setCascadeTargetClass(cascadeTargetType);
        } else { //if not, then inferit by the declaration on the dto.
            Class targetType = inferTypeOfProperty(accessorType, readAccesor.getGenericReturnType(), cascadeType);
            target.setCascadeTargetClass(targetType);
        }

        target.setCascadeType(cascadeType);
    }

    /**
     * Try to infer the type of a property, if the property is a collection, then
     * try to infer generic type. <br />
     * COPIED FROM BEAN INSPECTOR - //TODO UNIFY THIS INTO JUST ONE METHOD
     * @param field
     * @param ter
     * @param cascadeType
     * @return 
     */
    private static Class inferTypeOfProperty(Class accessorType, Type accesorGenericType, CascadeType cascadeType) {

        switch (cascadeType) {
            case SINGLE:
                return accessorType;
            case ARRAY:
                return accessorType.getComponentType();
            case COLLECTION:
                return BeanPropertyUtils.getGenericTypeParameter(accesorGenericType);
        }
        return null;
    }
}
