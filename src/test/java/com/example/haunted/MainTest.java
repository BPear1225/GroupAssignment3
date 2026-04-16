package com.example.haunted;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {
	private final PrintStream originalOut = System.out;
	private static OutputStream captureOutput;

	@BeforeEach
	public void setUp() {
		captureOutput = new ByteArrayOutputStream();
		System.setOut(new PrintStream(captureOutput));
	}

	@AfterEach
	public void restore() {
		System.out.flush();
		System.setOut(originalOut);
	}

	@Test
	void testMain() {
		Main.main(new String[] {});
		String[] outputStrings = captureOutput.toString().split(System.lineSeparator());
		assertEquals(outputStrings[0], "Maintenance Stairwell");
		assertEquals(outputStrings[1], "Moved to Abandoned Lecture Hall.");
		assertEquals(outputStrings[2], "Picked up Coffee Potion.");
	}
	
	@Test
	void createMain() {
		Main testMain = new Main();
		assertNotEquals(testMain, null);
	}

}
