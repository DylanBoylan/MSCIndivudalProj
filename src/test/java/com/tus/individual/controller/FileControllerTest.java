package com.tus.individual.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tus.individual.dto.UserLoginDto;
import com.tus.individual.exception.FileFormatException;
import com.tus.individual.exception.FileStorageException;
import com.tus.individual.model.FileProcessingResult;
import com.tus.individual.service.IFileProcessorService;


@SpringBootTest
@AutoConfigureMockMvc
class FileControllerTest {
	private String jwt;
	
	@Autowired
	private AuthController authController;


	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IFileProcessorService fileProcessor;

	private ArgumentCaptor<String> filenameCaptor;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this); // Required for Mockito 2.21.0
		filenameCaptor = ArgumentCaptor.forClass(String.class);
		
		jwt =  authController.createJwt(new UserLoginDto("admin@networksys.com", "Admin123!")).getBody().getToken();
	}
    
	@Test
	void testHandleFileUpload_Success() throws Exception {
		FileProcessingResult result = new FileProcessingResult(10, 2);
		when(fileProcessor.uploadDataset(anyString())).thenReturn(result);

		mockMvc.perform(post("/import/test.xlsx").header("Authorization", "Bearer " + jwt))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.rowsProcessed").value(10)).andExpect(jsonPath("$.rowsSkipped").value(2))
				.andExpect(jsonPath("$.message").value("File uploaded successfully"));

		verify(fileProcessor).uploadDataset(filenameCaptor.capture());
	}

	@Test
	void testHandleFileUpload_FileFormatException() throws Exception {
		when(fileProcessor.uploadDataset(anyString())).thenThrow(new FileFormatException("Invalid format"));

		mockMvc.perform(post("/import/test.txt").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid format"));
	}

	@Test
	void testHandleFileUpload_FileStorageException() throws Exception {
		when(fileProcessor.uploadDataset(anyString())).thenThrow(new FileStorageException("File not found"));

		mockMvc.perform(post("/import/notfound.xlsx").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("File not found"));
	}


	@Test
	void testHandleFileUpload_InternalServerError() throws Exception {
	    when(fileProcessor.uploadDataset(anyString())).thenThrow(new RuntimeException("Internal Server Error"));

	    mockMvc.perform(post("/import/error.xlsx")
	            .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + jwt))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$.message").value("Internal Server Error"));
	}

	
	@Test
	void testHandleFileUpload_EmptyFile() throws Exception {
		when(fileProcessor.uploadDataset(anyString())).thenThrow(new FileFormatException("File is empty"));

		mockMvc.perform(post("/import/empty.xlsx").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("File is empty"));
	}

	@Test
	void testHandleFileUpload_LargeFile() throws Exception {
		when(fileProcessor.uploadDataset(anyString())).thenThrow(new FileStorageException("File size exceeds limit"));

		mockMvc.perform(post("/import/large.xlsx").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("File size exceeds limit"));
	}

	@Test
	void testHandleFileUpload_FileWithErrorData() throws Exception {
		when(fileProcessor.uploadDataset(anyString())).thenThrow(new FileFormatException("File contains invalid data"));

		mockMvc.perform(post("/import/errorData.xlsx").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("File contains invalid data"));
	}

	@Test
	void testHandleFileUpload_MismatchingData() throws Exception {
		when(fileProcessor.uploadDataset(anyString()))
				.thenThrow(new FileFormatException("Data does not match expected format"));

		mockMvc.perform(post("/import/mismatch.xlsx").header("Authorization", "Bearer " + jwt)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Data does not match expected format"));
	}

	@Test
	void testGetExcelFiles_Success() throws Exception {
		when(fileProcessor.getAllExcelFiles()).thenReturn(Arrays.asList("file1.xlsx", "file2.xlsx"));

		mockMvc.perform(get("/excel-files").header("Authorization", "Bearer " + jwt)).andExpect(status().isOk()).andExpect(jsonPath("$[0]").value("file1.xlsx"))
				.andExpect(jsonPath("$[1]").value("file2.xlsx"));
	}

	@Test
	void testGetExcelFiles_EmptyList() throws Exception {
		when(fileProcessor.getAllExcelFiles()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/excel-files").header("Authorization", "Bearer " + jwt)).andExpect(status().isOk()) // Empty list should return 200 OK, not 400 Bad
																		// Request
				.andExpect(jsonPath("$.length()").value(0));
	}
	
	@Test
	void testGetExcelFiles_Exception() throws Exception {
	    when(fileProcessor.getAllExcelFiles()).thenThrow(new RuntimeException("Unexpected error"));

	    mockMvc.perform(get("/excel-files").header("Authorization", "Bearer " + jwt))
	            .andExpect(status().isInternalServerError())
	            .andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	void testGetExcelFiles_LargeFileList() throws Exception {
	    List<String> largeFileList = IntStream.range(1, 101)
	            .mapToObj(i -> "file" + i + ".xlsx")
	            .collect(Collectors.toList());

	    when(fileProcessor.getAllExcelFiles()).thenReturn(largeFileList);

	    mockMvc.perform(get("/excel-files").header("Authorization", "Bearer " + jwt))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.length()").value(100));
	}

}