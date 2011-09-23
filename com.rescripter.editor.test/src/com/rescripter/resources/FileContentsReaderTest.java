package com.rescripter.resources;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class FileContentsReaderTest {
	
	@Test public void
	reads_contents_of_a_stream() throws IOException, CoreException {
		String contents = "first line\nof the contents\n";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		FileContentsReader reader = new FileContentsReader();
		String readContents = reader.getContents(inputStream);
		
		assertThat(readContents, is(contents));
	}
	
	@Test public void
	reads_windows_format_file_contents() throws IOException, CoreException {
		String contents = "first line\r\nof the contents\r\n";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		FileContentsReader reader = new FileContentsReader();
		String readContents = reader.getContents(inputStream);
		
		assertThat(readContents, is(contents.replaceAll("\r\n", "\n")));
	}
	
	@Test public void
	appends_cr_to_contents() throws IOException, CoreException {
		String contents = "contents";
		final InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		
		FileContentsReader reader = new FileContentsReader();
		String readContents = reader.getContents(inputStream);
		
		assertThat(readContents, is(contents + "\n"));
	}
	
}