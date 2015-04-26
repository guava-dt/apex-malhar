/*
 * Copyright (c) 2015 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatorrent.lib.appbuilder.convert.pojo;

import com.google.common.base.Preconditions;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IScriptEvaluator;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtils
{
  public static final String JAVA_DOT = ".";
  public static final String DEFAULT_TEMP_POJO_NAME = "dt_pojo";
  public static final String DEFAULT_POJO_NAME = "pojo";

  public static final String GET = "get";
  public static final String IS = "is";

  private static final IScriptEvaluator se;

  static {
    try
    {
      se = CompilerFactoryFactory.getDefaultCompilerFactory().newScriptEvaluator();
    }
    catch(Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private ConvertUtils()
  {
  }

  public static String upperCaseWord(String field)
  {
    Preconditions.checkArgument(!field.isEmpty(), field);
    return field.substring(0, 1).toUpperCase() + field.substring(1);
  }

  public static String getFieldGetter(String field)
  {
    return GET + upperCaseWord(field);
  }

  public static String getBooleanGetter(String field)
  {
    return IS + upperCaseWord(field);
  }

  public static String getFieldGetter(String field, boolean isBoolean)
  {
    if(isBoolean) {
      return getBooleanGetter(field);
    }
    else {
      return getFieldGetter(field);
    }
  }

  public static String fieldListToGetExpression(List<String> fields, boolean isBoolean)
  {
    StringBuilder sb = new StringBuilder();

    for(int index = 0;
        index < fields.size() - 1;
        index++) {
      String field = fields.get(index);
      sb.append(sb).append(getFieldGetter(field)).append(JAVA_DOT);
    }

    sb.append(getFieldGetter(fields.get(fields.size() - 1), isBoolean));

    return sb.toString();
  }

  public static Object createExpressionGetter(String fqClassName,
                                              String getterString,
                                              Class castClass,
                                              Class getterClass)
  {
    if(getterString.startsWith(".")) {
      getterString = getterString.substring(1);
    }

    if(getterString.isEmpty()) {
      throw new IllegalArgumentException("The getter string: "
                                         + getterString
                                         + "\nis invalid.");
    }

    try {
      //TODO In the future go through method stack and determine return types to avoid creating
      //an object if a primitive is present instead.

      //Corner cases return type was specified to be Boolean via generics
      //Return type is a Boolean object
      //Return type is a boolean primitive
      return se.createFastEvaluator("return (" + castClass.getName() +
                                      ") (((" + fqClassName +
                                      ")obj)." + getterString + ");",
                                      getterClass,
                                      new String[] {ConvertUtils.DEFAULT_POJO_NAME});
    }
    catch(CompileException ex) {
      throw new RuntimeException();
    }
  }

  public static GetterBoolean createExpressionGetterBoolean(String fqClassName,
                                                            String getterString)
  {
    return (GetterBoolean) createExpressionGetter(fqClassName,
                                                  getterString,
                                                  Boolean.class,
                                                  GetterBoolean.class);
  }

  public static GetterByte createExpressionGetterByte(String fqClassName,
                                                      String getterString)
  {
    return (GetterByte) createExpressionGetter(fqClassName,
                                               getterString,
                                               Byte.class,
                                               GetterByte.class);
  }

  public static GetterChar createExpressionGetterChar(String fqClassName,
                                                      String getterString)
  {
    return (GetterChar) createExpressionGetter(fqClassName,
                                               getterString,
                                               Character.class,
                                               GetterChar.class);
  }

  public static GetterDouble createExpressionGetterDouble(String fqClassName,
                                                          String getterString)
  {
    return (GetterDouble) createExpressionGetter(fqClassName,
                                                  getterString,
                                                  Double.class,
                                                  GetterDouble.class);
  }

  public static GetterFloat createExpressionGetterFloat(String fqClassName,
                                                        String getterString)
  {
    return (GetterFloat) createExpressionGetter(fqClassName,
                                                getterString,
                                                Float.class,
                                                GetterFloat.class);
  }

  public static GetterInt createExpressionGetterInt(String fqClassName,
                                                    String getterString)
  {
    return (GetterInt) createExpressionGetter(fqClassName,
                                              getterString,
                                              Integer.class,
                                              GetterInt.class);
  }

  public static GetterLong createExpressionGetterLong(String fqClassName,
                                                        String getterString)
  {
    return (GetterLong) createExpressionGetter(fqClassName,
                                               getterString,
                                               Long.class,
                                               GetterLong.class);
  }

  public static GetterShort createExpressionGetterShort(String fqClassName,
                                                        String getterString)
  {
    return (GetterShort) createExpressionGetter(fqClassName,
                                                getterString,
                                                Short.class,
                                                GetterShort.class);
  }

  public static GetterString createExpressionGetterString(String fqClassName,
                                                          String getterString)
  {
    return (GetterString) createExpressionGetter(fqClassName,
                                                 getterString,
                                                 String.class,
                                                 GetterString.class);
  }

  public static GetterObject createExpressionGetterObject(String fqClassName,
                                                          String getterString)
  {
    return (GetterObject) createExpressionGetter(fqClassName,
                                                 getterString,
                                                 Object.class,
                                                 GetterObject.class);
  }

  public static Object createExpressionGetter(String fqClassName,
                                              ArrayList<String> fieldList,
                                              Class castClass,
                                              Class getterClass)
  {
    return createExpressionGetter(fqClassName,
                                  ConvertUtils.fieldListToGetExpression(fieldList, castClass.equals((Boolean.class))),
                                  castClass,
                                  getterClass);
  }

  public static GetterBoolean createExpressionGetterBoolean(String fqClassName,
                                                            ArrayList<String> fieldList)
  {
    return (GetterBoolean) createExpressionGetter(fqClassName,
                                                  fieldList,
                                                  Boolean.class,
                                                  GetterBoolean.class);
  }

  public static GetterByte createExpressionGetterByte(String fqClassName,
                                                      ArrayList<String> fieldList)
  {
    return (GetterByte) createExpressionGetter(fqClassName,
                                               fieldList,
                                               Byte.class,
                                               GetterByte.class);
  }

  public static GetterChar createExpressionGetterChar(String fqClassName,
                                                      ArrayList<String> fieldList)
  {
    return (GetterChar) createExpressionGetter(fqClassName,
                                               fieldList,
                                               Character.class,
                                               GetterChar.class);
  }

  public static GetterDouble createExpressionGetterDouble(String fqClassName,
                                                          ArrayList<String> fieldList)
  {
    return (GetterDouble) createExpressionGetter(fqClassName,
                                                 fieldList,
                                                 Double.class,
                                                 GetterDouble.class);
  }

  public static GetterFloat createExpressionGetterFloat(String fqClassName,
                                                        ArrayList<String> fieldList)
  {
    return (GetterFloat) createExpressionGetter(fqClassName,
                                                fieldList,
                                                Float.class,
                                                GetterFloat.class);
  }

  public static GetterInt createExpressionGetterInt(String fqClassName,
                                                    ArrayList<String> fieldList)
  {
    return (GetterInt) createExpressionGetter(fqClassName,
                                              fieldList,
                                              Integer.class,
                                              GetterInt.class);
  }

  public static GetterLong createExpressionGetterLong(String fqClassName,
                                                      ArrayList<String> fieldList)
  {
    return (GetterLong) createExpressionGetter(fqClassName,
                                               fieldList,
                                               Long.class,
                                               GetterLong.class);
  }

  public static GetterShort createExpressionGetterShort(String fqClassName,
                                                        ArrayList<String> fieldList)
  {
    return (GetterShort) createExpressionGetter(fqClassName,
                                                fieldList,
                                                Short.class,
                                                GetterShort.class);
  }

  public static GetterString createExpressionGetterString(String fqClassName,
                                                          ArrayList<String> fieldList)
  {
    return (GetterString) createExpressionGetter(fqClassName,
                                                 fieldList,
                                                 String.class,
                                                 GetterString.class);
  }

  public static GetterObject createExpressionGetterObject(String fqClassName,
                                                          ArrayList<String> fieldList)
  {
    return (GetterObject) createExpressionGetter(fqClassName,
                                                 fieldList,
                                                 Object.class,
                                                 GetterObject.class);
  }

  public static Object createExtractionGetter(String extractionString,
                                              Class getterClass)
  {
    try {
      return (Object) se.createFastEvaluator(extractionString,
                                            getterClass,
                                            new String[] {ConvertUtils.DEFAULT_POJO_NAME});
    }
    catch(CompileException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static GetterBoolean createExtractionGetterBoolean(String extractionString)
  {
    return (GetterBoolean) createExtractionGetter(extractionString, GetterBoolean.class);
  }

  public static GetterByte createExtractionGetterByte(String extractionString)
  {
    return (GetterByte) createExtractionGetter(extractionString, GetterByte.class);
  }

  public static GetterChar createExtractionGetterChar(String extractionString)
  {
    return (GetterChar) createExtractionGetter(extractionString, GetterChar.class);
  }

  public static GetterDouble createExtractionGetterDouble(String extractionString)
  {
    return (GetterDouble) createExtractionGetter(extractionString, GetterDouble.class);
  }

  public static GetterFloat createExtractionGetterFloat(String extractionString)
  {
    return (GetterFloat) createExtractionGetter(extractionString, GetterFloat.class);
  }

  public static GetterInt createExtractionGetterInt(String extractionString)
  {
    return (GetterInt) createExtractionGetter(extractionString, GetterInt.class);
  }

  public static GetterLong createExtractionGetterLong(String extractionString)
  {
    return (GetterLong) createExtractionGetter(extractionString, GetterLong.class);
  }

  public static GetterShort createExtractionGetterShort(String extractionString)
  {
    return (GetterShort) createExtractionGetter(extractionString, GetterShort.class);
  }

  public static GetterString createExtractionGetterString(String extractionString)
  {
    return (GetterString) createExtractionGetter(extractionString, GetterString.class);
  }

  public static GetterObject createExtractionGetterObject(String extractionString)
  {
    return (GetterObject) createExtractionGetter(extractionString, GetterObject.class);
  }
}
