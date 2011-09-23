package com.rescripter.script;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class WorkspaceFileReaderTest {

	private Mockery context = new Mockery();
	
	@Test public void
	reads_contents_of_a_stream() throws IOException, CoreException {
		final IFile file = context.mock(IFile.class);
		String contents = "first line\nof the contents\n";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		context.checking(new Expectations() {{
			oneOf(file).getContents(); will(returnValue(inputStream));
		}});
		
		WorkspaceFileReader reader = new WorkspaceFileReader();
		String readContents = reader.getContents(file);
		
		assertThat(readContents, is(contents));
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	reads_windows_format_file_contents() throws IOException, CoreException {
		final IFile file = context.mock(IFile.class);
		String contents = "first line\r\nof the contents\r\n";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		context.checking(new Expectations() {{
			oneOf(file).getContents(); will(returnValue(inputStream));
		}});
		
		WorkspaceFileReader reader = new WorkspaceFileReader();
		String readContents = reader.getContents(file);
		
		assertThat(readContents, is(contents.replaceAll("\r\n", "\n")));
		
		context.assertIsSatisfied();
	}
	
	@Test public void
	appends_cr_to_contents() throws IOException, CoreException {
		final IFile file = context.mock(IFile.class);
		String contents = "contents";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		context.checking(new Expectations() {{
			oneOf(file).getContents(); will(returnValue(inputStream));
		}});
		
		WorkspaceFileReader reader = new WorkspaceFileReader();
		String readContents = reader.getContents(file);
		
		assertThat(readContents, is(contents + "\n"));
		
		context.assertIsSatisfied();
	}
	
}