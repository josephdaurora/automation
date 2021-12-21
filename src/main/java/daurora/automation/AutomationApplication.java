package daurora.automation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@SpringBootApplication
@Controller
public class AutomationApplication {


	public static void main(String[] args) {
		SpringApplication.run(AutomationApplication.class, args);
	}

	@GetMapping("/")
	public String automation(Model model) {
		model.addAttribute("sqlCode", new sqlCode());
		return "index";
	}

	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> automationSave(sqlCode inputs, Model model) throws IOException {
		File previousFile = new File("exportedFile");
		previousFile.delete();
		automation.databaseReader(inputs.getCodetoExecute());
		String generatedCode = automation.testGenerator(inputs.getNumThreads(), inputs.getBuildName(), inputs.getQueueSize(), inputs.getTimeoutTime());
		File exportedFile = Files.write((Paths.get(Paths.get("").toAbsolutePath().toString(), "exportedFile")), generatedCode.getBytes(), StandardOpenOption.CREATE).toFile();
		InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream((exportedFile)));

		automation(model);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "exportedFile.java")
				.contentType(MediaType.TEXT_PLAIN)
				.contentLength(exportedFile.length())
				.body(inputStreamResource);
		}





}
