package com.ghx.auto.core.reporting;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;


public class Reporter implements IReporter {
	
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {
		
		renderPDFReport(suites);
		
	}

	public void renderPDFReport(List<ISuite> suites) {
		 
	    try {
	    	SuiteResultsDataset suiteDataset = new SuiteResultsDataset(suites);
	        suiteDataset.getSuiteResultsData();
	    	HashMap<String,List<SuiteResults>> dataset = new HashMap<String, List<SuiteResults>>();
	    	dataset.put("myKey", suiteDataset.getSuiteResultsLst());
	    	
	    	EngineConfig config = new EngineConfig();  
	        config.setLogConfig("", Level.OFF);
	        Platform.startup(config);
	        final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
	        											.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	        IReportEngine engine = FACTORY.createReportEngine(config);       
	 
	        // Open the report design
	        IReportRunnable design = engine.openReportDesign(this.getClass().getResourceAsStream("/reportdesign/test.rptdesign")); 
	        IRunAndRenderTask task = engine.createRunAndRenderTask(design);        
	        
	        task.setAppContext(dataset);
	        
	        // render the output in PDF format
	        PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
	        PDF_OPTIONS.setOutputFileName("/Users/utadiparthi/Workspace/birt/test.pdf");
	        PDF_OPTIONS.setOutputFormat("pdf");
	 
	        task.setRenderOption(PDF_OPTIONS);
	        task.run();
	        task.close();
	        engine.destroy();
	        
	    } catch(final Exception ex) {
	        ex.printStackTrace();
	    } finally {
	       Platform.shutdown();
	    }
	}
}
