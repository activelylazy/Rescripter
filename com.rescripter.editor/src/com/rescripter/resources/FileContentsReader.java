package com.rescripter.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class FileContentsReader {

	public String getContents(InputStream inputStream) throws IOException {
        StringBuilder buff = new StringBuilder();
        LineNumberReader in = new LineNumberReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                buff.append(line).append("\n");
            }
        } finally {
            in.close();
        }

        return buff.toString();
	}
}
