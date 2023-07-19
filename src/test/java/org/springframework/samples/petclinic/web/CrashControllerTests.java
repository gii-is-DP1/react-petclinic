package org.springframework.samples.petclinic.web;

/**
 * Test class for {@link CrashController}
 *
 * @author Colin But
 */
//Waiting https://github.com/spring-projects/spring-boot/issues/5574
/*@WebMvcTest(CrashController.class)
class CrashControllerTests {

	@Autowired
	private CrashController crashController;

	
	@Autowired
	private MockMvc mockMvc;
	

	@Test
	void testTriggerException() throws Exception {
		mockMvc.perform(get("/oups")).andExpect(view().name("exception"))
				.andExpect(model().attributeExists("exception")).andExpect(forwardedUrl("exception"))
				.andExpect(status().isOk());
	}

}*/
