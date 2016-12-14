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
package org.wso2.ballerina.core.model.expressions;

import org.wso2.ballerina.core.interpreter.Context;
import org.wso2.ballerina.core.model.Node;
import org.wso2.ballerina.core.model.types.TypeC;
import org.wso2.ballerina.core.model.values.BValueRef;

/**
 * {@code Expression} represents a generic expression in Ballerina
 *
 * @see AddExpression
 * @see VariableRefExpr
 * @see FunctionInvocationExpr
 * @since 1.0.0
 */
public interface Expression extends Node {

    BValueRef evaluate(Context ctx);

    TypeC getType();

    void setType(TypeC type);

    BValueRef getBValueRef();

    int getOffset();

    void setBValueRef(BValueRef value);

    void setOffset(int offset);
}