package org.codehaus.mojo.gwt.webxml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * The goal is to find classed annotated with {@link RemoteServiceRelativePath}
 * to generated {@link ServletDescriptor}
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 * @plexus.component role="org.codehaus.mojo.gwt.webxml.ServletAnnotationFinder"
 * @since 2.1.0-1
 */
public class ServletAnnotationFinder extends AbstractLogEnabled {
	// test

	public ServletAnnotationFinder() {
		// no op
	}

	/**
	 * @param packageName
	 * @return cannot return <code>null</null>
	 * @throws IOException
	 */
	public Set<ServletDescriptor> findServlets(final String packageName, final String startPath,
			final ClassLoader classLoader) throws IOException {
		final Set<ServletDescriptor> servlets = new LinkedHashSet<ServletDescriptor>();
		final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(
				classLoader);
		final String patternFinder = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ ClassUtils.convertClassNameToResourcePath(packageName) + "/**/*.class";

		final Resource[] resources = pathMatchingResourcePatternResolver.getResources(patternFinder);
		final SimpleMetadataReaderFactory simpleMetadataReaderFactory = new SimpleMetadataReaderFactory();
		getLogger().debug("springresource " + resources.length + " for pattern " + patternFinder);
		for (final Resource resource : resources) {
			getLogger().debug("springresource " + resource.getFilename());
			final MetadataReader metadataReader = simpleMetadataReaderFactory.getMetadataReader(resource);

			if (metadataReader.getAnnotationMetadata().hasAnnotation(
					RemoteServiceRelativePath.class.getName())) {
				final Map<String, Object> annotationAttributes = metadataReader.getAnnotationMetadata().getAnnotationAttributes(
						RemoteServiceRelativePath.class.getName());
				getLogger().debug(
						"found RemoteServiceRelativePath annotation for class "
								+ metadataReader.getClassMetadata().getClassName());
				if (StringUtils.isNotBlank(startPath)) {
					final StringBuilder path = new StringBuilder();
					if (!startPath.startsWith("/")) {
						path.append('/');
					}
					path.append(startPath);
					final String annotationPathValue = (String) annotationAttributes.get("value");
					if (!annotationPathValue.startsWith("/")) {
						path.append('/');
					}
					path.append(annotationPathValue);
					final ServletDescriptor servletDescriptor = new ServletDescriptor(path.toString(),
							metadataReader.getClassMetadata().getClassName());
					servlets.add(servletDescriptor);
				} else {
					final StringBuilder path = new StringBuilder();
					final String annotationPathValue = (String) annotationAttributes.get("value");
					if (!annotationPathValue.startsWith("/")) {
						path.append('/');
					}
					path.append(annotationPathValue);
					final ServletDescriptor servletDescriptor = new ServletDescriptor(path.toString(),
							metadataReader.getClassMetadata().getClassName());
					servlets.add(servletDescriptor);
				}
			}
		}
		return servlets;
	}

}
