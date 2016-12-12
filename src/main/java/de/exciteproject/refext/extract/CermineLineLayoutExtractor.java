package de.exciteproject.refext.extract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import de.exciteproject.refext.util.CsvUtils;
import de.exciteproject.refext.util.FileUtils;
import de.exciteproject.refext.util.TextUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 * Extension of {@link CermineLineExtractor} to also store layout information on
 * the inividual lines.
 */
public class CermineLineLayoutExtractor extends CermineLineExtractor {

    /**
     *
     * @param args
     *            args[0]: directory containing pdf files and/or subfolders with
     *            pdf files </br>
     *            args[1]: directory in which the outputfiles are stored,
     *            including the subdirectories
     *
     * @throws IOException
     * @throws AnalysisException
     */
    public static void main(String[] args) throws IOException, AnalysisException {
	File inputDir = new File(args[0]);
	File outputDir = new File(args[1]);

	if (!outputDir.exists()) {
	    outputDir.mkdirs();
	}

	List<File> inputFiles = FileUtils.listFilesRecursively(inputDir);
	CermineLineLayoutExtractor cermineLineLayoutExtractor = new CermineLineLayoutExtractor();

	Instant start = Instant.now();
	for (File inputFile : inputFiles) {
	    System.out.println("processing: " + inputFile);

	    File currentOutputDirectory;

	    String subDirectories = inputFile.getParentFile().getAbsolutePath().replaceFirst(inputDir.getAbsolutePath(),
		    "");
	    currentOutputDirectory = new File(outputDir.getAbsolutePath() + File.separator + subDirectories);

	    String outputFileName = FilenameUtils.removeExtension(inputFile.getName()) + ".txt";
	    File outputFile = new File(currentOutputDirectory.getAbsolutePath() + File.separator + outputFileName);

	    // skip computation if outputFile already exists
	    // if (outputFile.exists()) {
	    // continue;
	    // }

	    if (!currentOutputDirectory.exists()) {
		currentOutputDirectory.mkdirs();
	    }
	    List<String> lines = cermineLineLayoutExtractor.extract(inputFile);
	    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
	    for (String line : lines) {
		bufferedWriter.write(line);
		bufferedWriter.newLine();
	    }
	    bufferedWriter.close();
	}

	Instant end = Instant.now();
	System.out.println("Done. Execution time: " + Duration.between(start, end));
    }

    public CermineLineLayoutExtractor() throws AnalysisException {
	super();
    }

    /**
     * TODO add explanation to x/y/height/width/zoneID
     */
    @Override
    protected String extractFromLine(BxLine bxLine) {
	String fixedLine = TextUtils.fixAccents(bxLine.toText());
	fixedLine = CsvUtils.normalize(fixedLine);
	fixedLine += "\t" + bxLine.getX() + "\t" + bxLine.getY() + "\t" + bxLine.getHeight() + "\t" + bxLine.getWidth()
		+ "\t" + bxLine.getParent().getId();
	return fixedLine;

    }

}