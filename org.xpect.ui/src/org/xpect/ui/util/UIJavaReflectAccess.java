package org.xpect.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.access.IMirror;
import org.eclipse.xtext.common.types.access.TypeResource;
import org.eclipse.xtext.common.types.access.jdt.JdtTypeMirror;
import org.osgi.framework.Bundle;
import org.xpect.util.IJavaReflectAccess;

@SuppressWarnings("restriction")
public class UIJavaReflectAccess implements IJavaReflectAccess {
	private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

	private IPackageFragmentRoot getPackageFragmentRoot(IJavaElement ele) {
		IJavaElement current = ele;
		while (current != null) {
			if (current instanceof IPackageFragmentRoot)
				return (IPackageFragmentRoot) current;
			current = current.getParent();
		}
		return null;
	}

	private String getBundleNameFromDir(File file) {
		File current = file;
		int counter = 5;
		while (current != null && counter > 0) {
			File cand = new File(current + "/META-INF/MANIFEST.MF");
			if (cand.isFile()) {
				InputStream in = null;
				try {
					in = new FileInputStream(cand);
					return getBundleNameFromJar(new Manifest(in));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						return null;
					}
				}
			}
			current = current.getParentFile();
		}
		return null;
	}

	private String getBundleNameFromJar(Manifest manifest) {
		String name = manifest.getMainAttributes().getValue(BUNDLE_SYMBOLIC_NAME);
		if (name != null) {
			int i = name.indexOf(';');
			if (i >= 0)
				name = name.substring(0, i);
		}
		return name;
	}

	private String getBundleNameFromJar(File file) {
		try {
			return getBundleNameFromJar(new JarFile(file).getManifest());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Class<?> getRawType(JvmType jvmType) {
		if (jvmType == null || jvmType.eIsProxy())
			return null;
		if (!(jvmType.eResource() instanceof TypeResource))
			return null;
		IMirror typeMirror = ((TypeResource) jvmType.eResource()).getMirror();
		if (!(typeMirror instanceof JdtTypeMirror))
			return null;
		IType mirroredType = ((JdtTypeMirror) typeMirror).getMirroredType();
		IPackageFragmentRoot fragmentRoot = getPackageFragmentRoot(mirroredType);
		if (fragmentRoot == null || fragmentRoot.getPath() == null)
			return null;
		File file = fragmentRoot.getPath().toFile();
		String bundleName;
		if (file.isFile())
			bundleName = getBundleNameFromJar(file);
		else if (file.isDirectory())
			bundleName = getBundleNameFromDir(file);
		else
			return null;
		if (bundleName == null)
			return null;
		Bundle bundle = Platform.getBundle(bundleName);
		String className = jvmType.getQualifiedName();
		try {
			return bundle.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
