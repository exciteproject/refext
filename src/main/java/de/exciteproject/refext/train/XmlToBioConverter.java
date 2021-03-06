package de.exciteproject.refext.train;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;

import de.exciteproject.refext.util.FileUtils;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * Class for converting XML tagged references into BIO notation
 */
public class XmlToBioConverter {

    public static void main(String[] args) throws AnalysisException, IOException {
        File inputDir = new File(args[0]);
        File outputDir = new File(args[1]);
        String bRefLabel = args[2];
        String iRefLabel = args[3];
        String oRefLabel = args[4];
        String otherLabel = args[5];
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        XmlToBioConverter xmlToBioConverter = new XmlToBioConverter(bRefLabel, iRefLabel, oRefLabel, otherLabel);
        List<File> inputFiles = FileUtils.asList(inputDir);
        String inputDirectoryPath = FileUtils.getDirctory(inputDir).getAbsolutePath();

        for (File inputFile : inputFiles) {
            String inputFileSubPath = inputFile.getAbsolutePath().replace("\\", "/").replaceAll(inputDirectoryPath.replace("\\", "/"), "");
            inputFileSubPath = FilenameUtils.removeExtension(inputFileSubPath).concat(".csv");

            File outputFile = new File(outputDir + inputFileSubPath);
            // if (outputFile.exists()) {
            // continue;
            // }

            List<String> annotatedText = xmlToBioConverter.annotateText(inputFile);
            Files.write(Paths.get(outputFile.getAbsolutePath()), annotatedText, Charset.defaultCharset());
        }
    }

    private String bRefLabel;
    private String iRefLabel;
    private String oRefLabel;
    private String otherLabel;

    public XmlToBioConverter(String bRefLabel, String iRefLabel, String oRefLabel, String otherLabel) {
        this.bRefLabel = bRefLabel;
        this.iRefLabel = iRefLabel;
        this.oRefLabel = oRefLabel;
        this.otherLabel = otherLabel;
    }

    public List<String> annotateText(File inputFile) throws IOException, AnalysisException {
        Scanner s = new Scanner(inputFile,"UTF-8");
        ArrayList<String> xmlAnnotatedLines = new ArrayList<String>();
        while (s.hasNextLine()) {
            xmlAnnotatedLines.add(s.nextLine());
        }
        s.close();

        List<String> bioAnnotatedLines = new ArrayList<String>();
        boolean insideRef = false;
        boolean insideOth = false;
        for (int i = 0; i < xmlAnnotatedLines.size(); i++) {
            String label = "";
            String line = xmlAnnotatedLines.get(i);
            if (line.contains("<ref>")) {
                label = this.bRefLabel;
                insideRef = true;
                line = line.replaceFirst("<ref>", "");
            }
            if (line.contains("</ref")) {
                if (label.isEmpty()) {
                    label = this.iRefLabel;
                }
                insideRef = false;
                line = line.replaceFirst("</ref>", "");
            }
            if (line.contains("<oth>")) {
                label = this.oRefLabel;
                insideOth = true;
                line = line.replaceFirst("<oth>", "");
            }
            if (line.contains("</oth")) {
                if (label.isEmpty()) {
                    label = this.oRefLabel;
                }
                insideOth = false;
                line = line.replaceFirst("</oth>", "");
            }
            if (label.isEmpty()) {
                if (insideOth) {
                    label = this.oRefLabel;
                } else {
                    if (insideRef) {
                        label = this.iRefLabel;
                    } else {
                        label = this.otherLabel;
                    }
                }
            }
            bioAnnotatedLines.add(label + "\t" + line);

        }
        return bioAnnotatedLines;
    }
}
