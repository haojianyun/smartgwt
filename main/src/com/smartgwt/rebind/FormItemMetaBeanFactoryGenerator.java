/*
 * Smart GWT (GWT for SmartClient)
 * Copyright 2008 and beyond, Isomorphic Software, Inc.
 *
 * Smart GWT is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * is published by the Free Software Foundation.  Smart GWT is also
 * available under typical commercial license terms - see
 * http://smartclient.com/license
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package com.smartgwt.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.rebind.BeanClass;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FormItemMetaBeanFactoryGenerator extends Generator {
    @Override
    public String generate (TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        JClassType formItemType = oracle.findType(FormItem.class.getCanonicalName());

        final String genPackageName = "com.smartgwt.client.bean";
        final String genClassName = "FormItemMetaBeanFactoryImpl";

        ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
        composer.addImplementedInterface(BeanFactory.FormItemMetaFactory.class.getCanonicalName());

        PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);
        if (printWriter != null) {
            SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
            sourceWriter.println("// This class lovingly generated by " + FormItemMetaBeanFactoryGenerator.class.getCanonicalName() + "\n");

            // Our constructor ... will be called by GWT.create()
            sourceWriter.println(genClassName + " () {");
            sourceWriter.indent();

            for (JClassType classType : oracle.getTypes()) {
                if (classType.isAssignableTo(formItemType) && isEligibleForGeneration(classType)) {
                    BeanClass beanClass = new BeanClass(classType, oracle);
                    beanClass.generateFactory(logger, context);

                    // We have to instantiate the factory to register it in the BeanFactory static API
                    sourceWriter.println(beanClass.getQualifiedFactoryName() + ".create(false);");
                }
            }

            sourceWriter.outdent();
            sourceWriter.println("}");
            sourceWriter.commit(logger);
        }

        return composer.getCreatedClassName();
    }

    private boolean isEligibleForGeneration (JClassType classType) {
        // Abstract classses will probably be generated as superclasses anyway, but
        // we don't need to generate them from here.
        if (classType.isAbstract()) return false;

        // We only generate factories for classes that have a no-arg constructor
        JConstructor constructor = classType.findConstructor(new JType[]{});
        if (constructor == null) return false;

        return true;
    }
}
