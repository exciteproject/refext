package de.exciteproject.refext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import de.exciteproject.refext.extract.CermineLineLayoutExtractor;
import de.exciteproject.refext.extract.CrfReferenceLineAnnotator;
import de.exciteproject.refext.extract.ReferenceLineMerger;
import pl.edu.icm.cermine.ComponentConfiguration;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * Class for applying reference extraction on text files that include layout
 * information (see {@link CermineLineLayoutExtractor}).
 *
 */
public class ReferenceExtractor {

    private CrfReferenceLineAnnotator crfReferenceLineAnnotator;

    private CermineLineLayoutExtractor cermineLineLayoutExtractor;

    public ReferenceExtractor(File crfModelFile) throws AnalysisException {
        this.crfReferenceLineAnnotator = new CrfReferenceLineAnnotator(crfModelFile);
        ComponentConfiguration componentConfiguration = new ComponentConfiguration();

        this.cermineLineLayoutExtractor = new CermineLineLayoutExtractor(componentConfiguration);
    }

    public List<String> extractReferencesFromLayoutFile(File layoutFile, Charset charset)
            throws IOException, AnalysisException {
        List<String> layoutLines = org.apache.commons.io.FileUtils.readLines(layoutFile, charset);
        return this.extractReferencesFromLayoutLines(layoutLines);
    }

    public List<String> extractReferencesFromLayoutLines(List<String> layoutLines)
            throws IOException, AnalysisException {
        List<String> annotatedLines = this.crfReferenceLineAnnotator.annotate(layoutLines);
        List<String> referenceStrings = ReferenceLineMerger.merge(annotatedLines);
        return referenceStrings;
    }

    public List<String> extractReferencesFromPdf(File pdfFile) throws IOException, AnalysisException {
        List<String> layoutLines = this.cermineLineLayoutExtractor.extract(pdfFile);
        return this.extractReferencesFromLayoutLines(layoutLines);
    }
}
