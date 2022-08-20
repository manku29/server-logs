/**
 * 
 */
package com.md.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.md.logs.ApplicationLogsCalculation;

/**
 * @author md
 *
 */
public class LogsCheckTest {
private static final String[] FILE = {"logfile.txt"};
private static final String[] FILE1 = {"log.txt"};
	
	@Test
	void testValidateInput() {
		assertThat(new ApplicationLogsCalculation().validateInputFile(FILE));
	}
	
	@Test
	void testValidateInputNVe() {
		assertThat(!new ApplicationLogsCalculation().validateInputFile(FILE1));
	}
	
	
	@Test 
	void test_readEvents() {
		new ApplicationLogsCalculation().readEvents("logfile.txt");
	}
	

}
