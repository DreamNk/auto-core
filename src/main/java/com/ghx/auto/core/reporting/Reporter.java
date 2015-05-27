package com.ghx.auto.core.reporting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang3.StringUtils;
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

import com.ghx.auto.core.reporting.data.SuiteResults;
import com.ghx.auto.core.reporting.data.SuiteResultsDao;
import com.ghx.auto.core.ui.support.EnvConfig;


public class Reporter implements IReporter {
	
	EnvConfig envConfig;
	String outputDirectory;
	
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
			String outputDirectory) {
		
		if (suites.size() > 0) {
			envConfig = (EnvConfig) suites.get(0).getAttribute(EnvConfig.class.getName());
		}
		
		this.outputDirectory = System.getProperty("reportOutputLocation");
        if (StringUtils.isBlank(this.outputDirectory)) {
    		this.outputDirectory = getConfigParamValue("REPORTING", "reportOutputLocation");
        }
		
		if(StringUtils.isBlank(this.outputDirectory)) {
			this.outputDirectory = outputDirectory;
		}
		
		String pdfFormat = System.getProperty("pdf");
		if (StringUtils.isBlank(pdfFormat)) {
			pdfFormat = getConfigParamValue("REPORTING", "pdf");
        }

		if("yes".equalsIgnoreCase(pdfFormat)) {
			SuiteResultsDao dao = new SuiteResultsDao(suites);
			renderPDFReport(dao.getSuiteResultsLst());
		}
	}

	private void renderPDFReport(List<SuiteResults> suiteResultsLst) {
		 
	    try {
	    	HashMap<String,List<SuiteResults>> context = new HashMap<String, List<SuiteResults>>();
	    	context.put("APP_CONTEXT_KEY_SUITERESULTSDATASET", suiteResultsLst);
	    	EngineConfig config = new EngineConfig();  
	        config.setLogConfig("", Level.OFF);
	        Platform.startup(config);
	        final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
	        											.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	        IReportEngine engine = FACTORY.createReportEngine(config);       
	 
	        // Open the report design
	        IReportRunnable design = engine.openReportDesign(this.getClass().getResourceAsStream("/reportdesign/testresults.rptdesign")); 
	        IRunAndRenderTask task = engine.createRunAndRenderTask(design);        
	        
	        task.setAppContext(context);
	        
	        DateFormat df = new SimpleDateFormat("MMddyy_HHmmss");
	        
	        // render the output in PDF format
	        PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
	        PDF_OPTIONS.setOutputFileName(outputDirectory + "/" + "testresults_" + df.format(new Date()) + ".pdf");
	        PDF_OPTIONS.setOutputFormat("pdf");
	 
	        task.setRenderOption(PDF_OPTIONS);
	        task.run();
	        task.close();
	        engine.destroy();
	        
	    } catch(final Exception ex) {
	        throw new RuntimeException("Unable to generate the report, " + ex.getMessage());
	    } finally {
	       Platform.shutdown();
	    }
	}
	
	private String getConfigParamValue(String section, String param) {
    	Map<String, String> configSection = this.envConfig.get(section.toUpperCase());
    	if (configSection != null) {
    		return configSection.get(param);
    	}
    	
    	return "";
    }
}
