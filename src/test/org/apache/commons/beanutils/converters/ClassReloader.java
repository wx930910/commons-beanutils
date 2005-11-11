/*
 * Copyright 2005 The Apache Software Foundation.
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

package org.apache.commons.beanutils.converters;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A special classloader useful for testing j2ee-like scenarios.
 * 
 * <p>In some tests we want to be able to emulate "container" frameworks,
 * where code runs in a hierarchy of classloaders, and certain classes may
 * be loaded by various classloaders in the hierarchy.</p>
 *
 * <p>Normally this is done by having certain jars or class-file-directories
 * in the classpath of some classloaders but not others. This is quite 
 * difficult difficult to integrate with the build process for the unit
 * tests though; compiling certain classes and having the output go into
 * places that is not in the default classpath for the unit tests would be
 * a major pain.</p>
 *
 * <p>So this class takes a sneaky alternative approach: it can grab any class
 * already loaded by a parent classloader and <i>reload</i> that class via this
 * classloader. The effect is exactly as if a class (or jar file) had been
 * present in the classpath for a container's "shared" classloader <i>and</i>
 * been present in the component-specific classpath too, without any messing
 * about with the way unit test code is compiled or executed.
 */

public class ClassReloader extends ClassLoader {
    public ClassReloader(ClassLoader parent) {
        super(parent);
    }
    
    /**
     * Given a class already in the classpath of a parent classloader,
     * reload that class via this classloader.
     */
    public Class reload(Class clazz) throws FileNotFoundException, IOException {
        String className = clazz.getName();
        String classFile = className.replace('.', '/') + ".class";
        InputStream classStream = getParent().getResourceAsStream(classFile);
        
        if (classStream == null) {
            throw new FileNotFoundException(classFile);
        }
        
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(;;) {
            int bytesRead = classStream.read(buf);
            if (bytesRead == -1)
                break;
            baos.write(buf, 0, bytesRead);
        }
        classStream.close();
        
        byte[] classData = baos.toByteArray();
        
        // now we have the raw class data, let's turn it into a class
        Class newClass = defineClass(className, classData, 0, classData.length);
        resolveClass(newClass);
        return newClass;
    }
}