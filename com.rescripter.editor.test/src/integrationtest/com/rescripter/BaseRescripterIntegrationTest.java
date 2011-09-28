package com.rescripter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.eclipse.ui.IWorkbenchWindow;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;

abstract public class BaseRescripterIntegrationTest {
	private final Mockery context = new Mockery() {{ setImposteriser(ClassImposteriser.INSTANCE); }};
	private final IWorkbenchWindow window = context.mock(IWorkbenchWindow.class);
	
	private IJavaProject javaProject;

	@Before public void 
	create_java_project() throws CoreException {
		String projectName = generateProjectName();
		System.out.println("Creating project " + projectName);
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
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
	
	protected IWorkbenchWindow getWindow() { return window; }
	protected IJavaProject getJavaProject() { return javaProject; }

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
	
	private static String generateProjectName() {
		return "Test-" + UUID.randomUUID().toString();
	}
}
