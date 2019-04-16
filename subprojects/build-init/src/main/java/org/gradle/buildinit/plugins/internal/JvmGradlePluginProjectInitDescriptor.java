/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.buildinit.plugins.internal;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.util.GUtil;

public abstract class JvmGradlePluginProjectInitDescriptor extends JvmProjectInitDescriptor {
    private final DocumentationRegistry documentationRegistry;

    public JvmGradlePluginProjectInitDescriptor(BuildScriptBuilderFactory scriptBuilderFactory, TemplateOperationFactory templateOperationFactory, FileResolver fileResolver, TemplateLibraryVersionProvider libraryVersionProvider, DocumentationRegistry documentationRegistry) {
        super(scriptBuilderFactory, templateOperationFactory, fileResolver, libraryVersionProvider);
        this.documentationRegistry = documentationRegistry;
    }

    @Override
    public void generate(InitSettings settings, BuildScriptBuilder buildScriptBuilder) {
        super.generate(settings, buildScriptBuilder);

        String pluginId = settings.getPackageName() + "." + settings.getProjectName();
        String pluginClassName = StringUtils.capitalize(GUtil.toCamelCase(settings.getProjectName())) + "Plugin";
        String testClassName = pluginClassName + "Test";

        buildScriptBuilder
            .fileComment("This generated file contains a sample Gradle plugin project to get you started.")
            .fileComment("For more details take a look at the Writing Custom Plugins chapter in the Gradle")
            .fileComment("User Manual available at " + documentationRegistry.getDocumentationFor("custom_plugins"));
        buildScriptBuilder.plugin("Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins", "java-gradle-plugin");
        ScriptBlockBuilder pluginBlock = buildScriptBuilder.block(null, "gradlePlugin")
            .block(null, "plugins")
            .block("Define the plugin", "create(\"greeting\")");
        pluginBlock.propertyAssignment(null, "id", pluginId);
        pluginBlock.propertyAssignment(null, "implementationClass", settings.getPackageName() + "." + pluginClassName);

        TemplateOperation javaSourceTemplate = sourceTemplate(settings, pluginClassName);
        TemplateOperation javaTestTemplate = testTemplate(settings, pluginClassName, testClassName);
        whenNoSourcesAvailable(javaSourceTemplate, javaTestTemplate).generate();
    }

    protected abstract TemplateOperation sourceTemplate(InitSettings settings, String pluginClassName);

    protected abstract TemplateOperation testTemplate(InitSettings settings, String pluginClassName, String testClassName);
}
