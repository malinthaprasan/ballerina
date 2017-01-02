/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerina.lang.util;

import org.wso2.ballerina.core.interpreter.BLangInterpreter;
import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.interpreter.StackFrame;
import org.wso2.ballerina.core.model.BallerinaFile;
import org.wso2.ballerina.core.model.Function;
import org.wso2.ballerina.core.model.SymbolName;
import org.wso2.ballerina.core.model.expressions.Expression;
import org.wso2.ballerina.core.model.expressions.FunctionInvocationExpr;
import org.wso2.ballerina.core.model.expressions.VariableRefExpr;
import org.wso2.ballerina.core.model.values.BValue;
import org.wso2.ballerina.core.utils.ParserUtils;

import java.util.Arrays;

/**
 * This contains test utils related to Ballerina function invocations
 *
 * @since 1.0.0
 */
public class Functions {

    private Functions() {
    }

    /**
     * Invokes a Ballerina function defined in the given source file
     *
     * @param sourceFilePath Ballerina source file path relative to test resources dir. If not absolute path
     * @param functionName   name of the function to be invoked
     * @param args           function arguments
     * @return return values from the function
     */
    public static BValue[] invoke(String sourceFilePath, String functionName, BValue[] args) {

        // 1) Get the Ballerina language model from the source file.
        BallerinaFile bFile = ParserUtils.parseBalFile(sourceFilePath);
        return invoke(bFile, functionName, args);
    }

    /**
     * Invokes a Ballerina function defined in the given language model
     *
     * @param bFile        parsed, analyzed and linked object model
     * @param functionName name of the function to be invoked
     * @param args         function arguments
     * @return return values from the function
     */

    public static BValue[] invoke(BallerinaFile bFile, String functionName, BValue[] args) {

        // 1) Check whether the given function is defined in the source file.
        Function function = bFile.getFunctions().get(functionName);
        if (function == null) {
            throw new RuntimeException("Function '" + functionName + "' is not defined");
        }

        // 2) Create variable reference expressions for each argument value;
        Expression[] exprs = new Expression[args.length];
        for (int i = 0; i < args.length; i++) {
            VariableRefExpr variableRefExpr = new VariableRefExpr(new SymbolName("arg" + i));
            variableRefExpr.setOffset(i);
            exprs[i] = variableRefExpr;
        }

        // 3) Create a function invocation expression
        FunctionInvocationExpr funcIExpr = new FunctionInvocationExpr(new SymbolName(functionName), exprs);
        funcIExpr.setOffset(args.length);
        funcIExpr.setFunction(function);


        //4) Invoke the function
        Context bContext = new Context();
        BValue[] functionArgs = args;
        if (function.getReturnTypes().length != 0) {
            functionArgs = Arrays.copyOf(args, args.length + function.getReturnTypes().length);
        }

        StackFrame currentStackFrame = new StackFrame(functionArgs, new BValue[0]);
        bContext.getControlStack().pushFrame(currentStackFrame);

        // 5) Invokes the function
        BLangInterpreter interpreter = new BLangInterpreter(bContext);
        funcIExpr.accept(interpreter);

        return Arrays.copyOfRange(functionArgs, function.getParameters().length, functionArgs.length);
    }
}