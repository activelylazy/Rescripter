package com.rescripter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

public class RescripterIntegrationTest {

	private IJavaProject javaProject;

	@Before public void 
	create_java_project() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
		project.create(null);
		project.open(null);
		
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
		
		javaProject = JavaCore.create(project);
		
		javaProject.setOutputLocation(project.getFolder("bin").getFullPath(), null);
		List<IClasspathEntry> classpaths = new ArrayList<IClasspathEntry>();
		classpaths.add(JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER")));
		classpaths.add(JavaCore.newSourceEntry(project.getFolder("src").getFullPath()));
		javaProject.setRawClasspath(classpaths.toArray(new IClasspathEntry[0]), null);
		
		IFile file = project.getFile(new Path("/src/com/example/Person.java"));
		createFile(file,
				getClass().getResourceAsStream("/com/example/Person.jav"));
	}
	
	@Test public void
	loads_files() throws JavaModelException {
		assertThat(javaProject.findType("com.example.Person"), is(notNullValue()));
	}

	private void createFile(IFile file, InputStream in) throws CoreException {
		if (file.exists()) {
			return;
		}
		if (!file.getParent().exists()) {
			createFolder((IFolder) file.getParent());
		}
		file.create(in, true, null);
	}

	private void createFolder(IFolder parent) throws CoreException {
		if (!parent.getParent().exists()) {
			createFolder((IFolder) parent.getParent());
		}
		parent.create(false, false, null);
	}
}