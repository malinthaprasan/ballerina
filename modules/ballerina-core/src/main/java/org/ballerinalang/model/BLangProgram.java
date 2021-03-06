/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.model;

import org.ballerinalang.model.symbols.BLangSymbol;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code BLangProgram} represents a Ballerina program.
 *
 * @since 0.8.0
 */
public class BLangProgram implements SymbolScope, Node {

    private Category programCategory;
    private BLangPackage entryPackage;
    private List<String> entryPoints = new ArrayList<>();
    private List<BLangPackage> libraryPackageList = new ArrayList<>();

    // Scope related variables
    private GlobalScope globalScope;
    private NativeScope nativeScope;
    private Map<SymbolName, BLangSymbol> symbolMap;

    // This is the actual path given by the user and this is used primarily for error reporting
    private Path programFilePath;

    public BLangProgram(GlobalScope globalScope, NativeScope nativeScope) {
        this.globalScope = globalScope;
        this.nativeScope = nativeScope;
        this.programCategory = programCategory;
        symbolMap = new HashMap<>();
    }

    public Path getProgramFilePath() {
        return programFilePath;
    }

    public void setProgramFilePath(Path programFilePath) {
        this.programFilePath = programFilePath;
    }

    public void setEntryPackage(BLangPackage entryPackage) {
        this.entryPackage = entryPackage;
    }

    public BLangPackage getEntryPackage() {
        return entryPackage;
    }

    public Category getProgramCategory() {
        return programCategory;
    }

    public void addLibraryPackage(BLangPackage bLangPackage) {
        libraryPackageList.add(bLangPackage);
    }

    public BLangPackage[] getLibraryPackages() {
        return libraryPackageList.toArray(new BLangPackage[0]);
    }

    public String[] getEntryPoints() {
        return entryPoints.toArray(new String[0]);
    }

    public void addEntryPoint(String entryPoint) {
        this.entryPoints.add(entryPoint);
    }

    public BLangPackage[] getPackages() {
        return symbolMap.values().stream().map(symbol -> (BLangPackage) symbol).toArray(BLangPackage[]::new);
    }


    // Methods in the SymbolScope interface

    @Override
    public ScopeName getScopeName() {
        return ScopeName.PROGRAM;
    }

    @Override
    public SymbolScope getEnclosingScope() {
        return globalScope;
    }

    public SymbolScope getNativeScope() {
        return nativeScope;
    }

    @Override
    public void define(SymbolName name, BLangSymbol symbol) {
        symbolMap.put(name, symbol);
    }

    @Override
    public BLangSymbol resolve(SymbolName name) {
        return resolve(symbolMap, name);
    }

    @Override
    public Map<SymbolName, BLangSymbol> getSymbolMap() {
        return Collections.unmodifiableMap(this.symbolMap);
    }


    // Methods in the Node interface

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NodeLocation getNodeLocation() {
        return null;
    }

    @Override
    public WhiteSpaceDescriptor getWhiteSpaceDescriptor() {
        return null;
    }


    /**
     * Indicates whether this program is a main program or a service program.
     *
     * @since 0.8.0
     */
    public enum Category {
        SERVICE_PROGRAM("service", ".bsz"),
        MAIN_PROGRAM("main", ".bmz"),
        LIBRARY_PROGRAM("library", ".blz");

        String name;
        String extension;

        Category(String name, String extension) {
            this.name = name;
            this.extension = extension;
        }

        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }
    }
}
