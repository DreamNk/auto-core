/*
 * Copyright (c) 2015 Global Healthcare Exchange, LLC. All rights reserved.
 */

package com.ghx.auto.core.reporting;

import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;


/**
 * @author utadiparthi
 *
 */
public class PDFReporter {
	
	public void executeReport() throws EngineException {
		 
	    IReportEngine engine = null;
	    EngineConfig config = null;
	 
	    try {
	        config = new EngineConfig();          
	        //config.setEngineHome("/Users/utadiparthi/Downloads/birt-runtime-4_4_2/ReportEngine");
	        config.setLogConfig("", Level.OFF);
	        Platform.startup(config);
	        final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
	            .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	        engine = FACTORY.createReportEngine(config);       
	 
	        // Open the report design
	        IReportRunnable design = null;
	        design = engine.openReportDesign(this.getClass().getResourceAsStream("/reportdesign/test.rptdesign")); 
	        IRunAndRenderTask task = engine.createRunAndRenderTask(design);        
	 
	        PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
	        PDF_OPTIONS.setOutputFileName("/Users/utadiparthi/Workspace/birt/test.pdf");
	        PDF_OPTIONS.setOutputFormat("pdf");
	 
	        task.setRenderOption(PDF_OPTIONS);
	        task.run();
	        task.close();
	        engine.destroy();
	    } catch(final Exception EX) {
	        EX.printStackTrace();
	    } finally {
	       Platform.shutdown();
	    }
	}
	
	public static void main(final String[] ARGUMENTS) {
        try {
           new PDFReporter().executeReport();
        } catch (final Exception EX) {
           EX.printStackTrace();
        }
    }
	
}
