package com.tus.individual.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@ConfigurationProperties("storage")
@Service
public class FilesConfig {

	private Path location;

	public Path getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = Paths.get(location);
	}

}
